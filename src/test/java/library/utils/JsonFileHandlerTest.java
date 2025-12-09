package library.utils;

import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class JsonFileHandlerTest {

    private JsonFileHandler handler;
    private String testFilePath;

    @BeforeEach
    void setup() {
        handler = new JsonFileHandler();
        testFilePath = "src/test/resources/tmp/test-file.json";

        // حذف الملف قبل كل اختبار
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
        Files.createDirectories(Paths.get("src/test/resources/tmp"));
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

    @Test
    void testWriteToFile_IOException() {
        JsonFileHandler brokenHandler = new JsonFileHandler() {
            @Override
            public boolean writeToFile(String filePath, String content) {
                return super.writeToFile("Z:/invalid-path/not-allowed.json", content); // يسبب IOException مضمونة
            }
        };

        boolean result = brokenHandler.writeToFile("unused.json", "{}");

        assertFalse(result);
    }

    @Test
    void testReadFromFile_IOException() {
        JsonFileHandler brokenHandler = new JsonFileHandler() {
            @Override
            public String readFromFile(String filePath) {
                return super.readFromFile("Z:/invalid-path/not-allowed.json"); // يسبب IOException
            }
        };

        String result = brokenHandler.readFromFile("unused.json");

        assertEquals("", result); // لأن الكلاس يرجع "" عند الخطأ
    }
}
