package com.revolut.model;

public class RevolutTransactionException extends Exception {
    public RevolutTransactionException(String errorMessage) {
        super(errorMessage);
    }

    public RevolutTransactionException(Throwable cause) {
        super(cause);
    }
}
