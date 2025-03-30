package org.sysc4907.votingsystem.Registration;

import java.util.Set;

/**
 * Manage and validate sign-in keys provided by the user.
 * Load, store, and track the list of issued sign-in keys.
 * Ensure that each key is used only once.
 *
 * @author Jasmine Gad El Hak
 * @version 1.0
 */

public class SignInKeyService {
    private Set<Integer> signInKeys; // TODO have two lists to distinguish between admin & voter keys
    //private Set<Integer> usedSignInKeys; // TODO would allow us to provide better error messaging by indicating to the user that the key has been used before
    public SignInKeyService(Set<Integer> signInKeys) {
        this.signInKeys = signInKeys;
    }

    /**
     *  Validates sign-in key against list of unused sign-in keys associated with configured election.
     * @param key the sign in key provided by the user for account registration
     * @return true if key is valid, otherwise false
     */
    public boolean keyIsValid(Integer key){
        return signInKeys.contains(key);

    }
    /**
     * Marks a key as used, such that it cannot be used again.
     * @param key the sign in key provided by the user for account registration
     * @return true if key is found and removed successfully, false if key is not found
     */
    public boolean markKeyAsUsed(Integer key) {
        return signInKeys.remove(key);

    }
}
