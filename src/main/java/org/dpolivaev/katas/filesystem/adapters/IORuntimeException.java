package org.dpolivaev.katas.filesystem.adapters;

import java.io.IOException;

public class IORuntimeException extends RuntimeException {
    public IORuntimeException(final IOException e) {
    }
}
