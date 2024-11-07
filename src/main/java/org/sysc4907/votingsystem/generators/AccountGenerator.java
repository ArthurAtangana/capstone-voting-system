package org.sysc4907.votingsystem.generators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sysc4907.votingsystem.Accounts.AccountRepository;
import org.sysc4907.votingsystem.Accounts.AdminAccount;
import org.sysc4907.votingsystem.Accounts.VoterAccount;

import java.security.SecureRandom;

/**
 *
 */
@Service
public class AccountGenerator {

    public final int NUMBER_OF_VOTER_ACCOUNTS = 10;
    public final int NUMBER_OF_ADMIN_ACCOUNTS = 1;

    private final AccountRepository accountRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    @Autowired
    public AccountGenerator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // Method to generate a random string of specified length
    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(characters.length());
            result.append(characters.charAt(index));
        }
        return result.toString();
    }

    // Method to generate n dummy accounts
    public void generateDummyAccounts() {
        accountRepository.deleteAll();
        for (int i = 0; i < NUMBER_OF_VOTER_ACCOUNTS; i++) {
            String username = generateRandomString(8); // Random username of length 8
            String password = generateRandomString(12); // Random password of length 12
            VoterAccount account = new VoterAccount(username, password);
            accountRepository.save(account); // Save account to repository
        }
        for (int i = 0; i < NUMBER_OF_ADMIN_ACCOUNTS; i++) {
            String username = generateRandomString(8); // Random username of length 8
            String password = generateRandomString(12); // Random password of length 12
            AdminAccount account = new AdminAccount(username, password);
            accountRepository.save(account); // Save account to repository
        }

    }
}