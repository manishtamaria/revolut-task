package com.revolut.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Transaction implements BaseDbModel{
    private Long id;
    private Long fromBankAccountId;
    private Long toBankAccountId;
    private BigDecimal amount;
    private Currency currency;
    private LocalDate creationDate;
    private LocalDate updateDate;
    private TransactionStatus status;

    public Transaction() {
        this.creationDate = LocalDate.now();
        this.updateDate = LocalDate.now();
        this.status = TransactionStatus.NEW;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromBankAccountId() {
        return fromBankAccountId;
    }

    public void setFromBankAccountId(Long fromBankAccountId) {
        this.fromBankAccountId = fromBankAccountId;
    }

    public Long getToBankAccountId() {
        return toBankAccountId;
    }

    public void setToBankAccountId(Long toBankAccountId) {
        this.toBankAccountId = toBankAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
