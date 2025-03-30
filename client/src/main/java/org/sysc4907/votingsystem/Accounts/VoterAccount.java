package org.sysc4907.votingsystem.Accounts;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.sysc4907.votingsystem.Accounts.Account;

@Entity
@DiscriminatorValue("VOTER")
public class VoterAccount extends Account {

    public VoterAccount(String username, String password) {
        super(username, password);
    }

    public VoterAccount() {

    }
}
