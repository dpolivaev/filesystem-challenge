package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pages.PageEditor;
import org.dpolivaev.katas.filesystem.internal.pages.Pair;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;

import java.util.UUID;

import static org.dpolivaev.katas.filesystem.internal.filesystem.FileDescriptorStructure.*;

class FilePage implements Page {

    private final UUID uuid;
    private final Page startPage;
    private final Page dataDescriptor;
    private final PageEditor editor;
    private final HugePage data;

    FilePage(final PagePool pagePool, final Page startPage) {
        this.startPage = startPage;
        final Pair<Page, Page> pagePair = startPage.split(DATA_POSITION);
        this.dataDescriptor = pagePair.first;
        this.editor = new PageEditor();
        UUID uuid = readUUID();
        if (uuid.getMostSignificantBits() == 0 && uuid.getLeastSignificantBits() == 0) {
            uuid = UUID.randomUUID();
            editor.setPage(dataDescriptor);
            editor.setPosition(UUID_POSITION);
            editor.write(uuid);
        }
        this.uuid = uuid;
        this.data = new HugePage(pagePool, editor, startPage);
    }

    private UUID readUUID() {
        return editor.on(dataDescriptor, UUID_POSITION, editor::readUUID);
    }

    void validateUuid() {
        if (!exists())
            throw new IllegalStateException("File UUID changed");
    }

    boolean exists() {
        return uuid.equals(readUUID());
    }


    public void destroy() {
        truncate();
        startPage.erase(0, DATA_POSITION);
    }

    public void truncate() {
        data.destroy();
        setFileSize(0);
    }
    @Override
    public long size() {
        return data.size();
    }


    private PageEditor descriptor(final int position) {
        editor.setPage(dataDescriptor);
        editor.setPosition(position);
        return editor;
    }

    public long fileSize() {
        return descriptor(SIZE_POSITION).readLong();
    }

    private void setFileSize(final long size) {
        descriptor(SIZE_POSITION).write(size);
    }

    String name() {
        return descriptor(NAME_POSITION).readString();
    }

    void setName(final String name) {
        descriptor(NAME_POSITION).write(name);
    }

    @Override
    public void write(final long offset, final byte source) {
        increaseSize(offset + 1);
        data.write(offset, source);
    }

    private void increaseSize(final long requiredSize) {
        if (fileSize() < requiredSize) {
            setFileSize(requiredSize);
        }
    }

    @Override
    public void write(final long offset, final int length, final byte[] source, final int sourceOffset) {
        increaseSize(offset + length);
        data.write(offset, length, source, sourceOffset);

    }

    @Override
    public byte readByte(final long offset) {
        if (offset >= size()) {
            throw new EndOfFileException();
        }
        return data.readByte(offset);
    }

    @Override
    public void read(final long offset, final int length, final byte[] destination, final int destinationOffset) {
        if (offset > size() + length) {
            throw new EndOfFileException();
        }
        data.read(offset, length, destination, destinationOffset);
    }

    @Override
    public void erase(final long offset, final long length) {
        data.erase(offset, length);
    }

    public UUID uuid() {
        return uuid;
    }
}
