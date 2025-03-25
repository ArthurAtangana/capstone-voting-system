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

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);
        if (electionService.electionIsConfigured()) {
            Election election = electionService.getElection();
            model.addAttribute("electionName", election.NAME);
        }
        return "home-page";
    }


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
        if (electionService.electionIsConfigured()) {
            Election election = electionService.getElection();
            model.addAttribute("electionName", election.NAME);
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

    /**
     * Handles login requests by comparing the provided username and password
     * with the stored credentials, and redirects to the appropriate page based on the authentication result.
     *
     * @param userName The username provided by the user.
     * @param password The password provided by the user.
     * @param model The model object used to pass data to the view.
     * @return name of the template to display based on authentication outcome
     */
//    @PostMapping("/login")
//    public String compare(@RequestParam("userName") String userName,
//                          @RequestParam("password") String password, Model model, HttpSession session) {
//
//        model.addAttribute("username", userName);
//        AuthenticationService.Response response = authenticationService.authenticate(userName, password);
//        switch (response){
//            case ADMIN_AUTH_SUCCESS -> {
//                session.setAttribute("username", userName);
//                session.setAttribute("accountType", "admin");
//                return "redirect:/home";}
//            case VOTER_AUTH_SUCCESS -> {
//                session.setAttribute("username", userName);
//                session.setAttribute("accountType", "voter");
//                return "redirect:/home";}
//            case RATE_LIMIT_EXCEEDED -> {
//                model.addAttribute("errorMessage", "Too many failed attempts. Please try again in 1 minute.");
//                return "login-page";}
//            default -> {
//                model.addAttribute("errorMessage", authenticationService.getRateLimitMessage(userName));
//                return "login-page";}
//        }
//    }

//    @GetMapping("/logout")
//    public String logout(Model model, HttpSession session) {
//        session.setAttribute("username", null);
//        session.setAttribute("validKey", null);
//        // Redirect to the home page
//        return "redirect:/home";
//    }
}
