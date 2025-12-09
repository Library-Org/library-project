package library.controllers;

import library.models.User;
import library.repositories.UserRepository;
import library.services.AuthService;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserRepository userRepository;
    private AuthService authService;
    private UserController userController;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        authService = Mockito.mock(AuthService.class);
        userController = new UserController(userRepository, authService);

        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        outputStream.reset();
    }

    // -----------------------------------------------------
    // viewAllUsers()
    // -----------------------------------------------------

    @Test
    void testViewAllUsers_emptyList() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        userController.viewAllUsers();

        assertTrue(outputStream.toString().contains("No users found."));
    }

    @Test
    void testViewAllUsers_withUsers() {
        User u1 = new User();
        u1.setId("1");
        u1.setName("Alice");
        u1.setEmail("alice@mail.com");
        u1.setRole("USER");
        u1.setActive(true);

        User u2 = new User();
        u2.setId("2");
        u2.setName("Bob");
        u2.setEmail("bob@mail.com");
        u2.setRole("ADMIN");
        u2.setActive(false);

        List<User> users = Arrays.asList(u1, u2);
        when(userRepository.findAll()).thenReturn(users);

        userController.viewAllUsers();

        String out = outputStream.toString();
        assertTrue(out.contains("Alice"));
        assertTrue(out.contains("Bob"));
        assertTrue(out.contains("Active"));
        assertTrue(out.contains("Inactive"));
    }

    // -----------------------------------------------------
    // deactivateUser()
    // -----------------------------------------------------

    @Test
    void testDeactivateUser_notFound() {
        when(userRepository.findById("X")).thenReturn(null);

        userController.deactivateUser("X");

        assertTrue(outputStream.toString().contains("User not found!"));
    }

    @Test
    void testDeactivateUser_alreadyInactive() {
        User u = new User();
        u.setActive(false);
        when(userRepository.findById("1")).thenReturn(u);

        userController.deactivateUser("1");

        assertTrue(outputStream.toString().contains("User is already inactive!"));
    }

    @Test
    void testDeactivateUser_success() {
        User u = new User();
        u.setActive(true);

        when(userRepository.findById("1")).thenReturn(u);
        when(userRepository.save(u)).thenReturn(true);

        userController.deactivateUser("1");

        assertFalse(u.isActive());
        assertTrue(outputStream.toString().contains("User deactivated successfully!"));
    }

    @Test
    void testDeactivateUser_failure() {
        User u = new User();
        u.setActive(true);

        when(userRepository.findById("1")).thenReturn(u);
        when(userRepository.save(u)).thenReturn(false);

        userController.deactivateUser("1");

        assertTrue(outputStream.toString().contains("Failed to deactivate user!"));
    }

    // -----------------------------------------------------
    // activateUser()
    // -----------------------------------------------------

    @Test
    void testActivateUser_notFound() {
        when(userRepository.findById("X")).thenReturn(null);

        userController.activateUser("X");

        assertTrue(outputStream.toString().contains("User not found!"));
    }

    @Test
    void testActivateUser_alreadyActive() {
        User u = new User();
        u.setActive(true);
        when(userRepository.findById("1")).thenReturn(u);

        userController.activateUser("1");

        assertTrue(outputStream.toString().contains("User is already active!"));
    }

    @Test
    void testActivateUser_success() {
        User u = new User();
        u.setActive(false);

        when(userRepository.findById("1")).thenReturn(u);
        when(userRepository.save(u)).thenReturn(true);

        userController.activateUser("1");

        assertTrue(u.isActive());
        assertTrue(outputStream.toString().contains("User activated successfully!"));
    }

    @Test
    void testActivateUser_failure() {
        User u = new User();
        u.setActive(false);

        when(userRepository.findById("1")).thenReturn(u);
        when(userRepository.save(u)).thenReturn(false);

        userController.activateUser("1");

        assertTrue(outputStream.toString().contains("Failed to activate user!"));
    }

    // -----------------------------------------------------
    // viewUserStatistics()
    // -----------------------------------------------------

    @Test
    void testViewUserStatistics() {

        User admin = new User();
        admin.setRole("ADMIN");
        admin.setActive(true);

        User inactiveUser = new User();
        inactiveUser.setRole("USER");
        inactiveUser.setActive(false);

        User activeUser = new User();
        activeUser.setRole("USER");
        activeUser.setActive(true);

        List<User> list = Arrays.asList(admin, inactiveUser, activeUser);
        when(userRepository.findAll()).thenReturn(list);

        userController.viewUserStatistics();

        String out = outputStream.toString();
        assertTrue(out.contains("Total Users: 3"));
        assertTrue(out.contains("Active Users: 2"));
        assertTrue(out.contains("Admin Users: 1"));
        assertTrue(out.contains("Regular Users: 2"));
        assertTrue(out.contains("Inactive Users: 1"));
    }
}
