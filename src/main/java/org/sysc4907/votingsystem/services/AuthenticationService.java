package org.sysc4907.votingsystem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sysc4907.votingsystem.models.Account;
import org.sysc4907.votingsystem.models.AdminAccount;
import org.sysc4907.votingsystem.models.VoterAccount;
import org.sysc4907.votingsystem.repositories.AccountRepository;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private AccountRepository accountRepository;

    public String authenticate(String userName, String password) {

        Optional<Account> account = accountRepository.findById(userName);

        if (account.isPresent()) {
            Account a = account.get();
            if (account.get().getPassword().equals(password)) {
                if (a instanceof VoterAccount) {
                    return "successful-voter-login";
                }
                if (a instanceof AdminAccount) {
                    return "successful-admin-login";
                }
            }
        }

        return "";
    }
}
