package org.sysc4907.votingsystem.Ballots;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.sysc4907.votingsystem.Elections.ElectionService;

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
    public String threeBallot(Model model) {
        List<String> candidates = electionService.getElection().getCandidates();
        ThreeBallot threeBallot = new ThreeBallot(candidates);
        List<Map<String, Object>> attributes = threeBallot.getBallotAttributes();
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
}
