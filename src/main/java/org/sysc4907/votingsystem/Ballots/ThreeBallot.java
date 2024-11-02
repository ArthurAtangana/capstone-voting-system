package org.sysc4907.votingsystem.Ballots;

import java.util.List;

public class ThreeBallot {
    private Ballot firstBallot;
    private Ballot secondBallot;
    private Ballot thirdBallot;
    private List<String> candidateList;

    public ThreeBallot(List<String> candidates) {
        this.candidateList = candidates;
        int numCandidates = candidates.size();

        // call order generator
        int candidateOrder = 123; // returned by generator


        // call id generator
        int id1 = 0; // returned by generator
        int id2 = 0; // returned by generator
        int id3 = 0; // returned by generator

        boolean[][] premarked = new boolean[3][candidates.size()];
        // call generator to randomly mark each candidate once

        firstBallot = new Ballot(id1, numCandidates, candidateOrder, premarked[0]);
        secondBallot = new Ballot(id2, numCandidates, candidateOrder, premarked[1]);
        thirdBallot = new Ballot(id3, numCandidates, candidateOrder, premarked[2]);
    }

    public Ballot selectReceipt(int ballotNumber) {
        return switch (ballotNumber) {
            case 1 -> firstBallot;
            case 2 -> secondBallot;
            case 3 -> thirdBallot;
            default -> null;
        };
    }

    public Ballot getFirstBallot() {
        return firstBallot;
    }

    public Ballot getSecondBallot() {
        return secondBallot;
    }

    public Ballot getThirdBallot() {
        return thirdBallot;
    }

    public List<String> getCandidateList() {
        return candidateList;
    }
}
