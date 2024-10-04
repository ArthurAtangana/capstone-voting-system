package org.sysc4907.votingsystem;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Account {

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
