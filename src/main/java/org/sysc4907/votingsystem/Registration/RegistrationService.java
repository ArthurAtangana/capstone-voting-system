package org.sysc4907.votingsystem.Registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sysc4907.votingsystem.Accounts.Account;
import org.sysc4907.votingsystem.Accounts.AccountService;
import org.sysc4907.votingsystem.Elections.ElectionService;

import java.util.*;

/**
 * Service class responsible for logic of processing account registration.
 *
 * @author Jasmine Gad El Hak
 * @version 1.0
 */
@Service
public class RegistrationService {
    public enum Response {
        ADMIN_REG_SUCCESS,  // Registration successful for an admin user
        VOTER_REG_SUCCESS,  // Registration successful for a voter user
        REG_FAILED          // Registration failed
    }

    @Autowired
    private ElectionService electionService;
    public boolean submitSignInKey(Integer key) {
        if (! electionService.electionIsConfigured()) {
            System.out.println("Poll is not configured!");
            return false;
        }
        AccountService accountService = electionService.getAccountService();
        return accountService.markKeyAsUsed(key);
    }

    public Response submitAccountCredentials(String username, String password) {
        if (! electionService.electionIsConfigured()) {
            System.out.println("Poll is not configured!");
            return Response.REG_FAILED;
        }
        AccountService accountService = electionService.getAccountService();
        if (accountService.configureAndSaveNewAccount(username, password)) {
            return Response.VOTER_REG_SUCCESS; // TODO admin accounts
        }

        return Response.REG_FAILED;
    }
}
