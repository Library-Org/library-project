package library.repositories;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import library.models.User;
import library.utils.JsonFileHandler;
import library.utils.GsonUtils;

import java.lang.reflect.Type;
import java.util.*;

/**
 * UserRepository — compatible, test-friendly, and non-invasive.
 */
public class UserRepository {

    public static String FILE_PATH = "data/users.json";

    private final String filePath;
    private final JsonFileHandler fileHandler;
    private final Gson gson;

    private final Type mapType = new TypeToken<Map<String, User>>() {}.getType();
    private Map<String, User> users = new HashMap<>();

    // ---------- Constructors ----------

    public UserRepository() {
        this(FILE_PATH, new JsonFileHandler(), GsonUtils.createGson());
    }

    public UserRepository(String filePath, JsonFileHandler fileHandler, Gson gson) {
        this.filePath = (filePath != null) ? filePath : FILE_PATH;
        this.fileHandler = (fileHandler != null) ? fileHandler : new JsonFileHandler();
        this.gson = (gson != null) ? gson : GsonUtils.createGson();
        loadUsers();
    }

    public UserRepository(JsonFileHandler fileHandler, Gson gson, String filePath) {
        this(filePath, fileHandler, gson);
    }

    // ---------- Loading / Saving ----------

    private void loadUsers() {
        try {
            String json = fileHandler.readFromFile(this.filePath);

            if (json == null || json.trim().isEmpty() || json.trim().equals("{}")) {
                users = new HashMap<>();
                return;
            }

            Map<String, User> loaded = gson.fromJson(json, mapType);
            users = (loaded != null) ? loaded : new HashMap<>();

        } catch (Exception e) {
            System.err.println("UserRepository.loadUsers() error: " + e.getMessage());
            users = new HashMap<>();
        }
    }

    private boolean saveUsers() {
        try {
            String json = gson.toJson(users);
            return fileHandler.writeToFile(this.filePath, json);
        } catch (Exception e) {
            System.err.println("UserRepository.saveUsers() error: " + e.getMessage());
            return false;
        }
    }

    // ---------- ID Generator ----------

    private String generateId() {
        return "USER_" + UUID.randomUUID();
    }

    // ---------- CRUD ----------

    public boolean save(User user) {
        if (user == null) return false;

        if (user.getId() == null || user.getId().trim().isEmpty()) {
            user.setId(generateId());
            user.setCreatedAtDateTime(java.time.LocalDateTime.now());
        }

        user.setUpdatedAtDateTime(java.time.LocalDateTime.now());

        users.put(user.getId(), user);
        return saveUsers();
    }

    public User findById(String id) {
        if (id == null) return null;
        return users.get(id);
    }

    public User findByEmail(String email) {
        if (email == null) return null;

        return users.values().stream()
                .filter(u -> email.equals(u.getEmail()))
                .findFirst()
                .orElse(null);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public boolean delete(String id) {
        if (id == null) return false;

        User removed = users.remove(id);
        if (removed != null) {
            return saveUsers();
        }
        return false;
    }

    public void clearAll() {
        users.clear();
        saveUsers();
    }
}

