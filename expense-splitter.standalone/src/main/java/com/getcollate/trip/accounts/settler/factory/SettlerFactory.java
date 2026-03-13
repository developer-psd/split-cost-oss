package com.getcollate.trip.accounts.settler.factory;

import com.getcollate.trip.accounts.settler.BasicSettler;
import com.getcollate.trip.accounts.settler.SettlementMode;
import com.getcollate.trip.accounts.settler.Settler;
import com.getcollate.trip.accounts.settler.SimplifiedSettler;

public final class SettlerFactory {
    private SettlerFactory() {}

    public static Settler create(SettlementMode mode) {
        return switch (mode) {
            case BASIC -> new BasicSettler();
            case SIMPLIFIED -> new SimplifiedSettler();
        };
    }
}
