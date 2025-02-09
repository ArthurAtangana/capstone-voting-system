package org.sysc4907.votingsystem;

import org.junit.jupiter.api.Test;
import org.sysc4907.votingsystem.generators.BallotIdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BallotIdGeneratorTest {

    @Test
    public void test() {
        int numEligibleVoters = 1;
        List<Integer> allIds = new ArrayList<>();
        for (int i = 0; i < numEligibleVoters * 3; i++) {
            allIds.add(i);
        }

        BallotIdGenerator b = new BallotIdGenerator(numEligibleVoters);
        List<Integer> usedIds = new ArrayList<>();
        for (int i = 0; i < numEligibleVoters * 3; i++) {
            usedIds.add(b.generateBallotId());
        }
        assertEquals(Set.copyOf(allIds), Set.copyOf(usedIds));
        assertNotEquals(allIds, usedIds);
    }
}
