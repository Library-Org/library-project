package library.repositories;

import library.models.CDLoan;
import library.utils.JsonFileHandler;
import library.utils.GsonUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
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

        // By default, simulate empty JSON
        when(fileHandler.readFromFile(anyString())).thenReturn("{}");
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repo = new CDLoanRepository(fileHandler, gson, "test.json");
    }

    // -------------------------------------------------------------------
    // loadCDLoans()
    // -------------------------------------------------------------------

    @Test
    void testLoad_NullJson() {
        when(fileHandler.readFromFile(anyString())).thenReturn(null);

        CDLoanRepository r = new CDLoanRepository(fileHandler, gson, "x.json");
        assertEquals(0, r.findAll().size());
    }

    @Test
    void testLoad_EmptyJson() {
        when(fileHandler.readFromFile(anyString())).thenReturn("   ");

        CDLoanRepository r = new CDLoanRepository(fileHandler, gson, "x.json");
        assertEquals(0, r.findAll().size());
    }

    @Test
    void testLoad_InvalidJson() {
        when(fileHandler.readFromFile(anyString())).thenReturn("{ invalid }");

        CDLoanRepository r = new CDLoanRepository(fileHandler, gson, "x.json");
        assertEquals(0, r.findAll().size());
    }

    @Test
    void testLoad_ValidJson() {
        String json = "{ \"L1\": { \"id\":\"L1\", \"userId\":\"U1\", \"cdId\":\"CD1\", \"borrowDate\":\"2025-01-01T10:00\" } }";

        when(fileHandler.readFromFile(anyString())).thenReturn(json);

        CDLoanRepository r = new CDLoanRepository(fileHandler, gson, "x.json");

        assertEquals(1, r.findAll().size());
        assertEquals("U1", r.findById("L1").getUserId());
    }

    @Test
    void testLoad_ThrowsException() {
        when(fileHandler.readFromFile(anyString())).thenThrow(new RuntimeException("ERROR"));

        CDLoanRepository r = new CDLoanRepository(fileHandler, gson, "x.json");

        // Repository must NOT crash → must fallback to empty map
        assertEquals(0, r.findAll().size());
    }

    // -------------------------------------------------------------------
    // saveCDLoans()
    // -------------------------------------------------------------------

    @Test
    void testSave_WriteSuccess() {
        CDLoan loan = new CDLoan("U1", "CD1");
        boolean result = repo.save(loan);

        assertTrue(result);
    }

    @Test
    void testSave_WriteFailsButMethodReturnsTrue() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(false);

        CDLoan loan = new CDLoan("U1", "CD1");
        assertTrue(repo.save(loan));  // Repo always returns true
    }

    // -------------------------------------------------------------------
    // generateId
    // -------------------------------------------------------------------

    @Test
    void testGeneratedId_NotNull() {
        CDLoan loan = new CDLoan("U", "C");
        repo.save(loan);

        assertNotNull(loan.getId());
        assertTrue(loan.getId().startsWith("CDLOAN_"));
    }

    // -------------------------------------------------------------------
    // save() & findById()
    // -------------------------------------------------------------------

    @Test
    void testSaveAndFindById() {
        CDLoan loan = new CDLoan("A", "B");
        repo.save(loan);

        assertNotNull(repo.findById(loan.getId()));
    }

    @Test
    void testFindById_NotFound() {
        assertNull(repo.findById("XXX"));
    }

    // -------------------------------------------------------------------
    // findByUserId()
    // -------------------------------------------------------------------

    @Test
    void testFindByUserId() {
        repo.save(new CDLoan("U1", "C1"));
        repo.save(new CDLoan("U1", "C2"));
        repo.save(new CDLoan("U2", "C3"));

        var list = repo.findByUserId("U1");
        assertEquals(2, list.size());
    }

    @Test
    void testFindByUserId_Empty() {
        assertEquals(0, repo.findByUserId("NONE").size());
    }

    // -------------------------------------------------------------------
    // findByCDId()
    // -------------------------------------------------------------------

    @Test
    void testFindByCDId() {
        repo.save(new CDLoan("U1", "CD1"));
        repo.save(new CDLoan("U2", "CD1"));
        repo.save(new CDLoan("U3", "CD2"));

        var list = repo.findByCDId("CD1");
        assertEquals(2, list.size());
    }

    // -------------------------------------------------------------------
    // findOverdueCDLoans()
    // -------------------------------------------------------------------

    @Test
    void testFindOverdue_NotReturnedOverdue() {
        CDLoan overdue = new CDLoan("U1", "C1");
        overdue.setDueDateTime(LocalDateTime.now().minusDays(2));
        repo.save(overdue);

        var list = repo.findOverdueCDLoans();
        assertEquals(1, list.size());
    }

    @Test
    void testFindOverdue_ReturnedLate() {
        CDLoan loan = new CDLoan("U1", "CD7");

        loan.setDueDateTime(LocalDateTime.now().minusDays(2)); // overdue
        loan.setReturned(true);
        loan.setReturnDateTime(LocalDateTime.now()); // returned today → definitely late

        repo.save(loan);

        var list = repo.findOverdueCDLoans();
        assertEquals(1, list.size());
    }


    @Test
    void testFindOverdue_ReturnedOnTime() {
        CDLoan loan = new CDLoan("U1", "CD8");
        loan.setDueDateTime(LocalDateTime.now());
        loan.setReturned(true);
        loan.setReturnDateTime(LocalDateTime.now());

        repo.save(loan);

        assertEquals(0, repo.findOverdueCDLoans().size());
    }

    // -------------------------------------------------------------------
    // update()
    // -------------------------------------------------------------------

    @Test
    void testUpdateSuccess() {
        CDLoan l = new CDLoan("U1", "C1");
        repo.save(l);

        l.setReturned(true);
        assertTrue(repo.update(l));
    }

    @Test
    void testUpdateFail() {
        CDLoan fake = new CDLoan("U", "C");
        fake.setId("NOT_EXIST");

        assertFalse(repo.update(fake));
    }

    // -------------------------------------------------------------------
    // findAll()
    // -------------------------------------------------------------------

    @Test
    void testFindAll() {
        repo.save(new CDLoan("A", "1"));
        repo.save(new CDLoan("B", "2"));

        assertEquals(2, repo.findAll().size());
    }
}
