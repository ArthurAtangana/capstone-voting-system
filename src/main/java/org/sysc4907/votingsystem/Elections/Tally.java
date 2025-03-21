package org.sysc4907.votingsystem.Elections;


import org.json.JSONArray;
import org.springframework.core.env.Environment;
import org.sysc4907.votingsystem.Ballots.Ballot;

import org.json.JSONObject;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Tally {
    private final List<Integer> tallyOfVotes = new ArrayList<>();
    private final List<Integer> tallyOfVotesAdjusted = new ArrayList<>(); // Adjusted to compensate for three ballot system
    private final int numCandidates;

    private boolean fabricEnabled = false;

    public Tally(Environment environment, int numCandidates) {
        fabricEnabled = environment.getProperty("fabric.enabled", Boolean.class, true);
        this.numCandidates = numCandidates;
    }

    public Tally() {
        numCandidates = 2;
    }

    public List<Integer> tallyVotes(JSONArray jsonArray, List<PrivateKey> privateKeys) {
        tallyOfVotes.clear();
        tallyOfVotesAdjusted.clear();
        for (int i = 0; i < numCandidates; i++) {
            tallyOfVotes.add(0);
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonTransaction = jsonArray.getJSONObject(i);
            if (jsonTransaction.has("ballotId")) {
                CastBallot ballot = getCastBallotFromJSON(jsonTransaction, privateKeys);
                addVotes(ballot);
            }
        }
        int numberOfVotes = totalNumVotes(jsonArray);
        for (Integer tallyOfVote : tallyOfVotes) {
            tallyOfVotesAdjusted.add(tallyOfVote - numberOfVotes);
        }
        return tallyOfVotesAdjusted;
    }

    public CastBallot getCastBallotFromJSON(JSONObject jsonTransaction, List<PrivateKey> privateKeys) {
        return new CastBallot(jsonTransaction.getInt("ballotId"),
                getMarksFromString(jsonTransaction),
                decryptOrder(jsonTransaction, privateKeys),
                privateKeys);

    }

    private void addVotes(CastBallot ballot) {
        for (int i = 0; i < ballot.marks.size(); i++) {
            if (ballot.marks.get(i)) {
                tallyOfVotes.set(ballot.candidateOrder.get(i), tallyOfVotes.get(ballot.candidateOrder.get(i)) + 1);
            }
        }

    }

    private List<Boolean> getMarksFromString(JSONObject ballot) {
        String marks = ballot.getString("ballotMarks");
        String[] booleanArray = marks.replaceAll("[\\[\\]]", "").split(",");
        List<Boolean> booleanList = new ArrayList<>();
        for (String s : booleanArray) {
            booleanList.add(Boolean.parseBoolean(s.trim()));
        }
        return booleanList;
    }

    private List<Integer> decryptOrder(JSONObject ballot, List<PrivateKey> privateKeys) {
        String decryptedOrder = ballot.getString("candidateOrder");
        if (fabricEnabled) {
            for (PrivateKey privateKey : privateKeys) {
                try {
                    decryptedOrder = Ballot.decrypt(decryptedOrder, privateKey);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } // if fabric is not enabled we are using mocked data

        String[] parts = decryptedOrder.split(":");
        String orderString = parts[0];
        return Arrays.stream(orderString.replaceAll("[\\[\\]]", "").split(", "))
                .map(Integer::parseInt).collect(Collectors.toList());
    }

    public List<Integer> getTallyOfVotes() {return tallyOfVotes;}

    public int totalNumVotes(JSONArray jsonArray) {
        // Count objects with the "ballotId" field
        int count = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            if (obj.has("ballotId")) {
                count++;
            }
        }
        // adjust for three ballot system
        count = count / 3;
        return count;
    }

    public record CastBallot(int id, List<Boolean> marks, List<Integer> candidateOrder, List<java.security.PrivateKey> privateKeys) {}
}
