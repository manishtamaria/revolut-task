package com.revolut.controller;

import com.revolut.model.RevolutTransactionException;
import com.revolut.model.Transaction;
import com.revolut.service.TransactionService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(TransactionController.BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionController {

    public static final String BASE_URL = "/transactions";
    public static final String GET_TRANSACTION_BY_ID_PATH = "id";

    private TransactionService transactionsService = TransactionService.getInstance();

    @GET
    public List<Transaction> getAllTransactions() {
        return transactionsService.getAllTransactions();
    }

    @GET()
    @Path("{" + GET_TRANSACTION_BY_ID_PATH + "}")
    public Transaction getTransactionById(@PathParam(GET_TRANSACTION_BY_ID_PATH) Long id) {
        return transactionsService.getTransactionById(id);
    }

    @POST()
    public Transaction createTransaction(Transaction transaction) throws RevolutTransactionException {
        return transactionsService.createTransaction(transaction);
    }
}
