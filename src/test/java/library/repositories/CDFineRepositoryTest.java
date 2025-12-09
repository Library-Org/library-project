package library.repositories;

import library.models.CDFine;
import library.utils.JsonFileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CDFineRepositoryTest {

    private JsonFileHandler fileHandlerMock;
    private CDFineRepository repository;

    @BeforeEach
    void setUp() {
        fileHandlerMock = mock(JsonFileHandler.class);

  
        when(fileHandlerMock.readFromFile("data/cdfines.json")).thenReturn(null);

        repository = new CDFineRepository(fileHandlerMock);
    }

    @Test
    void testSave_NewFine_AssignsIdAndSaves() {
        CDFine fine = new CDFine();
        fine.setUserId("USER1");
        fine.setRemainingAmount(50);

        boolean result = repository.save(fine);

        assertTrue(result);
        assertNotNull(fine.getId());
        assertTrue(fine.getId().startsWith("CDFINE_"));
    }

    @Test
    void testFindById_Found() {
        CDFine fine = new CDFine();
        fine.setId("F100");
        fine.setUserId("USER1");

        repository.save(fine);

        CDFine found = repository.findById("F100");

        assertNotNull(found);
        assertEquals("USER1", found.getUserId());
    }

    @Test
    void testFindById_NotFound() {
        CDFine notFound = repository.findById("XXXX");
        assertNull(notFound);
    }

    @Test
    void testFindByUserId() {
        CDFine f1 = new CDFine();
        f1.setId("A");
        f1.setUserId("U1");

        CDFine f2 = new CDFine();
        f2.setId("B");
        f2.setUserId("U1");

        CDFine f3 = new CDFine();
        f3.setId("C");
        f3.setUserId("U2");

        repository.save(f1);
        repository.save(f2);
        repository.save(f3);

        List<CDFine> list = repository.findByUserId("U1");

        assertEquals(2, list.size());
    }

    @Test
    void testFindUnpaidCDFines() {
        CDFine f1 = new CDFine();
        f1.setId("A");
        f1.setRemainingAmount(20);

        CDFine f2 = new CDFine();
        f2.setId("B");
        f2.setRemainingAmount(0);

        CDFine f3 = new CDFine();
        f3.setId("C");
        f3.setRemainingAmount(10);

        repository.save(f1);
        repository.save(f2);
        repository.save(f3);

        List<CDFine> list = repository.findUnpaidCDFines();

        assertEquals(2, list.size());
    }

    @Test
    void testUpdate_Success() {
        CDFine fine = new CDFine();
        fine.setId("X");
        fine.setRemainingAmount(30);

        repository.save(fine);

        fine.setRemainingAmount(10);
        boolean updated = repository.update(fine);

        assertTrue(updated);

        CDFine fromRepo = repository.findById("X");
        assertEquals(10, fromRepo.getRemainingAmount());
    }

    @Test
    void testUpdate_Fail_WhenNotExists() {
        CDFine fine = new CDFine();
        fine.setId("UNKNOWN");
        fine.setRemainingAmount(5);

        boolean result = repository.update(fine);

        assertFalse(result);
    }

    @Test
    void testFindAll() {
        CDFine f1 = new CDFine();
        f1.setId("A");

        CDFine f2 = new CDFine();
        f2.setId("B");

        repository.save(f1);
        repository.save(f2);

        List<CDFine> all = repository.findAll();

        assertEquals(2, all.size());
    }
}
