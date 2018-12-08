package org.dpolivaev.katas.filesystem;


public class TestApp {
    public static void main(final String... args) {
        final String fileName = args.length > 0 ? args[0] : "hello.fs";
        final java.io.File file = new java.io.File(fileName);
        if (!file.exists())
            createFileSystem(file.getPath());
        else
            useFileSystem(file.getPath());
    }

    private static void createFileSystem(final String path) {
        try (final FileSystem fileSystem = FileSystem.create(path, 3072)) {
            fileSystem.root().createFile("hello").write("Hello world");
        }
        System.out.println("Filesystem created in file " + path);
    }

    private static void useFileSystem(final String path) {
        try (final FileSystem fileSystem = FileSystem.open(path)) {
            System.out.println(fileSystem.root().file("hello").get().readString());
        }
    }

}
