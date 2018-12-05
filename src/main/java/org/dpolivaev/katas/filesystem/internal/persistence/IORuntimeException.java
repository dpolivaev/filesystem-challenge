package org.dpolivaev.katas.filesystem.internal.persistence;

import java.io.IOException;

public class IORuntimeException extends RuntimeException {
    public IORuntimeException(final IOException e) {
        super(e);
    }
}
