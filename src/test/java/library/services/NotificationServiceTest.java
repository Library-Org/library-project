package library.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import library.config.EmailConfig;
import library.models.User;

class NotificationServiceTest {

    private NotificationService notificationService;
    private User user;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService();
        user = new User("1", "Kamal", "kamal@test.com", "MEMBER");
    }

    // ----------------------------- MOCK MODE EMAIL TEST -----------------------------
    @Test
    void testSendEmail_MockMode() {
        notificationService.setRealMode(false); // تفعيل الوضع المحاكي

        boolean result = notificationService.sendEmail(
                "test@test.com",
                "Test Subject",
                "Test Body"
        );

        assertTrue(result); // الوضع المحاكي دائمًا يعيد true
    }

    // ----------------------------- REAL MODE WITH INVALID CONFIG -----------------------------
    @Test
    void testSendEmail_RealMode_InvalidConfig() {
        notificationService.setRealMode(true); // تفعيل الوضع الحقيقي

        EmailConfig config = new EmailConfig(); // إعدادات غير صحيحة
        notificationService.setEmailConfig(config);

        boolean result = notificationService.sendEmail(
                "test@test.com",
                "Test Subject",
                "Test Body"
        );

        assertFalse(result); // لا يتم الإرسال في حالة الإعدادات غير الصحيحة
    }

    // ----------------------------- SEND OVERDUE REMINDER -----------------------------
    @Test
    void testSendOverdueReminder_UserObject() {
        boolean result = notificationService.sendOverdueReminder(
                user, 3, 12.50
        );
        assertTrue(result); // تأكد من إرسال التذكير بنجاح
    }

    // ----------------------------- SEND OVERDUE REMINDER (String version) -----------------------------
    @Test
    void testSendOverdueReminder_StringVersion() {
        boolean result = notificationService.sendOverdueReminder(
                "kamal@test.com", "Kamal", 2, 5.0
        );
        assertTrue(result); // تأكد من إرسال التذكير بنجاح
    }

    // ----------------------------- SEND WELCOME EMAIL -----------------------------
    @Test
    void testSendWelcomeEmail() {
        boolean result = notificationService.sendWelcomeEmail(
                user, "temp123"
        );
        assertTrue(result); // تأكد من إرسال البريد الإلكتروني للترحيب
    }

    // ----------------------------- SEND RETURN REMINDER -----------------------------
    @Test
    void testSendReturnReminder() {
        boolean result = notificationService.sendReturnReminder(
                user, "Clean Code", "2025-12-15"
        );
        assertTrue(result); // تأكد من إرسال تذكير العودة بنجاح
    }

    // ----------------------------- SEND PAYMENT CONFIRMATION -----------------------------
    @Test
    void testSendPaymentConfirmation() {
        boolean result = notificationService.sendPaymentConfirmation(
                user, 20.0, 5.0
        );
        assertTrue(result); // تأكد من إرسال تأكيد الدفع بنجاح
    }

    // ----------------------------- GETTERS / SETTERS TEST -----------------------------
    @Test
    void testGettersAndSetters() {
        notificationService.setEnabled(false);
        assertFalse(notificationService.isEnabled());

        notificationService.setRealMode(true);
        assertTrue(notificationService.isRealMode());

        EmailConfig config = new EmailConfig();
        config.setHost("smtp.server.com");
        notificationService.setEmailConfig(config);
        assertEquals("smtp.server.com", notificationService.getEmailConfig().getHost());
    }

    // ----------------------------- CONSTRUCTOR TEST -----------------------------
    @Test
    void testConstructorWithParams() {
        EmailConfig config = new EmailConfig();
        NotificationService service = new NotificationService(config, true);

        assertEquals(config, service.getEmailConfig());
        assertTrue(service.isRealMode());
        assertTrue(service.isEnabled());
    }
}

