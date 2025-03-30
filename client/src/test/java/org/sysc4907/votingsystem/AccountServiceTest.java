package org.sysc4907.votingsystem.Accounts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.sysc4907.votingsystem.Registration.SignInKeyService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private UserDetailsManager userDetailsManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountService.initAccountService(new HashSet<>(Arrays.asList(new Integer[] {123, 456}))); // Simulate election setup with keys
    }

    @Test
    void configureAndSaveNewAccount() {
        String username = "testUser";
        String password = "password";

        when(userDetailsManager.userExists(username)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        // Simulate registration being active
        accountService.markKeyAsUsed(123);

        assertTrue(accountService.configureAndSaveNewAccount(username, password));
        verify(userDetailsManager).createUser(any(UserDetails.class));
    }

    @Test
    void configureAndSaveNewAccountExistingUsername() {
        String username = "existingUser";
        String password = "password";

        when(userDetailsManager.userExists(username)).thenReturn(true);

        assertFalse(accountService.configureAndSaveNewAccount(username, password));
        verify(userDetailsManager, never()).createUser(any(UserDetails.class));
    }

    @Test
    void configureAndSaveNewAccountWhenKeyNotValidated() {
        String username = "newUser";
        String password = "password";

        when(userDetailsManager.userExists(username)).thenReturn(false);

        // Registration is inactive (activeRegistration remains false)
        assertFalse(accountService.configureAndSaveNewAccount(username, password));
        verify(userDetailsManager, never()).createUser(any(UserDetails.class));
    }

    @Test
    void markKeyAsUsed() {
        assertTrue(accountService.markKeyAsUsed(123));
    }

    @Test
    void markKeyAsUsedWhenKeyInvalid() {
        assertFalse(accountService.markKeyAsUsed(999));
    }
}
