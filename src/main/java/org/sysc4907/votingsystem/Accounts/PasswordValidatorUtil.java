package org.sysc4907.votingsystem.Accounts;

import org.passay.*;
import java.util.Arrays;

public class PasswordValidatorUtil {

    public static String validatePassword(String password) {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8, 16), // Password length between 8 and 16
                new CharacterRule(EnglishCharacterData.UpperCase, 1), // At least one uppercase letter
                new CharacterRule(EnglishCharacterData.LowerCase, 1), // At least one lowercase letter
                new CharacterRule(EnglishCharacterData.Digit, 1), // At least one digit
                new CharacterRule(EnglishCharacterData.Special, 1), // At least one special character
                new WhitespaceRule() // No whitespace allowed
        ));

        RuleResult result = validator.validate(new PasswordData(password));

        if (result.isValid()) {
            return null; // Valid password
        } else {
            return String.join("\n", validator.getMessages(result));
        }
    }
}

