package library.utils;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for handling JSON file operations
 * @author Library Team
 * @version 1.0
 */
public class JsonFileHandler {
    
    /**
     * Read content from file
     * @param filePath path to the file
     * @return file content as string
     */
    public String readFromFile(String filePath) {
        try {
            createFileIfNotExists(filePath);
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.err.println("Error reading file " + filePath + ": " + e.getMessage());
            return "";
        }
    }
    
    /**
     * Write content to file
     * @param filePath path to the file
     * @param content content to write
     * @return true if write successful, false otherwise
     */
    public boolean writeToFile(String filePath, String content) {
        try {
            createFileIfNotExists(filePath);
            Files.write(Paths.get(filePath), content.getBytes());
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Create file and directories if they don't exist
     * @param filePath path to the file
     */
    private void createFileIfNotExists(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {

            file.getParentFile().mkdirs();

            boolean created = file.createNewFile();
            if (!created) {
                System.err.println("Warning: Failed to create new file: " + filePath);
            }

            // Initialize with empty JSON object
            Files.write(Paths.get(filePath), "{}".getBytes());
        }
    }

}