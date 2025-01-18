package org.sysc4907.votingsystem.Ballots;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import org.sysc4907.votingsystem.Elections.ElectionService;
import org.sysc4907.votingsystem.FileHelper;
import org.sysc4907.votingsystem.LirisiCommandExecutor;

@Controller
public class ThreeBallotController {

    @Autowired
    private ElectionService electionService;

    @GetMapping("/threeBallotTest")
    public String threeBallotTest(Model model) {
        ThreeBallot threeBallot = new ThreeBallot(Arrays.asList("foo", "bar", "baz", "quux"));
        List<Map<String, Object>> attributes = threeBallot.getBallotAttributes();
        model.addAttribute("attributes", attributes);
        model.addAttribute("threeBallot", threeBallot);
        model.addAttribute("firstBallotMarks", threeBallot.getFirstBallot().getMarkValues());
        model.addAttribute("secondBallotMarks", threeBallot.getSecondBallot().getMarkValues());
        model.addAttribute("thirdBallotMarks", threeBallot.getThirdBallot().getMarkValues());
        return "three-ballot";
    }

    @GetMapping("/threeBallot")
    public String threeBallot(Model model, HttpSession session) {
        if (session.getAttribute("validKey") != null) { // if key has already been verified we don't need to display the pop-up again
            model.addAttribute("validKey", true); // adding this attribute will ensure it's not visible
        }

        List<String> candidates = electionService.getElection().getCandidates();
        ThreeBallot threeBallot = new ThreeBallot(candidates);
        List<Map<String, Object>> attributes = threeBallot.getBallotAttributes();
        model.addAttribute("electionName", electionService.getElection().NAME);
        model.addAttribute("attributes", attributes);
        model.addAttribute("threeBallot", threeBallot);
        model.addAttribute("firstBallotMarks", threeBallot.getFirstBallot().getMarkValues());
        model.addAttribute("secondBallotMarks", threeBallot.getSecondBallot().getMarkValues());
        model.addAttribute("thirdBallotMarks", threeBallot.getThirdBallot().getMarkValues());
        return "three-ballot";
    }

    @GetMapping("/instructions")
    public String showInstructionsPage() {
        return "instructions";
    }


    @PostMapping("/verify-signing-key")
    public String verifySigningKey(RedirectAttributes redirectAttributes, HttpSession session, @RequestParam("key")String key){
        LirisiCommandExecutor executor = new LirisiCommandExecutor();
        // Verify that private key belongs to "some ring member" before allowing the voter to proceed with candiate selection
        if (executor.verifySigningKey(key,
                LirisiCommandExecutor.DEFAULT_LIRISI_GENERATED_DIR + LirisiCommandExecutor.DEFAULT_SIGNING_KEY_FILENAME,
                LirisiCommandExecutor.DEFAULT_LIRISI_GENERATED_DIR + LirisiCommandExecutor.DEFAULT_AGGREGATED_RING_PUBLIC_KEYS_FILENAME)){
            session.setAttribute("validKey", true);
        }else{
            System.out.println("Couldn't generate signature");
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid signing key. Try again!");
        }
        return "redirect:/threeBallot";
    }

//    @PostMapping("submit-ballot-transactions")
//    @ResponseBody
//    public ResponseEntity<String> submitBallotTransactions(@RequestParam("ballot1") String ballot1Data, @RequestParam("ballot2")  String ballot2Data, @RequestParam("ballot3") String ballot3Data){
//        /* Ring signature component of the transaction */
//        String[] ballotData = {ballot1Data, ballot2Data, ballot3Data};
//
//        LirisiCommandExecutor executor = new LirisiCommandExecutor();
//        String signature;
//        for (String data : ballotData) {
//            System.out.println(FileHelper.getFileContents(LirisiCommandExecutor.DEFAULT_LIRISI_GENERATED_DIR + LirisiCommandExecutor.DEFAULT_SIGNING_KEY_FILENAME));
//            System.out.println(FileHelper.getFileContents(LirisiCommandExecutor.DEFAULT_LIRISI_GENERATED_DIR + LirisiCommandExecutor.DEFAULT_AGGREGATED_RING_PUBLIC_KEYS_FILENAME));
//            try {
//                signature = executor.signMessage(data,
//                        LirisiCommandExecutor.DEFAULT_LIRISI_GENERATED_DIR + LirisiCommandExecutor.DEFAULT_SIGNING_KEY_FILENAME,
//                        LirisiCommandExecutor.DEFAULT_LIRISI_GENERATED_DIR + LirisiCommandExecutor.DEFAULT_AGGREGATED_RING_PUBLIC_KEYS_FILENAME,
//                        "");
//                System.out.println("Generated signature:\n" + signature);
//            } catch (Exception e) {
//                return new ResponseEntity<>("The private key should have been verified prior but we still can't generate a signature.", HttpStatus.BAD_REQUEST);
//                //throw new RuntimeException("Could not generate signature, despite prior private key verification");
//            }
//            System.out.println("ballotData: " + ballot1Data);
//
//            // POST to blockchain... // TODO..
//        }
//        // Assuming everything works, you can return a success response
//        return ResponseEntity.ok().body("Ballot submitted successfully");
//    }

    @PostMapping("submit-ballot-transactions")
    @ResponseBody
    public ResponseEntity<String> submitBallotTransactions(@RequestBody Map<String, String> ballots) {
        String ballot1 = ballots.get("ballot1");
        String ballot2 = ballots.get("ballot2");
        String ballot3 = ballots.get("ballot3");

        String[] ballotData = {ballot1, ballot2, ballot3};

        LirisiCommandExecutor executor = new LirisiCommandExecutor();
        String signature;
        for (String data : ballotData) {
            try {
                signature = executor.signMessage(data,
                        LirisiCommandExecutor.DEFAULT_LIRISI_GENERATED_DIR + LirisiCommandExecutor.DEFAULT_SIGNING_KEY_FILENAME,
                        LirisiCommandExecutor.DEFAULT_LIRISI_GENERATED_DIR + LirisiCommandExecutor.DEFAULT_AGGREGATED_RING_PUBLIC_KEYS_FILENAME,
                        "");
                System.out.println("Generated signature:\n" + signature);
            } catch (Exception e) {
                return new ResponseEntity<>("The private key should have been verified prior but we still can't generate a signature.", HttpStatus.BAD_REQUEST);
                //throw new RuntimeException("Could not generate signature, despite prior private key verification");
            }
        }
        // Process the data
        return ResponseEntity.ok("Ballots received");
    }
}
