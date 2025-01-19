package org.sysc4907.votingsystem.Elections;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.sysc4907.votingsystem.backend.FabricGatewayService;

import org.springframework.http.HttpHeaders;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    @Autowired
    private FabricGatewayService fabricGatewayService;


    @GetMapping("/create-election")
    public String showElectionForm(Model model) {
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
        electionService.createElection(electionForm);
        return "redirect:/home";
    }
    @GetMapping("/view-election-details")
    public String showElectionDetailsPage(Model model, HttpSession session) {

        String username = (String) session.getAttribute("username");

        model.addAttribute("isLoggedIn", username != null);
        model.addAttribute("username", username);

        LocalDateTime now = LocalDateTime.now();

        if (electionService.electionIsConfigured()) {
            Election election = electionService.getElection();
            model.addAttribute("election", election);

            model.addAttribute("formattedStart", election.START_DATE_TIME.format(DateTimeFormatter.ofPattern("MMMM d, yyyy @ h:mm a")));
            model.addAttribute("formattedEnd", election.END_DATE_TIME.format(DateTimeFormatter.ofPattern("MMMM d, yyyy @ h:mm a")));
            model.addAttribute("currentCountdown", election.getElectionCountdown());
            model.addAttribute("postElection", election.END_DATE_TIME.isBefore(now));

            System.out.println(model.getAttribute("isLoggedIn"));
            System.out.println(election.END_DATE_TIME);

        } else {
            model.addAttribute("errorMessage", "No poll has been configured yet!");
        }
        return "election-details";
    }

    @GetMapping("/ledger")
    public String showElectionLedger(Model model, HttpSession session) {

        /* Preserve election details here */

        String username = (String) session.getAttribute("username");

        model.addAttribute("isLoggedIn", username != null);
        model.addAttribute("username", username);

        LocalDateTime now = LocalDateTime.now();

        // Pull this out for scope
        Election election;

        if (electionService.electionIsConfigured()) {
            election = electionService.getElection();
            model.addAttribute("election", election);

            model.addAttribute("formattedStart", election.START_DATE_TIME.format(DateTimeFormatter.ofPattern("MMMM d, yyyy @ h:mm a")));
            model.addAttribute("formattedEnd", election.END_DATE_TIME.format(DateTimeFormatter.ofPattern("MMMM d, yyyy @ h:mm a")));
            model.addAttribute("currentCountdown", election.getElectionCountdown());
            model.addAttribute("postElection", election.END_DATE_TIME.isBefore(now));

            System.out.println(model.getAttribute("isLoggedIn"));
            System.out.println(election.END_DATE_TIME);

        } else {
            model.addAttribute("errorMessage", "No poll has been configured yet!");
        }

        /* GET the Ledger and Tabulate */

        // String holding all contents of ledger (JSON)
        String ledgerString;
        // Strings for call to FabricGatewayService
        String function = "GetAllBallots";
        String[] args = {};

        // Retrieve ledger string from HyperLedger
        try {
            ledgerString = fabricGatewayService.evaluateTransaction(function, args);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        model.addAttribute("ledgerString", ledgerString);

        // Convert the JSON string to a JSONArray for easier manipulation
        JSONArray ledgerJsonArray = new JSONArray(ledgerString);
        // Container to hold LedgerRecord objects for pass to model
        ArrayList<LedgerEntry> ledgerEntries = new ArrayList<>();

        // Populate
        for (int i = 0; i < ledgerJsonArray.length(); i++) {
            JSONObject ledgerTransaction = (JSONObject) ledgerJsonArray.get(i);
            // If valid number of fields, cast as LedgerRecord and add to array
            if (ledgerTransaction.length() == 4) {
                LedgerEntry lr = new LedgerEntry(
                        (String) ledgerTransaction.getString("ballotId"),
                        (String) ledgerTransaction.getString("ballotMarks"),
                        (String) ledgerTransaction.getString("candidateOrder"),
                        (String) ledgerTransaction.getString("ring"));
                ledgerEntries.add(lr);
            }
        }

        // Now, have an ArrayList of LedgerRecords we can pass to model
        model.addAttribute("ledgerEntries", ledgerEntries);

        /* File for Download */

        return "ledger-all-votes";
    }

    // Example endpoint for downloading a string as a JSON file
    @GetMapping("/download-ledger")
    public ResponseEntity<String> downloadJsonFile(Model model, HttpSession session) {

        /* GET the Ledger as a String */

        // String holding all contents of ledger (JSON)
        String ledgerString;

        // Strings for call to FabricGatewayService
        String function = "GetAllBallots";
        String[] args = {};

        // Retrieve ledger string from HyperLedger
        try {
            ledgerString = fabricGatewayService.evaluateTransaction(function, args);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: Failed to retrieve JSON content.", HttpStatus.BAD_REQUEST);
        }
        model.addAttribute("ledgerString", ledgerString);

        // Set headers for the file download
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=ledger.json");
        headers.add("Content-Type", "application/json");

        // Return the ResponseEntity with the JSON string
        ResponseEntity responseEntity = new ResponseEntity<>(ledgerString, headers, HttpStatus.OK);
        return responseEntity;
    }

}