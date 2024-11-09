package org.sysc4907.votingsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sysc4907.votingsystem.Accounts.Account;
import org.sysc4907.votingsystem.Accounts.AccountRepository;
import org.sysc4907.votingsystem.Accounts.VoterAccount;
import org.sysc4907.votingsystem.PollConfiguration.PollConfigurationService;

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
     * @return - // TODO update once complete
     */
    public boolean validateSignInKey(String key){
       // TODO verify sign in key from list of poll sign in keys
        return true;

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
