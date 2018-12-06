package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.Directory;
import org.dpolivaev.katas.filesystem.File;
import org.dpolivaev.katas.filesystem.internal.pages.PageEditor;

import java.util.UUID;

class PagedFile implements File {
    private final FilePage filePage;
    private final Directory parentDirectory;
    private final PageEditor editor;


    PagedFile(final FilePage filePage, final Directory parentDirectory) {
        this.filePage = filePage;
        this.parentDirectory = parentDirectory;
        this.editor = new PageEditor();
        editor.setPage(filePage);
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
    public void truncate() {
        filePage.validateUuid();
        filePage.truncate();
        editor.setPosition(0);
    }

    @Override
    public void setPosition(final long position) {
        filePage.validateUuid();
        editor.setPosition(position);
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

    @Override
    public byte readByte() {
        filePage.validateUuid();
        return editor.readByte();
    }

    @Override
    public void read(final byte[] destination, final int destinationOffset, final int length) {
        filePage.validateUuid();
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
        return editor.readInt();
    }

    @Override
    public long readLong() {
        filePage.validateUuid();
        return editor.readLong();
    }

    @Override
    public String readString() {
        filePage.validateUuid();
        return editor.readString();
    }

    @Override
    public void read(final byte[] destination) {
        filePage.validateUuid();
        editor.read(destination);
    }
}
