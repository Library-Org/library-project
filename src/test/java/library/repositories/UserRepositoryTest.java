package library.repositories;

import library.models.User;
import library.utils.JsonFileHandler;
import library.utils.GsonUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserRepositoryTest {

    private JsonFileHandler fileHandler;
    private Gson gson;
    private UserRepository repo;

    @BeforeEach
    void setUp() {
        fileHandler = mock(JsonFileHandler.class);
        gson = GsonUtils.createGson();

        // mock empty file
        when(fileHandler.readFromFile(UserRepository.FILE_PATH)).thenReturn("");

        repo = new UserRepository(fileHandler, gson);
    }

    @Test
    void testSaveUser_NewUser_IdGenerated_AndTimestampUpdated() {
        User u = new User();
        u.setEmail("aaa@test.com");

        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        boolean result = repo.save(u);

        assertTrue(result);
        assertNotNull(u.getId());
        assertNotNull(u.getUpdatedAt());
    }

    @Test
    void testFindByEmail_Found() {
        User u = new User();
        u.setEmail("test@test.com");

        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);
        repo.save(u);

        User found = repo.findByEmail("test@test.com");

        assertNotNull(found);
    }

    @Test
    void testFindByEmail_NotFound() {
        User found = repo.findByEmail("unknown@test.com");
        assertNull(found);
    }

    @Test
    void testFindAll() {
        User u1 = new User();
        User u2 = new User();

        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repo.save(u1);
        repo.save(u2);

        List<User> all = repo.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void testFindById() {
        User u = new User();
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repo.save(u);

        User found = repo.findById(u.getId());
        assertNotNull(found);
    }

    @Test
    void testFindById_Null() {
        assertNull(repo.findById(null));
    }

    @Test
    void testDelete_Success() {
        User u = new User();

        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repo.save(u);

        boolean result = repo.delete(u.getId());

        assertTrue(result);
    }

    @Test
    void testDelete_Failed_NoUser() {
        boolean result = repo.delete("unknown");
        assertFalse(result);
    }

    @Test
    void testDelete_NullId() {
        assertFalse(repo.delete(null));
    }
}
