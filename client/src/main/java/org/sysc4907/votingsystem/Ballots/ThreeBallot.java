package org.sysc4907.votingsystem.Ballots;

import org.sysc4907.votingsystem.generators.CandidateOrderGenerator;
import org.sysc4907.votingsystem.generators.PremarkGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreeBallot {
    private final Ballot firstBallot;
    private final Ballot secondBallot;
    private final Ballot thirdBallot;
    private final List<String> candidateList = new ArrayList<>();

    public ThreeBallot(List<String> candidates, List<java.security.PublicKey> orderKeys) {
        int numCandidates = candidates.size();

        // call order generator
        CandidateOrderGenerator cGen = new CandidateOrderGenerator(numCandidates);
        List<Integer> candidateOrder = cGen.generateRandomCandidateOrder(); // returned by generator

        //store candidate in the random order
        for (int i = 0; i < numCandidates; i++) {
            this.candidateList.add(candidates.get(candidateOrder.get(i)));
        }

        // call generator to randomly mark each candidate once
        PremarkGenerator pGen = new PremarkGenerator(numCandidates);
        boolean[][] premarked = pGen.generateMarks();

        firstBallot = new Ballot(numCandidates, candidateOrder.toString(), premarked[0], orderKeys);
        secondBallot = new Ballot(numCandidates, candidateOrder.toString(), premarked[1], orderKeys);
        thirdBallot = new Ballot(numCandidates, candidateOrder.toString(), premarked[2], orderKeys);
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

    public List<Map<String, Object>> getBallotAttributes() {
        List<Map<String, Object>> ballotsData = new ArrayList<>();

        // Process each ballot and add its attributes to the list
        for (Ballot ballot : List.of(firstBallot, secondBallot, thirdBallot)) {
            Map<String, Object> ballotMap = new HashMap<>();
            ballotMap.put("id", ballot.getId());
            ballotMap.put("candidateOrder", ballot.getEncryptedCandidateOrder());
            ballotMap.put("markableBoxes", ballot.getMarkValues());
            ballotsData.add(ballotMap);
        }

        return ballotsData;
    }

}
