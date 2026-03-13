package com.getcollate.trip.accounts.settler;

import com.getcollate.trip.accounts.Transaction;

import java.util.List;

public interface Settler {
    public List<Debt> settle(List<Transaction> transactions);
}
