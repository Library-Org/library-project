package library.repositories;

import library.models.User;
import library.utils.GsonUtils;
import library.utils.JsonFileHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.*;

public class UserRepository {

    static final String FILE_PATH = "data/users.json"; // جعلناه static final لاستخدامه في الاختبار
    private Map<String, User> users;
    private final Gson gson;
    private final JsonFileHandler fileHandler;
    private final SecureRandom secureRandom = new SecureRandom();

    public UserRepository() {
        this.gson = GsonUtils.createGson();
        this.fileHandler = new JsonFileHandler();
        this.users = loadUsers();
    }

    // Constructor خاص للاختبار (dependency injection)
    public UserRepository(JsonFileHandler handler, Gson gson) {
        this.gson = gson;
        this.fileHandler = handler;
        this.users = new HashMap<>();
    }

    private Map<String, User> loadUsers() {
        String json = fileHandler.readFromFile(FILE_PATH);

        if (json == null || json.trim().isEmpty()) {
            return new HashMap<>();
        }

        try {
            Type type = new TypeToken<Map<String, User>>() {}.getType();
            Map<String, User> loaded = gson.fromJson(json, type);
            return loaded != null ? loaded : new HashMap<>();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private boolean saveUsers() {
        try {
            String json = gson.toJson(users);
            return fileHandler.writeToFile(FILE_PATH, json);
        } catch (Exception e) {
            return false;
        }
    }

    // SecureRandom بدل Random
    private String generateId() {
        return "USER_" + System.currentTimeMillis() + "_" + secureRandom.nextInt(1000000);
    }

    public boolean save(User user) {
        if (user.getId() == null) {
            user.setId(generateId());
        }
        user.updateTimestamp();
        users.put(user.getId(), user);
        return saveUsers();
    }

    public User findByEmail(String email) {
        if (email == null) return null;

        return users.values().stream()
                .filter(u -> email.equals(u.getEmail()))
                .findFirst()
                .orElse(null);
    }

    public User findById(String id) {
        return id == null ? null : users.get(id);
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
}
