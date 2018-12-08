package org.dpolivaev.katas.filesystem;

import java.util.UUID;

/**
 * Basic interface representing elements contained in {@link FileSystem} element hierarchy.
 * <p>
 * All methods except for {@link Element#uuid()} and {@link Element#exists()} throw {@link IllegalStateIOException}
 * if the elements have been deleted from the file system hierarchy.
 */
public interface Element {
    /**
     * @return automatically assigned unique element identifier
     */
    UUID uuid();

    /**
     * @return parent directory element or itself for the root element
     */
    Directory parentDirectory();

    /**
     * @return name specified by user during element creation
     */
    String name();

    /**
     * @return true if and only if the element has not been deleted yet.
     */
    boolean exists();
}
