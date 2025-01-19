package org.sysc4907.votingsystem;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.sysc4907.votingsystem.Elections.Tally;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class TallyTest {
    @Autowired
    private Environment environment;
    Tally tally;
    String jsonString = "[" +
            "{\"ballotId\":\"1\",\"ballotMarks\":\"[true,false,true]\",\"candidateOrder\":\"102\",\"ring\":\"31\"}," +
            "{\"ballotId\":\"2\",\"ballotMarks\":\"[true,true,false]\",\"candidateOrder\":\"102\",\"ring\":\"31\"}," +
            "{\"ballotId\":\"3\",\"ballotMarks\":\"[false,false,false]\",\"candidateOrder\":\"102\",\"ring\":\"31\"}," +
            "{\"ballotId\":\"4\",\"ballotMarks\":\"[true,true,true]\",\"candidateOrder\":\"102\",\"ring\":\"31\"}," +
            "{\"ballotId\":\"5\",\"ballotMarks\":\"[false,true,false]\",\"candidateOrder\":\"102\",\"ring\":\"31\"}," +
            "{\"ballotId\":\"6\",\"ballotMarks\":\"[false,false,false]\",\"candidateOrder\":\"102\",\"ring\":\"31\"}]";
    JSONArray mockTransactions = new JSONArray(jsonString);
    List<java.security.PrivateKey> privateKeys = new ArrayList<>();
    List<String> candidateList = new ArrayList<>(Arrays.asList("foo", "bar", "baz"));
    Tally.CastBallot ballot1 = new Tally.CastBallot(1, new ArrayList<>(Arrays.asList(true, false, true)), new ArrayList<>(Arrays.asList(1, 0, 2)), new ArrayList<>());
    Tally.CastBallot ballot2 = new Tally.CastBallot(2, new ArrayList<>(Arrays.asList(true, true, false)), new ArrayList<>(Arrays.asList(1, 0, 2)), new ArrayList<>());
    Tally.CastBallot ballot3 = new Tally.CastBallot(3, new ArrayList<>(Arrays.asList(false, false, false)), new ArrayList<>(Arrays.asList(1, 0, 2)), new ArrayList<>());

    public TallyTest() throws JSONException {
    }

    @BeforeEach
    public void setUpBeforeClass() {
        tally = new Tally(environment, candidateList.size());
    }

    @Test
    public void testNumVotesCast() {
        assertEquals(2, tally.totalNumVotes(mockTransactions));
    }

    @Test
    public void testTally() {
        List<Integer> totals = tally.tallyVotes(mockTransactions, privateKeys);
        assertEquals(3, totals.size());
        assertEquals(1, totals.get(0));
        assertEquals(1, totals.get(1));
        assertEquals(0, totals.get(2));
    }

    @Test
    public void testGetCastBallot() throws JSONException {
        List<Tally.CastBallot> ballots = new ArrayList<>();
        for (int i = 0; i < mockTransactions.length(); i++) {
            JSONObject jsonTransaction = mockTransactions.getJSONObject(i); // Get JSONObject at index i
            if (jsonTransaction.has("ballotId")) {
                // Assuming `tally.getCastBallotFromJSON(jsonTransaction, privateKeys)` is a valid method call
                Tally.CastBallot ballot = tally.getCastBallotFromJSON(jsonTransaction, privateKeys);
                ballots.add(ballot);
            }
        }
        assertEquals(ballot1, ballots.get(0));
        assertEquals(ballot2, ballots.get(1));
        assertEquals(ballot3, ballots.get(2));
    }
}
