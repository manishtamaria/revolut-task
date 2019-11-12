package com.revolut.model;

public enum Currency {
    EURO(1),DOLLAR(2),INR(3),PLN(4);
    private int id;

    Currency(int id) {
        this.id = id;
    }

    public static Currency valueOf(int id) {
        for(Currency e : values()) {
            if(e.id == id) return e;
        }

        return null;
    }

    public int getId() {
        return id;
    }
}
