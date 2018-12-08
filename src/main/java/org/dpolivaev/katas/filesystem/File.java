package org.dpolivaev.katas.filesystem;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * Interface representing files contained in {@link FileSystem}.
 *
 *
 * <p>
 * All methods except for {@link Element#uuid()} and {@link Element#exists()} throw {@link IllegalStateIOException}
 * if the elements have been deleted from the file system hierarchy.
 * <p>
 * All read operations can throw {@link EndOfFileException} if the file
 * does not contain the required data at current position
 * <p>
 * All write operations can throw {@link OutOfMemoryException} if all memory pages are already allocated.
 * All write operations can throw {@link OutOfMemoryException} if the file would exceed maximum single file side.
 */
public interface File extends Element {
    /**
     * @return current file size in bytes.
     */
    long size();

    /**
     * Sets file size to 0 and frees all additionally allocated memory pages.
     */
    void deleteContent();

    /**
     * Sets position of cursor used for write and read operations.
     *
     * @param position the new position
     * @return this instance
     * @throws IllegalArgumentIOException if position is negative.
     */
    File setPosition(long position) throws IllegalArgumentIOException;

    /**
     * Executes given runnable at given cursor position.
     * <p>
     * Restores the previous cursor position afterwards.
     *
     * @param position the new position
     * @throws IllegalArgumentIOException if position is negative.
     */
    void at(long position, Runnable runnable) throws IllegalArgumentIOException;

    /**
     * Executes given supplier at given cursor position.
     * <p>
     * Restores the original cursor position afterwards.
     *
     * @param position the new position
     * @return supplier provided value.
     * @throws IllegalArgumentIOException if position is negative.
     */
    long at(long position, LongSupplier supplier) throws IllegalArgumentIOException;

    /**
     * Executes given supplier at given cursor position.
     * <p>
     * Restores the original cursor position afterwards.
     *
     * @param position the new position
     * @return supplier provided value.
     * @throws IllegalArgumentIOException if position is negative.
     */
    <T> T at(long position, Supplier<T> supplier) throws IllegalArgumentIOException;

    /**
     * @return current cursor position
     */
    long getPosition();

    /**
     * Writes one byte at current position and moves the cursor forward.
     */
    void write(byte value);

    /**
     * Writes long value at current position and moves the cursor forward.
     *
     * @param value the written value
     */
    void write(long value);

    /**
     * Writes integer value at current position and moves the cursor forward.
     *
     * @param value the written value
     */
    void write(int value);

    /**
     * Writes String value at current position and moves the cursor forward.
     * <p>
     * The string is saved as integer length value followed by UTF-8 encoded bytes.
     *
     * @param value the written value
     */
    void write(String value);

    /**
     * Writes all input bytes at current position and moves the cursor forward.
     *
     * @param source input byte array
     */
    void write(byte[] source);

    /**
     * Writes bytes at current position and moves the cursor forward.
     *
     * @param source       input byte array
     * @param sourceOffset first written byte offset
     * @param length       amount of written bytes
     */
    void write(byte[] source, int sourceOffset, int length);

    /**
     * Reads one byte at current position and moves the cursor forward.
     *
     * @return the read value
     */
    byte readByte();

    /**
     * Reads integer value at current position and moves the cursor forward.
     *
     * @return the read value
     */
    int readInt();

    /**
     * Reads long value at current position and moves the cursor forward.
     *
     * @return the read value
     */
    long readLong();

    /**
     * Reads string value at current position and moves the cursor forward.
     *
     * @return the read value
     */
    String readString();

    /**
     * Copies bytes from file into the destination and moves the cursor forward.
     * <p>
     * The amount of copied bytes is given by the length of the parameter array
     *
     * @param destination destination byte array
     */
    void read(byte[] destination);

    /**
     * Copies bytes from file into the destination and moves the cursor forward.
     *
     * @param destination       destination byte array
     * @param destinationOffset first updated byte offset
     * @param length            amount of read bytes
     */
    void read(byte[] destination, int destinationOffset, int length);
}
