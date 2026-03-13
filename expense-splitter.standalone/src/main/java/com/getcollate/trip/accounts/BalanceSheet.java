package com.getcollate.trip.accounts;

import com.getcollate.trip.accounts.settler.Debt;
import com.getcollate.trip.accounts.settler.Settler;

import java.util.ArrayList;
import java.util.List;

public class BalanceSheet {
    List<Transaction> transactions;

    public BalanceSheet() {
        this.transactions = new ArrayList<Transaction>();
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Debt> settle(Settler settler) {
        return settler.settle(this.transactions);
    }

    public void addTransactions(List<Transaction> transactions) {
        this.transactions.addAll(transactions);
    }

}