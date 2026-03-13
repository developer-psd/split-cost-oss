package com.getcollate.trip;

import com.getcollate.trip.accounts.BalanceSheet;
import com.getcollate.trip.accounts.settler.Debt;
import com.getcollate.trip.accounts.settler.Settler;
import com.getcollate.trip.accounts.Transaction;

import java.util.List;
import java.util.Map;

public class Trip {
    String tripId;
    String tripName;
    List<Participant> participants;

    // used to create an access pattern for participants using their name
    private Map<String, Participant> participantMap;
    BalanceSheet balanceSheet;

    public Trip(String tripName, List<Participant> participants) {
        // trip id will be set automatically and should be unique
        // generate a unique trip id using the tripName and participants
        this.tripName = tripName;
        this.participants = participants;
        if (this.participants == null || this.participants.isEmpty())
            throw new RuntimeException("Participants cannot be empty");
        participants.forEach(participant -> participantMap.put(participant.name(), participant));
        this.balanceSheet = new BalanceSheet();
    }

    public Trip addParticipants(List<Participant> participants) {
        this.participants.addAll(participants);
        participants.forEach(participant -> participantMap.put(participant.name(), participant));
        return this;
    }

    public Trip removeParticipants(List<String> participants) {
        participants.forEach((participant) -> {
            Participant temp = participantMap.get(participant);
            participantMap.remove(participant);
            this.participants.remove(temp);
        });
        return this;
    }

    public Participant getParticipant(String participantName) {
        Participant participant = participantMap.get(participantName);
        if (participant == null)
            throw new RuntimeException("Participant not found in the trip: " + this.tripName + " with id: " + this.tripId);
        return participant;
    }

    public Trip addTransactions(List<Transaction> transactions) {
        balanceSheet.addTransactions(transactions);
        return this;
    }

    public List<Debt> settle(Settler settler) {
        return balanceSheet.settle(settler);
    }

}
