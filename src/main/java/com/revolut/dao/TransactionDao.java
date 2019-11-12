package com.revolut.dao;

import com.revolut.database.DbUtils;
import com.revolut.database.InMemDataSource;
import com.revolut.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.revolut.dao.DaoConstant.*;


public class TransactionDao {
    private static final Logger log = LoggerFactory.getLogger(TransactionDao.class);
    private static TransactionDao transactionDto;
    private AccountDao bankAccountDao = AccountDao.getInstance();
    private DbUtils dbUtils = DbUtils.getInstance();

    private TransactionDao() {

    }

    TransactionDao(DbUtils dbUtils) {
        this.dbUtils = dbUtils;
    }

    public static TransactionDao getInstance() {
        if (transactionDto == null) {
            synchronized (TransactionDao.class) {
                if (transactionDto == null) {
                    transactionDto = new TransactionDao();
                }
            }
        }
        return transactionDto;
    }

    public List<Transaction> getAllTransactions() {
        return getAllTransactionsRowFromDb();
    }

    public List<Long> getAllTransactionIdsByStatus(TransactionStatus transactionStatus) {
        if (transactionStatus == null) {
            return null;
        }
        return getAllTransactionsRowsFromDbByStatus(transactionStatus);
    }

    public Transaction getTransactionById(Long id) {
        return dbUtils.executeQuery(GET_TRANSACTIONS_BY_ID_QUERY, getTransactionById -> {
            getTransactionById.setLong(1, id);
            try (ResultSet transactionRS = getTransactionById.executeQuery()) {
                if (transactionRS != null && transactionRS.first()) {
                    return extractTransactionFromResultSet(transactionRS);
                }
            }
            return null;
        }).getResult();
    }

    public Transaction createTransaction(Transaction transaction) throws RevolutTransactionException {
        verify(transaction);
        Connection con = InMemDataSource.getConnection();
        try {
            transaction = executeTransaction(transaction, con);
            if (transaction == null) {
                throw new RevolutTransactionException("Couldn't able to create entity");
            }
            con.commit();
        } catch (RuntimeException | SQLException e) {
            DbUtils.safeRollback(con);
            log.error("Unexpected exception", e);
            throw new RevolutTransactionException(e);
        } finally {
            DbUtils.quietlyClose(con);
        }
        return transaction;
    }

    public void executeTransaction(Long id) throws RevolutTransactionException {
        if (id == null) {
            throw new RevolutTransactionException("The specified transaction doesn't exists");
        }
        Connection con = InMemDataSource.getConnection();
        Transaction transaction = null;
        try {
            transaction = getTransaction(id, con);
            con.commit();
        } catch (RuntimeException | SQLException e) {
            transactionRollBack(con, transaction, e);
        } finally {
            DbUtils.quietlyClose(con);
        }
    }

    private List<Transaction> getAllTransactionsRowFromDb() {
        return dbUtils.executeQuery(GET_ALL_TRANSACTIONS_SQL, getAllTransactions -> {
            List<Transaction> transactions = new ArrayList<>();
            try (ResultSet transactionsRS = getAllTransactions.executeQuery()) {
                if (transactionsRS != null) {
                    while (transactionsRS.next()) {
                        transactions.add(extractTransactionFromResultSet(transactionsRS));
                    }
                }
            }
            return transactions;
        }).getResult();
    }

    private List<Long> getAllTransactionsRowsFromDbByStatus(TransactionStatus transactionStatus) {
        return dbUtils.executeQuery(GET_TRANSACTIONS_BY_STATUS_QUERY, getTransactionsByStatus -> {
            List<Long> transactionIds = new ArrayList<>();
            getTransactionsByStatus.setLong(1, transactionStatus.getValue());
            try (ResultSet transactionsRS = getTransactionsByStatus.executeQuery()) {
                if (transactionsRS != null) {
                    while (transactionsRS.next()) {
                        transactionIds.add(transactionsRS.getLong(TRANSACTION_ID_ROW));
                    }
                }
            }
            return transactionIds;
        }).getResult();
    }

    private Transaction executeTransaction(Transaction transaction, Connection con) throws RevolutTransactionException {
        transaction = dbUtils.executeQueryInConnection(con, INSERT_TRANSACTION_QUERY,
                new DbUtils.CreationQueryExecutor<>(transaction, TransactionDao::fillInPreparedStatement)).getResult();
        return transaction;
    }


    private void transactionRollBack(Connection con, Transaction transaction, Exception e) throws RevolutTransactionException {
        DbUtils.safeRollback(con);
        if (transaction != null) {
            transaction.setStatus(TransactionStatus.FAILED);
            updateTransaction(transaction);
        }
        log.error("Unexpected exception", e);
        throw new RevolutTransactionException(e);
    }

