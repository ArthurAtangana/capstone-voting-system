package org.sysc4907.votingsystem.PollConfiguration;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.sysc4907.votingsystem.Elections.Election;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class PollConfigurationService {

    private Election election;

    public boolean validateAndConfigurePoll(LocalDate startDate, LocalTime startTime, LocalDate endDate,
                                            LocalTime endTime, String name, String candidates, MultipartFile voterKeysFile) {

        boolean validDateTime = validateDateTime(startDate, endDate, startTime, endTime);
        boolean validName = validateElectionName(name);
        List<String> splitCandidates = Arrays.asList(candidates.split("\\r?\\n"));
        boolean validCandidates = validateCandidates(splitCandidates);
        List<Integer> voterKeyList = convertFileToList(voterKeysFile);
        boolean validVoterKeys = validateVoterKeys(voterKeyList);

        if (validDateTime && validName && validCandidates && validVoterKeys) {
            this.election = new Election(startDate, startTime, endDate, endTime, name, splitCandidates, voterKeyList.size(), voterKeyList);
            return true;
        }

        return false;
    }

    public Election getElection() {
        if (election == null) {
            System.out.println("Election has not been configured yet.");
        }
        return election;
    }


    private boolean validateDateTime(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime)  {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        if (startDate == null || endDate == null || startTime == null || endTime == null) {
            return false;
        }
        if (startDate.isBefore(currentDate) || endDate.isBefore(currentDate)) {
            return false;
        }
        if (startDate.isAfter(endDate)) {
            return false;
        }
        if (startDate.equals(endDate)) {
            if (endTime.isBefore(startTime)) {
                return false;
            }
            if (startDate.equals(currentDate) && (startTime.isBefore(currentTime)) || endTime.isBefore(currentTime)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateElectionName(String electionName) {
        if (electionName == null || electionName.trim().isEmpty() || electionName.length() > 100) {
            return false;
        }
        return true;
    }

    private boolean validateCandidates(List<String> candidates) {

        if (candidates == null || candidates.isEmpty()) {
            return false;
        }

        Set<String> uniqueCandidates = new HashSet<>(candidates);
        return uniqueCandidates.size() == candidates.size();
    }

    private boolean validateVoterKeys(List<Integer> voterKeyList) {
        Set<Integer> uniqueKeys = new HashSet<>(voterKeyList);
        return uniqueKeys.size() == voterKeyList.size();
    }

    private List<Integer> convertFileToList(MultipartFile voterKeysFile) {
        List<Integer> keysList = new ArrayList<>();

        try {
            String content = new String(voterKeysFile.getBytes());
            String[] lines = content.split("\\r?\\n");

            for (String line : lines) {
                try {
                    keysList.add(Integer.parseInt(line.trim()));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid key: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read voter keys file");
        }
        return keysList;
    }
}


