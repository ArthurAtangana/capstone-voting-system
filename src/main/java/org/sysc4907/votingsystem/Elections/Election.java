package org.sysc4907.votingsystem.Elections;

import org.sysc4907.votingsystem.generators.BallotIdGenerator;
import org.sysc4907.votingsystem.generators.CandidateOrderGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class Election {
    private Long id;
    public final LocalDate START_DATE;
    public final LocalDate END_DATE;
    public final LocalTime START_TIME;
    public final LocalTime END_TIME;
    public final String NAME;

    private final List<String> candidates;
    public final int NumberOfDecryptionKeys;
    public boolean votingOpen = true;
    private final Tally tallier;

    private final BallotIdGenerator ballotIdGenerator = new BallotIdGenerator();
    private final CandidateOrderGenerator candidateOrderGenerator;

    // List of voter keys or accounts?
    private final List<Integer> voterKeys;


    public Election(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, String name, List<String> candidates, int numberOfDecryptionKeys, List<Integer> voterKeys) {
        START_DATE = startDate;
        START_TIME = startTime;
        END_DATE = endDate;
        END_TIME = endTime;
        NAME = name;
        this.candidates = List.copyOf(candidates);
        NumberOfDecryptionKeys = numberOfDecryptionKeys;
        this.voterKeys = voterKeys;
        tallier = new Tally(candidates.size(), numberOfDecryptionKeys);
        this.candidateOrderGenerator = new CandidateOrderGenerator(candidates.size());
    }

    public Election() {
        START_DATE = null;
        END_DATE = null;
        START_TIME = null;
        END_TIME = null;
        NAME = "";
        candidates = new ArrayList<>();
        NumberOfDecryptionKeys = 0;
        voterKeys = new ArrayList<>();
        tallier = new Tally(0,0);
        candidateOrderGenerator = new CandidateOrderGenerator(0);
    }

    //init things if needed
    public void startElection() {}

    public void endElection() {
        votingOpen = false;

        // get keys to decrypt candidate order on ballots
        // count votes


        int[] FinalTally = tallier.getTallyOfVotes();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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
}
