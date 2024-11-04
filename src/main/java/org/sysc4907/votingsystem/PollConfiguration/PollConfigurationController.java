package org.sysc4907.votingsystem.PollConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
public class PollConfigurationController {
    @Autowired
    private PollConfigurationService pollConfigurationService;

    @GetMapping("/create-election")
    public String showLoginForm() {
        return "poll-configuration";
    }

    @PostMapping("/election")
    public String compare(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                          @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                          @RequestParam("name") String name,
                          @RequestParam("candidates") String candidates,
                          Model model) {

        model.addAttribute("name", name);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        List<String> splitCandidates = Arrays.asList(candidates.split("\\r?\\n"));
        model.addAttribute("candidates", splitCandidates);

        return pollConfigurationService.validatePollConfiguration(startDate, endDate, name, candidates);
    }
}