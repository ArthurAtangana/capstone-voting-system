package org.sysc4907.votingsystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sysc4907.votingsystem.Ballots.Ballot;
import org.sysc4907.votingsystem.Ballots.ThreeBallot;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
public class ThreeBallotTest {
    private Ballot b1;
    private Ballot b2;
    private Ballot b3;
    private ThreeBallot threeBallot;

    @BeforeEach
    public void setUp() {
        threeBallot = new ThreeBallot(Arrays.asList("foo", "bar", "baz", "qux"));
    }

    @Test
    public void testThreeBallot() {
        b1 = threeBallot.getFirstBallot();
        b2 = threeBallot.getSecondBallot();
        b3 = threeBallot.getThirdBallot();

        assertEquals(4, b1.getMarkValues().length);
        assertEquals(4, b2.getMarkValues().length);
        assertEquals(4, b3.getMarkValues().length);

        // candidate order is 4 digits and contains 1, 2, 3, 4 in any order
        String candidateOrder1 = String.valueOf(b1.getCandidateOrder());
        assertEquals(4, candidateOrder1.length());
        assertTrue(candidateOrder1.contains("1"));
        assertTrue(candidateOrder1.contains("2"));
        assertTrue(candidateOrder1.contains("3"));
        assertTrue(candidateOrder1.contains("4"));

        String candidateOrder2 = String.valueOf(b2.getCandidateOrder());
        assertEquals(4, candidateOrder2.length());
        assertTrue(candidateOrder2.contains("1"));
        assertTrue(candidateOrder2.contains("2"));
        assertTrue(candidateOrder2.contains("3"));
        assertTrue(candidateOrder2.contains("4"));

        String candidateOrder3 = String.valueOf(b3.getCandidateOrder());
        assertEquals(4, candidateOrder3.length());
        assertTrue(candidateOrder3.contains("1"));
        assertTrue(candidateOrder3.contains("2"));
        assertTrue(candidateOrder3.contains("3"));
        assertTrue(candidateOrder3.contains("4"));

        assertEquals(1, countRowMarks(0));
        assertEquals(1, countRowMarks(1));
        assertEquals(1, countRowMarks(2));
        assertEquals(1, countRowMarks(3));

    }

    public int countRowMarks(int row) {
        int count = 0;
        if (b1.isBoxMarked(row)) count++;
        if (b2.isBoxMarked(row)) count++;
        if (b3.isBoxMarked(row)) count++;
        return count;
    }
}
