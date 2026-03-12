package com.getcollate.expense_splitter.pojo;

import java.util.List;

public class PostTransactionRequest {

    List<Transactions> transactions;

    public String toString() {
        return "PostTransactionRequest{" + "transactions=" + transactions + '}';
    }

    public List<Transactions> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transactions> transactions) {
        this.transactions = transactions;
    }

    public static class Transactions {
        String spentAmount;
        String spentBy;
        String spentOn;
        String spentDate;
        List<String> benefittedBy;

        public String toString() {
            return "Transactions{" + "spentAmount=" + spentAmount + ", spentBy=" + spentBy + ", spentOn=" + spentOn + ", spentDate=" + spentDate + ", benefittedBy=" + benefittedBy + '}';
        }

        public String getSpentAmount() {
            return spentAmount;
        }

        public void setSpentAmount(String spentAmount) {
            this.spentAmount = spentAmount;
        }

        public String getSpentBy() {
            return spentBy;
        }

        public void setSpentBy(String spentBy) {
            this.spentBy = spentBy;
        }

        public String getSpentOn() {
            return spentOn;
        }

        public void setSpentOn(String spentOn) {
            this.spentOn = spentOn;
        }

        public String getSpentDate() {
            return spentDate;
        }

        public void setSpentDate(String spentDate) {
            this.spentDate = spentDate;
        }

        public List<String> getBenefittedBy() {
            return benefittedBy;
        }

        public void setBenefittedBy(List<String> benefittedBy) {
            this.benefittedBy = benefittedBy;
        }
    }
}
