package com.revolut;

import com.revolut.model.Currency;
import com.revolut.model.Transaction;
import com.revolut.model.TransactionStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;

public class TestDataProvider {
    public static final Long FIRST_TEST_ID = 1L;
    public static final Long SECOND_TEST_ID = 2L;
    public static final Long THIRD_TEST_ID = 3L;



    public static List<Transaction> getListOfTransactions(){
        return Arrays.asList(
                new Transaction(
                        FIRST_TEST_ID,
                        SECOND_TEST_ID,
                        BigDecimal.ZERO,
                        Currency.EURO),
                new Transaction(
                        SECOND_TEST_ID,
                        THIRD_TEST_ID,
                        BigDecimal.ZERO,
                        Currency.EURO)
        );
    }

    public static Transaction createNewTransaction(){
        Transaction transaction = new Transaction(
                FIRST_TEST_ID,
                SECOND_TEST_ID,
                BigDecimal.TEN,
                Currency.INR
        );
        transaction.setStatus(TransactionStatus.NEW);
        transaction.setId(5L);
        return transaction;
    }

}
