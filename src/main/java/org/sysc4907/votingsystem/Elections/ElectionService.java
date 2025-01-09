package org.sysc4907.votingsystem.Elections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.sysc4907.votingsystem.Accounts.AccountService;
import org.sysc4907.votingsystem.Elections.Election;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Service class responsible for logic of processing poll configuration.
 */
@Service
public class ElectionService {


    private Election election;

    public AccountService getAccountService() {
        return accountService;
    }
    @Autowired
    private AccountService accountService;

    public boolean validateAndConfigurePoll(LocalDate startDate, LocalTime startTime, LocalDate endDate,
                                            LocalTime endTime, String name, String candidates, MultipartFile voterKeysFile) {

        boolean validDateTime = validateDateTime(startDate, endDate, startTime, endTime);
        boolean validName = validateElectionName(name);
        List<String> splitCandidates = Arrays.asList(candidates.split("\\r?\\n"));
        boolean validCandidates = validateCandidates(splitCandidates);
        List<Integer> voterKeyList = convertFileToList(voterKeysFile);
        boolean validVoterKeys = validateVoterKeys(voterKeyList);


        if (validDateTime && validName && validCandidates && validVoterKeys) {
            election = new Election(startDate, startTime, endDate, endTime, name, splitCandidates, new HashSet<>(voterKeyList));
            accountService.initAccountService(new HashSet<>(voterKeyList));
            return true;
        }

        return false;
    }

    /**
     * Returns current Election object (singleton).
     * @return Election
     */
    public Election getElection() {
        if (election == null) {
            System.out.println("Election has not been configured yet.");
        }
        return election;
    }
    public boolean electionIsConfigured() {
        return election != null;
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
        if (startTime.isBefore(currentTime) || endTime.isBefore(currentTime)) {
            return false;
        }
        if (startDate.equals(endDate)) {
            if (endTime.isBefore(startTime) || startTime.equals(endTime)) {
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

        if (candidates == null || candidates.size() <= 1) {
            return false;
        }

        Set<String> uniqueCandidates = new HashSet<>(candidates);
        return uniqueCandidates.size() == candidates.size();
    }

    private boolean validateVoterKeys(List<Integer> voterKeyList) {
        if (voterKeyList == null || voterKeyList.isEmpty()) {
            return false;
        }
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


