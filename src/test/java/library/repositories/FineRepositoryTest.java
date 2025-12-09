package library.repositories;

import library.models.Fine;
import library.utils.JsonFileHandler;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FineRepositoryTest {

    private FineRepository fineRepository;
    private JsonFileHandler fileHandlerMock;
    private Gson gson = new Gson();

    @BeforeEach
    void setUp() throws Exception {
        // Mock file handler (we won't rely on constructor load)
        fileHandlerMock = mock(JsonFileHandler.class);
        when(fileHandlerMock.writeToFile(anyString(), anyString())).thenReturn(true);

        // Prepare fake initial fines map (use setters to avoid constructor mismatch)
        Map<String, Fine> fakeData = new HashMap<>();
        Fine f1 = new Fine();
        f1.setId("F1");
        f1.setUserId("U1");
        f1.setAmount(50.0);
        f1.setPaidAmount(0.0);
        f1.setPaid(false);
        fakeData.put("F1", f1);

        // Create repository normally (it will call loadFines() internally,
        // but we'll overwrite the internal fields right after)
        fineRepository = new FineRepository();

        // Inject mocks & fake data into private fields using reflection
        injectField(FineRepository.class, fineRepository, "fileHandler", fileHandlerMock);
        injectField(FineRepository.class, fineRepository, "gson", gson);
        injectField(FineRepository.class, fineRepository, "fines", fakeData);
    }

    /** Utility to inject private fields via reflection */
    private void injectField(Class<?> cls, Object target, String fieldName, Object value) {
        try {
            Field f = cls.getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (NoSuchFieldException e) {
            // try in superclass (defensive)
            try {
                Field f = target.getClass().getSuperclass().getDeclaredField(fieldName);
                f.setAccessible(true);
                f.set(target, value);
            } catch (Exception ex) {
                throw new RuntimeException("Injection failed for field: " + fieldName, ex);
            }
        } catch (Exception e) {
            throw new RuntimeException("Injection failed for field: " + fieldName, e);
        }
    }

    @Test
    void testFindById() {
        Fine f = fineRepository.findById("F1");
        assertNotNull(f);
        assertEquals("F1", f.getId());
        assertEquals("U1", f.getUserId());
    }

    @Test
    void testFindByUserId() {
        List<Fine> list = fineRepository.findByUserId("U1");
        assertEquals(1, list.size());
        assertEquals("F1", list.get(0).getId());
    }

    @Test
    void testFindUnpaidFines() {
        List<Fine> list = fineRepository.findUnpaidFines();
        assertEquals(1, list.size());
        assertFalse(list.get(0).isPaid());
    }

    @Test
    void testSaveNewFineGeneratesIdAndPersists() {
        Fine newFine = new Fine();
        newFine.setUserId("U2");
        newFine.setAmount(30.0);
        newFine.setPaidAmount(0.0);
        newFine.setPaid(false);

        boolean saved = fineRepository.save(newFine);

        assertTrue(saved);
        assertNotNull(newFine.getId()); // id should be generated
        // confirm it's present in repository
        boolean found = fineRepository.findAll().stream()
                .anyMatch(f -> newFine.getId().equals(f.getId()));
        assertTrue(found);
    }

    @Test
    void testUpdateExistingFineSucceeds() {
        Fine existing = fineRepository.findById("F1");
        assertNotNull(existing);

        existing.setPaidAmount(50.0);
        existing.setPaid(true);

        boolean updated = fineRepository.update(existing);
        assertTrue(updated);

        Fine fromRepo = fineRepository.findById("F1");
        assertEquals(50.0, fromRepo.getPaidAmount());
        assertTrue(fromRepo.isPaid());
    }

    @Test
    void testUpdateNonExistingFineFails() {
        Fine non = new Fine();
        non.setId("DOES_NOT_EXIST");
        non.setUserId("UX");
        non.setAmount(10.0);
        non.setPaidAmount(0.0);
        non.setPaid(false);

        boolean updated = fineRepository.update(non);
        assertFalse(updated);
    }

    @Test
    void testFindAll() {
        List<Fine> all = fineRepository.findAll();
        assertFalse(all.isEmpty());
        assertEquals(1, all.size()); // only F1 initially
    }
}
