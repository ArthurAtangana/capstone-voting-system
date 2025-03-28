package org.sysc4907.votingsystem;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import org.sysc4907.votingsystem.Elections.ElectionService;
import org.sysc4907.votingsystem.Registration.RegistrationController;
import org.sysc4907.votingsystem.Registration.RegistrationService;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
@AutoConfigureMockMvc(addFilters = false)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private ElectionService electionService;

    /**
     * Verifying GET /registration-key endpoint has OK status and returns expected template name.
     */
    @Test
    public void testShowRegistrationKeyPage() throws Exception {
        // Registration when poll is not yet configured
        mockMvc.perform(get("/registration-key"))
                .andExpect(status().isOk())
                .andExpect(view().name("login-page"))
                .andExpect(model().attributeExists("errorMessage"));

        // Registration when poll has been configured
        when(electionService.electionIsConfigured()).thenReturn(true); // mocking that poll has been configured
        mockMvc.perform(get("/registration-key"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration-key-page"));
    }
    /**
     * Verifying GET /register/credentials endpoint has OK status and returns expected template name.
     */
    @Test
    public void testShowRegistrationCredentialsPage() throws Exception {
        // Create a MockHttpSession and add attributes if needed
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("validRegKey", true);
        mockMvc.perform(get("/register-credentials").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("registration-credentials-page"));
    }
    /**
     * Verifying POST /registration-key endpoint has OK status and returns expected template name.
     */
    @Test
    public void testSubmitKeyValid() throws Exception {
        when(registrationService.submitSignInKey(anyInt())).thenReturn(true); // mocking valid key value

        mockMvc.perform(post("/registration-key").with(csrf())
                        .param("registrationKey", "123"))
                .andExpect(status().is3xxRedirection()) // we expect redirection to credentials page
                .andExpect(redirectedUrl("/register-credentials"));
    }

    @Test
    public void testSubmitKeyInvalid() throws Exception {
        when(registrationService.submitSignInKey(anyInt())).thenReturn(false); // mocking invald key value

        mockMvc.perform(post("/registration-key")
                        .param("registrationKey", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration-key-page"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Invalid sign-in key. Please try again."));
    }

    @Test
    public void testCreateAccountVoter() throws Exception {
        when(registrationService.submitAccountCredentials(anyString(), anyString())).thenReturn(RegistrationService.Response.VOTER_REG_SUCCESS);

        mockMvc.perform(post("/register-credentials")
                        .param("userName", "userName")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection()) // we expect redirection to home page
                .andExpect(redirectedUrl("/home"))
                .andExpect(request().sessionAttribute("accountType", "voter"))
                .andExpect(request().sessionAttributeDoesNotExist("validRegKey"));
    }

    @Test
    public void testCreateAccountAdmin() throws Exception {
        when(registrationService.submitAccountCredentials(anyString(), anyString())).thenReturn(RegistrationService.Response.ADMIN_REG_SUCCESS);

        mockMvc.perform(post("/register-credentials")
                        .param("userName", "adminName")
                        .param("password", "adminPass"))
                .andExpect(status().is3xxRedirection()) // we expect redirection to home page
                .andExpect(redirectedUrl("/home"))
                .andExpect(request().sessionAttribute("accountType", "admin"))
                .andExpect(request().sessionAttributeDoesNotExist("validRegKey"));
    }
}

