package org.sysc4907.votingsystem.Elections;


import org.sysc4907.votingsystem.Ballots.Ballot;

import java.util.List;

public class Tally {
    private final int[] tallyOfVotes ;
    private int[] keys;

    public Tally(int numberOfCandidates, int numberOfDecryptionKeys) {
        tallyOfVotes = new int[numberOfCandidates];
        keys = new int[numberOfDecryptionKeys];
    }

    public void tallyVotes(List<Ballot> votes) {
        for (Ballot vote : votes) {
            // validity checks called here maybe?

            addVote(vote);
        }
    }

    private void addVote(Ballot vote) {
        // decrypt candidate order
        // int order = decryptOrder(vote.getEncryptedOrder)

        // add votes to the running tally
        int dummyOrder = 542;
        determineVotes(vote, dummyOrder);
    }

    private void determineVotes(Ballot vote, int order) {
        boolean[] boxes = vote.getMarkValues();
        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i]) {
                tallyOfVotes[i]++;
            }
        }
    }

    public int[] getTallyOfVotes() {return tallyOfVotes;}
}
