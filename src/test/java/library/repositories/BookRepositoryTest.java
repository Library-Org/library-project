package library.repositories;

import library.models.Book;
import library.utils.JsonFileHandler;
import library.utils.GsonUtils;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookRepositoryTest {

    private BookRepository bookRepository;
    private JsonFileHandler fileHandlerMock;
    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = GsonUtils.createGson();
        fileHandlerMock = mock(JsonFileHandler.class);

        when(fileHandlerMock.readFromFile(anyString())).thenReturn("{}");

        bookRepository = new BookRepository(gson, fileHandlerMock);
    }

    @Test
    void shouldLoadBooksSuccessfully() {
        String json = "{\"1\":{\"id\":\"1\",\"title\":\"Book A\",\"author\":\"Author A\",\"isbn\":\"111\",\"type\":\"BOOK\"}}";

        when(fileHandlerMock.readFromFile(anyString())).thenReturn(json);

        BookRepository repo = new BookRepository(gson, fileHandlerMock);
        List<Book> books = repo.findAll();

        assertEquals(1, books.size());
        assertEquals("Book A", books.get(0).getTitle());
    }

    @Test
    void shouldSaveBooksSuccessfully() {
        when(fileHandlerMock.writeToFile(anyString(), anyString())).thenReturn(true);

        Book book = new Book("Test", "Author", "123", "BOOK");

        boolean result = bookRepository.save(book);

        assertTrue(result);
    }

    @Test
    void shouldFailSavingBooksWhenWritingFails() {
        when(fileHandlerMock.writeToFile(anyString(), anyString())).thenReturn(false);

        Book book = new Book("Fail", "Author", "999", "BOOK");

        boolean result = bookRepository.save(book);

        assertFalse(result);
    }

    @Test
    void shouldFindBookById() {
        when(fileHandlerMock.writeToFile(anyString(), anyString())).thenReturn(true);

        Book book = new Book("Test", "Author", "101", "BOOK");
        bookRepository.save(book);

        Book found = bookRepository.findById(book.getId());

        assertNotNull(found);
        assertEquals("101", found.getIsbn());
    }

    @Test
    void shouldDeleteExistingBook() {
        when(fileHandlerMock.writeToFile(anyString(), anyString())).thenReturn(true);

        Book book = new Book("Delete", "Author", "555", "BOOK");
        bookRepository.save(book);

        boolean result = bookRepository.delete(book.getId());

        assertTrue(result);
    }

    @Test
    void shouldFailToDeleteNonExistingBook() {
        boolean result = bookRepository.delete("NOT_EXIST");

        assertFalse(result);
    }

    @Test
    void shouldSearchBooksCorrectly() {
        Book b1 = new Book("Java Basics", "Alice", "111", "BOOK");
        Book b2 = new Book("Python Guide", "Bob", "222", "BOOK");

        when(fileHandlerMock.writeToFile(anyString(), anyString())).thenReturn(true);

        bookRepository.save(b1);
        bookRepository.save(b2);

        List<Book> results = bookRepository.search("java");

        assertEquals(1, results.size());
        assertEquals("Java Basics", results.get(0).getTitle());
    }

    @Test
    void shouldLoadEmptyMapWhenJsonIsEmpty() {
        when(fileHandlerMock.readFromFile(anyString())).thenReturn("");

        BookRepository repo = new BookRepository(gson, fileHandlerMock);

        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void shouldHandleJsonLoadingError() {
        when(fileHandlerMock.readFromFile(anyString())).thenReturn("{invalid json");

        BookRepository repo = new BookRepository(gson, fileHandlerMock);

        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void shouldNotFailUpdatingNonExistingBook() {
        Book book = new Book("Ghost", "Unknown", "999", "BOOK");
        book.setId("NON_EXIST");

        boolean result = bookRepository.update(book);

        assertFalse(result);
    }

    @Test
    void shouldUpdateBookSuccessfully() {
        when(fileHandlerMock.writeToFile(anyString(), anyString())).thenReturn(true);

        Book book = new Book("Old Title", "Author A", "12345", "BOOK");
        bookRepository.save(book);

        Book savedBook = bookRepository.findByIsbn("12345");

        assertNotNull(savedBook);
        assertNotNull(savedBook.getId());

        savedBook.setTitle("Updated Title");

        boolean result = bookRepository.update(savedBook);

        assertTrue(result);

        Book updated = bookRepository.findById(savedBook.getId());
        assertEquals("Updated Title", updated.getTitle());
    }
}
