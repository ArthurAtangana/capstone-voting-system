package org.sysc4907.votingsystem.Registration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.sysc4907.votingsystem.Accounts.Account;
import org.sysc4907.votingsystem.Accounts.AccountService;
import org.sysc4907.votingsystem.Elections.ElectionService;

/**
 * Controller class responsible for handling web requests and responses for endpoints relating to account registration.
 *
 * @author Jasmine Gad El Hak
 * @version 1.0
 */
@Controller
public class RegistrationController {
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private ElectionService electionService;

    @GetMapping("/registration-key")
    public String showRegistrationSignInKeyPage(Model model) {
        if (!electionService.electionIsConfigured()) {
            model.addAttribute("errorMessage", "No poll has been configured yet!");
            return "login-page";
        }
        model.addAttribute("election", electionService.getElection());
        return "registration-key-page";
    }

    @GetMapping("/register-credentials")
    public String showRegistrationCredentialsPage(HttpSession session, Model model) {
        if (!electionService.electionIsConfigured()) {
            model.addAttribute("errorMessage", "No poll has been configured yet!");
            return "login-page";
        }
        model.addAttribute("election", electionService.getElection());
        if (session.getAttribute("validRegKey") != null) return "registration-credentials-page";
        return "redirect:/home";
    }

    @PostMapping("/registration-key")
    public String submitKey(@RequestParam("registrationKey") Integer key, Model model, HttpSession session) {
        if (registrationService.submitSignInKey(key)) {
            session.setAttribute("validRegKey", true);
            return "redirect:/register-credentials";
        } else {
            model.addAttribute("errorMessage", "Invalid sign-in key. Please try again.");
        }
        return "registration-key-page";
    }

    @PostMapping("/register-credentials")
    public String createAccount(@RequestParam("userName") String userName,
                                @RequestParam("password") String password, HttpSession session, HttpServletRequest request, Model model) {

        RegistrationService.Response registrationResponse;
        try {
            registrationResponse = registrationService.submitAccountCredentials(userName, password);
        } catch (AccountService.WeakPasswordException w) {
            model.addAttribute("WeakPasswordErrorMessage", w.getMessage());
            model.addAttribute("repeatUserName", userName);
            model.addAttribute("election", electionService.getElection());
            return "registration-credentials-page";
        }


        if (registrationResponse != RegistrationService.Response.REG_FAILED) {
            session.removeAttribute("validRegKey");

            // Registered user is already authenticated, so they're redirected to the home page rather than login.
            try {
                request.login(userName, password);
            } catch(ServletException ex) {
                // fail to authenticate
                System.out.println("Authentication of newly registered user failed");
            }

            switch (registrationResponse) {
                case VOTER_REG_SUCCESS:
                    session.setAttribute("accountType", "voter");
                    break;
                case ADMIN_REG_SUCCESS:
                    session.setAttribute("accountType", "admin");
                    break;
                default:
                    throw new RuntimeException("Unexpected response from registration service: " + registrationResponse.name());
            }
            return "redirect:/home";

        } else {
            return "redirect:/register-credentials";
        }


    }
}
