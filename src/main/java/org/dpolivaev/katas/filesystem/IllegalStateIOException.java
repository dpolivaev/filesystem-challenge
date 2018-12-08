package org.dpolivaev.katas.filesystem;

/**
 * The exception thrown when already deleted file or directory is accessed.
 */
public class IllegalStateIOException extends IORuntimeException {
    public IllegalStateIOException(final String message) {
        super(message);
    }
}
