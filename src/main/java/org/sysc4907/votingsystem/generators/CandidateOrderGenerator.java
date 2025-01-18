package org.sysc4907.votingsystem.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CandidateOrderGenerator {
    private int numberOfCandidates;

    public CandidateOrderGenerator(int numberOfCandidates) {
        this.numberOfCandidates = numberOfCandidates;
    }

    public int generateRandomCandidateOrder() {
        List<Integer> numbers = new ArrayList<>();

        // Add numbers from 0 to n to the list
        for (int i = 0; i < numberOfCandidates; i++) {
            numbers.add(i);
        }

        // Shuffle the list to get a random order
        Collections.shuffle(numbers);

        // Convert the list to a single integer
        int result = 0;
        for (int num : numbers) {
            result = result * 10 + num;
        }

        return result;
    }

    public int generateEncryptedOrder(int order) {
        return 0;
    }

    public int generateDecryptedOrder(int order) {return 0;}

}
