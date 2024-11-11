package org.sysc4907.votingsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sysc4907.votingsystem.Accounts.Account;
import org.sysc4907.votingsystem.Accounts.AccountRepository;
import org.sysc4907.votingsystem.Accounts.VoterAccount;
import org.sysc4907.votingsystem.PollConfiguration.PollConfigurationService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Service class responsible for logic of processing account registration.
 *
 * @author Jasmine Gad El Hak
 * @version 1.0
 */
@Service
public class RegistrationService {
    @Autowired
    private AccountRepository accountRepository;

    /**
     * Validates sign-in key against list of valid sign-in keys associated with configured election.
     *
     * @param key - the sign in key provided by the user
     * @return - true if sign in key is valid for current election, otherwise false.
     */
    public boolean validateSignInKey(String key){
        HashSet<String> signInKeys = new HashSet<>(); // TODO change to real list of poll sign in keys
        if (signInKeys.contains(key)) {
            signInKeys.remove(key);
            return true;
        }
        return false;

    }

    /**
     * Selects a blank account at random to use for account registration
     *
     * @return blank account
     */
    private Account selectRandomBlankAccount() {
        // TODO select randomly from list of available accounts

        return new VoterAccount();
    }

    /**
     * Registers a new account with the provided credentials.
     *
     * @param username - user-provided username
     * @param password - user-provided password
     * @return // TODO update once complete
     */
    public Account registerAccount(String username, String password) {
        Account newAccount = selectRandomBlankAccount();
        newAccount.setUserName(username);
        newAccount.setPassword(password);
        accountRepository.save(newAccount);
        // TODO admin accounts
        return newAccount;
    }
}