package org.sysc4907.votingsystem.Ballots;

import java.security.PublicKey;
import java.util.List;
import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.util.Base64;

public class Ballot {
    private final int id; // may want a helper class to generate these. Must be random and unique.
    private final Marks[] markableBoxes; // true if voting for candidate in that row, false otherwise.
    private final String encryptedCandidateOrder;
    private final static long moduloNumber = 9999999999999999L; // must be at least the length of the number of candidates

    public Ballot( int numberOfCandidates, int candidateOrder, boolean[] premarkedBoxes, List<PublicKey> orderKeys) {
        // this isn't big enough for real use but a sufficiently large random number would ensure uniqueness and non-sequential ids
        this.id = (int)(Math.random() * 9999999);
        markableBoxes = new Marks[numberOfCandidates];
        for (int i = 0; i < premarkedBoxes.length; i++) {
            if (premarkedBoxes[i]) {
                markableBoxes[i] = new Marks(true, true);
            } else {
                markableBoxes[i] = new Marks(false, false);
            }
        }
        String temp = Integer.toString(candidateOrder);
        for (PublicKey orderKey : orderKeys) {
            try {
                temp = encrypt(temp, orderKey);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        this.encryptedCandidateOrder = temp;
    }

    /**
     * cast ballot
     * @return true if ballot was cast successfully, false otherwise.
     */
    public boolean castBallot() {
        // interact with blockchain
        return false;
    }

    /**
     * get a list of where the boxes are marked or not.
     * @return array of whether a mark exists or not.
     */
    public boolean[] getMarkValues() {
        boolean[] marks = new boolean[markableBoxes.length];
        for (int i = 0; i < markableBoxes.length; i++) {
            marks[i] = markableBoxes[i].isMarked();
        }
        return marks;
    }

    public void setMarkableBox(int index, Boolean value) {
        markableBoxes[index].setMarked(value);
    }

    public boolean isBoxMarked(int box) {
        return markableBoxes[box].marked;
    }

    public String getEncryptedCandidateOrder() {
        return encryptedCandidateOrder;
    }

    public int getId() {
        return id;
    }

    public boolean isPremark(int box) {
        return markableBoxes[box].isPremark();
    }


    private class Marks {
        public boolean marked;
        public final boolean isPremark; // the auto generated marks for the three ballot system. Can't remove these marks.

        public Marks(boolean marked, boolean isPremark) {
            this.marked = marked;
            this.isPremark = isPremark;
        }

        public boolean isMarked() {
            return marked;
        }

        public void setMarked(boolean marked) {
            if (isPremark) return; // don't allow premarks to change
            this.marked = marked;
        }

        public boolean isPremark() {
            return isPremark;
        }
    }

    public static String encrypt(String data, PublicKey publicKey) throws Exception {
        data = obfuscateOrder(data);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return deObfuscateOrder(new String(decryptedBytes));
    }

    public static String obfuscateOrder(String order) {
        long temp = Long.parseLong(order);
        temp = temp + moduloNumber * (int)(Math.random() * 10);
        return Long.toString(temp);
    }

    public static String deObfuscateOrder(String order) {
        long temp = Long.parseLong(order);
        temp = temp % moduloNumber;
        return Long.toString(temp);
    }
}

