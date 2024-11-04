package org.sysc4907.votingsystem.PollConfiguration;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class PollConfigurationService {

    public String validatePollConfiguration(LocalDate startDate, LocalDate endDate, String name,
                                            String candidates) {

        boolean validDates = validateDates(startDate, endDate);
        boolean validName = validateElectionName(name);
        boolean validCandidates = validateCandidates(candidates);

        if (validDates && validName && validCandidates) {
            return "successful-poll-config";
        }

        return "unsuccessful-poll-config";
    }


    private boolean validateDates(LocalDate startDate, LocalDate endDate)  {
        LocalDate currentDate = LocalDate.now();

        if (startDate == null || endDate == null) {
            return false;
        }
        return endDate.compareTo(startDate) > 0 && endDate.compareTo(currentDate) > 0;
    }

    private boolean validateElectionName(String electionName) {
        if (electionName == null || electionName.trim().isEmpty() || electionName.length() > 100) {
            return false;
        }
        return true;
    }

    private boolean validateCandidates(String candidates) {
        List<String> splitCandidates = Arrays.asList(candidates.split("\\r?\\n"));

        if (candidates == null || candidates.isEmpty()) {
            return false;
        }

        Set<String> uniqueCandidates = new HashSet<>(splitCandidates);
        return uniqueCandidates.size() == splitCandidates.size();
    }
}


