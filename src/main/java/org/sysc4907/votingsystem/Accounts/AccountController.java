package org.sysc4907.votingsystem.Accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/cast")
    public ResponseEntity<String> cast(@RequestParam("username") String username) {
        VoterAccount account = (VoterAccount) accountService.getAccountByUsername(username);
        if (account == null) {
            return ResponseEntity.badRequest().body("Invalid username.");
        }

        if (account.hasVoted()) {
            return ResponseEntity.badRequest().body("User has already voted.");
        }

        account.castVote();
        accountService.save(account); // Ensure the vote is persisted

        return ResponseEntity.ok("Vote successfully cast.");
    }

    @GetMapping("/hasVoted")
    public ResponseEntity<Boolean> hasVoted(@RequestParam("username") String username) {
        VoterAccount account = (VoterAccount) accountService.getAccountByUsername(username);
        if (account == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(account.hasVoted());
    }



}
