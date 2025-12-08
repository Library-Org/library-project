package library.controllers;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import library.services.AuthService;
import library.services.NotificationService;
import library.models.User;

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

    // ----------------------------- اختبار التسجيل -----------------------------
    @Test
    public void testRegister_Success() {
        String name = "John Doe";
        String email = "john.doe@mail.com";
        String password = "password123";
        String role = "USER";

        when(authServiceMock.register(name, email, password, role)).thenReturn(true);
        when(authServiceMock.getCurrentUser()).thenReturn(new User(name, email, password, role));

        authController.register(name, email, password, role);

        verify(notificationServiceMock, times(1)).sendWelcomeEmail(any(), any());
        verify(authServiceMock, times(1)).register(name, email, password, role);
    }

    @Test
    public void testRegister_Failure() {
        String name = "John Doe";
        String email = "john.doe@mail.com";
        String password = "password123";
        String role = "USER";

        when(authServiceMock.register(name, email, password, role)).thenReturn(false);

        authController.register(name, email, password, role);

        // لا يجب إرسال البريد الترحيبي في حالة الفشل
        verify(notificationServiceMock, times(0)).sendWelcomeEmail(any(), any());
    }

    // ----------------------------- اختبار تسجيل الدخول -----------------------------
    @Test
    public void testLogin_Success() {
        String email = "john@mail.com";
        String password = "1234";

        when(authServiceMock.login(email, password)).thenReturn(true);

        authController.login(email, password);

        verify(authServiceMock, times(1)).login(email, password);
    }

    @Test
    public void testLogin_Failure() {
        when(authServiceMock.login("wrong@mail.com", "wrong")).thenReturn(false);

        authController.login("wrong@mail.com", "wrong");

        verify(authServiceMock, times(1)).login("wrong@mail.com", "wrong");
    }

    // ----------------------------- اختبار تسجيل الخروج -----------------------------
    @Test
    public void testLogout() {
        doNothing().when(authServiceMock).logout();

        authController.logout();

        verify(authServiceMock, times(1)).logout();
    }

    // ----------------------------- اختبار حالة تسجيل الدخول -----------------------------
    @Test
    public void testIsLoggedIn_True() {
        when(authServiceMock.isLoggedIn()).thenReturn(true);

        boolean result = authController.isLoggedIn();

        assertTrue(result);
        verify(authServiceMock).isLoggedIn();
    }

    @Test
    public void testIsLoggedIn_False() {
        when(authServiceMock.isLoggedIn()).thenReturn(false);

        boolean result = authController.isLoggedIn();

        assertFalse(result);
        verify(authServiceMock).isLoggedIn();
    }

    // ----------------------------- اختبار صلاحية الدور -----------------------------
    @Test
    public void testIsAdmin_True() {
        when(authServiceMock.isAdmin()).thenReturn(true);

        boolean result = authController.isAdmin();

        assertTrue(result);
        verify(authServiceMock).isAdmin();
    }

    @Test
    public void testIsAdmin_False() {
        when(authServiceMock.isAdmin()).thenReturn(false);

        boolean result = authController.isAdmin();

        assertFalse(result);
        verify(authServiceMock).isAdmin();
    }
}
