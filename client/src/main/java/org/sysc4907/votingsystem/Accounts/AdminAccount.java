package org.sysc4907.votingsystem.Accounts;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.sysc4907.votingsystem.Accounts.Account;

@Entity
@DiscriminatorValue("ADMIN")
public class AdminAccount extends Account {
    public AdminAccount(String username, String password) {
        super(username, password);
    }

    public AdminAccount() {

    }
}

