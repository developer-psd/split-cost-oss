package com.getcollate.trip.accounts.settler;

import com.getcollate.trip.accounts.Transaction;

import java.util.List;

public class BasicSettler implements Settler {
    @Override
    public List<Debt> settle(List<Transaction> transactions) {
        return List.of();
    }
}
