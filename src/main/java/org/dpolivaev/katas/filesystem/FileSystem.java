package org.dpolivaev.katas.filesystem;

import org.dpolivaev.katas.filesystem.internal.persistence.FileSystemFactory;

import java.io.Closeable;
import java.io.File;

/**
 * File system allows creation of files and directory hierarchy inside of its root directory.
 * It provides access to root {@link Directory} and contains static factory methods
 * linking file system objects with OS files.
 * <p>
 * File system instances can be thread safe.
 * They support lazy memory allocation for files and directories.
 */
public interface FileSystem extends Closeable {

    /**
     * Minimal size required for file system creation.
     */
    int MINIMAL_EXTERNAL_FILE_SIZE = FileSystemFactory.MINIMAL_EXTERNAL_FILE_SIZE;

    /**
     * Creates FileSystem instance linked to a new file.
     * <p>
     * The instance is not thread safe.
     *
     * @param file OS file to contain the file system
     * @param size the maximum allowed file size in bytes
     * @return FileSystem instance
     * @throws IORuntimeException         if the file can not be creared
     * @throws IllegalArgumentIOException if the given size is less than {@link FileSystem#MINIMAL_EXTERNAL_FILE_SIZE}
     */
    static FileSystem create(final File file, final long size) throws IORuntimeException, IllegalArgumentIOException {
        return FileSystemFactory.INSTANCE.create(file, size);
    }

    /**
     * Creates FileSystem instance linked to a new file.
     * <p>
     * The instance is not thread safe.
     *
     * @param filePath OS file path to file containing the file system
     * @param size     the maximum allowed file size in bytes
     * @return FileSystem instance
     * @throws IORuntimeException         if the file can not be creared
     * @throws IllegalArgumentIOException if the given size is less than {@link FileSystem#MINIMAL_EXTERNAL_FILE_SIZE}
     */
    static FileSystem create(final String filePath, final long size) throws IORuntimeException, IllegalArgumentIOException {
        return FileSystemFactory.INSTANCE.create(filePath, size);
    }


    /**
     * Creates FileSystem instance linked to existing OS file.
     * <p>
     * The instance is not thread safe.
     * Multiple instances linked to the same file are allowed.
     *
     * @param file OS file containing the file system
     * @return FileSystem instance
     * @throws IORuntimeException         if the file can not be creared
     * @throws IllegalArgumentIOException if file does not seem to be valid
     */
    static FileSystem open(final File file) throws IORuntimeException, IllegalArgumentIOException {
        return FileSystemFactory.INSTANCE.open(file);
    }

    /**
     * Creates FileSystem instance linked to existing OS file.
     * <p>
     * The instance is not thread safe.
     * Multiple instances linked to the same file are allowed.
     *
     * @param filePath OS file path to file containing the file system
     * @return FileSystem instance
     * @throws IORuntimeException         if the file can not be creared
     * @throws IllegalArgumentIOException if file does not seem to be valid
     */
    static FileSystem open(final String filePath) throws IORuntimeException, IllegalArgumentIOException {
        return FileSystemFactory.INSTANCE.open(filePath);
    }

    /**
     * Creates thread safe FileSystem instance linked to a new file.
     *
     * @param file OS file to contain the file system
     * @param size the maximum allowed file size in bytes
     * @return FileSystem instance
     * @throws IORuntimeException         if the file can not be creared
     * @throws IllegalArgumentIOException if the given size is less than {@link FileSystem#MINIMAL_EXTERNAL_FILE_SIZE}
     */
    static FileSystem createThreadSafe(final File file, final long size) throws IORuntimeException, IllegalArgumentIOException {
        return FileSystemFactory.INSTANCE.createThreadSafe(file, size);
    }

    /**
     * Creates thread safe FileSystem instance linked to a new file.
     *
     * @param filePath OS file path to file containing the file system
     * @param size     the maximum allowed file size in bytes
     * @return FileSystem instance
     * @throws IORuntimeException         if the file can not be creared
     * @throws IllegalArgumentIOException if the given size is less than {@link FileSystem#MINIMAL_EXTERNAL_FILE_SIZE}
     */
    static FileSystem createThreadSafe(final String filePath, final long size) throws IORuntimeException, IllegalArgumentIOException {
        return FileSystemFactory.INSTANCE.createThreadSafe(filePath, size);
    }

    /**
     * Creates thread safe FileSystem instance linked to existing OS file.
     * Multiple instances linked to the same file are allowed.
     *
     * @param file OS file containing the file system
     * @return FileSystem instance
     * @throws IORuntimeException         if the file can not be creared
     * @throws IllegalArgumentIOException if file does not seem to be valid
     */
    static FileSystem openThreadSafe(final File file) throws IORuntimeException, IllegalArgumentIOException {
        return FileSystemFactory.INSTANCE.openThreadSafe(file);
    }

    /**
     * Creates thread safe FileSystem instance linked to existing OS file.
     * Multiple instances linked to the same file are allowed.
     *
     * @param filePath OS file path to file containing the file system
     * @return FileSystem instance
     * @throws IORuntimeException         if the file can not be creared
     * @throws IllegalArgumentIOException if file does not seem to be valid
     */
    static FileSystem openThreadSafe(final String filePath) throws IORuntimeException, IllegalArgumentIOException {
        return FileSystemFactory.INSTANCE.openThreadSafe(filePath);
    }

    /**
     * File system root directory.
     * @return the root directory
     */
    Directory root();

    /**
     * Maximum size of a single internal file system file, currently about 256 GB.
     * @return the size
     */
    long maximumSupportedFileSize();

    /**
     * Closes the OS file.
     * <p>
     * This method actually does nothing in the current implementation using {@link java.nio.MappedByteBuffer}
     */
    @Override
    default void close() {
    }

}
