package org.dpolivaev.katas.filesystem.domain.internal.memory;

public class OutOfMemoryException extends RuntimeException{
    public OutOfMemoryException(String message) {
        super(message);
    }
}
