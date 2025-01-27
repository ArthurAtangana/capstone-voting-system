package org.sysc4907.votingsystem.Registration;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        return "registration-key-page";
    }

    @GetMapping("/register-credentials")
    public String showRegistrationCredentialsPage(HttpSession session) {
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
                                @RequestParam("password") String password, Model model, HttpSession session) {

        RegistrationService.Response registrationResponse = registrationService.submitAccountCredentials(userName, password);

        if (registrationResponse != RegistrationService.Response.REG_FAILED) {
            model.addAttribute("name", userName);
            session.removeAttribute("validRegKey");
            // redirect to the appropriate page according to the type of account.
            switch (registrationResponse) {
                case VOTER_REG_SUCCESS -> {
                    session.setAttribute("username", userName);
                    session.setAttribute("accountType", "voter");
                    return "redirect:/home";}
                case ADMIN_REG_SUCCESS -> {
                    session.setAttribute("username", userName);
                    session.setAttribute("accountType", "admin");
                    return "redirect:/home";}
                default ->  throw new RuntimeException("Unexpected response from registration service: " + registrationResponse.name());
            }
        } else {
            return "redirect:/register-credentials";
        }


    }
}
