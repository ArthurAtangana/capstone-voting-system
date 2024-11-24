package org.sysc4907.votingsystem.Elections;

import org.sysc4907.votingsystem.generators.BallotIdGenerator;
import org.sysc4907.votingsystem.generators.CandidateOrderGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Election {
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

    public Election(LocalDateTime startDateTime, LocalDateTime endDateTime, String name, List<String> candidates, Set<Integer> voterKeys) {
        START_DATE_TIME = startDateTime;
        END_DATE_TIME = endDateTime;
        NAME = name;
        this.candidates = List.copyOf(candidates);
        this.voterKeys = voterKeys;
        tallier = new Tally(candidates.size(), voterKeys.size());
        this.candidateOrderGenerator = new CandidateOrderGenerator(candidates.size());
    }

    public Election() {
        START_DATE_TIME = null;
        END_DATE_TIME = null;
        NAME = "";
        candidates = new ArrayList<>();
        voterKeys = new HashSet<>();
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

    public int getNumberOfVoterKeys() {
        return voterKeys.size();
    }
    public Set<Integer> getVoterKeys() {
        return voterKeys;
    }
}
