package org.sysc4907.votingsystem.Authentication;

import com.google.api.Http;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.sysc4907.votingsystem.Elections.Election;
import org.sysc4907.votingsystem.Elections.ElectionService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller class responsible for handling authentication-related requests.
 * This class provides endpoints for login, and showing the home page.
 *
 * @author Victoria Malouf
 * @author Jasmine Gad El Hak
 */
@Controller
public class AuthenticationController {
    @Autowired
    private ElectionService electionService;

    /**
     * Displays the login page of the application.
     *
     * @return name of login page template
     */
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                Model model, HttpSession session) {
        if (error != null) {
            model.addAttribute("errorMessage", session.getAttribute("errorMessage"));
        }
        return "login-page";
    }


    /**
     * Displays the home page of the application.
     *
     * @return name of home page template
     */
    @GetMapping("/home")
    public String showHomePage(Model model, Authentication authentication) {
        LocalDateTime now = LocalDateTime.now();
        String electionStatus;
        String dateTimeInfo;

        if (electionService.electionIsConfigured()) {
            Election election = electionService.getElection();
            model.addAttribute("electionName", election.NAME);

            if (now.isBefore(election.START_DATE_TIME)) {
                electionStatus = "Starts on";
                dateTimeInfo = election.START_DATE_TIME.format(DateTimeFormatter.ofPattern("MMMM d, yyyy @ h:mm a"));
            } else if (now.isAfter(election.END_DATE_TIME)) {
                electionStatus = "Ended on";
                dateTimeInfo = election.END_DATE_TIME.format(DateTimeFormatter.ofPattern("MMMM d, yyyy @ h:mm a"));
            } else {
                electionStatus = "Ends on";
                dateTimeInfo = election.END_DATE_TIME.format(DateTimeFormatter.ofPattern("MMMM d, yyyy @ h:mm a"));
            }

            model.addAttribute("electionStatus", electionStatus);
            model.addAttribute("dateTimeInfo", dateTimeInfo);
        } else {
            if (authentication.getAuthorities().toString().contains("VOTER")) {
                model.addAttribute("errorMessage", "No poll has been configured yet!");
            }
        }
        return "successful-login";
    }
}
