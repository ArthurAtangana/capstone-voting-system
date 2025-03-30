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

    // Dynamically deriving the correct path for the tool based on the users OS.
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
    private enum Commands {
        GENERATING_PRIVATE_KEY("genkey"),
        GENERATING_PUBLIC_KEY("pubout"),
        GENERATING_AGGREGATED_RING_PUBLIC_KEYS("fold-pub"),
        GENERATING_SIGNATURE("sign"),
        VERIFYING_SIGNATURE("verify");

        private final String command;

        Commands(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }
    }
    public enum VerificationResponse {
        VERIFIED_OK("Verified OK"),
        VERIFIED_FAIL("Verification Failure");

        private final String message;

        VerificationResponse(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }

    /**
     * Default directory where all generated files are stored.
     */
    public static final String DEFAULT_LIRISI_GENERATED_DIR = "lirisi-generated-files/";
    /**
     * Default filename for the file containing a condensed form of all the public keys of the ring members (used during signature generation & verification).
     */
    public static final String DEFAULT_AGGREGATED_RING_PUBLIC_KEYS_FILENAME = "agg-ring-pub-keys.pem";
    /**
     * Default file name to store the voter's sigining key.
     */
    public static final String DEFAULT_SIGNING_KEY_FILENAME = "signing-key.pem";


    /**
     * Generate private key and output to the given 'out' file name.
     * @param out the file name to output the generated private key
     * @throws InterruptedException
     * @throws IOException
     */
    public String genPrivateKey(String out) throws InterruptedException, IOException {
        if (! out.isEmpty()) {runCommand(TOOL_PATH, Commands.GENERATING_PRIVATE_KEY.getCommand(), "-out", out); return "";}
        else return runCommand(TOOL_PATH, Commands.GENERATING_PRIVATE_KEY.getCommand());
        //getCommandOutput(TOOL_NAME, Commands.GENERATING_PRIVATE_KEY.getCommand(), "-out", out);

    }

    /**
     * Generate public key for given private key, and output to the given 'out' file name.
     * @param privateKeyFilePath
     * @param out the file name to output the generated public key
     * @throws InterruptedException
     * @throws IOException
     */
    public String genPublicKey(String privateKeyFilePath, String out) throws IOException, InterruptedException {
        if (! out.isEmpty()) {runCommand(TOOL_PATH, Commands.GENERATING_PUBLIC_KEY.getCommand(), "-in",privateKeyFilePath, "-out", out); return "";}
        else return runCommand(TOOL_PATH, Commands.GENERATING_PUBLIC_KEY.getCommand(), "-in",privateKeyFilePath);

    }


    /**
     * Folds public keys into a single file.
     *
     * @param out           the file name to output the generated folded public keys
     * @param directoryPath where are public keys are stored (must have at least two public keys)
     * @throws IOException
     * @throws InterruptedException
     */
    public String genAggregatedPublicKeysFile(String out, String directoryPath) throws IOException, InterruptedException {
        if (!out.isEmpty()) {runCommand(TOOL_PATH, Commands.GENERATING_AGGREGATED_RING_PUBLIC_KEYS.getCommand(), "-inpath", directoryPath, "-out", out); return "";}
        else return runCommand(TOOL_PATH, Commands.GENERATING_AGGREGATED_RING_PUBLIC_KEYS.getCommand(), "-inpath", directoryPath);
    }

    /**
     * Generates a ring signature.
     * @param message being signed
     * @param privateKeyFilePath name of the file containing the signer's private key
     * @param aggregatedPublicKeysFilePath name of the file containing all ring members public keys
     * @param out the file name to output the generated signature
     * @throws IOException
     * @throws InterruptedException
     */
    public String signMessage(String message, String privateKeyFilePath, String aggregatedPublicKeysFilePath, String out) throws IOException, InterruptedException {
        if (!out.isEmpty()) {
            runCommand(TOOL_PATH, Commands.GENERATING_SIGNATURE.getCommand(), "-message", message, "-inpub", aggregatedPublicKeysFilePath, "-inkey", privateKeyFilePath, "-out", out);
            return "";
        } else {
            return runCommand(TOOL_PATH, Commands.GENERATING_SIGNATURE.getCommand(), "-message", message, "-inpub", aggregatedPublicKeysFilePath, "-inkey", privateKeyFilePath);
        }
    }
    private String ensurePemFormat(String signingKey){
        if (signingKey.contains("-----BEGIN EC PRIVATE KEY-----") && signingKey.contains("-----END EC PRIVATE KEY-----")){
            return signingKey;
        }
        return "-----BEGIN EC PRIVATE KEY-----\n" + signingKey + "\n-----END EC PRIVATE KEY-----";
    }

    /**
     * Attempts to sign a message using the provided signing key.
     * @param signingKey - the private signing key used to sign the message
     * @return true if signature generation was successful (i.e., valid key was provided), otherwise false.
     */
    public boolean verifySigningKey(String signingKey, String signingKeyFilePath, String aggregatedPublicKeysFilePath){
        String pemContent = ensurePemFormat(signingKey);
        FileHelper.createFile(signingKeyFilePath, pemContent);
        try {
            signMessage("test", signingKeyFilePath, aggregatedPublicKeysFilePath , "");
            return true;
        } catch (Exception e){
            System.out.println("Couldn't generate signature");
            FileHelper.deleteFileIfExists(signingKeyFilePath); //
            return false;
        }
    }

    /**
     * Verifies that a ring signature is valid for a particular message.
     * A signature is valid if it was signed by one of the ring members.
     * @param message the message that was signed
     * @param signatureFilePath name of the file containing the signature
     * @param aggregatedPublicKeysFilePath name of the file containing all ring members public keys
     * @return true when signature is valid, otherwise false
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean verifySignature(String message, String signatureFilePath, String aggregatedPublicKeysFilePath) throws IOException, InterruptedException {
        String response = runCommand(TOOL_PATH, Commands.VERIFYING_SIGNATURE.getCommand(), "-message",message, "-inpub", aggregatedPublicKeysFilePath, "-in", signatureFilePath);
        if (response.contains(VerificationResponse.VERIFIED_OK.getMessage())){
            return true;
        }else if (response.contains(VerificationResponse.VERIFIED_FAIL.getMessage())) {
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
                if (! command[1].equals(Commands.VERIFYING_SIGNATURE.getCommand())) { // when verification of signature fails, we expect exit code 0 (no exception needed)
                    throw new RuntimeException("Command failed: " + String.join(" ", command));
                }
            }
            return output; // Return captured output as a string
        }
    }

    /**
     * Generates the ring signature keys in the keysDirectory.
     * @param numRingMembers - the number of ring members
     * @param keysDirectory - the directory to store the keys
     */
    public void generateRingSignatureKeys(int numRingMembers, String keysDirectory) {
        try {
            String privateKeysDir = keysDirectory + "/priv/";
            String publicKeysDir = keysDirectory + "/pub/";

            // Create directory for keys
            Files.createDirectories(Paths.get(privateKeysDir));
            Files.createDirectories(Paths.get(publicKeysDir));

            // Generate individual public keys for a list of users
            for (int i = 1; i <= numRingMembers; i++) {
                // Note: The private keys are stored merely for testing purposes,
                // so we can select a private key to use for testing...
                // In reality this would not be stored in our system at all,
                // and would be distributed to voters by the adminstrating authority.
                String privateKeyFile = privateKeysDir + i + ".pem";
                genPrivateKey(privateKeyFile);
                genPublicKey(privateKeyFile, publicKeysDir + i + ".pem");
            }
            genAggregatedPublicKeysFile(keysDirectory + DEFAULT_AGGREGATED_RING_PUBLIC_KEYS_FILENAME, publicKeysDir );

            // Don't need the individual public keys, just need the folded public keys file
            for (int i = 1; i <= numRingMembers; i++) {
                FileHelper.deleteFileIfExists(publicKeysDir + i + ".pem");
            }
            FileHelper.deleteFileIfExists(publicKeysDir);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
