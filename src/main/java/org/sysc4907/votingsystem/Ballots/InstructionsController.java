package org.sysc4907.votingsystem.Ballots;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InstructionsController {

    @GetMapping("/instructions")
    public String showInstructionsPage() {
        return "instructions"; // Points to a Thymeleaf template named instructions.html
    }
}