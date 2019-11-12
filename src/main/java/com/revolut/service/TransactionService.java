package com.revolut.service;

import com.revolut.dao.TransactionDao;
import com.revolut.model.RevolutTransactionException;
import com.revolut.model.Transaction;
import com.revolut.model.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private static TransactionService transactionService;
    private TransactionDao transactionDao;
    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public TransactionService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
        executorService.scheduleAtFixedRate(() ->
                        transactionService.executeTransactions(),
                0, 5, TimeUnit.SECONDS);
        log.info("Transaction Executor planned");
    }

    public static TransactionService getInstance() {
        if(transactionService == null){
            synchronized (TransactionService.class) {
                if(transactionService == null){
                    transactionService = new TransactionService(TransactionDao.getInstance());
                }
            }
        }
        return transactionService;
    }

    public void executeTransactions() {
        log.info("Starting of Transaction executor");
        List<Long> plannedTransactionIds = getAllTransactionIdsByStatus(TransactionStatus.NEW);

        for (Long transactionId : plannedTransactionIds) {
            try {
                transactionDao.executeTransaction(transactionId);
            } catch (RevolutTransactionException e) {
                log.error("Could not execute transaction with id %d", transactionId, e);
            }
        }
        log.info("Transaction executor ended");
    }

    public Transaction createTransaction(Transaction transaction) throws RevolutTransactionException {
        if (transaction.getFromBankAccountId() == null || transaction.getToBankAccountId() == null) {
            throw new RevolutTransactionException("The transaction has not provided from Account or to Account values");
        }
        if (transaction.getFromBankAccountId().equals(transaction.getToBankAccountId())) {
            throw new RevolutTransactionException("The sender and recipient should not be same");
        }
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RevolutTransactionException("The amount should be more than 0");
        }

        return transactionDao.createTransaction(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionDao.getAllTransactions();
    }

    private List<Long> getAllTransactionIdsByStatus(TransactionStatus transactionStatus) {
        return transactionDao.getAllTransactionIdsByStatus(transactionStatus);
    }

    public Transaction getTransactionById(Long id) {
        return transactionDao.getTransactionById(id);
    }
}
