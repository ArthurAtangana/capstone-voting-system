package org.sysc4907.votingsystem.Accounts;

import jakarta.persistence.*;
import com.nulabinc.zxcvbn.Zxcvbn;
import com.nulabinc.zxcvbn.Strength;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type")
public abstract class Account {

    @Id
    private String userName;
    private String password;

    public Account(String userName, String password) {
        this.userName = userName;
        isVeryStrongPassword(password);
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
        isVeryStrongPassword(password);
        this.password = password;
    }

    public static boolean isVeryStrongPassword(String password) {
        Zxcvbn passwordStrengthEstimator = new Zxcvbn();
        Strength strength = passwordStrengthEstimator.measure(password);
        if (strength.getScore() == 4 || strength.getScore() == 3) return true; // 4 is a very strong password, 3 is strong
        System.out.println(strength.getPassword());
        throw new WeakPasswordException(strength.getFeedback().getWarning() + ", " + strength.getFeedback().getSuggestions());
    }

    public static class WeakPasswordException extends RuntimeException {
        public WeakPasswordException(String message) {
            super(message);
        }
    }

}
