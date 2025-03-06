package org.sysc4907.votingsystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sysc4907.votingsystem.Ballots.Ballot;
import org.sysc4907.votingsystem.Ballots.ThreeBallot;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BallotTest {

    private Ballot b1;
    private java.security.PrivateKey privateKey;
    private java.security.PublicKey publicKey;


    @BeforeEach
    public void setUp() {
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048); // Key size (2048 or higher is recommended)
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
            b1 = new Ballot(3, "201", new boolean[]{false, false, true},  Collections.singletonList(keyPair.getPublic()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testBallot() throws Exception {
        assertEquals("201", Ballot.decrypt(b1.getEncryptedCandidateOrder(), privateKey));
        assertEquals(3, b1.getMarkValues().length);
        assertFalse(b1.getMarkValues()[0]);
        assertFalse(b1.getMarkValues()[1]);
        assertTrue(b1.getMarkValues()[2]);
    }

    @Test
    public void testMarkingBoxes() {
        b1.setMarkableBox(0, true);
        assertTrue(b1.isBoxMarked(0));
        assertFalse(b1.isPremark(0));

        b1.setMarkableBox(0, false);
        assertFalse(b1.isBoxMarked(0));
        assertFalse(b1.isPremark(0));

        // should not be able to change premarked boxes
        b1.setMarkableBox(2, false);
        assertTrue(b1.isBoxMarked(2));
        assertTrue(b1.isPremark(2));
    }

    @Test
    public void testEncryptAndDecrypt() throws Exception {
        List<Integer> nums = new ArrayList<>(Arrays.asList(1,2,3,4,5));
        String data = nums.toString();
        String encrypted = Ballot.encrypt(data, publicKey);
        String decrypted = Ballot.decrypt(encrypted, privateKey);
        assertEquals(data, decrypted);
    }
}
