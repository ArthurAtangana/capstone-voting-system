package org.sysc4907.votingsystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
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
    public static final String TOOL_NAME = "lirisi";
    public static final String GENERATING_PRIVATE_KEY_CMD = "genkey";
    public static final String GENERATING_PUBLIC_KEY_CMD = "pubout" ;
    public static final String GENERATING_RING_MEMBERS_PUBLIC_KEYS_CMD = "fold-pub";
    public static final String GEN_SIGNATURE_CMD = "sign";
    public static final String VERIFY_SIGNATURE_CMD = "verify";
    public static final String EXPECTED_VERIFIED_OK_RESPONSE = "Verified OK";
    public static final String EXPECTED_VERIFIED_FAIL_RESPONSE = "Verification Failure";


    /**
     * Generate and return private key.
     * @throws InterruptedException
     * @throws IOException
     */
    public String genPrivateKey() throws InterruptedException, IOException {
        return getCommandOutput(TOOL_NAME, GENERATING_PRIVATE_KEY_CMD);
    }

    /**
     * Generate private key and output to the given 'out' file name.
     * @param out the file name to output the generated private key
     * @throws InterruptedException
     * @throws IOException
     */
    public void genPrivateKey(String out) throws InterruptedException, IOException {
        runCommand(TOOL_NAME, GENERATING_PRIVATE_KEY_CMD, "-out", out);
        //getCommandOutput(TOOL_NAME, GENERATING_PRIVATE_KEY_CMD, "-out", out);

    }
    /**
     * Generate public key for given private key, and output to the given 'out' file name.
     * @param privateKeyFileName
     * @param out the file name to output the generated public key
     * @throws InterruptedException
     * @throws IOException
     */
    public void genPublicKey(String privateKeyFileName, String out) throws IOException, InterruptedException {
        runCommand(TOOL_NAME, GENERATING_PUBLIC_KEY_CMD, "-in",privateKeyFileName, "-out", out);
    }

    /**
     * Generates dummy public keys in a "public-keys" directory.
     * @param directory the directory where dummy public keys are stored
     * @throws IOException
     * @throws InterruptedException
     */
    public void generateDemoPublicKeys(String directory) throws IOException, InterruptedException {
        // Create directory for public keys
        Files.createDirectories(Paths.get(directory));

        // Generate individual public keys for a list of users
        String[] names = {"Alice", "Bob", "Carol", "Dave", "Eve", "Frank", "George", "Helen", "Iva"};
        for (String name : names) {
            runPipeCommand(
                    Arrays.asList(TOOL_NAME, GENERATING_PRIVATE_KEY_CMD),
                    Arrays.asList(TOOL_NAME, GENERATING_PUBLIC_KEY_CMD, "-in", "-", "-out", directory + "/" + name + ".pem")
            );
        }
    }

    /**
     * Folds public keys into a single file.
     * @param out the file name to output the generated folded public keys
     * @param directory where are public keys are stored
     * @throws IOException
     * @throws InterruptedException
     */
    public void genFoldedPublicKeyFile(String out, String directory) throws IOException, InterruptedException {
        runCommand(TOOL_NAME, GENERATING_RING_MEMBERS_PUBLIC_KEYS_CMD, "-inpath", directory, "-out", out);
    }

    /**
     * Generates a ring signature.
     * @param message being signed
     * @param privateKeyFileName name of the file containing the signer's private key
     * @param foldedPublicKeyFile name of the file containing all ring members public keys
     * @param out the file name to output the generated signature
     * @throws IOException
     * @throws InterruptedException
     */
    public void signMessage(String message, String privateKeyFileName, String foldedPublicKeyFile, String out) throws IOException, InterruptedException {
        runCommand(TOOL_NAME, GEN_SIGNATURE_CMD, "-message", message, "-inpub", foldedPublicKeyFile, "-inkey", privateKeyFileName, "-out", out);
    }
    /**
     * Verifies that a ring signature is valid for a particular message.
     * A signature is valid if it was signed by one of the ring members.
     * @param message the message that was signed
     * @param signatureFile name of the file containing the signature
     * @param foldedPublicKeyFile name of the file containing all ring members public keys
     * @return true when signature is valid, otherwise false
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean verifySignature(String message, String signatureFile, String foldedPublicKeyFile) throws IOException, InterruptedException {
        String response = getCommandOutput(TOOL_NAME, VERIFY_SIGNATURE_CMD, "-message",message, "-inpub", foldedPublicKeyFile, "-in", signatureFile);
        if (response.contains(EXPECTED_VERIFIED_OK_RESPONSE)){
            return true;
        }else if (response.contains(EXPECTED_VERIFIED_FAIL_RESPONSE)) {
            return false;
        } else {
            throw new RuntimeException("verification gave unexpected results: " + response);
        }
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

    /**
     * Runs the specified command.
     * @param command an array of strings to construct a command with all desired options/flags
     * @throws IOException
     * @throws InterruptedException
     */
    private void runCommand(String... command) throws IOException, InterruptedException {
        System.out.print("executing: ");
        Arrays.stream(command).forEach(cmd -> System.out.print(cmd + " "));
        System.out.println();
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.inheritIO(); // Outputs the process's streams directly to the console
        Process process = builder.start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed: " + String.join(" ", command));
        }
    }

    /**
     * Executed and returns the output of the provided command.
     * @param command an array of strings to construct a command
     * @return the output of the command
     * @throws IOException
     * @throws InterruptedException
     */
    private String getCommandOutput(String... command) throws IOException, InterruptedException {
        System.out.print("executing: ");
        Arrays.stream(command).forEach(cmd -> System.out.print(cmd + " "));
        System.out.println();
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        // Capture output using BufferedReader and InputStreamReader
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String output = reader.lines().collect(Collectors.joining(System.lineSeparator()));

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Command failed: " + String.join(" ", command));
            }
            return output; // Return captured output as a string
        }
    }
    /**
     * Runs a command pipe command (command that takes the output of a previous command as input)
     * @param command1 an array of strings to construct first command
     * @param command2 an array of strings to construct second command
     * @throws IOException
     * @throws InterruptedException
     */
    private void runPipeCommand(List<String> command1, List<String> command2) throws IOException, InterruptedException {
        // Create a temporary file to hold the output of the first command
        Path tempFile = Files.createTempFile("tempOutput", ".tmp");

        // First command: write output to the temporary file
        ProcessBuilder builder1 = new ProcessBuilder(command1);
        builder1.redirectOutput(tempFile.toFile()); // Redirect output to temp file
        Process process1 = builder1.start();

        int exitCode1 = process1.waitFor();

        if (exitCode1 != 0) {
            throw new RuntimeException("First command failed: " + String.join(" ", command1));
        }

        // Second command: read from the temporary file
        // The second command reads from the temporary file created
        ProcessBuilder builder2 = new ProcessBuilder(command2);
        builder2.redirectInput(tempFile.toFile()); // Redirect input from temp file
        Process process2 = builder2.start();

        int exitCode2 = process2.waitFor();
        if (exitCode2 != 0) {
            throw new RuntimeException("Piped command failed: " + String.join(" ", command1) + " | " + String.join(" ", command2));
        }

        // Clean up: delete the temporary file
        Files.delete(tempFile);
    }

}
