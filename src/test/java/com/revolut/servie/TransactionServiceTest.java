package com.revolut.servie;

import com.revolut.TestDataProvider;
import com.revolut.dao.TransactionDao;
import com.revolut.model.Currency;
import com.revolut.model.RevolutTransactionException;
import com.revolut.model.Transaction;
import com.revolut.model.TransactionStatus;
import com.revolut.service.TransactionService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {
    private static final TransactionService staticTransactionService = TransactionService.getInstance();

    @Test
    void testAllTransactionsRetrieval(){
        TransactionDao transactionDao = mock(TransactionDao.class);
        TransactionService transactionsService = new TransactionService(transactionDao);
        List<Transaction> transactions = TestDataProvider.getListOfTransactions();

        when(transactionDao.getAllTransactions()).thenReturn(transactions);

        List<Transaction> result = transactionsService.getAllTransactions();

        assertNotNull(transactions);
        assertArrayEquals(transactions.toArray(), result.toArray());
    }

    @Test
    public void testCreateTransactionWithNullFrom(){
        assertThrows(RevolutTransactionException.class,
                () ->staticTransactionService.createTransaction(new Transaction(
                        null, 2L, BigDecimal.TEN, Currency.EURO
                )),
                "Expected doThing() to throw, but it didn't");
    }


    @Test
    void testCreateTransactionWithNullTo()  {
        assertThrows(RevolutTransactionException.class,
                () -> staticTransactionService.createTransaction(new Transaction(
                        1L, null, BigDecimal.TEN, Currency.EURO
                )));
    }


    @Test
    void testCreateTransactionWithSameAccounts() {
        assertThrows(RevolutTransactionException.class,
                () ->staticTransactionService.createTransaction(new Transaction(
                        TestDataProvider.FIRST_TEST_ID,
                        TestDataProvider.SECOND_TEST_ID,
                        BigDecimal.TEN,
                        Currency.INR
                )));

    }

    @Test
    void testCreateTransactionWithZeroAmount(){
        assertThrows(RevolutTransactionException.class,
                () ->staticTransactionService.createTransaction(new Transaction(
                        TestDataProvider.FIRST_TEST_ID,
                        TestDataProvider.SECOND_TEST_ID,
                        BigDecimal.ZERO,
                        Currency.INR
                )));
    }

    @Test
    void testCreateTransaction() throws RevolutTransactionException {
        TransactionDao transactionDto = mock(TransactionDao.class);
    Transaction transaction = TestDataProvider.createNewTransaction();
        when(transactionDto.createTransaction(any())).thenReturn(transaction);

        when(transactionDto.getAllTransactionIdsByStatus(any())).thenReturn(
                Collections.singletonList(transaction.getId())
        );

        doAnswer(invocation -> {
            transaction.setStatus(TransactionStatus.SUCCEED);
            return null;
        }).when(transactionDto).executeTransaction(anyLong());

        TransactionService transactionService = new TransactionService(transactionDto);
        Transaction createdTransaction = transactionService.createTransaction(transaction);

        assertEquals(createdTransaction, transaction);
        assertEquals(createdTransaction.getStatus(), TransactionStatus.NEW);

        transactionService.executeTransactions();

        assertEquals(transaction.getStatus(), TransactionStatus.SUCCEED);
    }

}
