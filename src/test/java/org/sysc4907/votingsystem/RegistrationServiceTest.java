package org.sysc4907.votingsystem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing the behaviour of the registration service which is responsible for validating sign-in keys, and creating new accounts.
 *
 * @author Jasmine Gad El Hak
 * @version 1.0
 */
class RegistrationServiceTest {


    @Test
    void validateSignInKey() {
        // TODO add tests once sign-in keys are added to poll config
    }

    @Test
    void registerAccount() {
//        RegistrationService registrationService = new RegistrationService();
//        registrationService.registerAccount("Testing123", "Testing456");
        // TODO mock account DB for testing to verify this
    }
}