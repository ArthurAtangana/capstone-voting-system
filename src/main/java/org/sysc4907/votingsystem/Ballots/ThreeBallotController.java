package org.sysc4907.votingsystem.Ballots;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.*;

@Controller
public class ThreeBallotController {

    @GetMapping("/threeBallot")
    public String threeBallot(@RequestParam String candidates, Model model){
        // Split by comma, then collect into a List
        List<String> candidatesAsList = Arrays.stream(candidates
                        .split(","))
                .map(String::trim)  // Trim any extra spaces
                .toList();

        ThreeBallot threeBallot = new ThreeBallot(candidatesAsList);
        model.addAttribute("threeBallot", threeBallot);
        model.addAttribute("firstBallotMarks", threeBallot.getFirstBallot().getMarkValues());
        model.addAttribute("secondBallotMarks", threeBallot.getSecondBallot().getMarkValues());
        model.addAttribute("thirdBallotMarks", threeBallot.getThirdBallot().getMarkValues());
        return "three-ballot";
    }
}
