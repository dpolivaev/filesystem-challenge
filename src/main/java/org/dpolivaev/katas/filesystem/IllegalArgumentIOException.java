package org.dpolivaev.katas.filesystem;

/**
 * The exception thrown if bad parameter is recognized.
 */
public class IllegalArgumentIOException extends IORuntimeException {
    public IllegalArgumentIOException(final String message) {
        super(message);
    }
}
