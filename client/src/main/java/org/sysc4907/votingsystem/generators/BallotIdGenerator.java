package org.sysc4907.votingsystem.generators;

import java.util.ArrayList;
import java.util.List;

public class BallotIdGenerator {
    private List<Integer> ballotIdsAlreadyUsed = new ArrayList<Integer>();

    public BallotIdGenerator() {}

    public int generateBallotId() {
        // large random number, check not already in ballotIdsAlreadyUsed
        return 0;
    }
}
