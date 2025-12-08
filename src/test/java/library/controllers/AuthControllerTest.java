package library.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import library.services.AuthService;
import library.services.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthControllerTest {

    private AuthService authServiceMock;
    private NotificationService notificationServiceMock;
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        authServiceMock = mock(AuthService.class);
        notificationServiceMock = mock(NotificationService.class);
        authController = new AuthController(authServiceMock, notificationServiceMock);
    }

    @Test
    public void testLoginCallsAuthService() {
        String email = "john@mail.com";
        String password = "1234";

        when(authServiceMock.login(email, password)).thenReturn(true);

        authController.login(email, password);

        verify(authServiceMock, times(1)).login(email, password);
    }

    @Test
    public void testLoginFailure() {
        when(authServiceMock.login("wrong@mail.com", "wrong")).thenReturn(false);

        authController.login("wrong@mail.com", "wrong");

        verify(authServiceMock, times(1)).login("wrong@mail.com", "wrong");
    }

    @Test
    public void testLogout() {
        doNothing().when(authServiceMock).logout();

        authController.logout();

        verify(authServiceMock, times(1)).logout();
    }

    @Test
    public void testIsLoggedIn() {
        when(authServiceMock.isLoggedIn()).thenReturn(true);

        boolean result = authController.isLoggedIn();

        assertTrue(result);
        verify(authServiceMock).isLoggedIn();
    }

    @Test
    public void testIsAdmin() {
        when(authServiceMock.isAdmin()).thenReturn(true);

        boolean result = authController.isAdmin();

        assertTrue(result);
        verify(authServiceMock).isAdmin();
    }
}
