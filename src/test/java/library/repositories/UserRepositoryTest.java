package library.repositories;

import library.models.User;
import library.utils.JsonFileHandler;
import library.utils.GsonUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;

import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserRepositoryTest {

    private JsonFileHandler fileHandler;
    private Gson gson;

    @BeforeEach
    void setUp() {
        fileHandler = mock(JsonFileHandler.class);
        gson = GsonUtils.createGson();
    }

    @Test
    void testLoadUsers_ValidJson() {
        String json = "{\"1\":{\"id\":\"1\",\"email\":\"a@test.com\"}}";
        when(fileHandler.readFromFile(UserRepository.FILE_PATH)).thenReturn(json);

        UserRepository repo = new UserRepository(fileHandler, gson);

        assertEquals(1, repo.findAll().size());
    }

    @Test
    void testLoadUsers_InvalidJson_ReturnsEmpty() {
        when(fileHandler.readFromFile(UserRepository.FILE_PATH)).thenReturn("{invalid json}");

        UserRepository repo = new UserRepository(fileHandler, gson);

        assertEquals(0, repo.findAll().size());
    }

    @Test
    void testSaveUsers_FailedWrite() {
        when(fileHandler.readFromFile(UserRepository.FILE_PATH)).thenReturn("");

        UserRepository repo = new UserRepository(fileHandler, gson);

        User u = new User();
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(false);

        boolean result = repo.save(u);

        assertFalse(result);
    }

    @Test
    void testSaveUsers_Success() {
        when(fileHandler.readFromFile(UserRepository.FILE_PATH)).thenReturn("");

        UserRepository repo = new UserRepository(fileHandler, gson);

        User u = new User();
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        assertTrue(repo.save(u));
    }

    @Test
    void testGenerateIdCoverage() {
        when(fileHandler.readFromFile(UserRepository.FILE_PATH)).thenReturn("");

        UserRepository repo = new UserRepository(fileHandler, gson);

        // Call save() to force generateId()
        User u = new User();
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repo.save(u);

        assertNotNull(u.getId());
        assertTrue(u.getId().startsWith("USER_"));
    }

    @Test
    void testFindByEmail_NullEmail() {
        when(fileHandler.readFromFile(UserRepository.FILE_PATH)).thenReturn("");

        UserRepository repo = new UserRepository(fileHandler, gson);

        assertNull(repo.findByEmail(null));
    }

    @Test
    void testFindByEmail_Found() {
        when(fileHandler.readFromFile(UserRepository.FILE_PATH)).thenReturn("");

        UserRepository repo = new UserRepository(fileHandler, gson);

        User u = new User();
        u.setEmail("aaa@test.com");

        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);
        repo.save(u);

        assertNotNull(repo.findByEmail("aaa@test.com"));
    }

    @Test
    void testFindByEmail_NotFound() {
        when(fileHandler.readFromFile(UserRepository.FILE_PATH)).thenReturn("");

        UserRepository repo = new UserRepository(fileHandler, gson);

        assertNull(repo.findByEmail("notfound@test.com"));
    }

    @Test
    void testFindById_Null() {
        when(fileHandler.readFromFile(UserRepository.FILE_PATH)).thenReturn("");

        UserRepository repo = new UserRepository(fileHandler, gson);

        assertNull(repo.findById(null));
    }

    @Test
    void testDelete_Success() {
        when(fileHandler.readFromFile(UserRepository.FILE_PATH)).thenReturn("");

        UserRepository repo = new UserRepository(fileHandler, gson);

        User u = new User();
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repo.save(u);

        assertTrue(repo.delete(u.getId()));
    }

    @Test
    void testDelete_Failed_NoSuchId() {
        when(fileHandler.readFromFile(UserRepository.FILE_PATH)).thenReturn("");

        UserRepository repo = new UserRepository(fileHandler, gson);

        assertFalse(repo.delete("unknown_id"));
    }

    @Test
    void testDelete_FailedWrite() {
        when(fileHandler.readFromFile(UserRepository.FILE_PATH)).thenReturn("");

        UserRepository repo = new UserRepository(fileHandler, gson);

        User u = new User();
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);
        repo.save(u);

        // Now fail writeToFile
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(false);

        assertFalse(repo.delete(u.getId()));
    }
}
