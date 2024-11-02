package org.sysc4907.votingsystem.Elections;

import org.sysc4907.votingsystem.generators.BallotIdGenerator;
import org.sysc4907.votingsystem.generators.CandidateOrderGenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Election {
    private Long id;
    public final Date START_DATE;
    public final Date END_DATE;
    public final String NAME;

    private final List<String> candidates;
    public final int NumberOfDecryptionKeys;
    public boolean votingOpen = true;
    private final Tally tallier;

    private final BallotIdGenerator ballotIdGenerator = new BallotIdGenerator();
    private final CandidateOrderGenerator candidateOrderGenerator;

    // List of voter keys or accounts?


    public Election(Date startDate, Date endDate, String name, List<String> candidates, int numberOfDecryptionKeys) {
        START_DATE = startDate;
        END_DATE = endDate;
        NAME = name;
        this.candidates = List.copyOf(candidates);
        NumberOfDecryptionKeys = numberOfDecryptionKeys;
        tallier = new Tally(candidates.size(), numberOfDecryptionKeys);
        this.candidateOrderGenerator = new CandidateOrderGenerator(candidates.size());
    }

    public Election() {
        START_DATE = new Date();
        END_DATE = new Date();
        NAME = "";
        candidates = new ArrayList<>();
        NumberOfDecryptionKeys = 0;
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
