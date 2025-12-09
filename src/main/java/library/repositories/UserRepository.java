package library.repositories;

import library.models.User;
import library.utils.GsonUtils;
import library.utils.JsonFileHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for user data management using JSON file storage
 * @author Library Team
 * @version 1.0
 */
public class UserRepository {

    public static final String FILE_PATH = "data/users.json";

    private Map<String, User> users;
    private Gson gson;
    private JsonFileHandler fileHandler;

    /**
     * Default constructor
     */
    public UserRepository() {
        this.gson = GsonUtils.createGson();
        this.fileHandler = new JsonFileHandler();
        this.users = loadUsers();
    }

    /**
     * Constructor for testing (dependency injection)
     */
    public UserRepository(JsonFileHandler fileHandler, Gson gson) {
        this.gson = (gson != null ? gson : GsonUtils.createGson());
        this.fileHandler = (fileHandler != null ? fileHandler : new JsonFileHandler());
        this.users = loadUsers();
    }

    /**
     * Load users from JSON file
     * @return map of users
     */
    private Map<String, User> loadUsers() {
        String json = fileHandler.readFromFile(FILE_PATH);

        if (json == null || json.trim().isEmpty() || json.trim().equals("{}"))
            return new HashMap<>();

        try {
            Type type = new TypeToken<Map<String, User>>() {}.getType();
            Map<String, User> loadedUsers = gson.fromJson(json, type);
            return (loadedUsers != null ? loadedUsers : new HashMap<>());
        } catch (Exception e) {
            System.err.println("Error loading users from JSON: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Save users to JSON file
     * @return true if success
     */
    private boolean saveUsers() {
        try {
            String json = gson.toJson(users);
            return fileHandler.writeToFile(FILE_PATH, json);
        } catch (Exception e) {
            System.err.println("Error saving users: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generate unique ID
     */
    private String generateId() {
        return "USER_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
    }

    /**
     * Save user
     */
    public boolean save(User user) {
        if (user.getId() == null) {
            user.setId(generateId());
        }
        user.updateTimestamp();
        users.put(user.getId(), user);
        return saveUsers();
    }

    /**
     * Find by email
     */
    public User findByEmail(String email) {
        return users.values().stream()
                .filter(u -> email.equals(u.getEmail()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Find by ID
     */
    public User findById(String id) {
        return users.get(id);
    }

    /**
     * Get all users
     */
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * Delete user
     */
    public boolean delete(String id) {
        User removed = users.remove(id);
        return (removed != null) && saveUsers();
    }
}
