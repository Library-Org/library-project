package library.repositories;

import library.models.CDLoan;
import library.utils.JsonFileHandler;
import library.utils.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for CD loan data management using JSON file storage
 */
public class CDLoanRepository {

    private String filePath = "data/cdloans.json";

    // نجعلهم protected لتسهيل الاختبار بدون اختراق private
    protected Map<String, CDLoan> cdLoans;
    protected Gson gson;
    protected JsonFileHandler fileHandler;

    /** Default constructor */
    public CDLoanRepository() {
        this.fileHandler = new JsonFileHandler();
        this.gson = GsonUtils.createGson();
        this.cdLoans = loadCDLoans();
    }

    /** Constructor for testing (Dependency Injection) */
    public CDLoanRepository(JsonFileHandler fileHandler, Gson gson, String filePath) {
        this.fileHandler = (fileHandler != null) ? fileHandler : new JsonFileHandler();
        this.gson = (gson != null) ? gson : GsonUtils.createGson();
        this.filePath = (filePath != null) ? filePath : "data/cdloans.json";
        this.cdLoans = loadCDLoans();
    }

    /** Load CD loans */
    protected Map<String, CDLoan> loadCDLoans() {
        String json = fileHandler.readFromFile(filePath);

        if (json == null || json.trim().isEmpty()) {
            return new HashMap<>();
        }

        try {
            Type type = new TypeToken<Map<String, CDLoan>>() {}.getType();
            Map<String, CDLoan> loadedCDLoans = gson.fromJson(json, type);
            return (loadedCDLoans != null) ? loadedCDLoans : new HashMap<>();
        } catch (Exception e) {
            System.err.println("Error loading CD loans: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /** Save CD loans */
    protected boolean saveCDLoans() {
        try {
            String json = gson.toJson(cdLoans);
            return fileHandler.writeToFile(filePath, json);
        } catch (Exception e) {
            System.err.println("Error saving CD loans: " + e.getMessage());
            return false;
        }
    }

    /** Generate ID */
    protected String generateId() {
        return "CDLOAN_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
    }

    /** Save */
    public boolean save(CDLoan cdLoan) {
        if (cdLoan.getId() == null) {
            cdLoan.setId(generateId());
        }
        cdLoans.put(cdLoan.getId(), cdLoan);
        return saveCDLoans();
    }

    /** Find by ID */
    public CDLoan findById(String id) {
        return cdLoans.get(id);
    }

    /** Find by user ID */
    public List<CDLoan> findByUserId(String userId) {
        return cdLoans.values().stream()
                .filter(loan -> userId.equals(loan.getUserId()))
                .collect(Collectors.toList());
    }

    /** Find by CD ID */
    public List<CDLoan> findByCDId(String cdId) {
        return cdLoans.values().stream()
                .filter(loan -> cdId.equals(loan.getCdId()))
                .collect(Collectors.toList());
    }

    /** Find overdue */
    public List<CDLoan> findOverdueCDLoans() {
        return cdLoans.values().stream()
                .filter(l -> !l.isReturned())
                .filter(CDLoan::isOverdue)
                .collect(Collectors.toList());
    }

    /** Update */
    public boolean update(CDLoan cdLoan) {
        if (!cdLoans.containsKey(cdLoan.getId())) {
            return false;
        }
        cdLoans.put(cdLoan.getId(), cdLoan);
        return saveCDLoans();
    }

    /** Find all */
    public List<CDLoan> findAll() {
        return new ArrayList<>(cdLoans.values());
    }
}
