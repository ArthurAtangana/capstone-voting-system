package org.sysc4907.votingsystem.Accounts;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.sysc4907.votingsystem.Registration.SignInKeyService;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
/**
 * Service class that handles account-related operations such as assigning blank accounts to users,
 * configuring new accounts with usernames and passwords, and saving those accounts to the repository.
 *
 * <p>This class interacts with the {@link AccountRepository} to persist accounts and uses a {@link SignInKeyService}
 * to validate keys before assigning blank accounts.</p>
 *
 * @author Jasmine Gad El Hak
 * @version 1.0
 */
@Service
public class AccountService {
    private boolean activeRegistration = false;

    private SignInKeyService signInKeyService;
    private UserDetailsManager userDetailsManager;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AccountService(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Configures the sign-in key service to use the provided voter keys.
     * @param voterKeySet the set of registration keys for an election
     */
    public void initAccountService(Set<Integer> voterKeySet){
        signInKeyService = new SignInKeyService(voterKeySet);
    }

    /**
     * Saves a new account with provided username and password.
     *
     * @param username the username to set for the account
     * @param password the password to set for the account
     * @return true if the account was successfully configured and saved, otherwise false
     */
    public boolean configureAndSaveNewAccount(String username, String password) {
        if (username.isEmpty() || password.isEmpty() || userDetailsManager.userExists(username) || !activeRegistration) {
            return false;
        }
        isVeryStrongPassword(password); // throws an exception if password is not strong! (caught in controller)
        UserDetails user = User.withUsername(username)
                .password(passwordEncoder.encode(password))
                .roles("VOTER")
                .build();

        userDetailsManager.createUser(user);
        activeRegistration = false;

        return true;
    }

    /**
     * Marks a given registration key as 'used' so it cannot be re-used for another account.
     *
     * @param key is the registration key
     * @return true if key is a valid unused registration key, otherwise false
     */

    public boolean markKeyAsUsed(Integer key) {
        activeRegistration = signInKeyService.markKeyAsUsed(key); // true if key is valid
        return activeRegistration;
    }

    public static boolean isVeryStrongPassword(String password) {
        Zxcvbn passwordStrengthEstimator = new Zxcvbn();
        Strength strength = passwordStrengthEstimator.measure(password);
        if (strength.getScore() == 4 || strength.getScore() == 3) return true; // 4 is a very strong password, 3 is strong
        System.out.println(strength.getPassword());
        String warning = strength.getFeedback().getWarning();
        String feedback = strength.getFeedback().getSuggestions().toString();
        throw new WeakPasswordException(warning.isEmpty() ? feedback : warning + ", " + feedback);
    }

    public Account getAccountByUsername(String username) {
        return accountRepository.findById(username).orElse(null);
    }
  
    public static class WeakPasswordException extends RuntimeException {
        public WeakPasswordException(String message) {
            super(message);
        }
    }

    public void save(VoterAccount account) {
        accountRepository.save(account);
    }
}
