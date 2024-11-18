package org.sysc4907.votingsystem.Accounts;

import jakarta.persistence.*;
import org.sysc4907.votingsystem.LirisiCommandExecutor;
import org.sysc4907.votingsystem.RingSignatureService;

import java.io.IOException;

@Entity
@Table(name="accounts")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type")
public abstract class Account {

    @Id
    private String userName;
    private String password;

    public Account(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public Account() {

    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
