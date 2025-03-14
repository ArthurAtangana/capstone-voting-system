package org.sysc4907.votingsystem.Ballots;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
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
        KeyPairGenerator keyGen;
        ThreeBallot threeBallot;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048); // Key size (2048 or higher is recommended)
            KeyPair keyPair = keyGen.generateKeyPair();

            threeBallot = new ThreeBallot(Arrays.asList("foo", "bar", "baz", "quux"), Collections.singletonList(keyPair.getPublic()));

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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
        String username = (String) session.getAttribute("username");
        if (username == null || ! electionService.electionIsConfigured()) {
            return "redirect:/home";
        }
        model.addAttribute("username", username );

        if (session.getAttribute("validKey") != null) { // if key has already been verified we don't need to display the pop-up again
            model.addAttribute("validKey", true); // adding this attribute will ensure it's not visible
        } else {
            // passing in one of the generated keys for auto-fill purposes (demo-only)
            model.addAttribute("autoFillKey", FileHelper.getFileContents("./lirisi-generated-files/priv/1.pem"));
        }

        List<String> candidates = electionService.getElection().getCandidates();
        ThreeBallot threeBallot = new ThreeBallot(candidates, electionService.getPublicOrderKeys());
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

    @PostMapping("/submit-ballot-transactions")
    @ResponseBody
    public ResponseEntity<String> submitBallotTransactions(@RequestBody Map<String, Object> payload, HttpSession session) {
        // Extract the 'ballot' field from the JSON payload
        String ballotData = (String) payload.get("ballot");
        LirisiCommandExecutor executor = new LirisiCommandExecutor();
        String signature;
        try {
            signature = executor.signMessage(ballotData,
                    LirisiCommandExecutor.DEFAULT_LIRISI_GENERATED_DIR + LirisiCommandExecutor.DEFAULT_SIGNING_KEY_FILENAME,
                    LirisiCommandExecutor.DEFAULT_LIRISI_GENERATED_DIR + LirisiCommandExecutor.DEFAULT_AGGREGATED_RING_PUBLIC_KEYS_FILENAME,
                    "");
                System.out.println("Generated signature: successfully\n");
        } catch (Exception e) {
            return new ResponseEntity<>("The private key should have been verified prior but we still can't generate a signature.", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(signature);
    }
}
