package org.sysc4907.votingsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/login-page")
    public String showLoginForm() {
        return "login-page";
    }

    @PostMapping("/login")
    public String compare(@RequestParam("userName") String userName,
                          @RequestParam("password") String password, Model model) {

        model.addAttribute("name", userName);

        boolean success = authenticationService.authenticate(userName, password);

        if (success) {
            return "successful-login";
        } else {
            return "unsuccessful-login";
        }
    }
}
