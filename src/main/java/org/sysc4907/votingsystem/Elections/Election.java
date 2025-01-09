package org.sysc4907.votingsystem.Elections;

import org.sysc4907.votingsystem.generators.BallotIdGenerator;
import org.sysc4907.votingsystem.generators.CandidateOrderGenerator;

import java.time.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Election {
    private Long id;
    public final LocalDate START_DATE;
    public final LocalDate END_DATE;
    public final LocalTime START_TIME;
    public final LocalTime END_TIME;
    public final String NAME;

    private final List<String> candidates;
    public boolean votingOpen = true;
    private final Tally tallier;

    private final BallotIdGenerator ballotIdGenerator = new BallotIdGenerator();
    private final CandidateOrderGenerator candidateOrderGenerator;

    private final Set<Integer> voterKeys;


    public Election(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, String name, List<String> candidates, Set<Integer> voterKeys) {
        START_DATE = startDate;
        START_TIME = startTime;
        END_DATE = endDate;
        END_TIME = endTime;
        NAME = name;
        this.candidates = List.copyOf(candidates);
        this.voterKeys = voterKeys;
        tallier = new Tally(candidates.size(), voterKeys.size());
        this.candidateOrderGenerator = new CandidateOrderGenerator(candidates.size());
    }

    public Election() {
        START_DATE = null;
        END_DATE = null;
        START_TIME = null;
        END_TIME = null;
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
    public String getElectionCountdown() {
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.of(START_DATE, START_TIME);

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
        return "";
    }
}
