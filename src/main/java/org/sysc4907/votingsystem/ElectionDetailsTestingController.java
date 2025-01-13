package org.sysc4907.votingsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.sysc4907.votingsystem.Elections.Election;
import org.sysc4907.votingsystem.Elections.ElectionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This controller allows you to easily configure elections that are in the past, ongoing or in the future.
 * It sets the election stored in the electionService directly, avoiding the limitations of validation, to be able to test these 3 scenarios.
 * Simply access /test-elections and select the election type you'd like to configure!
 * This is solely for testing purposes, and should not be accessible for users in the final release!
 */
@Controller // TODO eventually can be removed
public class ElectionDetailsTestingController {
    @Autowired
    private ElectionService electionService;

    @GetMapping("/test-elections")
    public String showTestElectionsPage() {
        return "test-elections";
    }

    /**
     * Configures a dummy election based on the requested type.
     * @param type - string indicating which timeframe the election is active ('past', 'ongoing' or 'future')
     * @return redirect to view election details
     */
    @GetMapping("/set-test-election")
    public String setTestElection(
            @RequestParam("type") String type) {
        Election election;
        List<String> candidateList = Arrays.asList("candidateX", "candidateY", "candidateZ");
        Set<Integer> keys =  new HashSet<>(Arrays.asList(123,456, 789));
        LocalDate today = LocalDate.now();
        switch (type) {
            case "past":
                election =  new Election (
                        LocalDateTime.of(today.minusDays(5), LocalTime.of(9, 0)),
                        LocalDateTime.of(today.minusDays(3), LocalTime.of(17, 0)),
                        "Past Election",
                        candidateList,
                        keys);
                break;
            case "ongoing":
                election =  new Election (
                        LocalDateTime.of(today.minusDays(1), LocalTime.of(9, 0)),
                        LocalDateTime.of(today.plusDays(1), LocalTime.of(17, 0)),
                        "Ongoing Election",
                        candidateList,
                        keys);
                break;
            case "upcoming":
                election =  new Election (
                        LocalDateTime.of(today.plusDays(5), LocalTime.of(9, 0)),
                        LocalDateTime.of(today.plusDays(6), LocalTime.of(17, 0)),
                        "Future Election",
                        candidateList,
                        keys);
                break;
            default:
                throw new IllegalArgumentException("Unknown election type");
        }
        election.setNumberOfVotes(207402);
        electionService.setElection(election);

        // Redirect to the election details page
        return "redirect:/view-election-details";
    }
}


