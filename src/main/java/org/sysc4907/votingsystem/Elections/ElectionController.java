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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public String showElectionForm(Model model, HttpSession session) {
        model.addAttribute("electionForm", new ElectionForm());
        return "poll-configuration";
    }

    @PostMapping("/election")
    public String submitElectionForm(@Valid ElectionForm electionForm, BindingResult bindingResult,
            @RequestParam("voterKeys") MultipartFile voterKeys) {

        boolean validCandidates = electionService.validateCandidates(electionForm.getCandidates(), bindingResult);
        boolean validVoterKeys = electionService.validateVoterKeys(voterKeys, bindingResult);
        boolean isValidDateTime = electionService.validateDateTime(electionForm, bindingResult);

        if (!validCandidates || !validVoterKeys || !isValidDateTime || bindingResult.hasErrors()) {
            return "poll-configuration";
        }
        electionService.createElection(electionForm);
        return "redirect:/home";
    }
    @GetMapping("/view-election-details")
    public String showElectionDetailsPage(Model model) {
        LocalDateTime now = LocalDateTime.now();

        if (electionService.electionIsConfigured()) {
            Election election = electionService.getElection();
            model.addAttribute("election", election);

            model.addAttribute("formattedStart", election.START_DATE_TIME.format(DateTimeFormatter.ofPattern("MMMM d, yyyy @ h:mm a")));
            model.addAttribute("formattedEnd", election.END_DATE_TIME.format(DateTimeFormatter.ofPattern("MMMM d, yyyy @ h:mm a")));
            model.addAttribute("currentCountdown", election.getElectionCountdown());
            model.addAttribute("postElection", election.END_DATE_TIME.isBefore(now));

            model.addAttribute("numVotesCast", electionService.getNumVotesCast());
                model.addAttribute("tally", electionService.getTally());

            System.out.println(election.END_DATE_TIME);

        } else {
            model.addAttribute("errorMessage", "No poll has been configured yet!");
        }
        return "election-details";
    }

}