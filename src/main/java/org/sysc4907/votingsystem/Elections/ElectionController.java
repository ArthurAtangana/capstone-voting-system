package org.sysc4907.votingsystem.Elections;

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
        if (!validCandidates) {
            return "poll-configuration";
        }

        boolean validVoterKeys = electionService.validateVoterKeys(voterKeys, bindingResult);
        if (!validVoterKeys) {
            return "poll-configuration";
        }

        boolean isValidDateTime = electionService.validateDateTime(electionForm, bindingResult);
        if (!isValidDateTime) {
            return "poll-configuration";
        }

        if (bindingResult.hasErrors()) {
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
}