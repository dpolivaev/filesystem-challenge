package org.dpolivaev.katas.filesystem;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface File extends Element {
    long size();

    void truncate();

    void setPosition(long position);

    void on(long position, Runnable runnable);

    long on(long position, LongSupplier supplier);

    <T> T on(long position, Supplier<T> supplier);

    long getPosition();

    void write(byte source);

    void write(byte[] source, int sourceOffset, int length);

    byte readByte();

    void read(byte[] destination, int destinationOffset, int length);

    void write(long source);

    void write(int source);

    void write(byte[] source);

    void write(String source);

    int readInt();

    long readLong();

    String readString();

    void read(byte[] destination);
}
