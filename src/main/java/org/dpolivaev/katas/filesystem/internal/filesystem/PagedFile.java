package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.Directory;
import org.dpolivaev.katas.filesystem.EndOfFileException;
import org.dpolivaev.katas.filesystem.File;
import org.dpolivaev.katas.filesystem.internal.pages.PageEditor;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

class PagedFile implements File {
    private final FilePage filePage;
    private final Directory parentDirectory;
    private final PageEditor editor;


    PagedFile(final FilePage filePage, final Directory parentDirectory) {
        this.filePage = filePage;
        this.parentDirectory = parentDirectory;
        this.editor = new PageEditor(filePage);
    }

    @Override
    public Directory parentDirectory() {
        filePage.validateUuid();
        return parentDirectory;
    }

    @Override
    public boolean exists() {
        return filePage.exists();
    }

    @Override
    public String name() {
        filePage.validateUuid();
        return filePage.name();
    }

    @Override
    public UUID uuid() {
        return filePage.uuid();
    }

    @Override
    public long size() {
        filePage.validateUuid();
        return filePage.fileSize();
    }

    @Override
    public void deleteContent() {
        filePage.validateUuid();
        filePage.truncate();
        editor.setPosition(0);
    }

    @Override
    public File setPosition(final long position) {
        filePage.validateUuid();
        editor.setPosition(position);
        return this;
    }

    @Override
    public void at(final long position, final Runnable runnable) {
        editor.at(position, runnable);
    }

    @Override
    public long at(final long position, final LongSupplier supplier) {
        return editor.at(position, supplier);
    }

    @Override
    public <T> T at(final long position, final Supplier<T> supplier) {
        return editor.at(position, supplier);
    }

    @Override
    public long getPosition() {
        filePage.validateUuid();
        return editor.getPosition();
    }

    @Override
    public void write(final byte source) {
        filePage.validateUuid();
        editor.write(source);
    }

    @Override
    public void write(final byte[] source, final int sourceOffset, final int length) {
        filePage.validateUuid();
        editor.write(source, sourceOffset, length);
    }

    private void validateReadSegment(final int length) {
        if (getPosition() + length > filePage.fileSize())
            throw new EndOfFileException();
    }

    @Override
    public byte readByte() {
        filePage.validateUuid();
        validateReadSegment(1);
        return editor.readByte();
    }

    @Override
    public void read(final byte[] destination, final int destinationOffset, final int length) {
        filePage.validateUuid();
        validateReadSegment(length);
        editor.read(destination, destinationOffset, length);
    }

    @Override
    public void write(final long source) {
        filePage.validateUuid();
        editor.write(source);
    }

    @Override
    public void write(final int source) {
        filePage.validateUuid();
        editor.write(source);
    }

    @Override
    public void write(final byte[] source) {
        filePage.validateUuid();
        editor.write(source);
    }

    @Override
    public void write(final String source) {
        filePage.validateUuid();
        editor.write(source);
    }

    @Override
    public int readInt() {
        filePage.validateUuid();
        validateReadSegment(Integer.BYTES);
        return editor.readInt();
    }

    @Override
    public long readLong() {
        filePage.validateUuid();
        validateReadSegment(Long.BYTES);
        return editor.readLong();
    }

    @Override
    public String readString() {
        filePage.validateUuid();
        validateReadSegment(Integer.BYTES);
        final int length = editor.readInt();
        validateReadSegment(length);
        final byte[] buffer = new byte[length];
        editor.read(buffer);
        return new String(buffer, StandardCharsets.UTF_8);
    }

    @Override
    public void read(final byte[] destination) {
        filePage.validateUuid();
        validateReadSegment(destination.length);
        editor.read(destination);
    }


    @Override
    public String toString() {
        final String name = name();
        return "PagedFile{" + (name.isEmpty() ? "<empty name>" : name) + '(' + getPosition() + '/' + size() + ')' + '}';
    }

}
