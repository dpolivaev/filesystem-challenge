package org.dpolivaev.katas.filesystem;

import java.util.List;
import java.util.Optional;

/**
 * Interface representing directories contained in {@link FileSystem}.
 *
 * <p>
 * All methods except for {@link Element#uuid()} and {@link Element#exists()} throw {@link IllegalStateIOException}
 * if the elements have been deleted from the file system hierarchy.
 */
public interface Directory extends Element {
    /**
     * Finds file by name.
     *
     * @param name file name, it should not be null or empty
     * @return some file if the file with given name exists in this directory or empty optional
     * @throws IllegalArgumentIOException if the given name is null or empty string
     */
    Optional<File> file(String name) throws IllegalArgumentIOException;

    /**
     * Creates new file with given name.
     *
     * @param name file name, it should not be null or empty
     * @return the created file
     * @throws IllegalArgumentIOException if the given name is null or empty string
     * @throws FileAlreadyExistsException if the directory already contains a file with the given name
     */
    File createFile(String name) throws IllegalArgumentIOException, FileAlreadyExistsException;

    /**
     * Deletes a file with given name.
     * <p>
     * The memory allocated for the file is released and becomes available.
     * <p>
     * If the file with given name does not exists, nothing happens.
     *
     * @param name file name, it should not be null or empty
     * @throws IllegalArgumentIOException if the given name is null or empty string
     */
    void deleteFile(String name) throws IllegalArgumentIOException;

    /**
     * Finds directory by name.
     *
     * @param name directory name, it should not be null or empty
     * @return some directory if the child directory with given name exists in this directory
     * or empty optional
     * @throws IllegalArgumentIOException if the given name is null or empty string
     */
    Optional<Directory> directory(String name) throws IllegalArgumentIOException;

    /**
     * Creates new directory with given name.
     *
     * @param name directory name, it should not be null or empty
     * @return the created directory
     * @throws IllegalArgumentIOException if the given name is null or empty string
     * @throws FileAlreadyExistsException if the directory already contains
     *                                    a child directory with the given name
     */
    Directory createDirectory(String name) throws IllegalArgumentIOException, FileAlreadyExistsException;

    /**
     * Deletes a child directory with given name recursively.
     * <p>
     * The memory allocated for the directory and all its child elements is released and becomes available.
     * <p>
     * If the directory with given name does not exists, nothing happens.
     *
     * @param name directory name, it should not be null or empty
     * @throws IllegalArgumentIOException if the given name is null or empty string
     */
    void deleteDirectory(String name) throws IllegalArgumentIOException;

    /**
     * @return list of names of all files contained in this directory
     */
    List<String> files();

    /**
     * @return list of names of all child directories contained in this directory
     */
    List<String> directories();
}
