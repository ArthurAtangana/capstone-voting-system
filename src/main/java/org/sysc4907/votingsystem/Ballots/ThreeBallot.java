package org.sysc4907.votingsystem.Ballots;

import org.sysc4907.votingsystem.generators.CandidateOrderGenerator;
import org.sysc4907.votingsystem.generators.PremarkGenerator;

import java.util.ArrayList;
import java.util.List;

public class ThreeBallot {
    private final Ballot firstBallot;
    private final Ballot secondBallot;
    private final Ballot thirdBallot;
    private final List<String> candidateList = new ArrayList<>();

    public ThreeBallot(List<String> candidates) {
        int numCandidates = candidates.size();

        // call order generator
        CandidateOrderGenerator cGen = new CandidateOrderGenerator(numCandidates);
        int candidateOrder = cGen.generateRandomCandidateOrder(); // returned by generator

        //store candidate in the random order
        String order = String.valueOf(candidateOrder);
        for (int i = 0; i < numCandidates; i++) {
            this.candidateList.add(candidates.get(Character.getNumericValue(order.charAt(i)) - 1));
        }

        // TODO: call id generator
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
