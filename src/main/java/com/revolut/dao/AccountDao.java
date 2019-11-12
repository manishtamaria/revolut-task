package com.revolut.dao;

import com.revolut.database.DbUtils;
import com.revolut.model.Account;
import com.revolut.model.Currency;
import com.revolut.model.RevolutTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.revolut.dao.DaoConstant.*;

public class AccountDao {
    private static final Logger log = LoggerFactory.getLogger(AccountDao.class);

    private static final AccountDao ACCOUNT_DAO = new AccountDao();
    private DbUtils dbUtils = DbUtils.getInstance();

    private AccountDao() {
    }

    public static AccountDao getInstance() {
        return ACCOUNT_DAO;
    }

    public List<Account> getAllAccounts() {
        return dbUtils.executeQuery("select * from " + ACCOUNT_TABLE_NAME, getBankAccounts -> {
            List<Account> bankAccounts = new ArrayList<>();

            try (ResultSet bankAccountsRS = getBankAccounts.executeQuery()) {
                if (bankAccountsRS != null) {
                    while (bankAccountsRS.next()) {
                        bankAccounts.add(extractAccountFromResultSet(bankAccountsRS));
                    }
                }
            }

            return bankAccounts;
        }).getResult();
    }

    public Account getAccountById(Long id) {


        return dbUtils.executeQuery(GET_BANK_ACCOUNT_BY_ID_SQL, getBankAccount -> {
            getBankAccount.setLong(1, id);
            try (ResultSet accountResultSet = getBankAccount.executeQuery()) {
                if (accountResultSet != null && accountResultSet.first()) {
                    return extractAccountFromResultSet(accountResultSet);
                }
            }

            return null;
        }).getResult();
    }

    Account getForUpdateAccountById(Connection con, Long id) {

        return dbUtils.executeQueryInConnection(con, GET_BANK_ACCOUNT_BY_ID_FOR_UPDATE, getAccount -> {
            getAccount.setLong(1, id);
            try (ResultSet accountResultSet = getAccount.executeQuery()) {
                if (accountResultSet != null && accountResultSet.first()) {
                    return extractAccountFromResultSet(accountResultSet);
                }
            }

            return null;
        }).getResult();
    }

    void updateAccount(Account account, Connection con) throws RevolutTransactionException {
        int result;
        verify(account);
        DbUtils.QueryExecutor<Integer> queryExecutor = updateBankAccount -> {
            fillInPreparedStatement(updateBankAccount, account);
            updateBankAccount.setLong(4, account.getId());

            return updateBankAccount.executeUpdate();
        };
        if (con == null) {
            result = dbUtils.executeQuery(UPDATE_BANK_ACCOUNT_SQL, queryExecutor).getResult();
        } else {
            result = dbUtils.executeQueryInConnection(con, UPDATE_BANK_ACCOUNT_SQL, queryExecutor).getResult();
        }
        if (result == 0) {
            throw new RevolutTransactionException("Object not found");
        }
    }

    public Account createAccount(Account account) throws RevolutTransactionException {
        verify(account);
        account = dbUtils.executeQuery(INSERT_BANK_ACCOUNT_SQL,
                new DbUtils.CreationQueryExecutor<>(account, AccountDao::fillInPreparedStatement)).getResult();
        if (account == null) {
            throw new RevolutTransactionException("Couldn't able to create entity in DB");
        }
        return account;
    }

    private Account extractAccountFromResultSet(ResultSet bankAccountsRS) throws SQLException {
        Account account = new Account();
        account.setId(bankAccountsRS.getLong(BANK_ACCOUNT_ID_ROW));
        account.setOwnerName(bankAccountsRS.getString(BANK_ACCOUNT_OWNER_NAME_ROW));
        account.setBalance(bankAccountsRS.getBigDecimal(BANK_ACCOUNT_BALANCE_ROW));
        account.setCurrency(Currency.valueOf(bankAccountsRS.getInt(BANK_ACCOUNT_CURRENCY_ID_ROW)));

        return account;
    }

    private void verify(Account account) throws RevolutTransactionException {
        if (account.getId() == null) {
            throw new RevolutTransactionException("ID value is invalid");
        }

        if (account.getOwnerName() == null || account.getBalance() == null ||
                 account.getCurrency() == null) {
            throw new RevolutTransactionException("Fields could not be NULL");
        }
    }

    private static void fillInPreparedStatement(PreparedStatement preparedStatement, Account account) {
        try {
            preparedStatement.setString(1, account.getOwnerName());
            preparedStatement.setBigDecimal(2, account.getBalance());
            preparedStatement.setLong(3, account.getCurrency().getId());
        } catch (SQLException e) {
            log.error("BankAccount prepared statement could not be initialized ", e);
        }
    }
}
