package com.getcollate.trip.accounts.settler;

import com.getcollate.trip.Participant;
import com.getcollate.trip.accounts.Transaction;
import com.getcollate.trip.accounts.SHARETYPE;

import java.util.*;

public class SimplifiedSettler implements Settler {

    @Override
    public List<Debt> settle(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return List.of();
        }

        Map<String, Integer> netBalancesInCents = buildNetBalancesInCents(transactions);

        List<String> participantIds = new ArrayList<>();
        List<Integer> balances = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : netBalancesInCents.entrySet()) {
            if (entry.getValue() != 0) {
                participantIds.add(entry.getKey());
                balances.add(entry.getValue());
            }
        }

        if (balances.isEmpty()) {
            return List.of();
        }

        BestSolution bestSolution = new BestSolution();
        dfs(0, participantIds, balances, new ArrayList<>(), bestSolution);

        return bestSolution.debts;
    }

    // bfs was the initial easiest solution for the shortest path... but when the number of participants grew, we'll have memory explosion.
    // second option was dfs which was optimal for memory... but without pruning was not really helpful (took a lot of time).
    // added dfs + pruning - which was better wrt to run time, but further optimization could be done by ordering the search,
    //
    // have introduced dfs with pruning
    private void dfs(
            int start,
            List<String> participantIds,
            List<Integer> balances,
            List<Debt> currentDebts,
            BestSolution bestSolution
    ) {
        while (start < balances.size() && balances.get(start) == 0) {
            start++;
        }

        if (start == balances.size()) {
            if (currentDebts.size() < bestSolution.minTransactions) {
                bestSolution.minTransactions = currentDebts.size();
                bestSolution.debts = new ArrayList<>(currentDebts);
            }
            return;
        }

        if (currentDebts.size() >= bestSolution.minTransactions) {
            return;
        }

        int startBalance = balances.get(start);

        List<Integer> candidateIndices = new ArrayList<>();
        Set<Integer> triedBalances = new HashSet<>();

        for (int i = start + 1; i < balances.size(); i++) {
            int candidateBalance = balances.get(i);

            if (candidateBalance == 0 || startBalance * candidateBalance >= 0) {
                continue; // must be opposite signs
            }

            if (!triedBalances.add(candidateBalance)) {
                continue; // skip duplicate balance states
            }

            candidateIndices.add(i);
        }

        candidateIndices.sort((i, j) -> {
            int balanceI = balances.get(i);
            int balanceJ = balances.get(j);

            boolean exactI = Math.abs(startBalance) == Math.abs(balanceI);
            boolean exactJ = Math.abs(startBalance) == Math.abs(balanceJ);

            if (exactI != exactJ) {
                return exactI ? -1 : 1; // exact cancellation first
            }

            return Integer.compare(Math.abs(balanceJ), Math.abs(balanceI));
        });

        for (int i : candidateIndices) {
            int candidateBalance = balances.get(i);
            int transferInCents = Math.min(Math.abs(startBalance), Math.abs(candidateBalance));

            Debt settlementDebt;
            int newStartBalance;
            int newCandidateBalance;

            if (startBalance < 0) {
                // start is debtor, candidate is creditor
                settlementDebt = createDebt(
                        participantIds.get(start),
                        participantIds.get(i),
                        transferInCents
                );
                newStartBalance = startBalance + transferInCents;
                newCandidateBalance = candidateBalance - transferInCents;
            } else {
                // start is creditor, candidate is debtor
                settlementDebt = createDebt(
                        participantIds.get(i),
                        participantIds.get(start),
                        transferInCents
                );
                newStartBalance = startBalance - transferInCents;
                newCandidateBalance = candidateBalance + transferInCents;
            }

            balances.set(start, newStartBalance);
            balances.set(i, newCandidateBalance);
            currentDebts.add(settlementDebt);

            dfs(
                    newStartBalance == 0 ? start + 1 : start,
                    participantIds,
                    balances,
                    currentDebts,
                    bestSolution
            );

            currentDebts.remove(currentDebts.size() - 1);
            balances.set(start, startBalance);
            balances.set(i, candidateBalance);

            if (Math.abs(startBalance) == Math.abs(candidateBalance)) {
                break; // strongest pruning: both sides close out exactly
            }
        }
    }

    private Debt createDebt(String fromParticipant, String toParticipant, int amountInCents) {
        return new Debt(fromParticipant, toParticipant, fromCents(amountInCents));
    }

    private Map<String, Integer> buildNetBalancesInCents(List<Transaction> transactions) {
        Map<String, Integer> netBalancesInCents = new LinkedHashMap<>();

        for (Transaction transaction : transactions) {
            validate(transaction);

            String payerId = participantKey(transaction.spentBy());
            ensurePresent(netBalancesInCents, payerId);

            for (Participant participant : transaction.benefittedBy()) {
                ensurePresent(netBalancesInCents, participantKey(participant));
            }

            if (transaction.shareType() == SHARETYPE.SPONSORED) {
                continue;
            }

            int totalAmountInCents = toCents(transaction.spentAmount());

            netBalancesInCents.put(
                    payerId,
                    netBalancesInCents.get(payerId) + totalAmountInCents
            );

            Map<String, Integer> sharesInCents =
                    splitEquallyInCents(totalAmountInCents, transaction.benefittedBy());

            for (Map.Entry<String, Integer> entry : sharesInCents.entrySet()) {
                String participantId = entry.getKey();
                int shareInCents = entry.getValue();

                netBalancesInCents.put(
                        participantId,
                        netBalancesInCents.get(participantId) - shareInCents
                );
            }
        }

        return netBalancesInCents;
    }

    private Map<String, Integer> splitEquallyInCents(int totalAmountInCents, List<Participant> beneficiaries) {
        int count = beneficiaries.size();
        int baseShare = totalAmountInCents / count;
        int remainder = totalAmountInCents % count;

        Map<String, Integer> shares = new LinkedHashMap<>();

        for (int i = 0; i < beneficiaries.size(); i++) {
            Participant participant = beneficiaries.get(i);
            int share = baseShare + (i < remainder ? 1 : 0);
            shares.put(participantKey(participant), share);
        }

        return shares;
    }

    private void ensurePresent(Map<String, Integer> netBalancesInCents, String participantId) {
        netBalancesInCents.putIfAbsent(participantId, 0);
    }

    private void validate(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        if (transaction.spentAmount() == null || transaction.spentAmount() <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
        if (transaction.spentBy() == null) {
            throw new IllegalArgumentException("Transaction payer cannot be null");
        }
        if (transaction.shareType() == null) {
            throw new IllegalArgumentException("Transaction share type cannot be null");
        }
        if (transaction.benefittedBy() == null || transaction.benefittedBy().isEmpty()) {
            throw new IllegalArgumentException("At least one beneficiary is required");
        }
    }

    private int toCents(Float amount) {
        return Math.round(amount * 100.0f);
    }

    private float fromCents(int cents) {
        return cents / 100.0f;
    }

    private String participantKey(Participant participant) {
        return participant.participantId();
    }

    private static class BestSolution {
        private int minTransactions = Integer.MAX_VALUE;
        private List<Debt> debts = List.of();
    }
}
