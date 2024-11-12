package org.sysc4907.votingsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sysc4907.votingsystem.Registration.SignInKeyService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SignInKeyServiceTest {
    private Set<Integer> validKeys;
    private SignInKeyService service;




    @BeforeEach
    void setUp() {
        validKeys = new HashSet<>(Arrays.asList(new Integer[] {123, 456, 789, 101}));
        service = new SignInKeyService(validKeys);
    }


    @Test
    void testValidateKey() {

        Integer validKey = 123;
        assertTrue(service.keyIsValid(validKey));

        Integer invalidKey = 0;
        assertFalse(service.keyIsValid(invalidKey));
    }

    @Test
    void testMarkKeyAsUsed() {
        Integer validKey = 123;
        assertTrue(service.markKeyAsUsed(validKey));

        Integer invalidKey = 0;
        assertFalse(service.markKeyAsUsed(invalidKey));

        Integer previouslyUsedKey = validKey;
        assertFalse(service.markKeyAsUsed(previouslyUsedKey));
    }


}