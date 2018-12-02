package org.dpolivaev.katas.filesystem.internal.pool;

public class OutOfMemoryException extends RuntimeException{
    public OutOfMemoryException(final String message) {
        super(message);
    }
}
