package org.sysc4907.votingsystem.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BallotIdGenerator {
    private final List<Integer> ballotIds = new ArrayList<>();
    int index = -1;

    public BallotIdGenerator(int numEligibleVoters) {
        for (int i = 0; i < numEligibleVoters * 3; i++) {
            ballotIds.add(i);
        }
        Collections.shuffle(ballotIds);
    }

    public int generateBallotId() {
        index++;
        return ballotIds.get(index);
    }
}
