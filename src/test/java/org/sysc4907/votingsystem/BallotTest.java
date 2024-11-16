package org.sysc4907.votingsystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sysc4907.votingsystem.Ballots.Ballot;

import static org.junit.jupiter.api.Assertions.*;

public class BallotTest {

    private Ballot b1;

    @BeforeEach
    public void setUp() {
        b1 = new Ballot(1, 3, 231, new boolean[]{false, false, true});

    }

    @Test
    public void testBallot() {
        assertEquals(b1.getId(), 1);
        assertEquals(b1.getCandidateOrder(), 231);
        assertEquals(3, b1.getMarkValues().length);
        assertFalse(b1.getMarkValues()[0]);
        assertFalse(b1.getMarkValues()[1]);
        assertTrue(b1.getMarkValues()[2]);
    }

    @Test
    public void testMarkingBoxes() {
        b1.setMarkableBox(0, true);
        assertTrue(b1.isBoxMarked(0));
        assertFalse(b1.isPremark(0));

        b1.setMarkableBox(0, false);
        assertFalse(b1.isBoxMarked(0));
        assertFalse(b1.isPremark(0));

        // should not be able to change premarked boxes
        b1.setMarkableBox(2, false);
        assertTrue(b1.isBoxMarked(2));
        assertTrue(b1.isPremark(2));
    }
}
