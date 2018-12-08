package org.dpolivaev.katas.filesystem;

/**
 * The exception thrown when required file memory page can not be allocated.
 */
public class OutOfMemoryException extends RuntimeException{
    public OutOfMemoryException(final String message) {
        super(message);
    }
}
