package org.sysc4907.votingsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.sysc4907.votingsystem.Accounts.Account;
import org.sysc4907.votingsystem.Accounts.AdminAccount;
import org.sysc4907.votingsystem.Accounts.VoterAccount;

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
    @GetMapping("/register/sign-in-key")
    public String showRegistrationSignInKeyPage() { return "registration-sign-in-key-page";}

    @GetMapping("/register/credentials")
    public String showRegistrationCredentialsPage() { return "registration-credentials-page";}

    @PostMapping("/register/sign-in-key")
    public String submitKey(@RequestParam("signInKey") String key, Model model) {
        // TODO handle invalid key workflow
        if (registrationService.validateSignInKey(key)) {
            return "redirect:/register/credentials";
        }
        return "redirect:/register/sign-in-key";
    }

    @PostMapping("/register/credentials")
    public String createAccount(@RequestParam("userName") String userName,
                                @RequestParam("password") String password, Model model) {
        // TODO select random available account
        // TODO set credentials for the account
        // TODO store in backend
        Account newAccount = registrationService.registerAccount(userName, password);
        model.addAttribute("name", newAccount.getUserName());

        if (newAccount instanceof VoterAccount){
            return "successful-voter-login";
        } else if (newAccount instanceof AdminAccount) {
            return "successful-admin-login";
        } else {
            return "redirect:/register/credentials";
        }


    }
}
