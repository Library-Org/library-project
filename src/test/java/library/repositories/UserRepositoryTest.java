package library.repositories;

import library.models.User;
import library.utils.GsonUtils;
import library.utils.JsonFileHandler;
import com.google.gson.Gson;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserRepositoryTest {

    private JsonFileHandler fileHandler;
    private Gson gson;

    @BeforeEach
    void setup() {
        fileHandler = mock(JsonFileHandler.class);
        gson = GsonUtils.createGson();
    }

    @Test
    void testLoadUsers_ValidJson() {
        String json = "{ \"U1\": { \"id\":\"U1\", \"name\":\"Test\", \"email\":\"t@t.com\" } }";

        when(fileHandler.readFromFile("test_users.json")).thenReturn(json);

        UserRepository repo = new UserRepository("test_users.json", fileHandler, gson);

        assertEquals(1, repo.findAll().size());
        assertNotNull(repo.findById("U1"));
    }

    @Test
    void testLoadUsers_EmptyJson() {
        when(fileHandler.readFromFile("test.json")).thenReturn("");

        UserRepository repo = new UserRepository("test.json", fileHandler, gson);

        assertEquals(0, repo.findAll().size());
    }

    @Test
    void testSaveUser() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);
        when(fileHandler.readFromFile(anyString())).thenReturn("");

        UserRepository repo = new UserRepository("save_test.json", fileHandler, gson);

        User user = new User();
        user.setName("Kamal");
        user.setEmail("k@k.com");

        boolean result = repo.save(user);

        assertTrue(result);
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void testFindByEmail() {
        String json = "{ \"U1\": { \"id\":\"U1\", \"name\":\"Test\", \"email\":\"abc@xyz.com\" } }";

        when(fileHandler.readFromFile("test.json")).thenReturn(json);

        UserRepository repo = new UserRepository("test.json", fileHandler, gson);

        User u = repo.findByEmail("abc@xyz.com");

        assertNotNull(u);
        assertEquals("U1", u.getId());
    }

    @Test
    void testDeleteUser() {
        String json = "{ \"U1\": { \"id\":\"U1\", \"name\":\"Test\", \"email\":\"abc@xyz.com\" } }";

        when(fileHandler.readFromFile("test.json")).thenReturn(json);
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        UserRepository repo = new UserRepository("test.json", fileHandler, gson);

        boolean deleted = repo.delete("U1");

        assertTrue(deleted);
        assertEquals(0, repo.findAll().size());
    }
}

