package org.kie.utils;

/**
 * RuntimeException to be thrown from java Functional interface to avoid try/catch cluttering.
 */
public class MaskedMavenMojoException extends RuntimeException{
    public MaskedMavenMojoException() {
        super();
    }

    public MaskedMavenMojoException(String message) {
        super(message);
    }

    public MaskedMavenMojoException(String message, Throwable cause) {
        super(message, cause);
    }

    public MaskedMavenMojoException(Throwable cause) {
        super(cause);
    }

    public MaskedMavenMojoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
