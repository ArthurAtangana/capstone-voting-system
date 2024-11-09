package org.sysc4907.votingsystem.Elections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Controller
public class ElectionController {

    private final ElectionService electionService;

    @Autowired
    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping
    public Election getElectionDetails() {
        return electionService.getElection();
    }

    @GetMapping("/create-election")
    public String showLoginForm() {
        return "poll-configuration";
    }

    @PostMapping("/election")
    public String compare(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                          @RequestParam("startTime") @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
                          @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                          @RequestParam("endTime") @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
                          @RequestParam("name") String name,
                          @RequestParam("candidates") String candidates,
                          @RequestParam("voterKeys") MultipartFile voterKeys,
                          Model model) {

        model.addAttribute("name", name);
        model.addAttribute("startDate", startDate);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endDate", endDate);
        model.addAttribute("endTime", endTime);
        List<String> splitCandidates = Arrays.asList(candidates.split("\\r?\\n"));
        model.addAttribute("candidates", splitCandidates);

        if (electionService.validateAndConfigurePoll(
                startDate, startTime, endDate, endTime, name, candidates, voterKeys) == true) {
            return "successful-poll-config";
        }
        return "unsuccessful-poll-config";
    }
}