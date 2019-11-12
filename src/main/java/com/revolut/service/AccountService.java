package com.revolut.service;

import com.revolut.dao.AccountDao;
import com.revolut.model.Account;
import com.revolut.model.RevolutTransactionException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class AccountService {
    private static final AccountService accountService = new AccountService();

    public static AccountService getInstance() {
        if(accountService ==null){
            new AccountService();
        }
        return accountService;
    }

    public List<Account> getAllAccounts() {
        return AccountDao.getInstance().getAllAccounts();
    }

    public Account getAccountById(Long id) {
        return AccountDao.getInstance().getAccountById(id);
    }

    public Account createAccount(Account Account) throws RevolutTransactionException {
        return AccountDao.getInstance().createAccount(Account);
    }
}
