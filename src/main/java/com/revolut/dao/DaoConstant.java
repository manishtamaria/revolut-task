package com.revolut.dao;

public class DaoConstant {
    public static final String TRANSACTION_TABLE_NAME = "transaction";
    public static final String TRANSACTION_ID_ROW = "id";
    public static final String TRANSACTION_FROM_ACCOUNT_ROW = "from_account_id";
    public static final String TRANSACTION_TO_ACCOUNT_ROW = "to_account_id";
    public static final String TRANSACTION_AMOUNT_ROW = "amount";
    public static final String TRANSACTION_CURRENCY_ROW = "currency_id";
    public static final String TRANSACTION_CREATION_DATE_ROW = "creation_date";
    public static final String TRANSACTION_UPDATE_DATE_ROW = "update_date";
    public static final String TRANSACTION_STATUS_ROW = "status_id";

    public static final String ACCOUNT_TABLE_NAME = "account";
    public static final String BANK_ACCOUNT_ID_ROW = "id";
    public static final String BANK_ACCOUNT_OWNER_NAME_ROW = "owner_name";
    public static final String BANK_ACCOUNT_BALANCE_ROW = "balance";
    public static final String BANK_ACCOUNT_CURRENCY_ID_ROW = "currency_id";

    public static final String GET_ALL_TRANSACTIONS_SQL = "select * from " + TRANSACTION_TABLE_NAME;
    public static final String GET_TRANSACTIONS_BY_STATUS_QUERY =
            "select id from " + TRANSACTION_TABLE_NAME + " trans " +
                    "where trans." + TRANSACTION_STATUS_ROW + " = ?";
    public static final String GET_TRANSACTIONS_BY_ID_QUERY =
            "select * from " + TRANSACTION_TABLE_NAME + " trans " +
                    "where trans." + TRANSACTION_ID_ROW + " = ?";
    public static final String GET_TRANSACTIONS_FOR_UPDATE_BY_ID_QUERY =
            GET_TRANSACTIONS_BY_ID_QUERY + " for update";

    public static final String INSERT_TRANSACTION_QUERY =
            "insert into " + TRANSACTION_TABLE_NAME +
                    " (" +
                    TRANSACTION_FROM_ACCOUNT_ROW + ", " +
                    TRANSACTION_TO_ACCOUNT_ROW + ", " +
                    TRANSACTION_AMOUNT_ROW + ", " +
                    TRANSACTION_CURRENCY_ROW + ", " +
                    TRANSACTION_STATUS_ROW + ", " +
                    TRANSACTION_CREATION_DATE_ROW + ", " +
                    TRANSACTION_UPDATE_DATE_ROW +
                    ") values (?, ?, ?, ?, ?, ?, ?)";

    public static final String UPDATE_TRANSACTION_QUERY =
            "update " + TRANSACTION_TABLE_NAME +
                    " set " +
                    TRANSACTION_STATUS_ROW + " = ?, " +
                    TRANSACTION_UPDATE_DATE_ROW + " = ? " +
                    "where " + TRANSACTION_ID_ROW + " = ?";
    public static final String INSERT_BANK_ACCOUNT_SQL =
            "insert into " + ACCOUNT_TABLE_NAME +
                    " (" +
                    BANK_ACCOUNT_OWNER_NAME_ROW + ", " +
                    BANK_ACCOUNT_BALANCE_ROW + ", " +
                    BANK_ACCOUNT_CURRENCY_ID_ROW +
                    ") values (?, ?, ?, ?)";

    public static final String UPDATE_BANK_ACCOUNT_SQL =
            "update " + ACCOUNT_TABLE_NAME +
                    " set " +
                    BANK_ACCOUNT_OWNER_NAME_ROW + " = ?, " +
                    BANK_ACCOUNT_BALANCE_ROW + " = ?, " +
                    BANK_ACCOUNT_CURRENCY_ID_ROW + " = ? " +
                    "where " + BANK_ACCOUNT_ID_ROW + " = ?";
    public static final String GET_BANK_ACCOUNT_BY_ID_FOR_UPDATE =
            "select * from " + ACCOUNT_TABLE_NAME + " ba " +
                    "where ba." + BANK_ACCOUNT_ID_ROW + " = ? " +
                    "for update";

    public static final String GET_BANK_ACCOUNT_BY_ID_SQL =
            "select * from " + ACCOUNT_TABLE_NAME + " ba " +
                    "where ba." + BANK_ACCOUNT_ID_ROW + " = ?";
}
