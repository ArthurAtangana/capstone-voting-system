package org.sysc4907.votingsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private AccountRepository repository;

    public boolean authenticate(String userName, String password) {
        Optional<Account> account = repository.findById(userName);

        if (account.isPresent()) {
            return account.get().getPassword().equals(password);
        }
        return false;
    }
}
