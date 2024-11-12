package org.sysc4907.votingsystem.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sysc4907.votingsystem.Accounts.Account;
import org.sysc4907.votingsystem.Accounts.AccountRepository;
import org.sysc4907.votingsystem.Accounts.AdminAccount;
import org.sysc4907.votingsystem.Accounts.VoterAccount;

import java.util.Optional;

/**
 * Service class responsible for handling authentication logic.
 * This service authenticates users based on their username and password
 * and determines whether the user is an admin, a voter, or if the authentication fails.
 *
 * @author Victoria Malouf
 * @author Jasmine Gad El Hak
 */
@Service
public class AuthenticationService {

    /**
     * Enum representing the possible outcomes of the authentication process.
     */
    public enum Response {
        ADMIN_AUTH_SUCCESS,  // Authentication successful for an admin user
        VOTER_AUTH_SUCCESS,  // Authentication successful for a voter user
        AUTH_FAILED          // Authentication failed due to incorrect username or password
    }

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Authenticates a user by verifying their username and password.
     * It checks the username in the repository, and if found, verifies the password.
     * Based on the account type, it returns the appropriate authentication response.
     *
     * @param userName The username of the user trying to authenticate.
     * @param password The password provided by the user for authentication.
     * @return {@link Response} representing the outcome of the authentication process.
     *         - {@link Response#ADMIN_AUTH_SUCCESS} if the user is an admin and credentials are correct.
     *         - {@link Response#VOTER_AUTH_SUCCESS} if the user is a voter and credentials are correct.
     *         - {@link Response#AUTH_FAILED} if the username is not found or the password is incorrect.
     */
    public Response authenticate(String userName, String password) {

        Optional<Account> account = accountRepository.findById(userName);

        if (account.isPresent()) {
            Account accountFound = account.get();
            if (accountFound.getPassword().equals(password)) {
                if (accountFound instanceof AdminAccount) return Response.ADMIN_AUTH_SUCCESS;
                if (accountFound instanceof VoterAccount) return Response.VOTER_AUTH_SUCCESS;
            }
        }
        return Response.AUTH_FAILED; // username was not present in repository, or password was incorrect
    }
}

