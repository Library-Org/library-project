package library.controllers;

import library.config.EmailConfig;
import library.services.NotificationManager;
import library.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class NotificationControllerTest {

    private NotificationManager notificationManager;
    private NotificationService notificationService;
    private NotificationController controller;

    @BeforeEach
    void setup() {
        notificationManager = mock(NotificationManager.class);
        notificationService = mock(NotificationService.class);
        controller = new NotificationController(notificationManager, notificationService);
    }

    @Test
    void testSendOverdueReminders() {
        when(notificationManager.sendOverdueReminders()).thenReturn(5);

        controller.sendOverdueReminders();

        verify(notificationManager, times(1)).sendOverdueReminders();
    }

    @Test
    void testSendReturnReminders() {
        when(notificationManager.sendReturnReminders(3)).thenReturn(2);

        controller.sendReturnReminders(3);

        verify(notificationManager, times(1)).sendReturnReminders(3);
    }

    @Test
    void testConfigureEmail() {
        ArgumentCaptor<EmailConfig> captor = ArgumentCaptor.forClass(EmailConfig.class);

        controller.configureEmail("smtp.com", "587", "user", "pass", true);

        verify(notificationService).setEmailConfig(captor.capture());
        verify(notificationService).setRealMode(true);

        EmailConfig cfg = captor.getValue();
        assert cfg.getHost().equals("smtp.com");
        assert cfg.getPort().equals("587");
        assert cfg.getUsername().equals("user");
        assert cfg.getPassword().equals("pass");
        assert cfg.isEnableTLS();
    }

    @Test
    void testEnableMockMode() {
        controller.enableMockMode();
        verify(notificationService).setRealMode(false);
    }

    @Test
    void testEnableRealMode_ConfigValid() {
        EmailConfig mockConfig = mock(EmailConfig.class);
        when(mockConfig.isValid()).thenReturn(true);
        when(notificationService.getEmailConfig()).thenReturn(mockConfig);

        controller.enableRealMode();

        verify(notificationService).setRealMode(true);
    }

    @Test
    void testEnableRealMode_ConfigInvalid() {
        EmailConfig mockConfig = mock(EmailConfig.class);
        when(mockConfig.isValid()).thenReturn(false);
        when(notificationService.getEmailConfig()).thenReturn(mockConfig);

        controller.enableRealMode();

        verify(notificationService, never()).setRealMode(true);
    }

    @Test
    void testTestEmail_Success() {
        when(notificationManager.testEmailConfiguration("test@mail.com")).thenReturn(true);
        controller.testEmail("test@mail.com");
        verify(notificationManager).testEmailConfiguration("test@mail.com");
    }

    @Test
    void testTestEmail_Failure() {
        when(notificationManager.testEmailConfiguration("test@mail.com")).thenReturn(false);
        controller.testEmail("test@mail.com");
        verify(notificationManager).testEmailConfiguration("test@mail.com");
    }

    @Test
    void testGetStatus_RealMode() {
        EmailConfig config = mock(EmailConfig.class);

        when(notificationService.isRealMode()).thenReturn(true);
        when(notificationService.isEnabled()).thenReturn(true);
        when(notificationService.getEmailConfig()).thenReturn(config);

        when(config.getHost()).thenReturn("smtp.com");
        when(config.getPort()).thenReturn("587");
        when(config.getUsername()).thenReturn("user@mail.com");
        when(config.isValid()).thenReturn(true);

        controller.getStatus();

        verify(notificationService, times(2)).isRealMode();
        verify(notificationService, times(1)).isEnabled();
        verify(notificationService, times(1)).getEmailConfig();

        verify(config, times(1)).getHost();
        verify(config, times(1)).getPort();
        verify(config, times(1)).getUsername();
        verify(config, times(1)).isValid();
    }

    @Test
    void testGetStatus_MockMode() {
        when(notificationService.isRealMode()).thenReturn(false);
        when(notificationService.isEnabled()).thenReturn(false);

        controller.getStatus();

        verify(notificationService, times(2)).isRealMode();
        verify(notificationService, times(1)).isEnabled();
        verify(notificationService, never()).getEmailConfig();
    }
}
