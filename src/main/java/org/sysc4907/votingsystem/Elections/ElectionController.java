package org.sysc4907.votingsystem.Elections;

import jakarta.servlet.http.HttpSession;
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
    @GetMapping("/view-election-details")
    public String showElectionDetailsPage(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            model.addAttribute("isLoggedIn", false);
        }
        model.addAttribute("isLoggedIn", true);


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
        return "election-details-orig";
    }

}