package org.dpolivaev.katas.filesystem;

public class OutOfMemoryException extends RuntimeException{
    public OutOfMemoryException(final String message) {
        super(message);
    }
}
