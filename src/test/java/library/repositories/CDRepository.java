package library.repositories;

import library.models.CD;
import library.utils.JsonFileHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

public class CDRepository {

    private static final String FILE_PATH = "data/cds.json";

    private final JsonFileHandler fileHandler;
    private final Gson gson;
    private Map<String, CD> cds;

    // --- Constructor for Production ---
    public CDRepository() {
        this(new JsonFileHandler(), new Gson());
    }

    // --- Constructor for Testing ---
    public CDRepository(JsonFileHandler fileHandler, Gson gson) {
        this.fileHandler = fileHandler;
        this.gson = gson;
        this.cds = loadCDs();
    }

    private Map<String, CD> loadCDs() {
        String json = fileHandler.readFromFile(FILE_PATH);
        if (json == null || json.trim().isEmpty()) {
            return new HashMap<>();
        }

        try {
            Type type = new TypeToken<Map<String, CD>>() {}.getType();
            Map<String, CD> loaded = gson.fromJson(json, type);
            return loaded != null ? loaded : new HashMap<>();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private boolean saveCDs() {
        try {
            String json = gson.toJson(cds);
            return fileHandler.writeToFile(FILE_PATH, json);
        } catch (Exception e) {
            return false;
        }
    }

    private String generateId() {
        return "CD_" + UUID.randomUUID();
    }

    public boolean save(CD cd) {
        if (cd.getId() == null) {
            cd.setId(generateId());
        }
        cd.updateTimestamp();
        cds.put(cd.getId(), cd);
        return saveCDs();
    }

    public CD findById(String id) {
        return cds.get(id);
    }

    public List<CD> findAll() {
        return new ArrayList<>(cds.values());
    }

    public List<CD> search(String query) {
        if (query == null || query.trim().isEmpty()) return findAll();
        String q = query.toLowerCase();

        List<CD> results = new ArrayList<>();
        for (CD cd : cds.values()) {
            if (cd.getTitle().toLowerCase().contains(q) ||
                cd.getArtist().toLowerCase().contains(q) ||
                cd.getGenre().toLowerCase().contains(q)) {
                results.add(cd);
            }
        }
        return results;
    }

    public boolean update(CD cd) {
        if (!cds.containsKey(cd.getId())) return false;
        cd.updateTimestamp();
        cds.put(cd.getId(), cd);
        return saveCDs();
    }

    public boolean delete(String id) {
        CD removed = cds.remove(id);
        if (removed == null) return false;
        return saveCDs();
    }

    public List<CD> findByArtist(String artist) {
        List<CD> results = new ArrayList<>();
        for (CD cd : cds.values()) {
            if (artist.equalsIgnoreCase(cd.getArtist())) {
                results.add(cd);
            }
        }
        return results;
    }

    public List<CD> findByGenre(String genre) {
        List<CD> results = new ArrayList<>();
        for (CD cd : cds.values()) {
            if (genre.equalsIgnoreCase(cd.getGenre())) {
                results.add(cd);
            }
        }
        return results;
    }
}
