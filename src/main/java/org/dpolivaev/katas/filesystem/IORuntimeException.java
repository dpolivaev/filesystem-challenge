package org.dpolivaev.katas.filesystem;

import java.io.IOException;

public class IORuntimeException extends RuntimeException {
    public IORuntimeException(final IOException e) {
        super(e);
    }

    public IORuntimeException() {
    }

    public IORuntimeException(final String message) {
        super(message);
    }
}
