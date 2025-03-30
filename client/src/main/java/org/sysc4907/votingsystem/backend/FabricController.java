package org.sysc4907.votingsystem.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fabric")
@ConditionalOnProperty(name = "fabric.enabled", havingValue = "true")
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
    public String submitTransaction(@RequestBody Map<String, Object> payload) {
        try {
            // Extract function and args from the request body
            String function = (String) payload.get("function");
            List<String> args = (List<String>) payload.get("args");
            return fabricGatewayService.submitTransaction(function, args.toArray(new String[args.size()]));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
