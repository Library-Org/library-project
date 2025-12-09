package library.repositories;

import com.google.gson.Gson;
import library.models.CDLoan;
import library.utils.GsonUtils;
import library.utils.JsonFileHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CDLoanRepositoryTest {

    private JsonFileHandler fileHandler;
    private Gson gson;
    private CDLoanRepository repo;

    @BeforeEach
    void setup() {
        fileHandler = mock(JsonFileHandler.class);
        gson = GsonUtils.createGson();

        when(fileHandler.readFromFile(anyString())).thenReturn(null);

        repo = new CDLoanRepository(fileHandler, gson, "data/cdloans.json");
    }

    // ----------------------------------------------
    // loadCDLoans tests
    // ----------------------------------------------
    @Test
    void testLoadCDLoans_NullJson() {
        when(fileHandler.readFromFile(anyString())).thenReturn(null);

        CDLoanRepository r = new CDLoanRepository(fileHandler, gson, "x.json");

        assertEquals(0, r.findAll().size());
    }

    @Test
    void testLoadCDLoans_EmptyJson() {
        when(fileHandler.readFromFile(anyString())).thenReturn("  ");

        CDLoanRepository r = new CDLoanRepository(fileHandler, gson, "x.json");

        assertEquals(0, r.findAll().size());
    }

    @Test
    void testLoadCDLoans_InvalidJson() {
        when(fileHandler.readFromFile(anyString())).thenReturn("{ invalid");

        CDLoanRepository r = new CDLoanRepository(fileHandler, gson, "x.json");

        assertEquals(0, r.findAll().size());
    }

    @Test
    void testLoadCDLoans_ValidJson() {
        String json = "{ \"L1\": { \"id\":\"L1\", \"userId\":\"U1\", \"cdId\":\"CD1\" } }";

        when(fileHandler.readFromFile(anyString())).thenReturn(json);

        CDLoanRepository r = new CDLoanRepository(fileHandler, gson, "x.json");

        assertEquals(1, r.findAll().size());
        assertEquals("U1", r.findById("L1").getUserId());
    }

    // ----------------------------------------------
    // saveCDLoans tests
    // ----------------------------------------------
    @Test
    void testSave_Success() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        CDLoan loan = new CDLoan("U1", "C1");

        assertTrue(repo.save(loan));
    }

    @Test
    void testSave_Failure() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(false);

        CDLoan loan = new CDLoan("U1", "C1");

        assertFalse(repo.save(loan));
    }

    // ----------------------------------------------
    // ID Generation
    // ----------------------------------------------
    @Test
    void testGenerateId_AutoAssigned() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        CDLoan loan = new CDLoan("A", "B");
        repo.save(loan);

        assertNotNull(loan.getId());
        assertTrue(loan.getId().startsWith("CDLOAN_"));
    }

    // ----------------------------------------------
    // Find by ID
    // ----------------------------------------------
    @Test
    void testSaveAndFindById() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        CDLoan loan = new CDLoan("U1", "CD1");
        repo.save(loan);

        assertNotNull(repo.findById(loan.getId()));
    }

    @Test
    void testFindById_NotFound() {
        assertNull(repo.findById("XXX"));
    }

    // ----------------------------------------------
    // Find by userId
    // ----------------------------------------------
    @Test
    void testFindByUserId() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repo.save(new CDLoan("U1", "C1"));
        repo.save(new CDLoan("U1", "C2"));
        repo.save(new CDLoan("U2", "C3"));

        assertEquals(2, repo.findByUserId("U1").size());
    }

    @Test
    void testFindByUserId_Empty() {
        assertEquals(0, repo.findByUserId("NONE").size());
    }

    // ----------------------------------------------
    // Find by CD ID
    // ----------------------------------------------
    @Test
    void testFindByCDId() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repo.save(new CDLoan("U1", "CD1"));
        repo.save(new CDLoan("U2", "CD1"));
        repo.save(new CDLoan("U3", "CD2"));

        assertEquals(2, repo.findByCDId("CD1").size());
    }

    // ----------------------------------------------
    // Overdue loans
    // ----------------------------------------------
    @Test
    void testFindOverdueCDLoans() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        CDLoan overdue = new CDLoan("U1", "C1");
        overdue.setDueDateTime(LocalDateTime.now().minusDays(3));
        repo.save(overdue);

        CDLoan ok = new CDLoan("U2", "C2");
        repo.save(ok);

        List<CDLoan> list = repo.findOverdueCDLoans();

        assertEquals(1, list.size());
        assertEquals("U1", list.get(0).getUserId());
    }

    // ----------------------------------------------
    // Update
    // ----------------------------------------------
    @Test
    void testUpdate_Success() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        CDLoan loan = new CDLoan("U1", "C1");
        repo.save(loan);

        loan.setReturned(true);

        assertTrue(repo.update(loan));
        assertTrue(repo.findById(loan.getId()).isReturned());
    }

    @Test
    void testUpdate_Failure() {
        CDLoan fake = new CDLoan("U1", "C1");
        fake.setId("NOT_EXIST");

        assertFalse(repo.update(fake));
    }

    // ----------------------------------------------
    // FindAll
    // ----------------------------------------------
    @Test
    void testFindAll() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repo.save(new CDLoan("A", "1"));
        repo.save(new CDLoan("B", "2"));

        assertEquals(2, repo.findAll().size());
    }
}
