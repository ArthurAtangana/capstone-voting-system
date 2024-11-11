package org.sysc4907.votingsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.sysc4907.votingsystem.Accounts.Account;
import org.sysc4907.votingsystem.Accounts.AccountRepository;
import org.sysc4907.votingsystem.Accounts.VoterAccount;
import org.sysc4907.votingsystem.Elections.AccountService;
import org.sysc4907.votingsystem.RegistrationService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(AccountService.class)
class AccountServiceTest {
    private Set<Integer> validKeys;

    @MockBean
    private AccountRepository accountRepository;

    private AccountService accountService;

//    @Autowired
//    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        validKeys = new HashSet<>(Arrays.asList(new Integer[] {123, 456, 789, 101}));
        accountService = new AccountService(validKeys);
    }


    @Test
    void testAssignBlankAccount() {
        Integer validKey = 123;
        assertTrue(accountService.assignBlankAccount(validKey).isPresent());

        Integer invalidKey = 0;
        assertFalse(accountService.assignBlankAccount(invalidKey).isPresent());

    }

    @Test
    void testConfigureAndSaveNewAccount() { // TODO need to figure out how to mock account repository encapsulated within the class
        /*
        // Configure credentials for a blank account
        Integer validKey = 123;
        Account newAccount = accountService.assignBlankAccount(validKey).get();

        when(accountRepository.save(any(Account.class))).thenReturn(newAccount);  // Mock save operation
        assertTrue(accountService.configureAndSaveNewAccount(newAccount, "user", "pass"));

        //verify(accountRepository).save(newAccount); // verify that save was called

        // Configure credentials for an account that is not one of the pre-generated blank accounts
        Integer invalidKey = 0;
        assertFalse(accountService.configureAndSaveNewAccount(new VoterAccount(), "user", "pass"));

         */
    }
}