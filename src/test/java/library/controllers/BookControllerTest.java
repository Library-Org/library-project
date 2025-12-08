package library.controllers;

import library.models.Book;
import library.services.BookService;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class BookControllerTest {

    private BookService bookService;
    private BookController controller;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        bookService = mock(BookService.class);
        controller = new BookController(bookService);

        System.setOut(new PrintStream(outContent));  // capture console output
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);      // restore console
        outContent.reset();
    }

    @Test
    void testAddBookSuccess() {
        when(bookService.addBook("A", "B", "111", "BOOK")).thenReturn(true);

        controller.addBook("A", "B", "111", "BOOK");

        assertEquals("", outContent.toString().trim());
    }

    @Test
    void testAddBookFail() {
        when(bookService.addBook("A", "B", "111", "BOOK")).thenReturn(false);

        controller.addBook("A", "B", "111", "BOOK");

        assertTrue(outContent.toString().contains("Failed to add book"));
    }

    @Test
    void testSearchBooksEmpty() {
        when(bookService.searchBooks("java")).thenReturn(Collections.emptyList());

        controller.searchBooks("java");

        assertTrue(outContent.toString().contains("No books found"));
    }

    @Test
    void testSearchBooksResults() {
        Book b = new Book("Java", "James", "111", "BOOK");
        b.setId("BOOK_123456");

        when(bookService.searchBooks("java")).thenReturn(List.of(b));

        controller.searchBooks("java");

        String output = outContent.toString();

        assertTrue(output.contains("=== Search Results ==="));
        assertTrue(output.contains("Java"));
        assertTrue(output.contains("James"));
    }

    @Test
    void testViewAllBooksEmpty() {
        when(bookService.getAllBooks()).thenReturn(Collections.emptyList());

        controller.viewAllBooks();

        assertTrue(outContent.toString().contains("No books available"));
    }

    @Test
    void testViewAllBooksNotEmpty() {
        Book b = new Book("Python", "Guido", "222", "BOOK");
        b.setId("BOOK_99999");

        when(bookService.getAllBooks()).thenReturn(Arrays.asList(b));

        controller.viewAllBooks();

        String output = outContent.toString();

        assertTrue(output.contains("=== All Books ==="));
        assertTrue(output.contains("Python"));
        assertTrue(output.contains("Guido"));
    }

    @Test
    void testShortenStringShorterThanLimit() {
        BookController c = new BookController(bookService);

        String result = invokeShortenString(c, "Hello", 10);

        assertEquals("Hello", result);
    }

    @Test
    void testShortenStringLongerThanLimit() {
        BookController c = new BookController(bookService);

        String result = invokeShortenString(c, "ThisIsVeryLong", 8);

        assertEquals("ThisI...", result);
    }

    // === Utility to invoke private method with reflection === //
    private String invokeShortenString(BookController c, String text, int limit) {
        try {
            var method = BookController.class.getDeclaredMethod("shortenString", String.class, int.class);
            method.setAccessible(true);
            return (String) method.invoke(c, text, limit);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
