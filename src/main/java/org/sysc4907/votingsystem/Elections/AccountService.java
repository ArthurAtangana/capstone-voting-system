package org.sysc4907.votingsystem.Elections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sysc4907.votingsystem.Accounts.Account;
import org.sysc4907.votingsystem.Accounts.AccountRepository;
import org.sysc4907.votingsystem.Accounts.VoterAccount;
import org.sysc4907.votingsystem.SignInKeyService;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    private SignInKeyService signInKeyService;
    private List<VoterAccount> blankVoterAccounts;
    // private List<AdminAccount> blankAdminAccounts; // TODO
    private List<Account> registeredAccounts;
    public AccountService(Set<Integer> voterKeySet){
        signInKeyService = new SignInKeyService(voterKeySet);

        int numberOfAccounts = voterKeySet.size();
        blankVoterAccounts = new ArrayList<>(numberOfAccounts);
        for (int i = 0; i < numberOfAccounts; i++) {
            blankVoterAccounts.add(new VoterAccount());
        }
        registeredAccounts = new ArrayList<>();
    }

    public Optional<Account> assignBlankAccount(Integer key) {
        if (signInKeyService.keyIsValid(key)) {
            SecureRandom secureRandom = new SecureRandom(); // SecureRandom is recommended as it generates non-deterministic random values based on cryptographic algorithms
            int randomIndex = secureRandom.nextInt(blankVoterAccounts.size());
            return Optional.of(this.blankVoterAccounts.get(randomIndex));
        }
        return Optional.empty();
    }


    public boolean configureAndSaveNewAccount(Account account, String username, String password) {
        if (username.isEmpty() || password.isEmpty() || registeredAccounts.contains(account) || ! blankVoterAccounts.contains(account)){
            return false;
        }
        blankVoterAccounts.remove(account);

        account.setUserName(username);
        account.setPassword(password);

        registeredAccounts.add(account);
        accountRepository.save(account);

        return true;


    }
}
