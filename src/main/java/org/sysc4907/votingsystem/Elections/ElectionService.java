package org.sysc4907.votingsystem.Elections;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.sysc4907.votingsystem.Accounts.AccountService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service class responsible for logic of processing poll configuration.
 */
@Service
public class ElectionService {


    private Election election;

    private final AccountService accountService;

    private Set<Integer> voterKeysList;
    private List<String> candidatesList;

    public ElectionService(AccountService accountService) {
        this.accountService = accountService;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public boolean createElection(ElectionForm electionForm) {
        election = new Election(electionForm.getStartDateTime(), electionForm.getEndDateTime(), electionForm.getName(), candidatesList, voterKeysList);
        accountService.initAccountService(new HashSet<>(voterKeysList));
        return true;
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



    public boolean validateCandidates(String candidates, BindingResult bindingResult) {
        List<String> candidatesList = Arrays.asList(candidates.split("\\r?\\n"));
        if (candidates == null || candidatesList.size() <= 1) {
            return false;
        }

        Set<String> uniqueCandidates = new HashSet<>(candidatesList);

        if (uniqueCandidates.size() != candidatesList.size()) {
            bindingResult.rejectValue("candidates", "invalid", "There can be no duplicate candidates.");
        }

        this.candidatesList = candidatesList;
        return true;
    }

    public boolean validateVoterKeys(MultipartFile voterKeys, BindingResult bindingResult) {
        List<Integer> voterKeyList = convertFileToList(voterKeys);

        if (voterKeyList == null || voterKeyList.isEmpty()) {
            return false;
        }
        Set<Integer> uniqueKeys = new HashSet<>(voterKeyList);
        if (uniqueKeys.size() != voterKeyList.size()) {
            bindingResult.rejectValue("voterKeys", "invalid", "There can be no duplicate voter keys.");
            return false;
        }
        this.voterKeysList = uniqueKeys;
        return true;
    }

    public boolean validateDateTime(ElectionForm electionForm, BindingResult bindingResult){
        LocalDateTime startDateTime = electionForm.getStartDateTime();
        LocalDateTime endDateTime = electionForm.getEndDateTime();

        if (startDateTime == null || endDateTime == null) {
            bindingResult.reject("invalidDateTime", "Start and End Date/Time must be provided.");
            return false;
        }

        if (endDateTime.isBefore(startDateTime)) {
            bindingResult.rejectValue("endDate", "invalid", "End date and time must be after start date and time.");
            return false;
        }

        return true;

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


