package org.sysc4907.votingsystem;

import org.junit.jupiter.api.Test;
import org.sysc4907.votingsystem.generators.PremarkGenerator;
import static org.junit.jupiter.api.Assertions.*;

public class PremarksGeneratorTest {

    @Test
    public void generatePremarks() {
        PremarkGenerator premarkGenerator = new PremarkGenerator(5);

        boolean[][] returnedMarks = premarkGenerator.generateMarks();
        assertEquals(1, countRowMarks(returnedMarks, 0));
        assertEquals(1, countRowMarks(returnedMarks,1));
        assertEquals(1, countRowMarks(returnedMarks,2));
        assertEquals(1, countRowMarks(returnedMarks,3));
    }

    public int countRowMarks(boolean[][] marks, int row) {
        int count = 0;
        if (marks[0][row]) count++;
        if (marks[1][row]) count++;
        if (marks[2][row]) count++;
        return count;
    }

}
