package library.repositories;

import library.models.CD;
import library.utils.JsonFileHandler;
import com.google.gson.Gson;

import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CDRepositoryTest {

    @Mock
    private JsonFileHandler fileHandler;

    private Gson gson;
    private CDRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        gson = new Gson();

        when(fileHandler.readFromFile(anyString())).thenReturn("{}");

        repository = new CDRepository(fileHandler, gson);
    }

    @Test
    void testSave() {
        CD cd = new CD("Album", "Artist", "Rock", 10, "Pub", 2020);

        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        boolean result = repository.save(cd);

        assertTrue(result);
        assertNotNull(cd.getId());
        verify(fileHandler).writeToFile(anyString(), anyString());
    }

    @Test
    void testFindById() {
        CD cd = new CD("Test", "A", "G", 10, "P", 2020);
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repository.save(cd);

        CD found = repository.findById(cd.getId());
        assertNotNull(found);
    }

    @Test
    void testFindAll() {
        CD c1 = new CD("A", "B", "C", 10, "P", 2020);
        CD c2 = new CD("X", "Y", "Z", 15, "P", 2021);

        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repository.save(c1);
        repository.save(c2);

        assertEquals(2, repository.findAll().size());
    }

    @Test
    void testSearch() {
        CD rock1 = new CD("Rock CD", "A", "Rock", 10, "P", 2020);
        CD rock2 = new CD("Another Rock", "A", "Rock", 10, "P", 2020);

        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repository.save(rock1);
        repository.save(rock2);

        assertEquals(2, repository.search("rock").size());
    }

    @Test
    void testUpdate() {
        CD cd = new CD("Old", "A", "G", 10, "P", 2020);
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repository.save(cd);

        cd.setTitle("New Title");

        boolean result = repository.update(cd);

        assertTrue(result);
        assertEquals("New Title", repository.findById(cd.getId()).getTitle());
    }

    @Test
    void testDelete() {
        CD cd = new CD("Delete", "A", "G", 10, "P", 2020);
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repository.save(cd);

        boolean result = repository.delete(cd.getId());

        assertTrue(result);
        assertNull(repository.findById(cd.getId()));
    }

    @Test
    void testFindByArtist() {
        CD c1 = new CD("A1", "Queen", "Rock", 10, "P", 2020);
        CD c2 = new CD("A2", "Queen", "Rock", 10, "P", 2021);

        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repository.save(c1);
        repository.save(c2);

        assertEquals(2, repository.findByArtist("Queen").size());
    }

    @Test
    void testFindByGenre() {
        CD c1 = new CD("A1", "A", "Jazz", 10, "P", 2020);
        CD c2 = new CD("A2", "B", "Jazz", 12, "P", 2021);

        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repository.save(c1);
        repository.save(c2);

        assertEquals(2, repository.findByGenre("Jazz").size());
    }
}
