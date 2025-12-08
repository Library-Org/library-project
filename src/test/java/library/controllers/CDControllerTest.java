package library.controllers;

import library.models.CD;
import library.services.CDService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CDControllerTest {

    @Mock
    private CDService cdService;

    @InjectMocks
    private CDController cdController;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    // ---------------- addCD() -----------------------

    @Test
    void testAddCD_Success() {
        when(cdService.addCD(anyString(), anyString(), anyString(),
                anyInt(), anyString(), anyInt()))
                .thenReturn(true);

        cdController.addCD("Title", "Artist", "Rock", 10, "Publisher", 2020);

        assertEquals("", outContent.toString().trim());
    }

    @Test
    void testAddCD_Failure() {
        when(cdService.addCD(anyString(), anyString(), anyString(),
                anyInt(), anyString(), anyInt()))
                .thenReturn(false);

        cdController.addCD("Title", "Artist", "Rock", 10, "Publisher", 2020);

        assertTrue(outContent.toString().contains("Failed to add CD"));
    }

    // ---------------- searchCDs() -----------------------

    @Test
    void testSearchCDs_NoResults() {
        when(cdService.searchCDs("abc")).thenReturn(Collections.emptyList());

        cdController.searchCDs("abc");

        assertTrue(outContent.toString().contains("No CDs found"));
    }

    @Test
    void testSearchCDs_WithResults() {
        CD mockCd = new CD("Test Title", "Artist", "Rock", 10, "Pub", 2020);
        mockCd.setId("12345ABCDE");
        when(cdService.searchCDs("test")).thenReturn(Collections.singletonList(mockCd));

        cdController.searchCDs("test");

        assertTrue(outContent.toString().contains("CD Search Results"));
        assertTrue(outContent.toString().contains("Test Title"));
    }

    // ---------------- viewAllCDs() -----------------------

    @Test
    void testViewAllCDs_NoResults() {
        when(cdService.getAllCDs()).thenReturn(Collections.emptyList());

        cdController.viewAllCDs();

        assertTrue(outContent.toString().contains("No CDs available"));
    }

    @Test
    void testViewAllCDs_WithResults() {
        CD cd = new CD("T", "A", "G", 5, "P", 2020);
        cd.setId("ABCDE12345");
        when(cdService.getAllCDs()).thenReturn(List.of(cd));

        cdController.viewAllCDs();

        assertTrue(outContent.toString().contains("All CDs"));
        assertTrue(outContent.toString().contains("ABCDE"));
    }

    // ---------------- viewCDsByArtist() -----------------------

    @Test
    void testViewCDsByArtist_Empty() {
        when(cdService.getCDsByArtist("John")).thenReturn(Collections.emptyList());

        cdController.viewCDsByArtist("John");

        assertTrue(outContent.toString().contains("No CDs found for artist"));
    }

    @Test
    void testViewCDsByArtist_WithData() {
        CD cd = new CD("T", "John", "Pop", 5, "P", 2020);
        cd.setId("AAAAA11111");
        when(cdService.getCDsByArtist("John")).thenReturn(List.of(cd));

        cdController.viewCDsByArtist("John");

        assertTrue(outContent.toString().contains("CDs by John"));
    }

    // ---------------- viewCDsByGenre() -----------------------

    @Test
    void testViewCDsByGenre_Empty() {
        when(cdService.getCDsByGenre("Rock")).thenReturn(Collections.emptyList());

        cdController.viewCDsByGenre("Rock");

        assertTrue(outContent.toString().contains("No CDs found in genre"));
    }

    @Test
    void testViewCDsByGenre_WithData() {
        CD cd = new CD("T", "A", "Rock", 7, "P", 2020);
        cd.setId("ZZZZZ99999");
        when(cdService.getCDsByGenre("Rock")).thenReturn(List.of(cd));

        cdController.viewCDsByGenre("Rock");

        assertTrue(outContent.toString().contains("Rock CDs"));
    }

    // ---------------- shortenString() -----------------------

    @Test
    void testShortenString_NoShortening() {
        String result = invokeShortenString("Hello", 10);
        assertEquals("Hello", result);
    }

    @Test
    void testShortenString_Shortened() {
        String result = invokeShortenString("LongStringHere", 8);
        assertEquals("LongS...", result);
    }

    @Test
    void testShortenString_Null() {
        String result = invokeShortenString(null, 10);
        assertEquals("", result);
    }

    // Use reflection to call private method
    private String invokeShortenString(String input, int len) {
        try {
            var method = CDController.class.getDeclaredMethod("shortenString", String.class, int.class);
            method.setAccessible(true);
            return (String) method.invoke(cdController, input, len);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
