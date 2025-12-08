package library.services;

import library.models.User;
import library.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for AuthService
 */
@DisplayName("AuthService Tests")
class AuthServiceTest {

    private AuthService authService;
    private UserRepository userRepository;
    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        securityService = mock(SecurityService.class);
        authService = new AuthService(userRepository, securityService);
    }

    // =====================================================================================
    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should register user successfully with valid data")
        void testRegisterUserSuccess() {
            String name = "John Doe";
            String email = "john@example.com";
            String password = "password123";
            String role = "USER";
            String hashedPassword = "hashed_password";

            when(securityService.isValidEmail(email)).thenReturn(true);
            when(userRepository.findByEmail(email)).thenReturn(null);
            when(securityService.hashPassword(password)).thenReturn(hashedPassword);
            when(userRepository.save(any(User.class))).thenReturn(true);

            boolean result = authService.register(name, email, password, role);

            assertTrue(result);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should fail registration with invalid email")
        void testRegisterUserInvalidEmail() {
            when(securityService.isValidEmail("bad")).thenReturn(false);
            boolean result = authService.register("John", "bad", "12345", "USER");
            assertFalse(result);
        }

        @Test
        @DisplayName("Should fail registration with duplicate email")
        void testRegisterUserDuplicateEmail() {
            String email = "existing@example.com";
            when(securityService.isValidEmail(email)).thenReturn(true);
            when(userRepository.findByEmail(email)).thenReturn(new User("x", email, "h", "USER"));

            boolean result = authService.register("John", email, "12345", "USER");
            assertFalse(result);
        }

        @Test
        @DisplayName("Should fail registration with weak password")
        void testRegisterWeakPassword() {
            when(securityService.isValidEmail(anyString())).thenReturn(true);
            when(userRepository.findByEmail(anyString())).thenReturn(null);

            boolean result = authService.register("John", "john@test.com", "12", "USER");
            assertFalse(result);
        }

        @Test
        @DisplayName("Should fail registration if save() returns false")
        void testRegisterSaveReturnsFalse() {
            when(securityService.isValidEmail("john@test.com")).thenReturn(true);
            when(userRepository.findByEmail("john@test.com")).thenReturn(null);
            when(securityService.hashPassword(anyString())).thenReturn("hash");
            when(userRepository.save(any(User.class))).thenReturn(false);

            boolean result = authService.register("John", "john@test.com", "password", "USER");
            assertFalse(result);
        }

        @Test
        @DisplayName("Should fail if name is null")
        void testRegisterNullName() {
            boolean result = authService.register(null, "x@test.com", "pass", "USER");
            assertFalse(result);
        }

        @Test
        @DisplayName("Should fail if role is null")
        void testRegisterNullRole() {
            boolean result = authService.register("John", "x@test.com", "pass", null);
            assertFalse(result);
        }
    }

    // =====================================================================================
    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully")
        void testLoginSuccess() {
            User user = new User("John", "john@test.com", "hash", "USER");
            user.setActive(true);

            when(userRepository.findByEmail("john@test.com")).thenReturn(user);
            when(securityService.verifyPassword("12345", "hash")).thenReturn(true);

            boolean result = authService.login("john@test.com", "12345");

            assertTrue(result);
            assertNotNull(authService.getCurrentUser());
        }

        @Test
        @DisplayName("Should fail login with wrong password")
        void testLoginWrongPassword() {
            User user = new User("John", "john@test.com", "hash", "USER");

            when(userRepository.findByEmail("john@test.com")).thenReturn(user);
            when(securityService.verifyPassword("wrong", "hash")).thenReturn(false);

            boolean result = authService.login("john@test.com", "wrong");

            assertFalse(result);
            assertNull(authService.getCurrentUser());
        }

        @Test
        @DisplayName("Should fail login when email does not exist")
        void testLoginEmailNotFound() {
            when(userRepository.findByEmail("none@test.com")).thenReturn(null);

            boolean result = authService.login("none@test.com", "12345");

            assertFalse(result);
        }

        @Test
        @DisplayName("Should fail login when user is inactive")
        void testLoginInactiveUser() {
            User user = new User("John", "john@test.com", "hash", "USER");
            user.setActive(false);

            when(userRepository.findByEmail("john@test.com")).thenReturn(user);

            boolean result = authService.login("john@test.com", "12345");

            assertFalse(result);
        }

        @Test
        @DisplayName("Should fail login when email is null")
        void testLoginNullEmail() {
            boolean result = authService.login(null, "pass");
            assertFalse(result);
        }

        @Test
        @DisplayName("Should fail login when password is null")
        void testLoginNullPassword() {
            boolean result = authService.login("x@test.com", null);
            assertFalse(result);
        }
    }

    // =====================================================================================
    @Nested
    @DisplayName("Authentication State Tests")
    class AuthenticationStateTests {

        @Test
        @DisplayName("Should return correct login state")
        void testLoginState() {
            assertFalse(authService.isLoggedIn());

            User user = new User("John", "john@test.com", "hash", "USER");
            when(userRepository.findByEmail(anyString())).thenReturn(user);
            when(securityService.verifyPassword(anyString(), anyString())).thenReturn(true);

            authService.login("john@test.com", "pass");
            assertTrue(authService.isLoggedIn());
        }

        @Test
        @DisplayName("Should logout successfully")
        void testLogout() {
            User user = new User("John", "john@test.com", "hash", "USER");
            when(userRepository.findByEmail(anyString())).thenReturn(user);
            when(securityService.verifyPassword(anyString(), anyString())).thenReturn(true);

            authService.login("john@test.com", "pass");
            assertTrue(authService.isLoggedIn());

            authService.logout();
            assertFalse(authService.isLoggedIn());
            assertNull(authService.getCurrentUser());
        }

        @Test
        @DisplayName("Should return false for admin when no user logged in")
        void testIsAdminNoUser() {
            assertFalse(authService.isAdmin());
        }

        @Test
        @DisplayName("Should detect admin role")
        void testIsAdminRole() {
            User admin = new User("Admin", "admin@test.com", "hash", "ADMIN");
            admin.setActive(true);

            when(userRepository.findByEmail("admin@test.com")).thenReturn(admin);
            when(securityService.verifyPassword(anyString(), anyString())).thenReturn(true);

            authService.login("admin@test.com", "pass");

            assertTrue(authService.isAdmin());
        }
    }
}
