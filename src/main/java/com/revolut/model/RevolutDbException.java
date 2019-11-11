package com.revolut.model;

public class RevolutDbException extends RuntimeException {
    public RevolutDbException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public RevolutDbException(Throwable cause) {
        super(cause);
    }
}
