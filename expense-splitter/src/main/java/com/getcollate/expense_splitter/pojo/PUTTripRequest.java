package com.getcollate.expense_splitter.pojo;

import java.util.List;

public class PUTTripRequest {

    List<String> addParticipants;
    List<String> removeParticipants;

    public List<String> getAddParticipants() {
        return addParticipants;
    }

    public List<String> getRemoveParticipants() {
        return removeParticipants;
    }

    public void setRemoveParticipants(List<String> removeParticipants) {
        this.removeParticipants = removeParticipants;
    }

    public void setAddParticipants(List<String> addParticipants) {
        this.addParticipants = addParticipants;
    }

    public String toString() {
        return "PUTTripRequest{" + "addParticipants=" + addParticipants + ", removeParticipants=" + removeParticipants + '}';
    }
}
