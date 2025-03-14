package org.sysc4907.votingsystem.Elections;

import org.json.JSONArray;
import org.springframework.core.env.Environment;
import org.sysc4907.votingsystem.generators.BallotIdGenerator;
import org.sysc4907.votingsystem.generators.CandidateOrderGenerator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.*;
import java.util.*;

public class Election {
    private boolean fabricEnabled = false;

    private int numberOfVotes;
    private Long id;

    public final LocalDateTime START_DATE_TIME;
    public final LocalDateTime END_DATE_TIME;
    public final String NAME;

    private final List<String> candidates;
    public boolean votingOpen = true;
    private final Tally tallier;

    private final BallotIdGenerator ballotIdGenerator = new BallotIdGenerator();
    private final CandidateOrderGenerator candidateOrderGenerator;

    private final Set<Integer> voterKeys;

    public Election(Environment environment, LocalDateTime startDateTime, LocalDateTime endDateTime, String name, List<String> candidates, Set<Integer> voterKeys) {
        START_DATE_TIME = startDateTime;
        END_DATE_TIME = endDateTime;
        NAME = name;
        this.candidates = List.copyOf(candidates);
        this.voterKeys = voterKeys;
        tallier = new Tally(environment, candidates.size());
        this.candidateOrderGenerator = new CandidateOrderGenerator(candidates.size());
        this.numberOfVotes = 0;
        fabricEnabled = environment.getProperty("fabric.enabled", Boolean.class, true);
    }

    public Election() {
        START_DATE_TIME = null;
        END_DATE_TIME = null;
        NAME = "";
        candidates = new ArrayList<>();
        voterKeys = new HashSet<>();
        tallier = new Tally();
        candidateOrderGenerator = new CandidateOrderGenerator(0);
    }

    //init things if needed

    public void startElection() {}
    public void endElection() {
        votingOpen = false;

        // get keys to decrypt candidate order on ballots
        // count votes


        List<Integer> FinalTally = tallier.getTallyOfVotes();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public int getNumberOfVotes() { // TODO determine this value dynamically, fetching from block chain
        // For now, I will just have a field for this value
        // This is just a placeholder for obtaining the real value, simply to allow the number of votes to be displayed on the details page
        return numberOfVotes;
    }

    public void setNumberOfVotes(int numberOfVotes) { // TODO remove me once dynamic retreival of total number of votes is implemented
        this.numberOfVotes = numberOfVotes;
    }

    public BallotIdGenerator getBallotIdGenerator() {
        return ballotIdGenerator;
    }

    public CandidateOrderGenerator getCandidateOrderGenerator() {
        return candidateOrderGenerator;
    }

    public List<String> getCandidates() {
        return candidates;
    }

    public int getNumberOfVoterKeys() {
        return voterKeys.size();
    }
    public Set<Integer> getVoterKeys() {
        return voterKeys;
    }

    /**
     *
     * @return map of candidates and their total number of votes.
     */
    public Map<String, Integer> getVotingResults() { // TODO fetch real results based on the tally of votes stored on block chain
        // Steps of future implementation:
        // should verify that election is closed / past before attempting to acquire results
        // retreive the tallies for each candidate
        // construct map to return the results

        // TODO remove dummy value return (added for purpose of testing election details page)
        Map<String, Integer> candiateTallies = new HashMap<>(candidates.size());
        int tally = 12345;
        for (String candidate : candidates) {
            candiateTallies.put(candidate, tally);
            tally += 56789;
        }
        return candiateTallies;
    }

    /**
     * Constructs a string to display the current countdown until the start of the election.
     * The string form is _y _m _d _h _m (i.e., corresponding to year month day hour minutes, with 0 values ommited).
     *
     * @return string of current countdown to the start of the election.
     */
    public String getElectionCountdown() {
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime start = START_DATE_TIME;

        // Calculate the countdown
        if (current.isBefore(start)) {
            Period dateDiff = Period.between(current.toLocalDate(), start.toLocalDate());
            Duration timeDiff = Duration.between(current.toLocalTime(), start.toLocalTime());

            if (timeDiff.isNegative()) {
                // Adjust for negative time difference (i.e., cross midnight)
                dateDiff = dateDiff.minusDays(1);
                timeDiff = timeDiff.plusDays(1);
            }

            long hours = timeDiff.toHours();
            long minutes = timeDiff.minusHours(hours).toMinutes();

            StringBuilder countdown = new StringBuilder();
            if (dateDiff.getYears() > 0) {
                countdown.append(dateDiff.getYears()).append("y ");
            }
            if (dateDiff.getMonths() > 0) {
                countdown.append(dateDiff.getMonths()).append("m ");
            }
            if (dateDiff.getDays() > 0) {
                countdown.append(dateDiff.getDays()).append("d ");
            }
            if (hours > 0) {
                countdown.append(hours).append("h ");
            }
            if (minutes > 0) {
                countdown.append(minutes).append("m");
            }
            return countdown.toString();
        }
        return null;
    }

    private JSONArray getAllTransactions() {
        String responseData = "";
        if (!fabricEnabled) {
            String jsonString = "[" +
                    "{\"ballotId\":\"1\",\"ballotMarks\":\"[true,false,true]\",\"candidateOrder\":\"[1, 0, 2]\",\"ring\":\"31\"}," +
                    "{\"ballotId\":\"2\",\"ballotMarks\":\"[true,true,false]\",\"candidateOrder\":\"[1, 0, 2]\",\"ring\":\"31\"}," +
                    "{\"ballotId\":\"3\",\"ballotMarks\":\"[false,false,false]\",\"candidateOrder\":\"[1, 0, 2]\",\"ring\":\"31\"}," +
                    "{\"ballotId\":\"4\",\"ballotMarks\":\"[true,true,true]\",\"candidateOrder\":\"[1, 0, 2]\",\"ring\":\"31\"}," +
                    "{\"ballotId\":\"5\",\"ballotMarks\":\"[false,true,false]\",\"candidateOrder\":\"[1, 0, 2]\",\"ring\":\"31\"}," +
                    "{\"ballotId\":\"6\",\"ballotMarks\":\"[false,false,false]\",\"candidateOrder\":\"[1, 0, 2]\",\"ring\":\"31\"}]";
            System.out.println("WARNING: using mocked blockchain response!");
            return new JSONArray(jsonString);
        }
        try {
            // Specify the endpoint URL
            String endpoint = "http://localhost:8080/fabric/evaluate?function=GetAllBallots";

            // Create a URL object
            URL url = new URL(endpoint);

            // Open a connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method (GET, POST, etc.)
            connection.setRequestMethod("GET");

            // Set request headers if needed
            connection.setRequestProperty("Accept", "application/json");

            // Get the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Store the response in a variable
                responseData = response.toString();

                // Print the response
                System.out.println("Response: " + responseData);
            } else {
                System.out.println("GET request failed. Response code: " + responseCode);
            }

            // Disconnect the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray(responseData);

    }

    public List<Integer> tallyOfVotes(List<java.security.PrivateKey> privateKeys) {
        return tallier.tallyVotes(getAllTransactions(), privateKeys);
    }

    public int numVotesCast() {
        return tallier.totalNumVotes(getAllTransactions());
    }
}
