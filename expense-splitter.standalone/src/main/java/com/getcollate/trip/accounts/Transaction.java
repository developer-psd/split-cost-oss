package com.getcollate.trip.accounts;

import com.getcollate.trip.Participant;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record Transaction (String transactionId,
                           Float spentAmount,
                           Participant spentBy,
                           CATEGORY spentOn,
                           SHARETYPE shareType,
                           Date spentDate,
                           List<Participant> benefittedBy
                           ){
    public Transaction(Float spentAmount, Participant spentBy, CATEGORY spentOn, SHARETYPE shareType, Date spentDate, List<Participant> benefittedBy) {
        this(UUID.randomUUID().toString(),
                spentAmount,
                spentBy,
                spentOn,
                shareType,
                spentDate,
                benefittedBy
                );
    }
}