    private Transaction getTransaction(Long id, Connection con) throws RevolutTransactionException {
        Transaction transaction;
        transaction = getForUpdateTransactionById(id, con);

        if (transaction.getStatus() != TransactionStatus.NEW) {
            throw new RevolutTransactionException("Could not execute transaction which is not in NEW status");
        }
        Account fromBankAccount = bankAccountDao.getForUpdateAccountById(con, transaction.getFromBankAccountId());
        Account toBankAccount = bankAccountDao.getForUpdateAccountById(con, transaction.getToBankAccountId());
        if(fromBankAccount.getBalance().compareTo(transaction.getAmount()) > 0){
            BigDecimal newBalance = fromBankAccount.getBalance().subtract(transaction.getAmount());
            fromBankAccount.setBalance(newBalance);
            bankAccountDao.updateAccount(fromBankAccount, con);
            toBankAccount.setBalance(toBankAccount.getBalance().add(transaction.getAmount()));
            bankAccountDao.updateAccount(toBankAccount, con);
            transaction.setStatus(TransactionStatus.SUCCEED);
        }
        else{
            transaction.setStatus(TransactionStatus.FAILED);
        }
        updateTransaction(transaction, con);
        return transaction;
    }

    private Transaction getForUpdateTransactionById(Long id, Connection con) {
        return dbUtils.executeQueryInConnection(con, GET_TRANSACTIONS_FOR_UPDATE_BY_ID_QUERY, getTransaction -> {
            getTransaction.setLong(1, id);
            try (ResultSet transactionRS = getTransaction.executeQuery()) {
                if (transactionRS != null && transactionRS.first()) {
                    return extractTransactionFromResultSet(transactionRS);
                }
            }
            return null;
        }).getResult();
    }

    private void updateTransaction(Transaction transaction) throws RevolutTransactionException {
        updateTransaction(transaction, null);
    }

    private void updateTransaction(Transaction transaction, Connection con) throws RevolutTransactionException {
        int result;
        verify(transaction);
        DbUtils.QueryExecutor<Integer> queryExecutor = getQueryExecutor(transaction);

        if (con == null) {
            result = dbUtils.executeQuery(UPDATE_TRANSACTION_QUERY, queryExecutor).getResult();
        } else {
            result = dbUtils.executeQueryInConnection(con, UPDATE_TRANSACTION_QUERY, queryExecutor).getResult();
        }

        if (result == 0) {
            throw new RevolutTransactionException("No result found");
        }
    }

    private DbUtils.QueryExecutor<Integer> getQueryExecutor(Transaction transaction) {
        return updateTransaction -> {
            updateTransaction.setInt(1, transaction.getStatus().getValue());
            updateTransaction.setDate(2, new Date(new java.util.Date().getTime()));
            updateTransaction.setLong(3, transaction.getId());
            return updateTransaction.executeUpdate();
        };
    }

    private void verify(Transaction transaction) throws RevolutTransactionException {
        if (transaction.getAmount() == null || transaction.getFromBankAccountId() == null ||
                transaction.getToBankAccountId() == null || transaction.getCurrency() == null
                || transaction.getStatus() == null || transaction.getCreationDate() == null
                || transaction.getUpdateDate() == null) {
            throw new RevolutTransactionException("Fields could not be NULL");
        }
    }

    private static void fillInPreparedStatement(PreparedStatement preparedStatement, Transaction transaction) {
        try {
            preparedStatement.setLong(1, transaction.getFromBankAccountId());
            preparedStatement.setLong(2, transaction.getToBankAccountId());
            preparedStatement.setBigDecimal(3, transaction.getAmount());
            preparedStatement.setInt(4, transaction.getCurrency().getId());
            preparedStatement.setInt(5, transaction.getStatus().getValue());
            preparedStatement.setDate(6, new java.sql.Date(transaction.getCreationDate().getTime()));
            preparedStatement.setDate(7, new java.sql.Date(transaction.getUpdateDate().getTime()));
        } catch (SQLException e) {
            log.error("Transactions prepared statement could not be initialized", e);
        }

    }

    private Transaction extractTransactionFromResultSet(ResultSet transactionsRS) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(transactionsRS.getLong(TRANSACTION_ID_ROW));
        transaction.setFromBankAccountId(transactionsRS.getLong(TRANSACTION_FROM_ACCOUNT_ROW));
        transaction.setToBankAccountId(transactionsRS.getLong(TRANSACTION_TO_ACCOUNT_ROW));
        transaction.setAmount(transactionsRS.getBigDecimal(TRANSACTION_AMOUNT_ROW));
        transaction.setCurrency(Currency.valueOf(transactionsRS.getInt(TRANSACTION_CURRENCY_ROW)));
        transaction.setStatus(TransactionStatus.valueOf(transactionsRS.getInt(TRANSACTION_STATUS_ROW)));
        transaction.setCreationDate(transactionsRS.getDate(TRANSACTION_CREATION_DATE_ROW));
        transaction.setUpdateDate(transactionsRS.getDate(TRANSACTION_UPDATE_DATE_ROW));
        return transaction;
    }
}
