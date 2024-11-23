package org.sysc4907.votingsystem.Elections;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ElectionForm {

    @NotEmpty(message = "Election name is required")
    @Size(max = 50, message = "Election name cannot exceed 50 characters")
    private String name;

    @NotNull(message = "Start date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull(message = "Start time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime startTime;

    @NotNull(message = "End date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @NotNull(message = "End time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime endTime;

    @NotEmpty(message = "Candidates are required")
    @Size(min = 2, message = "At least two candidates are required")
    private String candidates;

    private MultipartFile voterKeys;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getCandidates() {
        return candidates;
    }

    public void setCandidates(String candidates) {
        this.candidates = candidates;
    }

    public LocalDateTime getStartDateTime() {
        return startDate != null && startTime != null ? LocalDateTime.of(startDate, startTime) : null;
    }

    public LocalDateTime getEndDateTime() {
        return endDate != null && endTime != null ? LocalDateTime.of(endDate, endTime) : null;
    }

    public MultipartFile getVoterKeys() {
        return voterKeys;
    }

    public void setVoterKeys(MultipartFile voterKeys) {
        this.voterKeys = voterKeys;
    }
}
