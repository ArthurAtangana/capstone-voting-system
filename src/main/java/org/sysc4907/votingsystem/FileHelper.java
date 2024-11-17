package org.sysc4907.votingsystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper {
    /**
     * Prints the contents of a file (mainly for debugging purposes)
     *
     * @param filePath the file being output to the console
     * @return the contents of the file as a string
     */
    public static String getFileContents(String filePath) {
        try {
            // Read all bytes from the file and convert to String
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            // Print the contents of the file
            //System.out.println(fileName + " contents:");
            //System.out.println(content);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while reading the file.");
            return "";
        }

    }


    public static void deleteFileIfExists(String filePath) {
        Path path = Paths.get(filePath);
        try {
            if (Files.exists(path)) {
                Files.delete(path);
                //System.out.println("Deleted: " + filePath);
            } else {
                //System.out.println("File does not exist: " + filePath);
            }
        } catch (Exception e) {
            System.err.println("Error deleting file " + filePath + ": " + e.getMessage());
        }
    }
}
