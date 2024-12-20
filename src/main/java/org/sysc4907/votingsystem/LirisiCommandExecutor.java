package org.sysc4907.votingsystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Lirisi is a command line tool for creating a "Linkable ring signature".
 * Version: 0.0.1
 *
 * The Lirisi commands supported by this class include:
 *   genkey      - Generate EC private key.
 *   pubout      - Derive public key from private key.
 *   fold-pub    - Fold public keys into one file.
 *   sign        - Sign a message or file.
 *   verify      - Verify signature.
 */
public class LirisiCommandExecutor {
    public boolean debugMode = false;
    public static String TOOL_PATH;
    static {
        String toolDirectoryPath = "/tools/lirisi";
        String osSpecficDirectory;

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            osSpecficDirectory = "/windows-amd64";
        } else if (os.contains("mac")) {
            osSpecficDirectory = "/darwin-amd64";
        } else {
            osSpecficDirectory = "/linux-amd64";
        }

        String resourcePath = toolDirectoryPath + osSpecficDirectory + "/lirisi.exe";
        InputStream lirisiStream = LirisiCommandExecutor.class.getResourceAsStream(resourcePath);
        if (lirisiStream == null) {
            throw new RuntimeException("Resource not found: " + resourcePath);
        }
        // Copy to a temporary file
        try {
            Path tempLirisi = Files.createTempFile("lirisi", ".exe");
            Files.copy(lirisiStream, tempLirisi, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            tempLirisi.toFile().setExecutable(true);
            TOOL_PATH = tempLirisi.toString();
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException("Could not create temp file for executable!");
        }
    }

    public static final String GENERATING_PRIVATE_KEY_CMD = "genkey";
    public static final String GENERATING_PUBLIC_KEY_CMD = "pubout" ;
    public static final String GENERATING_RING_MEMBERS_PUBLIC_KEYS_CMD = "fold-pub";
    public static final String GEN_SIGNATURE_CMD = "sign";
    public static final String VERIFY_SIGNATURE_CMD = "verify";
    public static final String EXPECTED_VERIFIED_OK_RESPONSE = "Verified OK";
    public static final String EXPECTED_VERIFIED_FAIL_RESPONSE = "Verification Failure";


    /**
     * Generate private key and output to the given 'out' file name.
     * @param out the file name to output the generated private key
     * @throws InterruptedException
     * @throws IOException
     */
    public String genPrivateKey(String out) throws InterruptedException, IOException {
        if (! out.isEmpty()) {runCommand(TOOL_PATH, GENERATING_PRIVATE_KEY_CMD, "-out", out); return "";}
        else return runCommand(TOOL_PATH, GENERATING_PRIVATE_KEY_CMD);
        //getCommandOutput(TOOL_NAME, GENERATING_PRIVATE_KEY_CMD, "-out", out);

    }

    /**
     * Generate public key for given private key, and output to the given 'out' file name.
     * @param privateKeyFilePath
     * @param out the file name to output the generated public key
     * @throws InterruptedException
     * @throws IOException
     */
    public String genPublicKey(String privateKeyFilePath, String out) throws IOException, InterruptedException {
        if (! out.isEmpty()) {runCommand(TOOL_PATH, GENERATING_PUBLIC_KEY_CMD, "-in",privateKeyFilePath, "-out", out); return "";}
        else return runCommand(TOOL_PATH, GENERATING_PUBLIC_KEY_CMD, "-in",privateKeyFilePath);

    }


    /**
     * Folds public keys into a single file.
     *
     * @param out           the file name to output the generated folded public keys
     * @param directoryPath where are public keys are stored (must have at least two public keys)
     * @throws IOException
     * @throws InterruptedException
     */
    public String genFoldedPublicKeysFile(String out, String directoryPath) throws IOException, InterruptedException {
        if (!out.isEmpty()) {runCommand(TOOL_PATH, GENERATING_RING_MEMBERS_PUBLIC_KEYS_CMD, "-inpath", directoryPath, "-out", out); return "";}
        else return runCommand(TOOL_PATH, GENERATING_RING_MEMBERS_PUBLIC_KEYS_CMD, "-inpath", directoryPath);
    }

    /**
     * Generates a ring signature.
     * @param message being signed
     * @param privateKeyFilePath name of the file containing the signer's private key
     * @param foldedPublicKeysFilePath name of the file containing all ring members public keys
     * @param out the file name to output the generated signature
     * @throws IOException
     * @throws InterruptedException
     */
    public String signMessage(String message, String privateKeyFilePath, String foldedPublicKeysFilePath, String out) throws IOException, InterruptedException {
        if (!out.isEmpty()) {
            runCommand(TOOL_PATH, GEN_SIGNATURE_CMD, "-message", message, "-inpub", foldedPublicKeysFilePath, "-inkey", privateKeyFilePath, "-out", out);
            return "";
        } else {
            return runCommand(TOOL_PATH, GEN_SIGNATURE_CMD, "-message", message, "-inpub", foldedPublicKeysFilePath, "-inkey", privateKeyFilePath);
        }
    }
    /**
     * Verifies that a ring signature is valid for a particular message.
     * A signature is valid if it was signed by one of the ring members.
     * @param message the message that was signed
     * @param signatureFilePath name of the file containing the signature
     * @param foldedPublicKeysFilePath name of the file containing all ring members public keys
     * @return true when signature is valid, otherwise false
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean verifySignature(String message, String signatureFilePath, String foldedPublicKeysFilePath) throws IOException, InterruptedException {
        String response = runCommand(TOOL_PATH, VERIFY_SIGNATURE_CMD, "-message",message, "-inpub", foldedPublicKeysFilePath, "-in", signatureFilePath);
        if (response.contains(EXPECTED_VERIFIED_OK_RESPONSE)){
            return true;
        }else if (response.contains(EXPECTED_VERIFIED_FAIL_RESPONSE)) {
            return false;
        } else {
            throw new RuntimeException("verification gave unexpected results: " + response);
        }
    }

    /**
     * Executes and returns the output of the provided command.
     * @param command an array of strings to construct a command
     * @return the output of the command
     * @throws IOException
     * @throws InterruptedException
     */
    private String runCommand(String... command) throws IOException, InterruptedException {
        if (debugMode) {
            System.out.print("executing: ");
            Arrays.stream(command).forEach(cmd -> System.out.print(cmd + " "));
            System.out.println();
        }
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        // Capture output using BufferedReader and InputStreamReader
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String output = reader.lines().collect(Collectors.joining(System.lineSeparator()));

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                if (! command[1].equals(VERIFY_SIGNATURE_CMD)) { // when verification of signature fails, we expect exit code 0 (no exception needed)
                    throw new RuntimeException("Command failed: " + String.join(" ", command));
                }
            }
            return output; // Return captured output as a string
        }
    }

}
