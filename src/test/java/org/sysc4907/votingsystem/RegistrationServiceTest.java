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
 * Testing the behaviour of the registration service which is responsible for validating sign-in keys, and creating new accounts.
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
     * Tests that submitting a sign-in key succeeds when the election is configured
     * and the account service assigns a blank account. Verifies interactions with
     * the account service for assigning the blank account and marking the key as used.
     */
    @Test
    void submitSignInKeyWhenKeyAssigned() {
        when(electionService.electionIsConfigured()).thenReturn(true); // mocking election being configured
        when(electionService.getAccountService()).thenReturn(accountService);
        when(accountService.assignBlankAccount(123)).thenReturn(Optional.of(new VoterAccount())); // mocking available blank account

        assertTrue(registrationService.submitSignInKey(123));
        verify(accountService).assignBlankAccount(123);
        verify(accountService).markKeyAsUsed(123);
    }
    /**
     * Tests that submitting a sign-in key fails when the election is configured
     * but the account service does not assign a blank account. Verifies that the
     * key is still marked as used in this scenario.
     */
    @Test
    void submitSignInKeyWhenKeyNotAssigned() {
        when(electionService.electionIsConfigured()).thenReturn(true); // mocking election being configured
        when(electionService.getAccountService()).thenReturn(accountService);
        when(accountService.assignBlankAccount(123)).thenReturn(Optional.empty()); // mocking unavailable blank account

        assertFalse(registrationService.submitSignInKey(123));

        // verify that we invoked these methods
        verify(accountService).assignBlankAccount(123);
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
     * a blank account is assigned, and the account is successfully configured and saved.
     * Verifies interactions with the account service for configuring and saving the account.
     */
    @Test
    void submitAccountCredentialsWhenAccountConfigured() {
        Account mockAccount = new VoterAccount();
        when(electionService.electionIsConfigured()).thenReturn(true); // mocking election being configured
        when(electionService.getAccountService()).thenReturn(accountService);
        when(accountService.assignBlankAccount(123)).thenReturn(Optional.of(mockAccount)); // mocking available blank account
        when(accountService.configureAndSaveNewAccount(mockAccount, "user", "password"))
                .thenReturn(true);

        registrationService.submitSignInKey(123); // Simulate account registration flow

        assertEquals(Response.VOTER_REG_SUCCESS, registrationService.submitAccountCredentials("user", "password"));
        verify(accountService).configureAndSaveNewAccount(mockAccount, "user", "password");
    }
    /**
     * Tests that resubmitting account credentials after successfully registering an account
     * fails because the stored account for registration is reset. Verifies that no additional
     * account configuration occurs after the first submission.
     */
    @Test
    void resubmitAccountCredentialsForSameKey() {
        Account mockAccount = new VoterAccount();

        when(electionService.electionIsConfigured()).thenReturn(true); // mocking election being configured
        when(electionService.getAccountService()).thenReturn(accountService);
        when(accountService.assignBlankAccount(123)).thenReturn(Optional.of(mockAccount)); // mocking available blank account
        when(accountService.configureAndSaveNewAccount(mockAccount, "user", "password"))
                .thenReturn(true);

        registrationService.submitSignInKey(123); // Simulate account registration flow

        registrationService.submitAccountCredentials("user", "password"); // simulate first submit

        // Resubmission should fail, account stored should have been reset
        assertEquals(Response.REG_FAILED, registrationService.submitAccountCredentials("user", "password")); // verify resubmitting will not create another account

    }
    /**
     * Tests that submitting account credentials fails when no account is assigned for registration,
     * even though the election is configured. Verifies that the account service is not invoked.
     */
    @Test
    void submitAccountCredentialsWhenAccountNotConfigured() {
        when(electionService.electionIsConfigured()).thenReturn(true);

        assertEquals(Response.REG_FAILED, registrationService.submitAccountCredentials("user", "password"));
        verify(electionService, never()).getAccountService(); // verify that method was never invoked
    }

}