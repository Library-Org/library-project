package library.repositories;

import library.models.Loan;
import library.utils.JsonFileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoanRepositoryTest {

    private LoanRepository repository;
    private JsonFileHandler fileHandlerMock;
    private Gson gson = new Gson();

    @BeforeEach
    void setup() {
        fileHandlerMock = Mockito.mock(JsonFileHandler.class);

        // JSON فارغ عند التحميل
        when(fileHandlerMock.readFromFile(anyString())).thenReturn("");

        // عملية الكتابة دايماً ناجحة
        when(fileHandlerMock.writeToFile(anyString(), anyString())).thenReturn(true);

        // نعمل repository باستخدام Reflection لتبديل fileHandler
        repository = new LoanRepository();
        injectFileHandler(repository, fileHandlerMock);
    }

    // نحقن fileHandler mock داخل الريبو
    private void injectFileHandler(LoanRepository repo, JsonFileHandler mock) {
        try {
            var field = LoanRepository.class.getDeclaredField("fileHandler");
            field.setAccessible(true);
            field.set(repo, mock);
        } catch (Exception e) {
            fail("Injection failed");
        }
    }

    @Test
    void testSaveNewLoanCreatesId() {
        Loan loan = new Loan();
        loan.setUserId("u1");
        loan.setBookId("b1");

        boolean result = repository.save(loan);

        assertTrue(result);
        assertNotNull(loan.getId());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void testSaveExistingLoanDoesNotChangeId() {
        Loan loan = new Loan();
        loan.setId("L1");
        loan.setUserId("u1");
        loan.setBookId("b1");

        repository.save(loan);
        repository.save(loan);

        assertEquals("L1", loan.getId());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void testFindById() {
        Loan loan = new Loan();
        loan.setId("X");
        loan.setUserId("u");
        loan.setBookId("b");

        repository.save(loan);

        Loan found = repository.findById("X");
        assertNotNull(found);
        assertEquals("X", found.getId());
    }

    @Test
    void testFindByUserId() {
        Loan loan1 = new Loan();
        loan1.setId("1");
        loan1.setUserId("U1");

        Loan loan2 = new Loan();
        loan2.setId("2");
        loan2.setUserId("U2");

        repository.save(loan1);
        repository.save(loan2);

        List<Loan> result = repository.findByUserId("U1");

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
    }

    @Test
    void testFindByBookId() {
        Loan loan1 = new Loan();
        loan1.setId("1");
        loan1.setBookId("B1");

        Loan loan2 = new Loan();
        loan2.setId("2");
        loan2.setBookId("B2");

        repository.save(loan1);
        repository.save(loan2);

        List<Loan> result = repository.findByBookId("B2");

        assertEquals(1, result.size());
        assertEquals("2", result.get(0).getId());
    }

    @Test
    void testUpdateSuccess() {
        Loan loan = new Loan();
        loan.setId("L1");
        loan.setUserId("U1");

        repository.save(loan);

        loan.setUserId("U2");
        boolean result = repository.update(loan);

        assertTrue(result);
        assertEquals("U2", repository.findById("L1").getUserId());
    }

    @Test
    void testUpdateFail() {
        Loan loan = new Loan();
        loan.setId("NOT_FOUND");

        boolean result = repository.update(loan);

        assertFalse(result);
    }

    @Test
    void testFindAll() {
        Loan loan1 = new Loan();
        loan1.setId("1");
        Loan loan2 = new Loan();
        loan2.setId("2");

        repository.save(loan1);
        repository.save(loan2);

        List<Loan> all = repository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testFindOverdueLoans() {
        Loan activeOverdue = Mockito.mock(Loan.class);
        when(activeOverdue.getId()).thenReturn("1");
        when(activeOverdue.isReturned()).thenReturn(false);
        when(activeOverdue.isOverdue()).thenReturn(true);

        Loan returnedLoan = Mockito.mock(Loan.class);
        when(returnedLoan.getId()).thenReturn("2");
        when(returnedLoan.isReturned()).thenReturn(true);

        injectLoan("1", activeOverdue);
        injectLoan("2", returnedLoan);

        List<Loan> overdueLoans = repository.findOverdueLoans();

        assertEquals(1, overdueLoans.size());
        assertEquals("1", overdueLoans.get(0).getId());
    }

    // نحقن loans مباشرة بسهولة
    private void injectLoan(String id, Loan loan) {
        try {
            var field = LoanRepository.class.getDeclaredField("loans");
            field.setAccessible(true);
            Map<String, Loan> map = (Map<String, Loan>) field.get(repository);
            map.put(id, loan);
        } catch (Exception e) {
            fail("Injection failed");
        }
    }
}
