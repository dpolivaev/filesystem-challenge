package org.dpolivaev.katas.filesystem;

/**
 * The exception thrown on attempt of creation file or directory
 * with a name already used in the current directory.
 */
public class FileAlreadyExistsException extends IORuntimeException {
    public FileAlreadyExistsException(final String message) {
        super(message);
    }
}
