package org.sysc4907.votingsystem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class LirisiCommandExecutorTest {
    private final String generatedPemFilesDirectory = "target/generated-pem-files"; // it is best practice to store generated files in the target directory
    private final String privateKeyFile = generatedPemFilesDirectory + "/my-private-key.pem";
    private final String publicKeysDirectory = generatedPemFilesDirectory+"/public-keys";
    private final String publicKeyFile = publicKeysDirectory + "/my-public-key.pem";
    private final String foldedPublicKeysFile = generatedPemFilesDirectory + "/folded-public-keys.pem";
    private final String signatureFile = generatedPemFilesDirectory + "/signature.pem";

    private String message = "Hello";
    private LirisiCommandExecutor executor = new LirisiCommandExecutor();

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        // Cleanup any existing files
        System.out.println("Set up cleanup");
        deleteFileIfExists(privateKeyFile);
        deleteFileIfExists(foldedPublicKeysFile);
        deleteFileIfExists(signatureFile);
    }

    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        // Cleanup generated files
        System.out.println("Tear down cleanup");
        deleteFileIfExists(privateKeyFile);
        deleteFileIfExists(foldedPublicKeysFile);
        deleteFileIfExists(signatureFile);
    }
    @Test
    public void creatingRingWithOneMember() throws IOException, InterruptedException {
        try {
            // Generate private key
            assertDoesNotThrow(()->executor.genPrivateKey(privateKeyFile));
            //printFileContents(privateKeyFile);

            // Create directory for public key
            Files.createDirectories(Paths.get(publicKeysDirectory));

            // Generate public key from private key
            assertDoesNotThrow(()->executor.genPublicKey(privateKeyFile, publicKeyFile));
            //printFileContents(publicKeyFile);

            // Fold single public key into file
            assertDoesNotThrow(() -> executor.genFoldedPublicKeyFile(foldedPublicKeysFile, publicKeysDirectory));

            // Sign a message
            assertThrows(RuntimeException.class, () -> executor.signMessage(message,  privateKeyFile, foldedPublicKeysFile, signatureFile)); // exepecting error due to insufficient number of public keys

            //printFileContents(foldedPublicKeysFile);
        } finally {
            // Test-specific Cleanup
            deleteAllPublicKeys(new String[] {});
        }
    }

    @Test
    public void verifyingSignatureWithMinRingSizeTest() throws IOException, InterruptedException {
        try {
            // Generate private key
            assertDoesNotThrow(()->executor.genPrivateKey(privateKeyFile));
            //printFileContents(privateKeyFile);

            // Create one dummy public key
            generateDummyPublicKeys(new String[]{"test1"});

            // Generate public key from private key
            assertDoesNotThrow(()->executor.genPublicKey(privateKeyFile, publicKeyFile));
            //printFileContents(publicKeyFile);

            // Fold single public key into file
            assertDoesNotThrow(() -> executor.genFoldedPublicKeyFile(foldedPublicKeysFile, publicKeysDirectory));

            // Sign a message
            assertDoesNotThrow(() -> executor.signMessage(message,  privateKeyFile, foldedPublicKeysFile, signatureFile));

            //printFileContents(foldedPublicKeysFile);
        } finally {
            // Test-specific Cleanup
            deleteAllPublicKeys(new String[] {"test1"});
        }
    }


    @Test
    public void verifyingSignatureWithNominalRingSizeTest() throws IOException, InterruptedException {
        String[] names = {"Alice", "Bob", "Carol", "Dave", "Eve", "Frank", "George", "Helen", "Iva"};

        try {
            // Generate private key
            executor.genPrivateKey(privateKeyFile);
            //printFileContents(privateKeyFile);

            generateDummyPublicKeys(names);

            // Generate public key from private key
            executor.genPublicKey(privateKeyFile, publicKeyFile);
            //printFileContents(publicKeyFile);

            // Fold all public keys into a single file
            assertDoesNotThrow(() -> executor.genFoldedPublicKeyFile(foldedPublicKeysFile, publicKeysDirectory));
            //printFileContents(foldedPublicKeysFile);

            // Sign a message
            assertDoesNotThrow(() -> executor.signMessage(message,  privateKeyFile, foldedPublicKeysFile, signatureFile));
            //printFileContents(signatureFile);

            // Verify the signature
            assertTrue(executor.verifySignature(message, signatureFile, foldedPublicKeysFile));
            System.out.println("All commands executed successfully.");
        } finally {
            // Test-specific Cleanup
            deleteAllPublicKeys(names);
        }
    }
    @Test
    public void verifyingSignatureAfterAddingNewRingMember() throws IOException, InterruptedException {
        try {
            // Generate private key
            executor.genPrivateKey(privateKeyFile);

            // Create one dummy public key
            generateDummyPublicKeys(new String[]{"test1"});

            // Generate public key from private key
            executor.genPublicKey(privateKeyFile, publicKeyFile);

            // Fold all public keys into a single file
            executor.genFoldedPublicKeyFile(foldedPublicKeysFile, publicKeysDirectory);
            //printFileContents(foldedPublicKeysFile);

            // Sign a message
            executor.signMessage(message, privateKeyFile, foldedPublicKeysFile, signatureFile);
            //printFileContents(signatureFile);

            // Verify the signature
            executor.verifySignature(message, signatureFile, foldedPublicKeysFile);

            // Create another dummy public key
            generateDummyPublicKeys(new String[]{"test2"});

            // Generate new folded public keys fle
            deleteFileIfExists(foldedPublicKeysFile);
            executor.genFoldedPublicKeyFile(foldedPublicKeysFile, publicKeysDirectory);
            //printFileContents(foldedPublicKeysFile);

            // Verify the signature with new folded public keys file
            assertFalse(executor.verifySignature(message, signatureFile, foldedPublicKeysFile)); // verification is expected to fail

            System.out.println("All commands executed successfully.");
        } finally {
            // Test-specific Cleanup
            deleteAllPublicKeys(new String[] {"test1", "test2"});
        }

    }

    /**
     * Generates dummy public keys in a "public-keys" directory.
     * @throws IOException
     * @throws InterruptedException
     */
    public void generateDummyPublicKeys(String[] ringMemberNames) throws IOException, InterruptedException {
        // Create directory for public keys
        Files.createDirectories(Paths.get(publicKeysDirectory));

        // Generate individual public keys for a list of users
        for (String name : ringMemberNames) {
            String privateKeyFile = generatedPemFilesDirectory + "/"+ name + ".pem";
            executor.genPrivateKey(privateKeyFile);
            executor.genPublicKey(privateKeyFile, publicKeysDirectory + "/" + name + ".pem");
            deleteFileIfExists(privateKeyFile); // we don't need the private keys
        }

    }

    /**
     * Deletes all public keys in a "public-keys" directory.
     * @throws IOException
     * @throws InterruptedException
     */
    public void deleteAllPublicKeys(String[] otherRingMemberNames) throws IOException, InterruptedException {
        // Generate individual public keys for a list of users
        for (String name : otherRingMemberNames) {
            deleteFileIfExists(publicKeysDirectory + "/" + name + ".pem");
        }
        deleteFileIfExists(publicKeyFile);
        Files.delete(Paths.get(publicKeysDirectory));

    }
    /**
     * Prints the contents of a file (mainly for debugging purposes)
     * @param fileName the file being output to the console
     */
    public void printFileContents(String fileName) {
        try {
            // Read all bytes from the file and convert to String
            String content = new String(Files.readAllBytes(Paths.get(fileName)));

            // Print the contents of the file
            System.out.println(fileName + " contents:");
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while reading the file.");
        }


    }


    public static void deleteFileIfExists(String filePath) {
        Path path = Paths.get(filePath);
        try {
            if (Files.exists(path)) {
                Files.delete(path);
                System.out.println("Deleted: " + filePath);
            } else {
                //System.out.println("File does not exist: " + filePath);
            }
        } catch (Exception e) {
            System.err.println("Error deleting file " + filePath + ": " + e.getMessage());
        }
    }
}