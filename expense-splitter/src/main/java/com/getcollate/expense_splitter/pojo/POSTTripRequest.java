package com.getcollate.expense_splitter.pojo;

import java.util.List;

public class POSTTripRequest {
    String name;
    List<String> participants;

    @java.lang.Override
    public java.lang.String toString() {
        return "POSTTripRequest{" +
                "tripName='" + name + '\'' +
                ", participants=" + participants +
                '}';
    }

    // GETTERS
    public String getName() {
        return name;
    }

    public List<String> getParticipants() {
        return participants;
    }

    // SETTERS
    public void setName(String name) {
        this.name = name;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}
