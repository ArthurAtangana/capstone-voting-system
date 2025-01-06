package org.sysc4907.votingsystem.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fabric")
public class FabricController {

    @Autowired
    private FabricGatewayService fabricGatewayService;

    @GetMapping("/evaluate")
    public String evaluateTransaction(
            @RequestParam String function,
            @RequestParam(required = false) String[] args) {
        try {
            return fabricGatewayService.evaluateTransaction(function, args);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/submit")
    public String submitTransaction(
            @RequestParam String function,
            @RequestParam(required = false) String[] args) {
        try {
            return fabricGatewayService.submitTransaction(function, args);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
