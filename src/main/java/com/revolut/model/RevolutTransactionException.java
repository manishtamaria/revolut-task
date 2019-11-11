package com.revolut.model;

import com.sun.xml.internal.ws.api.model.ExceptionType;

public class RevolutTransactionException extends Exception {
    public RevolutTransactionException(String errorMessage) {
        super(errorMessage);
    }

    public RevolutTransactionException(Throwable cause) {
        super(cause);
    }
}
