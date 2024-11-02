package org.sysc4907.votingsystem;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("VOTER")
public class VoterAccount extends Account {

    public VoterAccount(String username, String password) {
        super(username, password);
    }

    public VoterAccount() {

    }
}
