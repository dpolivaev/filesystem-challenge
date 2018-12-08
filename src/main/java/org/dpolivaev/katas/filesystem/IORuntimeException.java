package org.dpolivaev.katas.filesystem;

import java.io.IOException;

/**
 * Base exception class for all exceptions which could be caused by the client.
 */
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
