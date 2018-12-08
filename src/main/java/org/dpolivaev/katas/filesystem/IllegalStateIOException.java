package org.dpolivaev.katas.filesystem;

public class IllegalStateIOException extends IORuntimeException {
    public IllegalStateIOException(final String message) {
        super(message);
    }
}
