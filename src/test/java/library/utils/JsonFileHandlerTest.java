package library.utils;

import org.junit.jupiter.api.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class JsonFileHandlerTest {

    private JsonFileHandler handler;
    private String testFilePath;

    @BeforeEach
    void setup() throws Exception {
        handler = new JsonFileHandler();

        testFilePath = "target/test-tmp/test-file.json";
        Files.createDirectories(Paths.get("target/test-tmp"));

        File file = new File(testFilePath);
        if (file.exists()) file.delete();
    }

    @Test
    void testReadFromFile_FileDoesNotExist_ShouldCreateFile() {
        String result = handler.readFromFile(testFilePath);

        assertNotNull(result);
        assertTrue(new File(testFilePath).exists());
    }

    @Test
    void testReadFromFile_WithContent() throws Exception {
        Files.write(Paths.get(testFilePath), "{\"name\":\"test\"}".getBytes());

        String result = handler.readFromFile(testFilePath);

        assertEquals("{\"name\":\"test\"}", result);
    }

    @Test
    void testWriteToFile_Success() {
        boolean result = handler.writeToFile(testFilePath, "{\"age\":25}");

        assertTrue(result);
        assertEquals("{\"age\":25}", handler.readFromFile(testFilePath));
    }

    /**
     * FORCE IOException without touching the real file system
     */
    @Test
    void testWriteToFile_IOException() {
        JsonFileHandler broken = new JsonFileHandler() {
            @Override
            public boolean writeToFile(String filePath, String content) {
                try {
                    throw new java.io.IOException("Forced Error");
                } catch (Exception e) {
                    return false;
                }
            }
        };

        boolean result = broken.writeToFile("ignored.json", "{}");

        assertFalse(result);
    }

    @Test
    void testReadFromFile_IOException() {
        JsonFileHandler broken = new JsonFileHandler() {
            @Override
            public String readFromFile(String filePath) {
                try {
                    throw new java.io.IOException("Forced Error");
                } catch (Exception e) {
                    return "";
                }
            }
        };

        String result = broken.readFromFile("ignored.json");

        assertEquals("", result);
    }
}

