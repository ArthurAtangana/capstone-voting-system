package org.sysc4907.votingsystem.Authentication;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.sysc4907.votingsystem.Elections.Election;
import org.sysc4907.votingsystem.Elections.ElectionService;

/**
 * Controller class responsible for handling authentication-related requests.
 * This class provides endpoints for login, authentication, and showing the home page.
 *
 * @author Victoria Malouf
 * @author Jasmine Gad El Hak
 */
@Controller
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ElectionService electionService;

    /**
     * Displays the home page of the application.
     *
     * @return name of home page template
     */
    @GetMapping("/home")
    public String showHomePage(HttpSession session, Model model) {
        model.addAttribute("election", electionService.getElection());
        String username = (String) session.getAttribute("username");
        if (username != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("username", username);
            return "successful-voter-login";
        }
        return "home-page";
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
    @PostMapping("/login")
    public String compare(@RequestParam("userName") String userName,
                          @RequestParam("password") String password, Model model, HttpSession session) {

        model.addAttribute("username", userName);
        AuthenticationService.Response response = authenticationService.authenticate(userName, password);
        switch (response){
            case ADMIN_AUTH_SUCCESS -> {
                session.setAttribute("username", userName);
                return "successful-admin-login";}
            case VOTER_AUTH_SUCCESS -> {
                session.setAttribute("username", userName);
                model.addAttribute("isLoggedIn", true);
                if (electionService.electionIsConfigured()) {
                    Election election = electionService.getElection();
                    model.addAttribute("election", election);
                } else {
                    model.addAttribute("errorMessage", "No poll has been configured yet!");
                }
                return "successful-voter-login";
            }
            default -> { return "unsuccessful-login";}
        }
    }


}
