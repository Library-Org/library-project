package library.repositories;

import library.models.Book;
import library.utils.JsonFileHandler;
import library.utils.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class BookRepository {
    private static final String FILE_PATH = "data/books.json";
    private Map<String, Book> books;
    private Gson gson;
    private JsonFileHandler fileHandler;

    public BookRepository() {
        this(GsonUtils.createGson(), new JsonFileHandler());
    }

    public BookRepository(Gson gson, JsonFileHandler fileHandler) {
        this.gson = gson;
        this.fileHandler = fileHandler;
        this.books = loadBooks();
    }

    private Map<String, Book> loadBooks() {
        String json = fileHandler.readFromFile(FILE_PATH);

        if (json == null || json.trim().isEmpty()) {
            return new HashMap<>();
        }

        try {
            Type type = new TypeToken<Map<String, Book>>() {}.getType();
            Map<String, Book> loadedBooks = gson.fromJson(json, type);
            return loadedBooks != null ? loadedBooks : new HashMap<>();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private boolean saveBooks() {
        try {
            String json = gson.toJson(books);
            return fileHandler.writeToFile(FILE_PATH, json);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean update(Book book) {
        if (books.containsKey(book.getId())) {
            book.updateTimestamp();
            books.put(book.getId(), book);
            return saveBooks();
        }
        return false;
    }

    private String generateId() {
        return "BOOK_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
    }

    public boolean save(Book book) {
        if (book.getId() == null) {
            book.setId(generateId());
        }
        books.put(book.getId(), book);
        return saveBooks();
    }

    public Book findByIsbn(String isbn) {
        return books.values().stream()
                .filter(book -> isbn.equals(book.getIsbn()))
                .findFirst()
                .orElse(null);
    }

    public Book findById(String id) {
        return books.get(id);
    }

    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }

    public List<Book> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(books.values());
        }

        String searchTerm = query.toLowerCase().trim();
        return books.values().stream()
                .filter(book ->
                        book.getTitle().toLowerCase().contains(searchTerm) ||
                        book.getAuthor().toLowerCase().contains(searchTerm) ||
                        book.getIsbn().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    public boolean delete(String id) {
        Book removed = books.remove(id);
        if (removed != null) {
            return saveBooks();
        }
        return false;
    }
}
