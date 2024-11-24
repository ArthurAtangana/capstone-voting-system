package org.sysc4907.votingsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.sysc4907.votingsystem.Accounts.AccountService;
import org.sysc4907.votingsystem.Elections.ElectionForm;
import org.sysc4907.votingsystem.Elections.ElectionService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ElectionServiceTest {
    private ElectionService electionService;
    @Mock
    private AccountService accountService;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private ElectionForm electionForm;
    private String candidates = "Espresso\nLatte\nMocha";
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;
    private LocalDate currentDate;
    private LocalTime currentTime;


    /** Simulates MultipartFile*/
    private MultipartFile mockFile;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        electionService = new ElectionService(accountService);
        bindingResult = mock(BindingResult.class);
        doNothing().when(bindingResult).rejectValue(anyString(), anyString(), anyString());
        mockFile = mock(MultipartFile.class);
        electionForm = mock(ElectionForm.class);
        electionForm.setName("Favourite Drink");
        when(electionForm.getName()).thenReturn("Favourite Drink");
        when(electionForm.getCandidates()).thenReturn(List.of("Espresso", "Latte", "Mocha").toString());

        currentDate = LocalDate.now();
        currentTime = LocalTime.now();

        // Set the start date to be tomorrow (future date)
        startDate = currentDate.plusDays(1);  // Tomorrow
        startTime = currentTime.plusHours(1); // 1 hour after the current time

        endDate = startDate.plusDays(5);   // 5 days after start
        endTime = startTime.plusHours(2);   // 2 hours after start time

        when(electionForm.getStartDate()).thenReturn(startDate);
        when(electionForm.getStartTime()).thenReturn(startTime);
        when(electionForm.getEndDate()).thenReturn(endDate);
        when(electionForm.getEndTime()).thenReturn(endTime);

        when(electionForm.getStartDateTime()).thenReturn(LocalDateTime.of(startDate, startTime));
        when(electionForm.getEndDateTime()).thenReturn(LocalDateTime.of(endDate, endTime));
    }

    @Test
    public void testValidateCandidates() {
        when(bindingResult.hasErrors()).thenReturn(false);
        boolean isValid = electionService.validateCandidates(candidates, bindingResult);
        assertTrue(isValid);
    }

    @Test
    public void testDuplicateCandidates() {
        String candidates = "Coffee\nCoffee";
        when(bindingResult.hasErrors()).thenReturn(true);
        boolean isValid = electionService.validateCandidates(candidates, bindingResult);
        assertFalse(isValid);
        verify(bindingResult, times(1)).rejectValue(eq("candidates"), eq("invalid"), eq("There can be no duplicate candidates."));
    }

    @Test
    public void testOneCandidate() {
        String candidates = "Mocha";
        boolean isValid = electionService.validateCandidates(candidates, bindingResult);
        assertFalse(isValid);
    }

    @Test
    public void testNoCandidates() {
        String candidates = "\n";
        boolean isValid = electionService.validateCandidates(candidates, bindingResult);
        assertFalse(isValid);
    }

    @Test
    public void testValidVoterKeys() {
        try {
            when(mockFile.getBytes()).thenReturn("123\n456".getBytes());
        } catch (IOException e) {
            System.out.println(e);
        }

        boolean isValid = electionService.validateVoterKeys(mockFile, bindingResult);
        assertTrue(isValid);
    }

    @Test
    public void testInValidVoterKeys() {
        try {
            when(mockFile.getBytes()).thenReturn("abc\ndef".getBytes());
        } catch (IOException e) {
            System.out.println(e);
        }

        boolean isValid = electionService.validateVoterKeys(mockFile, bindingResult);
        assertFalse(isValid);
    }

    @Test
    public void testEmptyVoterKeys() {
        try {
            when(mockFile.getBytes()).thenReturn("".getBytes());
        } catch (IOException e) {
            System.out.println(e);
        }

        boolean isValid = electionService.validateVoterKeys(mockFile, bindingResult);
        assertFalse(isValid);
    }

    @Test
    public void testDuplicateVoterKeys() {
        try {
            when(mockFile.getBytes()).thenReturn("123\n123".getBytes());
        } catch (IOException e) {
            System.out.println(e);
        }

        boolean isValid = electionService.validateVoterKeys(mockFile, bindingResult);
        assertFalse(isValid);
    }

    @Test
    public void testGetAccountService() {
        assertSame(accountService, electionService.getAccountService());
    }

    @Test
    public void testValidDateTime() {
        boolean isValid = electionService.validateDateTime(electionForm, bindingResult);
        assertTrue(isValid);
    }

    @Test
    public void testEndDateBeforeStartDate() {
        // endDate before startDate
        startDate = currentDate.plusDays(1);
        endDate = startDate.minusDays(2);
        when(electionForm.getEndDateTime()).thenReturn(LocalDateTime.of(endDate, endTime));
        when(electionForm.getStartDateTime()).thenReturn(LocalDateTime.of(startDate, startTime));
        boolean isValid = electionService.validateDateTime(electionForm, bindingResult);
        assertFalse(isValid);
    }

    @Test
    public void testEndDateInThePast() {
        // endDate in the past
        endDate = currentDate.minusDays(5);
        when(electionForm.getEndDateTime()).thenReturn(LocalDateTime.of(endDate, endTime));
        boolean isValid = electionService.validateDateTime(electionForm, bindingResult);
        assertFalse(isValid);

        verify(bindingResult).rejectValue("endDate", "invalid", "End date and time must be after start date and time.");
    }

    @Test
    public void testStartDateInThePast() {
        // startDate in the past
        startDate = currentDate.minusDays(2);
        when(electionForm.getStartDate()).thenReturn(startDate);
        boolean isValid = electionService.validateDateTime(electionForm, bindingResult);
        assertTrue(isValid);
    }

    @Test
    public void testStartDateEqualsEndDate() {
        // startDate equals endDate
        startDate = currentDate.plusDays(2);
        endDate = currentDate.plusDays(2);
        boolean isValid = electionService.validateDateTime(electionForm, bindingResult);
        assertTrue(isValid);
    }

    @Test
    public void testStartEndDateEqualsCurrentDate() {
        // startDate/endDate currentDate
        startDate = currentDate;
        endDate = currentDate;
        boolean isValid = electionService.validateDateTime(electionForm, bindingResult);
        assertTrue(isValid);
    }

    @Test
    public void testEndTimeBeforeStartTime() {
        // endTime before startTime on the same day
        startDate = currentDate.plusDays(1);
        startTime = currentTime.plusHours(2);
        endDate = currentDate.plusDays(1);
        endTime = currentTime.plusHours(1);
        when(electionForm.getStartDateTime()).thenReturn(LocalDateTime.of(startDate, startTime));
        when(electionForm.getEndDateTime()).thenReturn(LocalDateTime.of(endDate, endTime));
        boolean isValid = electionService.validateDateTime(electionForm, bindingResult);
        assertFalse(isValid);
    }

    @Test
    public void testEqualStartEndDateTime() {
        // same day and same time
        startDate = currentDate.plusDays(1);
        startTime = currentTime.plusHours(2);
        endDate = currentDate.plusDays(1);
        endTime = currentTime.plusHours(2);
        when(electionForm.getStartDateTime()).thenReturn(LocalDateTime.of(startDate, startTime));
        when(electionForm.getEndDateTime()).thenReturn(LocalDateTime.of(endDate, endTime));
        boolean isValid = electionService.validateDateTime(electionForm, bindingResult);
        assertFalse(isValid);

        verify(bindingResult).rejectValue("endDate", "invalid", "End date and time cannot be equal to the start date and time.");
    }

    @Test
    public void testStartDateBeforeCurrentDate() {
        startDate = currentDate.minusDays(1);
        when(electionForm.getStartDateTime()).thenReturn(LocalDateTime.of(startDate, startTime));
        boolean isValid = electionService.validateDateTime(electionForm, bindingResult);

        assertFalse(isValid);
        verify(bindingResult).rejectValue("startDate", "invalid", "Start date and time must be after the current date and time.");
    }

}

