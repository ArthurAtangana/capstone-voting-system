package org.sysc4907.votingsystem;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class AdminAccount extends Account {
    public AdminAccount(String username, String password) {
        super(username, password);
    }

    public AdminAccount() {

    }
}

