package org.sysc4907.votingsystem.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CandidateOrderGenerator {
    private final int numberOfCandidates;

    public CandidateOrderGenerator(int numberOfCandidates) {
        this.numberOfCandidates = numberOfCandidates;
    }

    public List<Integer> generateRandomCandidateOrder() {
        List<Integer> numbers = new ArrayList<>();

        // Add numbers from 0 to n to the list
        for (int i = 0; i < numberOfCandidates; i++) {
            numbers.add(i);
        }

        // Shuffle the list to get a random order
        Collections.shuffle(numbers);



        return numbers;
    }
}
