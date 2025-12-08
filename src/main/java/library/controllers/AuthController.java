package library.controllers;

import library.services.AuthService;
import library.services.NotificationService;
import library.models.User;

/**
 * Controller for authentication operations
 * @author Library Team
 * @version 1.0
 */
public class AuthController {
    private AuthService authService;
    private NotificationService notificationService;

    public AuthController(AuthService authService) {
        this.authService = authService;
        this.notificationService = new NotificationService();
    }

    public AuthController(AuthService authService, NotificationService notificationService) {
        this.authService = authService;
        this.notificationService = notificationService;
    }

    /**
     * Register a new user
     * @param name user's name
     * @param email user's email
     * @param password user's password
     * @param role user's role
     */
    public void register(String name, String email, String password, String role) {
        boolean success = authService.register(name, email, password, role);
        if (success) {
            System.out.println("✅ Registration successful! You can now login.");
            
            // Send welcome email
            User newUser = authService.getCurrentUser();
            if (newUser != null) {
                notificationService.sendWelcomeEmail(newUser, null);
            }
        } else {
            System.out.println("❌ Registration failed! Please try again.");
        }
    }

    /**
     * Login user
     * @param email user's email
     * @param password user's password
     */
    public void login(String email, String password) {
        authService.login(email, password);
    }

    /**
     * Logout current user
     */
    public void logout() {
        authService.logout();
    }

    /**
     * Check if user is logged in
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return authService.isLoggedIn();
    }

    /**
     * Check if current user is admin
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin() {
        return authService.isAdmin();
    }
}
