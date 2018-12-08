package org.dpolivaev.katas.filesystem;

public class IllegalArgumentIOException extends IORuntimeException {
    public IllegalArgumentIOException(final String message) {
        super(message);
    }
}
