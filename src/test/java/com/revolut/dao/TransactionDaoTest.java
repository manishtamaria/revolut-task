package com.revolut.dao;

import com.revolut.TestDataProvider;
import com.revolut.database.DbUtils;
import com.revolut.model.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionDaoTest {
    private TransactionDao transactionDao;
    List<Transaction> transactions;

    @BeforeEach
    void initTestData() {
        DbUtils dbUtils = mock(DbUtils.class);
        transactionDao = new TransactionDao(dbUtils);
        transactions = TestDataProvider.getListOfTransactions();

        when(dbUtils.executeQuery(eq(DaoConstant.GET_ALL_TRANSACTIONS_SQL), any())).thenReturn(new DbUtils.QueryResult<>(transactions));

        when(dbUtils.executeQuery(eq(DaoConstant.GET_TRANSACTIONS_BY_STATUS_QUERY), any())).thenReturn(
                new DbUtils.QueryResult<>(transactions.stream().map(Transaction::getId).collect(Collectors.toList()))
        );

        when(dbUtils.executeQueryInConnection(any(), eq(DaoConstant.GET_TRANSACTIONS_FOR_UPDATE_BY_ID_QUERY), any()))
                .thenReturn(new DbUtils.QueryResult<>(transactions));
    }

    @Test
    void testGetAllTransactions() {
        Collection<Transaction> resultList = transactionDao.getAllTransactions();

        assertNotNull(resultList);
        assertEquals(transactions, resultList);
    }


    @Test
    void testGetAllTransactionIdsByStatus() {
        List<Long> resultTransactionIds = transactionDao.getAllTransactionIdsByStatus(TransactionStatus.NEW);

        assertNotNull(resultTransactionIds);
        assertEquals(resultTransactionIds.size(), 2);
    }

    @Test
    void testTransactionCreation() throws RevolutTransactionException {
        TransactionDao transactionDto = TransactionDao.getInstance();
        AccountDao accountDao = AccountDao.getInstance();

        Account firstAccount = accountDao.getAccountById(1L);
        Account secondAccount = accountDao.getAccountById(2L);

        BigDecimal firstAccountInitialBalance = firstAccount.getBalance();
        BigDecimal secondAccountInitialBalance = secondAccount.getBalance();

        Transaction resultTransaction = transactionDto.createTransaction(TestDataProvider.createNewTransaction());

        assertEquals(resultTransaction.getStatus(), TransactionStatus.NEW);

        firstAccount = accountDao.getAccountById(1L);
        secondAccount = accountDao.getAccountById(2L);

        assertThat(firstAccountInitialBalance, Matchers.comparesEqualTo(firstAccount.getBalance()));

        assertThat(secondAccountInitialBalance, Matchers.comparesEqualTo(secondAccount.getBalance()));
    }

    @Test
    void testTransactionExecutionSuccess() throws RevolutTransactionException {
        TransactionDao transactionDto = TransactionDao.getInstance();
        AccountDao accountDao = AccountDao.getInstance();

        Transaction secondNewTransaction = new Transaction(
                1L,
                2L,
                BigDecimal.TEN,
                Currency.EURO);
        secondNewTransaction.setId(2L);

        Account firstAccount = accountDao.getAccountById(1L);
        Account secondAccount = accountDao.getAccountById(2L);

        BigDecimal firstAccountInitialBalance = firstAccount.getBalance();
        BigDecimal secondAccountInitialBalance = secondAccount.getBalance();

        Transaction resultTransaction = transactionDto.createTransaction(TestDataProvider.createNewTransaction());
        transactionDto.executeTransaction(resultTransaction.getId());

        resultTransaction = transactionDto.getTransactionById(resultTransaction.getId());
        firstAccount = accountDao.getAccountById(secondNewTransaction.getFromBankAccountId());
        secondAccount = accountDao.getAccountById(secondNewTransaction.getToBankAccountId());


        assertEquals(resultTransaction.getStatus(), TransactionStatus.SUCCEED);
        assertThat(firstAccountInitialBalance.subtract(secondNewTransaction.getAmount()), Matchers.comparesEqualTo(firstAccount.getBalance()));


        assertThat(secondAccountInitialBalance.add(secondNewTransaction.getAmount()), Matchers.comparesEqualTo(secondAccount.getBalance()));

    }

}
