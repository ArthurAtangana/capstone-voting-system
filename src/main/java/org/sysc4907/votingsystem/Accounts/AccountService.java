package org.sysc4907.votingsystem.Accounts;

import org.springframework.beans.factory.annotation.Autowired;
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
    private SignInKeyService signInKeyService;
    private List<VoterAccount> blankVoterAccounts;
    // private List<AdminAccount> blankAdminAccounts; // TODO
    private List<Account> registeredAccounts;

    @Autowired
    private  AccountRepository accountRepository;

    public void initAccountService(Set<Integer> voterKeySet){
        signInKeyService = new SignInKeyService(voterKeySet);

        int numberOfAccounts = voterKeySet.size();
        blankVoterAccounts = new ArrayList<>(numberOfAccounts);
        for (int i = 0; i < numberOfAccounts; i++) {
            blankVoterAccounts.add(new VoterAccount());
        }
        registeredAccounts = new ArrayList<>();
    }
    /**
     * Assigns a random blank account to a user if a valid sign-in key is provided.
     *
     * @param key the sign-in key provided by the user
     * @return optional selected account value if the key is valid, otherwise returns empty optional value
     */
    public Optional<Account> assignBlankAccount(Integer key) {
        if (signInKeyService.keyIsValid(key)) {
            SecureRandom secureRandom = new SecureRandom(); // SecureRandom is recommended as it generates non-deterministic random values based on cryptographic algorithms
            int randomIndex = secureRandom.nextInt(blankVoterAccounts.size());
            return Optional.of(this.blankVoterAccounts.get(randomIndex));
        }
        return Optional.empty();
    }

    /**
     * Configures and saves a new account with a username and password.
     *
     * @param account the account to be configured and saved (should be a blank account assigned by the account service)
     * @param username the username to set for the account
     * @param password the password to set for the account
     * @return true if the account was successfully configured and saved, otherwise false
     */
    public boolean configureAndSaveNewAccount(Account account, String username, String password) {
        if (username.isEmpty() || password.isEmpty() || registeredAccounts.contains(account) || ! blankVoterAccounts.contains(account)){
            return false;
        }
        blankVoterAccounts.remove(account);

        account.setUserName(username);
        account.setPassword(password);

        registeredAccounts.add(account);
        if (accountRepository == null) {
            throw new RuntimeException("Account repo was not injected properly! Account will not be saved."); // TODO resolve this bug
        }
        accountRepository.save(account);
        return true;


    }

    public boolean markKeyAsUsed(Integer key) {
        return signInKeyService.markKeyAsUsed(key);
    }
}
