package com.revolut.model;

import java.util.Arrays;

public enum TransactionStatus {
    NEW(1), PROCESSING(2), FAILED(3), SUCCEED(4);
    private int value;

    TransactionStatus(int value) {
        this.value = value;
    }

    public static TransactionStatus valueOf(int value) {
        return Arrays.stream(values()).filter(e -> e.value == value).findFirst().orElse(null);

    }

    public int getValue() {
        return value;
    }
}
