package org.sysc4907.votingsystem.Ballots;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        model.addAttribute("threeBallot", threeBallot);
        // Extract the IDs from the ballots
        List<Map<String, Object>> attributes = threeBallot.getBallotAttributes();
        model.addAttribute("attributes", attributes);

        for (Map<String, Object> attribute : attributes) {
            System.out.println("Ballot: ");
            for (Map.Entry<String, Object> entry : attribute.entrySet()) {
                // Print the markableBoxes array in a readable format
                if (entry.getKey().equals("markableBoxes")) {
                    boolean[] markValues = (boolean[]) entry.getValue(); // Assuming it's a boolean array
                    String marksReadable = Arrays.toString(markValues); // Converts the array to a string representation
                    System.out.println("Markable Boxes: " + marksReadable);
                } else {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
            }
        }

        model.addAttribute("message", "hey");
        model.addAttribute("firstBallotMarks", threeBallot.getFirstBallot().getMarkValues());
        model.addAttribute("secondBallotMarks", threeBallot.getSecondBallot().getMarkValues());
        model.addAttribute("thirdBallotMarks", threeBallot.getThirdBallot().getMarkValues());
        return "three-ballot";
    }

    @GetMapping("/threeBallot")
    public String threeBallot(Model model) {
        List<String> candidates = electionService.getElection().getCandidates();
        ThreeBallot threeBallot = new ThreeBallot(candidates);
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
