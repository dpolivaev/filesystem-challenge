package org.dpolivaev.katas.filesystem;

public class FileAlreadyExistsException extends IORuntimeException {
    public FileAlreadyExistsException(String message) {
        super(message);
    }
}
