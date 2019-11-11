package com.revolut.controller;

import com.revolut.model.Account;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Produces(MediaType.APPLICATION_JSON)
public class AccountController {

    @GET
    public Response getAllBankAccounts() {
        Set<Account> accounts;

//        bankAccounts = BANK_ACCOUNT_SERVICE.getAllBankAccounts();
//
//        if (bankAccounts == null) {
//            Response.noContent().build();
//        }

        return Response.ok().build();
    }
}
