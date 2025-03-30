package org.sysc4907.votingsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.sysc4907.votingsystem.Elections.Election;
import org.sysc4907.votingsystem.Elections.ElectionForm;
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
        String name;
        LocalDate startDate;
        LocalTime startTime;
        LocalDate endDate;
        LocalTime endTime;

        switch (type) {
            case "past":
                name = "Past Election";
                startDate = today.minusDays(5);
                startTime = LocalTime.of(9, 0);
                endDate = today.minusDays(3);
                endTime = LocalTime.of(17, 0);
                break;
            case "ongoing":
                name = "Ongoing Election";
                startDate = today.minusDays(1);
                startTime = LocalTime.of(9, 0);
                endDate = today.plusDays(1);
                endTime = LocalTime.of(17, 0);
                break;
            case "upcoming":
                name = "Future Election";
                startDate = today.plusDays(5);
                startTime = LocalTime.of(9, 0);
                endDate = today.plusDays(6);
                endTime = LocalTime.of(17, 0);
                break;
            default:
                throw new IllegalArgumentException("Unknown election type");
        }

        ElectionForm electionForm = new ElectionForm();
        electionForm.setName(name);
        electionForm.setStartDate(startDate);
        electionForm.setStartTime(startTime);
        electionForm.setEndDate(endDate);
        electionForm.setEndTime(endTime);
        electionService.validateCandidates(String.join("\n", candidateList), null);
        electionService.setVoterKeysList(keys); // used to bypass validateVoterKeys since it requires a MultiPartFile
        electionService.createElection(electionForm);

        if (! type.equals("upcoming")) {
            electionService.getElection().setNumberOfVotes(207402);
        }

        // Redirect to the election details page
        return "redirect:/view-election-details";
    }
}


