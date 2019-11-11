package com.revolut.service;

import com.revolut.model.Account;

import java.util.Set;

public class AccountService {
    private static final AccountService accountService = new BankAccountService();

    public static AccountService getInstance() {
        if(accountService ==null){
            new AccountService();
        }
        return accountService;
    }

//    public Set<Account> getAllAccounts() {
//        return AccountDto.getInstance().getAllAccounts();
//    }
//
//    public Account getAccountById(Long id) {
//        return AccountDto.getInstance().getAccountById(id);
//    }
//
//    public void updateAccount(Account Account) throws ObjectModificationException {
//        AccountDto.getInstance().updateAccountSafe(Account);
//    }
//
//    public Account createAccount(Account Account) throws ObjectModificationException {
//        return AccountDto.getInstance().createAccount(Account);
//    }
}
