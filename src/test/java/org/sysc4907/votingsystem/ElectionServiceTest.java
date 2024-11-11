package org.sysc4907.votingsystem;

import org.sysc4907.votingsystem.Elections.Election;
import org.sysc4907.votingsystem.Elections.ElectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ElectionServiceTest {

    private ElectionService electionService;
    private LocalDate startDate = LocalDate.now().plusDays(1);
    private LocalTime startTime = LocalTime.now().plusHours(1);
    private LocalDate endDate = LocalDate.now().plusDays(2);
    private LocalTime endTime = LocalTime.now().plusHours(2);
    private String name = "Favourite Drink";
    private String candidates = "Espresso\nLatte\nMocha";
    private int numberOfDecryptionKeys = 2;

    /** Simulates MultipartFile*/
    private MultipartFile mockFile;

    @BeforeEach
    public void setUp() {
        electionService = new ElectionService();
        mockFile = mock(MultipartFile.class);
    }

    @Test
    public void testValidPollConfiguration() throws IOException {

        // when(mockFile.getBytes()) is executed return "123\n456".getBytes()
        when(mockFile.getBytes()).thenReturn("123\n456".getBytes());

        boolean result = electionService.validateAndConfigurePoll(startDate, startTime, endDate, endTime, name, candidates, mockFile);

        assertTrue(result);

        Election election = electionService.getElection();
        assertNotNull(election);
        assertEquals(name, election.NAME);
        assertEquals(startDate, election.START_DATE);
        assertEquals(startTime, election.START_TIME);
        assertEquals(endDate, election.END_DATE);
        assertEquals(endTime, election.END_TIME);
        assertEquals(Arrays.asList("Espresso", "Latte", "Mocha"), election.getCandidates());
    }

    /**
     * Assert that the election does not get configured due to an invalid start date.
     * @throws IOException
     */
    @Test
    public void testInvalidStartDate() throws IOException {
        // Invalid start date (before current date)
        LocalDate startDate = LocalDate.now().minusDays(1);

        when(mockFile.getBytes()).thenReturn("123\n456".getBytes());

        boolean result = electionService.validateAndConfigurePoll(startDate, startTime, endDate, endTime, name, candidates, mockFile);

        assertFalse(result);
    }

    /**
     * Assert that the election does not get configured due to an invalid start time.
     * @throws IOException
     */
    @Test
    public void testInvalidStartTime() throws IOException {
        // Invalid start time (before current time)
        LocalTime startTime = LocalTime.now().minusMinutes(1);

        when(mockFile.getBytes()).thenReturn("123\n456".getBytes());

        boolean result = electionService.validateAndConfigurePoll(startDate, startTime, endDate, endTime, name, candidates, mockFile);

        assertFalse(result);
    }

    /**
     * Assert that the election does not get configured due to an invalid end date.
     * @throws IOException
     */
    @Test
    public void testInvalidEndDate() throws IOException {
        // Invalid end date (before start date)
        LocalDate endDate = LocalDate.now();

        when(mockFile.getBytes()).thenReturn("12345\n67890".getBytes());

        boolean result = electionService.validateAndConfigurePoll(startDate, startTime, endDate, endTime, name, candidates, mockFile);

        assertFalse(result);
    }

    /**
     * Assert that the election does not get configured due to an invalid end time.
     * @throws IOException
     */
    @Test
    public void testInvalidEndTime() throws IOException {
        // Invalid end time (before current time)
        LocalTime endTime = LocalTime.now().minusMinutes(1);

        when(mockFile.getBytes()).thenReturn("123\n456".getBytes());

        boolean result = electionService.validateAndConfigurePoll(startDate, startTime, endDate, endTime, name, candidates, mockFile);

        assertFalse(result);
    }

    /**
     * Assert that the election does not get configured due to an invalid start/end time for the current date.
     * @throws IOException
     */
    @Test
    public void testInvalidTimeOnCurrentDate() throws IOException {
        LocalDate startDate = LocalDate.now();
        LocalTime startTime = LocalTime.now().minusMinutes(1);
        LocalDate endDate = LocalDate.now();
        LocalTime endTime = LocalTime.now().minusMinutes(1);

        when(mockFile.getBytes()).thenReturn("123\n456".getBytes());

        boolean result = electionService.validateAndConfigurePoll(startDate, startTime, endDate, endTime, name, candidates, mockFile);

        assertFalse(result);
    }

    /**
     * Assert that the election does not get configured due to having no candidates.
     * @throws IOException
     */
    @Test
    public void testEmptyCandidates() throws IOException {
        String candidates = "\n";

        when(mockFile.getBytes()).thenReturn("123\n456".getBytes());

        boolean result = electionService.validateAndConfigurePoll(startDate, startTime, endDate, endTime, name, candidates, mockFile);

        assertFalse(result);
    }

    /**
     * Assert that the election does not get configured due to duplicate candidates.
     * @throws IOException
     */
    @Test
    public void testDuplicateCandidates() throws IOException {
        String candidates = "Espresso\nLatte\nLatte";

        when(mockFile.getBytes()).thenReturn("123\n456".getBytes());

        boolean result = electionService.validateAndConfigurePoll(startDate, startTime, endDate, endTime, name, candidates, mockFile);

        assertFalse(result);
    }

    /**
     * Assert that the election does not get configured if there is only one candidate.
     * @throws IOException
     */
    @Test
    public void testMultipleCandidates() throws IOException {
        String candidates = "Espresso";

        when(mockFile.getBytes()).thenReturn("123\n456".getBytes());

        boolean result = electionService.validateAndConfigurePoll(startDate, startTime, endDate, endTime, name, candidates, mockFile);

        assertFalse(result);
    }

    /**
     * Assert that the election does not get configured due to invalid voter keys (non-integer).
     * @throws IOException
     */
    @Test
    public void testInvalidVoterKeys() throws IOException {
        when(mockFile.getBytes()).thenReturn("abc\ndef".getBytes());

        boolean result = electionService.validateAndConfigurePoll(startDate, startTime, endDate, endTime, name, candidates, mockFile);

        assertFalse(result);
    }

    /**
     * Assert that the election does not get configured due to not having voter keys.
     * @throws IOException
     */
    @Test
    public void testEmptyVoterKeys() throws IOException {
        when(mockFile.getBytes()).thenReturn("".getBytes());

        boolean result = electionService.validateAndConfigurePoll(startDate, startTime, endDate, endTime, name, candidates, mockFile);

        assertFalse(result);
    }

    /**
     * Assert that the election does not get configured due to having duplicate voter keys.
     * @throws IOException
     */
    @Test
    public void testDuplicateVoterKeys() throws IOException {
        when(mockFile.getBytes()).thenReturn("abc\nabc".getBytes());

        boolean result = electionService.validateAndConfigurePoll(startDate, startTime, endDate, endTime, name, candidates, mockFile);

        assertFalse(result);
    }

    /**
     * Assert that a non-configured election returns null.
     */
    @Test
    public void testNonConfiguredElection() {
        Election election = electionService.getElection();

        assertNull(election);
    }

}

