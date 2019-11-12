package com.revolut.controller;

import com.revolut.model.Account;
import com.revolut.model.RevolutTransactionException;
import com.revolut.service.AccountService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountController {
    private final static AccountService BANK_ACCOUNT_SERVICE = AccountService.getInstance();

    @GET
    public List<Account> getAllBankAccounts() {
        return BANK_ACCOUNT_SERVICE.getAllAccounts();
    }

    @GET
    @Path("{id}")
    public Account getBankAccountById(@PathParam("id") Long id) {
        Account bankAccount = BANK_ACCOUNT_SERVICE.getAccountById(id);
        if (bankAccount == null) {
            throw new WebApplicationException("The account is not exists", Response.Status.NOT_FOUND);
        }
        return bankAccount;
    }

    @POST
    public Account createBankAccount(Account bankAccount) throws RevolutTransactionException {
        return BANK_ACCOUNT_SERVICE.createAccount(bankAccount);
    }

}
