package org.sysc4907.votingsystem.Accounts;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("VOTER")
public class VoterAccount extends Account {

    private Boolean voted =false;

    public VoterAccount(String username, String password) {
        super(username, password);
    }

    public VoterAccount() {

    }

    public void castVote() {voted = true;}
    public Boolean hasVoted() {return voted;}
}
