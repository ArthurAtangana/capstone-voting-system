package org.sysc4907.votingsystem.Elections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.sysc4907.votingsystem.Accounts.AccountService;
import org.sysc4907.votingsystem.LirisiCommandExecutor;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * Service class responsible for logic of processing poll configuration.
 */
@Service
public class ElectionService {
    private Election election;
    private final AccountService accountService;
    private Set<Integer> voterKeysList;
    private List<String> candidatesList;
    private final List<java.security.PrivateKey> privateOrderKeys = new ArrayList<>();
    private final List<java.security.PublicKey> publicOrderKeys = new ArrayList<>();
    Environment environment;

    @Autowired
    public ElectionService(AccountService accountService,  Environment environment) {
        this.accountService = accountService;
        this.environment = environment;

        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048); // Key size (2048 or higher is recommended)
            KeyPair keyPair = keyGen.generateKeyPair();
            privateOrderKeys.add(keyPair.getPrivate());
            publicOrderKeys.add(keyPair.getPublic());

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean createElection(ElectionForm electionForm) {
        election = new Election(environment, electionForm.getStartDateTime(), electionForm.getEndDateTime(), electionForm.getName(), candidatesList, voterKeysList);
        accountService.initAccountService(new HashSet<>(voterKeysList));
        // generate public & private keys
        LirisiCommandExecutor executor = new LirisiCommandExecutor();
        executor.generateRingSignatureKeys(voterKeysList.size(), LirisiCommandExecutor.DEFAULT_LIRISI_GENERATED_DIR);
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

    /**
     * Should strictly be using validateVoterKeys() to set voterKeysList!
     * Only use for testing purposes.
     * @param voterKeysList of registration keys
     */
    public void setVoterKeysList(Set<Integer> voterKeysList) {
        System.out.println("warning: only use setter for voterKeysList if using /test-elections to configure the elections");
        this.voterKeysList = voterKeysList;
    }

    public AccountService getAccountService() {
        return accountService;
    }
    public boolean electionIsConfigured() {
        return election != null;
    }

    public boolean validateCandidates(String candidates, BindingResult bindingResult) {
        List<String> candidatesList = Arrays.asList(candidates.split("\\r?\\n"));
        if (candidates == null) {
            bindingResult.rejectValue("candidates", "invalid", "List of candidates must be provided.");
            return false;
        }

        if (candidatesList.size() <= 1) {
            bindingResult.rejectValue("candidates", "invalid", "At least two candidates are required.");
            return false;
        }

        Set<String> uniqueCandidates = new HashSet<>(candidatesList);

        if (uniqueCandidates.size() != candidatesList.size()) {
            bindingResult.rejectValue("candidates", "invalid", "There can be no duplicate candidates.");
            return false;
        }

        this.candidatesList = candidatesList;
        return true;
    }

    public boolean validateVoterKeys(MultipartFile voterKeys, BindingResult bindingResult) {
        if (voterKeys == null || voterKeys.isEmpty()) {
            bindingResult.rejectValue("voterKeys", "invalid", "Voter key file is required and cannot be empty.");
            return false;
        }

        List<Integer> voterKeyList = convertFileToList(voterKeys);

        if (voterKeyList == null || voterKeyList.isEmpty()) {
            bindingResult.rejectValue("voterKeys", "invalid", "Voter key file must contain at least one valid key.");
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
        LocalDateTime now = LocalDateTime.now();


        if (endDateTime.isBefore(startDateTime)) {
            bindingResult.rejectValue("endDate", "invalid", "End date and time must be after start date and time.");
            return false;
        }

        if (endDateTime.isBefore(now)) {
            bindingResult.rejectValue("endDate", "invalid", "End date and time must be after the current date and time.");
            return false;
        }

        if (startDateTime.isBefore(now)) {
            bindingResult.rejectValue("startDate", "invalid", "Start date and time must be after the current date and time.");
            return false;
        }

        if (endDateTime.equals(startDateTime)) {
            bindingResult.rejectValue("endDate", "invalid", "End date and time cannot be equal to the start date and time.");
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

    public List<Integer> getTally() {
        return election.tallyOfVotes(privateOrderKeys);
    }

    public int getNumVotesCast() {
        return election.numVotesCast();
    }

    public List<java.security.PublicKey> getPublicOrderKeys() {return publicOrderKeys;}
    public List<java.security.PrivateKey> getPrivateOrderKeys() {return privateOrderKeys;}
}


