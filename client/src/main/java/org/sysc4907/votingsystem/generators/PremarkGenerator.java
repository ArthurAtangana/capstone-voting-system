package org.sysc4907.votingsystem.generators;

import java.util.Random;

public class PremarkGenerator {
    private final int numCandidates;

    public PremarkGenerator(int numCandidates) {
        this.numCandidates = numCandidates;
    }

    public boolean[][] generateMarks() {
        Random rand = new Random();
        boolean[][] marks = new boolean[3][numCandidates];
        boolean[] ballot1Marks = new boolean[numCandidates];
        boolean[] ballot2Marks = new boolean[numCandidates];
        boolean[] ballot3Marks = new boolean[numCandidates];

        for (int row = 0; row < numCandidates; row++) {
            int randomBallotToMark = rand.nextInt(3);
            switch (randomBallotToMark) {
                case 0 -> ballot1Marks[row] = true;
                case 1 -> ballot2Marks[row] = true;
                case 2 -> ballot3Marks[row] = true;
            }
        }
        marks[0] = ballot1Marks;
        marks[1] = ballot2Marks;
        marks[2] = ballot3Marks;
        return marks;
    }

}
