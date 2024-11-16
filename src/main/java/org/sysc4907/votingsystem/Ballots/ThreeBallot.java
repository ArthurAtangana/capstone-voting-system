package org.sysc4907.votingsystem.Ballots;

import org.sysc4907.votingsystem.generators.CandidateOrderGenerator;
import org.sysc4907.votingsystem.generators.PremarkGenerator;

import java.util.List;

public class ThreeBallot {
    private final Ballot firstBallot;
    private final Ballot secondBallot;
    private final Ballot thirdBallot;
    private final List<String> candidateList;

    public ThreeBallot(List<String> candidates) {
        this.candidateList = candidates;
        int numCandidates = candidates.size();

        // call order generator
        CandidateOrderGenerator cGen = new CandidateOrderGenerator(numCandidates);
        int candidateOrder = cGen.generateRandomCandidateOrder(); // returned by generator


        // call id generator
        int id1 = 0; // returned by generator
        int id2 = 0; // returned by generator
        int id3 = 0; // returned by generator

        // call generator to randomly mark each candidate once
        PremarkGenerator pGen = new PremarkGenerator(numCandidates);
        boolean[][] premarked = pGen.generateMarks();

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
