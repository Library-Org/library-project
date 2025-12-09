package library.repositories;



import library.models.Fine;
import library.utils.JsonFileHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for fine data management using JSON file storage
 * @author Library Team
 * @version 1.0
 */
public class FineRepository {
    private static final String FILE_PATH = "data/fines.json";
    private Map<String, Fine> fines;
    private Gson gson;
    private JsonFileHandler fileHandler;
    private static final Random RANDOM = new Random();


    public FineRepository() {
        this.gson = new Gson();
        this.fileHandler = new JsonFileHandler();
        this.fines = loadFines();
    }

    /**
     * Load fines from JSON file
     * @return map of fines
     */
    private Map<String, Fine> loadFines() {
        String json = fileHandler.readFromFile(FILE_PATH);
        if (json.isEmpty()) {
            return new HashMap<>();
        }

        Type type = new TypeToken<Map<String, Fine>>(){}.getType();
        Map<String, Fine> loadedFines = gson.fromJson(json, type);
        
        // Generate IDs for fines that don't have them
        for (Fine fine : loadedFines.values()) {
            if (fine.getId() == null) {
                fine.setId(generateId());
            }
        }
        
        return loadedFines;
    }

    /**
     * Save fines to JSON file
     * @return true if save successful, false otherwise
     */
    private boolean saveFines() {
        String json = gson.toJson(fines);
        return fileHandler.writeToFile(FILE_PATH, json);
    }

    /**
     * Generate unique ID for fine
     * @return generated ID
     */
    private String generateId() {
        return "FINE_" + System.currentTimeMillis() + "_" + RANDOM.nextInt(1000);
    }

    /**
     * Save fine to repository
     * @param fine fine to save
     * @return true if save successful, false otherwise
     */
    public boolean save(Fine fine) {
        if (fine.getId() == null) {
            fine.setId(generateId());
        }
        fines.put(fine.getId(), fine);
        return saveFines();
    }

    /**
     * Find fine by ID
     * @param id fine ID
     * @return fine or null if not found
     */
    public Fine findById(String id) {
        return fines.get(id);
    }

    /**
     * Find fines by user ID
     * @param userId user ID
     * @return list of user's fines
     */
    public List<Fine> findByUserId(String userId) {
        return fines.values().stream()
                .filter(fine -> userId.equals(fine.getUserId()))
                .collect(Collectors.toList());
    }

    /**
     * Find unpaid fines
     * @return list of unpaid fines
     */
    public List<Fine> findUnpaidFines() {
        return fines.values().stream()
                .filter(fine -> !fine.isPaid())
                .collect(Collectors.toList());
    }

    /**
     * Update fine in repository
     * @param fine fine to update
     * @return true if update successful, false otherwise
     */
    public boolean update(Fine fine) {
        if (fines.containsKey(fine.getId())) {
            fines.put(fine.getId(), fine);
            return saveFines();
        }
        return false;
    }

    /**
     * Get all fines
     * @return list of all fines
     */
    public List<Fine> findAll() {
        return new ArrayList<>(fines.values());
    }
}