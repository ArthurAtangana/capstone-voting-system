package org.sysc4907.votingsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sysc4907.votingsystem.Accounts.Account;
import org.sysc4907.votingsystem.Accounts.AccountService;
import org.sysc4907.votingsystem.Accounts.VoterAccount;
import org.sysc4907.votingsystem.Elections.ElectionService;
import org.sysc4907.votingsystem.Registration.RegistrationService;
import org.sysc4907.votingsystem.Registration.RegistrationService.Response;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testing the behaviour of the registration service, which is responsible for validating
 * sign-in keys and managing the account registration process.
 *
 * @author Jasmine Gad El Hak
 * @version 1.0
 */
class RegistrationServiceTest {
    @Mock
    private ElectionService electionService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    /**
     * Tests that submitting a sign-in key fails when the election is not configured.
     * Verifies that no interactions are made with the account service in this scenario.
     */
    @Test
    void submitSignInKeyWhenElectionNotConfigured() {
        when(electionService.electionIsConfigured()).thenReturn(false); // mocking election not being configured

        assertFalse(registrationService.submitSignInKey(123));

        verify(electionService).electionIsConfigured(); // verify that method was invoked
        verifyNoInteractions(accountService); // verify that we never invoke any methods on the account service
    }
    /**
     * Tests that submitting a registration key succeeds when the election is configured and the key is valid. */
    @Test
    void submitValidRegistrationKey() {
        when(electionService.electionIsConfigured()).thenReturn(true); // mocking election being configured
        when(electionService.getAccountService()).thenReturn(accountService);
        when(accountService.markKeyAsUsed(123)).thenReturn(true); // mocking available blank account

        assertTrue(registrationService.submitSignInKey(123));
        verify(accountService).markKeyAsUsed(123);
    }
    /**
     * Tests that submitting a registration key fails when the election is configured
     * but the key is not one of the configured keys for the election.
     */
    @Test
    void submitInvalidRegistrationKey() {
        when(electionService.electionIsConfigured()).thenReturn(true); // mocking election being configured
        when(electionService.getAccountService()).thenReturn(accountService);
        when(accountService.markKeyAsUsed(123)).thenReturn(false); // mocking invalid key

        assertFalse(registrationService.submitSignInKey(123));
        verify(accountService).markKeyAsUsed(123);
    }
    /**
     * Tests that submitting account credentials fails when the election is not configured.
     * Verifies that the account service is not invoked in this scenario.
     */
    @Test
    void submitAccountCredentialsWhenElectionNotConfigured() {
        when(electionService.electionIsConfigured()).thenReturn(false); // mocking election not being configured

        assertEquals(Response.REG_FAILED, registrationService.submitAccountCredentials("user", "password"));

        verify(electionService).electionIsConfigured(); // verify that we invoked this method
        verify(electionService, never()).getAccountService(); // verify we never invoke this method
    }
    /**
     * Tests that submitting account credentials succeeds when the election is configured,
     * the registration key has been validated, and the account is successfully created and saved.
     */
    @Test
    void submitAccountCredentialsWhenAccountConfigured() {
        when(electionService.electionIsConfigured()).thenReturn(true); // mocking election being configured
        when(electionService.getAccountService()).thenReturn(accountService);
        when(accountService.markKeyAsUsed(123)).thenReturn(true); // mocking valid key
        when(accountService.configureAndSaveNewAccount("user", "password"))
                .thenReturn(true);

        registrationService.submitSignInKey(123); // Simulate account registration flow

        assertEquals(Response.VOTER_REG_SUCCESS, registrationService.submitAccountCredentials("user", "password"));
        verify(accountService).configureAndSaveNewAccount("user", "password");
    }
    /**
     * Tests that resubmitting account credentials after successfully registering an account
     * fails. Verifies that no additional account configuration occurs after the first submission.
     */
    @Test
    void resubmitAccountCredentialsForSameKey() {
        when(electionService.electionIsConfigured()).thenReturn(true); // mocking election being configured
        when(electionService.getAccountService()).thenReturn(accountService);
        when(accountService.markKeyAsUsed(123)).thenReturn(true); // mocking valid key

        registrationService.submitSignInKey(123); // Simulate account registration flow

        registrationService.submitAccountCredentials("user", "password"); // simulate first submit

        // Resubmission should fail, flag for 'registrationInProgress' should have been reset
        assertEquals(Response.REG_FAILED, registrationService.submitAccountCredentials("user", "password")); // verify resubmitting will not create another account

    }
    /**
     * Tests that submitting account credentials fails when key is never validated,
     * even though the election is configured. Verifies that the account service is not invoked.
     */
    @Test
    void submitAccountCredentialsWhenAccountNotConfigured() {
        when(electionService.electionIsConfigured()).thenReturn(true);
        when(electionService.getAccountService()).thenReturn(accountService);

        assertEquals(Response.REG_FAILED, registrationService.submitAccountCredentials("user", "password"));
    }

}