package org.sysc4907.votingsystem.Elections;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;

/**
 * Controller class responsible for handling web requests and responses for endpoints relating to poll configuration.
 */
@Controller
public class ElectionController {

    private final ElectionService electionService;

    @Autowired
    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }


    @GetMapping("/create-election")
    public String showLoginForm(Model model) {
        model.addAttribute("electionForm", new ElectionForm());
        return "poll-configuration";
    }

    @PostMapping("/election")
    public String submitElectionForm(@Valid ElectionForm electionForm, BindingResult bindingResult,
            @RequestParam("voterKeys") MultipartFile voterKeys, Model model) {

        boolean validCandidates = electionService.validateCandidates(electionForm.getCandidates(), bindingResult);
        boolean validVoterKeys = electionService.validateVoterKeys(voterKeys, bindingResult);
        boolean isValidDateTime = electionService.validateDateTime(electionForm, bindingResult);

        if (!validCandidates || !validVoterKeys || !isValidDateTime || bindingResult.hasErrors()) {
            return "poll-configuration";
        }

        model.addAttribute("name", electionForm.getName());
        model.addAttribute("startDate", electionForm.getStartDate());
        model.addAttribute("startTime", electionForm.getStartTime());
        model.addAttribute("endDate", electionForm.getEndDate());
        model.addAttribute("endTime", electionForm.getEndTime());
        List<String> splitCandidates = Arrays.asList(electionForm.getCandidates().split("\\r?\\n"));
        model.addAttribute("candidates", splitCandidates);
        model.addAttribute("voterKeys", electionForm.getVoterKeys());

        electionService.createElection(electionForm);

        return "successful-poll-config";
    }
    @GetMapping("/view-election-details")
    public String showElectionDetailsPage(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");

        model.addAttribute("isLoggedIn", username != null);
        model.addAttribute("username", username);

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        if (electionService.electionIsConfigured()) {
            Election election = electionService.getElection();
            model.addAttribute("election", election);
            boolean preElection = election.START_DATE.isAfter(currentDate) || (election.START_DATE.isEqual(currentDate) && election.START_TIME.isAfter(currentTime));
            model.addAttribute("preElection", preElection);
            if (preElection){
                model.addAttribute("currentCountdown", election.getElectionCountdown());
            }
            boolean postElection = ! preElection && election.END_DATE.isBefore(currentDate) || (election.END_DATE.isEqual(currentDate) && election.END_TIME.isBefore(currentTime));
            model.addAttribute("postElection", postElection);
        } else {
            model.addAttribute("errorMessage", "No poll has been configured yet!");
        }
        return "election-details";
    }

}