package org.sysc4907.votingsystem;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sysc4907.votingsystem.Accounts.AccountService;
import org.sysc4907.votingsystem.Elections.Election;
import org.sysc4907.votingsystem.Elections.ElectionService;
import org.sysc4907.votingsystem.Elections.Tally;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TallyTest {
    Tally tally;
    String jsonString = "[{\"ballotId\":\"1\",\"ballotMarks\":\"[true,false]\",\"candidateOrder\":\"10\",\"ring\":\"31\"}," +
                        "{\"ballotId\":\"2\",\"ballotMarks\":\"[true,true]\",\"candidateOrder\":\"10\",\"ring\":\"31\"}," +
                        "{\"ballotId\":\"3\",\"ballotMarks\":\"[false,false]\",\"candidateOrder\":\"10\",\"ring\":\"31\"}]";
    JSONArray mockTransactions = new JSONArray(jsonString);
    List<java.security.PrivateKey> privateKeys = new ArrayList<>();
    List<String> candidateList = new ArrayList<>(Arrays.asList("foo", "bar"));
    Tally.CastBallot ballot1 = new Tally.CastBallot(1, new ArrayList<>(Arrays.asList(true, false)), new ArrayList<>(Arrays.asList(1, 0)), new ArrayList<>());
    Tally.CastBallot ballot2 = new Tally.CastBallot(2, new ArrayList<>(Arrays.asList(true, true)), new ArrayList<>(Arrays.asList(1, 0)), new ArrayList<>());
    Tally.CastBallot ballot3 = new Tally.CastBallot(3, new ArrayList<>(Arrays.asList(false, false)), new ArrayList<>(Arrays.asList(1, 0)), new ArrayList<>());

    public TallyTest() throws JSONException {
    }

    @BeforeEach
    public void setUpBeforeClass() {
        tally = new Tally(candidateList.size());
    }

    @Test
    public void testNumVotesCast() {
        assertEquals(1, tally.totalNumVotes(mockTransactions));
    }

    @Test
    public void testTally() {
        List<Integer> totals = tally.tallyVotes(mockTransactions, privateKeys);
        assertEquals(2, totals.size());
        assertEquals(1, totals.get(0));
        assertEquals(0, totals.get(1));
    }

    @Test
    public void testGetCastBallot() throws JSONException {
        System.out.println(mockTransactions.length());
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
