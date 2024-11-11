package org.sysc4907.votingsystem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import org.sysc4907.votingsystem.Accounts.AdminAccount;
import org.sysc4907.votingsystem.Accounts.VoterAccount;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the {@link RegistrationController} class.
 * Uses MockMvc to simulate HTTP requests and verify responses, view names, and model attributes.
 *
 * @author Jasmine Gad El Hak
 */
@WebMvcTest(RegistrationController.class)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrationService registrationService;

    /**
     * Verifying GET /register/sign-in-key endpoint has OK status and returns expected template name.
     */
    @Test
    public void testShowRegistrationSignInKeyPage() throws Exception {
        mockMvc.perform(get("/register/sign-in-key"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration-sign-in-key-page"));
    }
    /**
     * Verifying GET /register/credentials endpoint has OK status and returns expected template name.
     */
    @Test
    public void testShowRegistrationCredentialsPage() throws Exception {
        mockMvc.perform(get("/register/credentials"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration-credentials-page"));
    }
    /**
     * Verifying POST /register/sign-in-key endpoint has OK status and returns expected template name.
     */
    @Test
    public void testSubmitKeyValid() throws Exception {
        when(registrationService.validateSignInKey("validKey")).thenReturn(true); // mocking valid key value

        mockMvc.perform(post("/register/sign-in-key")
                        .param("signInKey", "validKey"))
                .andExpect(status().is3xxRedirection()) // we expect redirection to credentials page
                .andExpect(redirectedUrl("/register/credentials"));
    }

    @Test
    public void testSubmitKeyInvalid() throws Exception {
        when(registrationService.validateSignInKey("invalidKey")).thenReturn(false); // mocking invald key value

        mockMvc.perform(post("/register/sign-in-key")
                        .param("signInKey", "invalidKey"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration-sign-in-key-page"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Invalid sign-in key. Please try again."));
    }

    @Test
    public void testCreateAccountVoter() throws Exception {
        VoterAccount voterAccount = new VoterAccount("userName", "password");
        when(registrationService.registerAccount(anyString(), anyString())).thenReturn(voterAccount);

        mockMvc.perform(post("/register/credentials")
                        .param("userName", "userName")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("successful-voter-login"))
                .andExpect(model().attribute("name", "userName"));
    }

    @Test
    public void testCreateAccountAdmin() throws Exception {
        AdminAccount adminAccount = new AdminAccount("adminName", "adminPass");
        when(registrationService.registerAccount(anyString(), anyString())).thenReturn(adminAccount);

        mockMvc.perform(post("/register/credentials")
                        .param("userName", "adminName")
                        .param("password", "adminPass"))
                .andExpect(status().isOk())
                .andExpect(view().name("successful-admin-login"))
                .andExpect(model().attribute("name", "adminName"));
    }
}

