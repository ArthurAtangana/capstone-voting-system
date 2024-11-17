package org.sysc4907.votingsystem;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class LirisiCommandExecutorTest {
    private static final String generatedPemFilesDirectory = "target/generated-pem-files"; // it is best practice to store generated files in the target directory
    private final String privateKeyFile = generatedPemFilesDirectory + "/my-private-key.pem";
    private final String publicKeysDirectory = generatedPemFilesDirectory+"/public-keys";
    private final String publicKeyFile = publicKeysDirectory + "/my-public-key.pem";
    private final String foldedPublicKeysFile = generatedPemFilesDirectory + "/folded-public-keys.pem";
    private final String signatureFile = generatedPemFilesDirectory + "/signature.pem";

    private String message = "Hello";
    private LirisiCommandExecutor executor = new LirisiCommandExecutor();

    @BeforeAll
    static void beforeAll() throws IOException {
        Files.createDirectories(Paths.get(generatedPemFilesDirectory));
    }



    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        executor.debugMode = true;

        // Cleanup any existing files
        //System.out.println("Set up cleanup");
        FileHelper.deleteFileIfExists(privateKeyFile);
        FileHelper.deleteFileIfExists(foldedPublicKeysFile);
        FileHelper.deleteFileIfExists(signatureFile);
    }
    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        // Cleanup generated files
        //System.out.println("Tear down cleanup");
        FileHelper.deleteFileIfExists(privateKeyFile);
        FileHelper.deleteFileIfExists(foldedPublicKeysFile);
        FileHelper.deleteFileIfExists(signatureFile);
    }
    @AfterAll
    static void afterAll() throws IOException {
        Files.delete(Paths.get(generatedPemFilesDirectory));
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
            assertDoesNotThrow(() -> executor.genFoldedPublicKeysFile(foldedPublicKeysFile, publicKeysDirectory));

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
            assertDoesNotThrow(() -> executor.genFoldedPublicKeysFile(foldedPublicKeysFile, publicKeysDirectory));

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

            generateDummyPublicKeys(names);

            // Generate public key from private key
            executor.genPublicKey(privateKeyFile, publicKeyFile);

            // Fold all public keys into a single file
            assertDoesNotThrow(() -> executor.genFoldedPublicKeysFile(foldedPublicKeysFile, publicKeysDirectory));
            assertTrue(FileHelper.getFileContents(foldedPublicKeysFile).contains("NumberOfKeys: 10"));

            // Sign a message
            assertDoesNotThrow(() -> executor.signMessage(message,  privateKeyFile, foldedPublicKeysFile, signatureFile));

            // Verify the signature
            assertTrue(executor.verifySignature(message, signatureFile, foldedPublicKeysFile));
            System.out.println("All commands executed successfully.");
        } finally {
            // Test-specific Cleanup
            deleteAllPublicKeys(names);
        }
    }
    @Test
    public void verifyingSignatureWithLargeRingSizeTest() throws IOException, InterruptedException {
        String[] names = new String[100];
        for (int i = 0; i < 100; i++) {
            names[i] = "member" + i;
        }

        try {
            // Generate private key
            executor.genPrivateKey(privateKeyFile);

            executor.debugMode = false; // keep the output clean
            System.out.println("Generating 100 public keys... be patient");
            generateDummyPublicKeys(names);

            // Generate public key from private key
            executor.genPublicKey(privateKeyFile, publicKeyFile);

            System.out.println("Done generating public keys...");
            executor.debugMode = true;

            // Fold all public keys into a single file
            assertDoesNotThrow(() -> executor.genFoldedPublicKeysFile(foldedPublicKeysFile, publicKeysDirectory));
            assertTrue(FileHelper.getFileContents(foldedPublicKeysFile).contains("NumberOfKeys: 101"));

            // Sign a message
            assertDoesNotThrow(() -> executor.signMessage(message,  privateKeyFile, foldedPublicKeysFile, signatureFile));

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
            executor.genFoldedPublicKeysFile(foldedPublicKeysFile, publicKeysDirectory);

            // Sign a message
            executor.signMessage(message, privateKeyFile, foldedPublicKeysFile, signatureFile);

            // Verify the signature
            executor.verifySignature(message, signatureFile, foldedPublicKeysFile);

            // Create another dummy public key
            generateDummyPublicKeys(new String[]{"test2"});

            // Generate new folded public keys fle
            FileHelper.deleteFileIfExists(foldedPublicKeysFile);
            executor.genFoldedPublicKeysFile(foldedPublicKeysFile, publicKeysDirectory);
            assertTrue(FileHelper.getFileContents(foldedPublicKeysFile).contains("NumberOfKeys: 3"));

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
            FileHelper.deleteFileIfExists(privateKeyFile); // we don't need the private keys
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
            FileHelper.deleteFileIfExists(publicKeysDirectory + "/" + name + ".pem");
        }
        FileHelper.deleteFileIfExists(publicKeyFile);
        Files.delete(Paths.get(publicKeysDirectory));

    }



}