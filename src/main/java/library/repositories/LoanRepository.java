package library.repositories;



import library.models.Loan;
import library.utils.JsonFileHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.security.SecureRandom;

/**
 * Repository for loan data management using JSON file storage
 * @author Library Team
 * @version 1.0
 */
public class LoanRepository {
    private static final String FILE_PATH = "data/loans.json";
    private Map<String, Loan> loans;
    private Gson gson;
    private JsonFileHandler fileHandler;

    public LoanRepository() {
        this.gson = new Gson();
        this.fileHandler = new JsonFileHandler();
        this.loans = loadLoans();
    }

    /**
     * Load loans from JSON file
     * @return map of loans
     */
    private Map<String, Loan> loadLoans() {
        String json = fileHandler.readFromFile(FILE_PATH);
        if (json.isEmpty()) {
            return new HashMap<>();
        }

        Type type = new TypeToken<Map<String, Loan>>(){}.getType();
        Map<String, Loan> loadedLoans = gson.fromJson(json, type);
        
        // Generate IDs for loans that don't have them
        for (Loan loan : loadedLoans.values()) {
            if (loan.getId() == null) {
                loan.setId(generateId());
            }
        }
        
        return loadedLoans;
    }

    /**
     * Save loans to JSON file
     * @return true if save successful, false otherwise
     */
    private boolean saveLoans() {
        String json = gson.toJson(loans);
        return fileHandler.writeToFile(FILE_PATH, json);
    }

    /**
     * Generate unique ID for loan
     * @return generated ID
     */
   

    private String generateId() {
        SecureRandom secureRandom = new SecureRandom();
        return "LOAN_" + System.currentTimeMillis() + "_" + secureRandom.nextInt(1000);
    }


    /**
     * Save loan to repository
     * @param loan loan to save
     * @return true if save successful, false otherwise
     */
    public boolean save(Loan loan) {
        if (loan.getId() == null) {
            loan.setId(generateId());
        }
        loans.put(loan.getId(), loan);
        return saveLoans();
    }

    /**
     * Find loan by ID
     * @param id loan ID
     * @return loan or null if not found
     */
    public Loan findById(String id) {
        return loans.get(id);
    }

    /**
     * Find loans by user ID
     * @param userId user ID
     * @return list of user's loans
     */
    public List<Loan> findByUserId(String userId) {
        return loans.values().stream()
                .filter(loan -> userId.equals(loan.getUserId()))
                .collect(Collectors.toList());
    }

    /**
     * Find loans by book ID
     * @param bookId book ID
     * @return list of book's loans
     */
    public List<Loan> findByBookId(String bookId) {
        return loans.values().stream()
                .filter(loan -> bookId.equals(loan.getBookId()))
                .collect(Collectors.toList());
    }

   
    /**
     * Update loan in repository
     * @param loan loan to update
     * @return true if update successful, false otherwise
     */
    public boolean update(Loan loan) {
        if (loans.containsKey(loan.getId())) {
            loans.put(loan.getId(), loan);
            return saveLoans();
        }
        return false;
    }

    /**
     * Get all loans
     * @return list of all loans
     */
    public List<Loan> findAll() {
        return new ArrayList<>(loans.values());
    }
    
    
    /**
     * Find overdue loans
     * @return list of overdue loans
     */
    public List<Loan> findOverdueLoans() {
        return loans.values().stream()
                .filter(loan -> !loan.isReturned()) // Only active loans
                .filter(loan -> {
                    try {
                        return loan.isOverdue();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }
}