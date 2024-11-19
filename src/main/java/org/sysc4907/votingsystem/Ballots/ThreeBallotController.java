package org.sysc4907.votingsystem.Ballots;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;

@Controller
public class ThreeBallotController {

    @GetMapping("/threeBallot")
    public String threeBallot(Model model) {
        ThreeBallot threeBallot = new ThreeBallot(Arrays.asList("foo", "bar", "baz", "quux"));
        model.addAttribute("threeBallot", threeBallot);
        model.addAttribute("firstBallotMarks", threeBallot.getFirstBallot().getMarkValues());
        model.addAttribute("secondBallotMarks", threeBallot.getSecondBallot().getMarkValues());
        model.addAttribute("thirdBallotMarks", threeBallot.getThirdBallot().getMarkValues());
        return "three-ballot";
    }
}
