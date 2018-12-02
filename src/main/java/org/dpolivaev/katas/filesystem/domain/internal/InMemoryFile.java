package org.dpolivaev.katas.filesystem.domain.internal;

import org.dpolivaev.katas.filesystem.domain.Directory;
import org.dpolivaev.katas.filesystem.domain.File;

class InMemoryFile implements File {
    private final FilePage filePage;
    private final Directory parentDirectory;
    private final PageEditor editor;

    InMemoryFile(final FilePage filePage, final Directory parentDirectory) {
        this.filePage = filePage;
        this.parentDirectory = parentDirectory;
        this.editor = new PageEditor();
        editor.setPage(filePage);
    }

    @Override
    public Directory parentDirectory() {
        return parentDirectory;
    }

    @Override
    public String name() {
        return filePage.name();
    }

    @Override
    public long size() {
        return filePage.fileSize();
    }

    @Override
    public void truncate() {
        filePage.truncate();
    }

    @Override
    public void setPosition(final long position) {
        editor.setPosition(position);
    }

    @Override
    public long getPosition() {
        return editor.getPosition();
    }

    @Override
    public void write(final byte source) {
        editor.write(source);
    }

    @Override
    public void write(final byte[] source, final int sourceOffset, final int length) {
        editor.write(source, sourceOffset, length);
    }

    @Override
    public byte readByte() {
        return editor.readByte();
    }

    @Override
    public void read(final byte[] destination, final int destinationOffset, final int length) {
        editor.read(destination, destinationOffset, length);
    }

    @Override
    public void write(final long source) {
        editor.write(source);
    }

    @Override
    public void write(final int source) {
        editor.write(source);
    }

    @Override
    public void write(final byte[] source) {
        editor.write(source);
    }

    @Override
    public void write(final String source) {
        editor.write(source);
    }

    @Override
    public int readInt() {
        return editor.readInt();
    }

    @Override
    public long readLong() {
        return editor.readLong();
    }

    @Override
    public String readString() {
        return editor.readString();
    }

    @Override
    public void read(final byte[] destination) {
        editor.read(destination);
    }
}
