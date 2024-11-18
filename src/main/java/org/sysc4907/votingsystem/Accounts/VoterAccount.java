package org.sysc4907.votingsystem.Accounts;

import jakarta.persistence.*;
import org.springframework.stereotype.Controller;
import org.sysc4907.votingsystem.Accounts.Account;

@Entity
@DiscriminatorValue("VOTER")
@SecondaryTable(name="voter_signing_keys", pkJoinColumns = @PrimaryKeyJoinColumn(name = "voter_id"))
public class VoterAccount extends Account {
    @Column(table="voter_signing_keys")
    private String signingKey;

    public VoterAccount(String username, String password) {
        super(username, password);
    }

    public VoterAccount() {

    }

    public String getSigningKey() {
        return signingKey;
    }

    public void setSigningKey(String signingKey) {
        this.signingKey = signingKey;
    }
}
