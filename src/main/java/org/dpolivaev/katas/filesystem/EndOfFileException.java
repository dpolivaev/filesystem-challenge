package org.dpolivaev.katas.filesystem;

/**
 * The exception thrown on attempt to read after the logical end of file.
 */
public class EndOfFileException extends IORuntimeException {
    public EndOfFileException() {
        super();
    }
}
