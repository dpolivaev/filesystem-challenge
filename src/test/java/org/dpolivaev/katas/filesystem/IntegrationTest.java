package org.dpolivaev.katas.filesystem;

import org.dpolivaev.katas.filesystem.internal.filesystem.TestFileSystem;
import org.dpolivaev.katas.filesystem.internal.persistence.FileSystemFactory;
import org.junit.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class IntegrationTest {
    public static final int FILE_SYSTEM_SIZE = 1024 * 1024;
    java.io.File fsFile;
    public static final int POSITION_NEAR_THE_END = FILE_SYSTEM_SIZE * 99 / 100;

    private final static Random random = new Random(0);
    final private static java.io.File tempDirectory = new java.io.File("temp");

    @BeforeClass
    public static void prepateTestFileDirectory() {
        tempDirectory.mkdirs();
        cleanTestFileDirectory();
    }

    @AfterClass
    public static void cleanTestFileDirectory() {
        Stream.of(tempDirectory.listFiles()).forEach(java.io.File::delete);
    }

    private static java.io.File createFileSystemFile() {
        try {
            final java.io.File tempFile = java.io.File.createTempFile("filesystem", ".kata", tempDirectory);
            tempFile.delete();
            return tempFile;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FileSystem createFilesystem() {
        return FileSystem.create(fsFile.getPath(), FILE_SYSTEM_SIZE);
    }

    private FileSystem openFilesystem() {
        return FileSystem.open(fsFile.getPath());
    }

    private FileSystem createThreadSafeFilesystem() {
        return FileSystem.createThreadSafe(fsFile.getPath(), (long) FILE_SYSTEM_SIZE);
    }

    private FileSystem createThreadSafeFilesystemInMemory() {
        return TestFileSystem.createThreadSafe(FILE_SYSTEM_SIZE / 1024, 1024).fileSystem;
    }

    private FileSystem openThreadSafeFilesystem() {
        return FileSystem.openThreadSafe(fsFile.getPath());
    }

    @Before
    public void setUp() {
        fsFile = createFileSystemFile();
    }

    @After
    public void tearDown() {
        fsFile.delete();
    }

    private File createHugeFile(final FileSystem fileSystem, final String fileName,
                                final String string) {
        final File hugeFile = fileSystem.root().createFile(fileName);
        assertThat(hugeFile.size()).isEqualTo(0L);
        hugeFile.setPosition(POSITION_NEAR_THE_END);
        hugeFile.write(string);
        return hugeFile;
    }

    private void checkHugeFileContent(final File hugeFile, final String expectedString) {
        hugeFile.setPosition(POSITION_NEAR_THE_END);
        final String string = hugeFile.readString();
        assertThat(string).isEqualTo(expectedString);
    }

    @Test
    public void createAndUseHugeFile() {
        try (final FileSystem fileSystem = createFilesystem()) {
            final File hugeFile = createHugeFile(fileSystem, "hugeFile", "Hello world");
            checkHugeFileContent(hugeFile, "Hello world");
        }

        try (final FileSystem fileSystem = openFilesystem()) {
            final File hugeFile = fileSystem.root().file("hugeFile").get();
            checkHugeFileContent(hugeFile, "Hello world");
            fileSystem.root().deleteFile(hugeFile.name());
            final File otherFile = createHugeFile(fileSystem, "otherFile", "Other");
            checkHugeFileContent(otherFile, "Other");
            fileSystem.root().deleteFile(otherFile.name());
        }
    }

    @Test
    public void writesAndReadsNumbers() {
        try (final FileSystem fileSystem = createFilesystem()) {
            for (int i = 0; i < 100; i++) {
                checkWritingAndReadingNumbers(fileSystem, "file");
                fileSystem.root().deleteFile("file");
            }
        }
    }

    @Test
    public void writesAndReadsNumbersThreadSafelyInMemory() throws Throwable {
        final Semaphore availableThreads = new Semaphore(10, true);
        final LinkedBlockingQueue<Optional<Throwable>> testResults = new LinkedBlockingQueue<>();
        final int testThreadCounter = 200;
        try (final FileSystem fileSystem = createThreadSafeFilesystemInMemory()) {
            for (int i = 0; i < testThreadCounter; i++) {
                checkWritingAndReadingNumbersAsync(fileSystem, i, availableThreads, testResults);
            }
            checkAsyncTestResults(testResults, testThreadCounter);
        }
    }

    @Test
    public void writesAndReadsNumbersThreadSafely() throws Throwable {
        final Semaphore availableThreads = new Semaphore(10, true);
        final LinkedBlockingQueue<Optional<Throwable>> testResults = new LinkedBlockingQueue<>();
        final int testThreadCounter = 200;
        try (final FileSystem fileSystem = createThreadSafeFilesystem()) {
            for (int i = 0; i < testThreadCounter; i++) {
                checkWritingAndReadingNumbersAsync(fileSystem, i, availableThreads, testResults);
            }
            checkAsyncTestResults(testResults, testThreadCounter);
        }
    }

    private final AtomicBoolean canceled = new AtomicBoolean(false);
    private void checkWritingAndReadingNumbersAsync(final FileSystem fileSystem, final int testThreadCounter, final Semaphore availableThreads,
                                                    final LinkedBlockingQueue<Optional<Throwable>> testResults) throws InterruptedException {
        availableThreads.acquire();
        new Thread("writesAndReadsNumbers-" + testThreadCounter) {
            @Override
            public void run() {
                try {
                    if (canceled.get())
                        return;
                    final String fileName = "file" + testThreadCounter;
                    checkWritingAndReadingNumbers(fileSystem, fileName);
                    fileSystem.root().deleteFile(fileName);
                    testResults.put(Optional.empty());
                } catch (final Throwable e) {
                    canceled.set(true);
                    System.out.println(Thread.currentThread().getName() + ": mismatch" + ":" + e
                            .getMessage());
                    e.printStackTrace(System.out);
                    try {
                        testResults.put(Optional.of(e));
                    } catch (final InterruptedException ie) {
                        throw new RuntimeException(ie);
                    }
                } finally {
                    availableThreads.release();
                }
            }
        }.start();
    }

    private void checkAsyncTestResults(final LinkedBlockingQueue<Optional<Throwable>> testResults,
                                       final int testThreadCounter) throws Throwable {
        for (int i = 0; i < testThreadCounter; i++) {
            final Optional<Throwable> result = testResults.take();
            if (result.isPresent()) {
                throw result.get();
            }
        }
    }

    private void checkWritingAndReadingNumbers(final FileSystem fileSystem, final String fileName) {
        final byte randomByte = (byte) random.nextInt();
        final int randomInt = random.nextInt();
        final long randomLong = random.nextLong();

        final byte[] sourceArray = new byte[20 * 1024];
        final byte[] destinationArray = new byte[20 * 1024];
        random.nextBytes(sourceArray);
        final File file = fileSystem.root().createFile(fileName);

        file.setPosition(10);
        file.write(randomByte);
        file.write(randomInt);
        file.write(randomLong);
        file.write(sourceArray);
        file.write(sourceArray, 33, 80);

        final long expectedPosition = 10L + Byte.BYTES + Integer.BYTES + Long.BYTES
                + (sourceArray.length + 80) * Byte.BYTES;
        assertThat(file.getPosition()).isEqualTo(expectedPosition);
        assertThat(file.size()).isEqualTo(expectedPosition);

        file.setPosition(10);
        assertThat(file.readByte()).isEqualTo(randomByte);
        assertThat(file.readInt()).isEqualTo(randomInt);
        assertThat(file.readLong()).isEqualTo(randomLong);
        file.read(destinationArray);
        assertThat(destinationArray).containsExactly(sourceArray);
        file.read(destinationArray, 33, 80);
        assertThat(destinationArray).containsExactly(sourceArray);
        assertThat(file.getPosition()).isEqualTo(expectedPosition);
        assertThat(file.size()).isEqualTo(expectedPosition);
    }

    @Test
    public void truncatesFile() {
        try (final FileSystem fileSystem = createFilesystem()) {
            final File file = fileSystem.root().createFile("file");
            file.write("content");
            file.deleteContent();

            assertThat(file.size()).isEqualTo(0);
            assertThat(file.getPosition()).isEqualTo(0);
        }
    }

    @Test
    public void recognizesCorruptedFiles() throws IOException {
        try (final FileSystem fileSystem = createFilesystem()) {
            //
        }

        try (final FileChannel channel = FileChannel.open(fsFile.toPath(), StandardOpenOption.WRITE)) {
            channel.position(0);
            channel.write(ByteBuffer.allocate(8));
        }

        assertThatThrownBy(() -> openFilesystem()).isInstanceOf(IllegalArgumentIOException.class)
                .hasMessageContaining("Unexpected file system version");
    }


    @Test
    public void recognizesTooShortFiles() throws IOException {
        try (final FileChannel channel = FileChannel.open(fsFile.toPath(), CREATE_NEW, WRITE)) {
            channel.position(FileSystemFactory.MINIMAL_EXTERNAL_FILE_SIZE - FileSystemFactory.PAGE_SIZE - 2);
            channel.write(ByteBuffer.allocate(1));
        }

        assertThatThrownBy(() -> openFilesystem()).isInstanceOf(IllegalArgumentIOException.class)
                .hasMessage("File is too short");
    }

    @Test
    public void minimalSize() {
        try (final FileSystem fileSystem = FileSystem.create(fsFile, FileSystemFactory.MINIMAL_EXTERNAL_FILE_SIZE)) {
            fileSystem.root().createFile("hello").write("Hello world");
        }
    }

    @Test
    public void lessThanMinimalSize() {
        assertThatThrownBy(() -> FileSystem.create(fsFile, FileSystemFactory.MINIMAL_EXTERNAL_FILE_SIZE - 1)).isInstanceOf(IllegalArgumentIOException.class)
                .hasMessage("File size is too small");
    }
}
