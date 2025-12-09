package library.services;

import library.models.*;
import library.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class NotificationManagerTest {

    private NotificationService notificationService;
    private LoanRepository loanRepository;
    private CDLoanRepository cdLoanRepository;
    private FineRepository fineRepository;
    private CDFineRepository cdFineRepository;
    private UserRepository userRepository;

    private NotificationManager manager;

    @BeforeEach
    void setup() {
        notificationService = mock(NotificationService.class);
        loanRepository = mock(LoanRepository.class);
        cdLoanRepository = mock(CDLoanRepository.class);
        fineRepository = mock(FineRepository.class);
        cdFineRepository = mock(CDFineRepository.class);
        userRepository = mock(UserRepository.class);

        manager = new NotificationManager(
                notificationService,
                loanRepository,
                cdLoanRepository,
                fineRepository,
                cdFineRepository,
                userRepository
        );
    }

    private User createActiveUser(String id) {
        User u = new User();
        u.setId(id);
        u.setName("User " + id);
        u.setEmail(id + "@mail.com");
        u.setActive(true);
        return u;
    }

    
    @Test
    void testSendOverdueReminders() {

    	
        Loan loan = mock(Loan.class);
        when(loan.getUserId()).thenReturn("U1");
        when(loan.isOverdue()).thenReturn(true);
        when(loanRepository.findOverdueLoans()).thenReturn(List.of(loan));
        when(loanRepository.findByUserId("U1")).thenReturn(List.of(loan));

        
        CDLoan cdLoan = mock(CDLoan.class);
        when(cdLoan.getUserId()).thenReturn("U1");
        when(cdLoan.isOverdue()).thenReturn(true);
        when(cdLoanRepository.findOverdueCDLoans()).thenReturn(List.of(cdLoan));
        when(cdLoanRepository.findByUserId("U1")).thenReturn(List.of(cdLoan));

       
        User user = createActiveUser("U1");
        when(userRepository.findById("U1")).thenReturn(user);

       
        Fine fine = mock(Fine.class);
        when(fine.isPaid()).thenReturn(false);
        when(fine.getRemainingAmount()).thenReturn(10.0);
        when(fineRepository.findByUserId("U1")).thenReturn(List.of(fine));

        CDFine cdFine = mock(CDFine.class);
        when(cdFine.isPaid()).thenReturn(false);
        when(cdFine.getRemainingAmount()).thenReturn(5.0);
        when(cdFineRepository.findByUserId("U1")).thenReturn(List.of(cdFine));

        
        when(notificationService.sendEmail(anyString(), anyString(), anyString()))
                .thenReturn(true);

        int result = manager.sendOverdueReminders();

        assertEquals(2, result);  // book + cd
        verify(notificationService, times(2))
                .sendEmail(anyString(), anyString(), anyString());
    }

    
    @Test
    void testSendCombinedReminders() {
        User user = createActiveUser("U1");
        when(userRepository.findAll()).thenReturn(List.of(user));

       
        Loan loan = mock(Loan.class);
        when(loan.getUserId()).thenReturn("U1");
        when(loan.isOverdue()).thenReturn(true);
        when(loanRepository.findByUserId("U1")).thenReturn(List.of(loan));

       
        CDLoan cdLoan = mock(CDLoan.class);
        when(cdLoan.getUserId()).thenReturn("U1");
        when(cdLoan.isOverdue()).thenReturn(true);
        when(cdLoanRepository.findByUserId("U1")).thenReturn(List.of(cdLoan));

        
        when(fineRepository.findByUserId("U1"))
                .thenReturn(List.of(mock(Fine.class)));

        when(cdFineRepository.findByUserId("U1"))
                .thenReturn(List.of(mock(CDFine.class)));

        
        when(notificationService.sendEmail(anyString(), anyString(), anyString()))
                .thenReturn(true);

        int result = manager.sendCombinedReminders();

        assertEquals(1, result);
    }

    
    @Test
    void testSendReturnReminders() {

        Loan loan = mock(Loan.class);
        when(loan.isReturned()).thenReturn(false);
        when(loan.getUserId()).thenReturn("U1");

        when(loan.getBookId()).thenReturn("B1");
        when(loan.getDueDateTime()).thenReturn(LocalDateTime.now().plusDays(1));

        when(loanRepository.findAll()).thenReturn(List.of(loan));

        User user = createActiveUser("U1");
        when(userRepository.findById("U1")).thenReturn(user);

        when(notificationService.sendReturnReminder(any(), anyString(), anyString()))
                .thenReturn(true);

        int result = manager.sendReturnReminders(2);

        assertEquals(1, result);
    }

    
    @Test
    void testSendWelcomeNotification() {
        User user = createActiveUser("U1");

        when(notificationService.sendWelcomeEmail(eq(user), isNull()))
                .thenReturn(true);

        assertTrue(manager.sendWelcomeNotification(user));
    }

    
    @Test
    void testSendPaymentNotification() {
        User user = createActiveUser("U1");

        when(notificationService.sendPaymentConfirmation(user, 20.0, 5.0))
                .thenReturn(true);

        assertTrue(manager.sendPaymentNotification(user, 20.0, 5.0));
    }

    @Test
    void testEmailConfiguration() {

        when(notificationService.isRealMode()).thenReturn(false);
        when(notificationService.sendEmail(anyString(), anyString(), anyString()))
                .thenReturn(true);

        boolean result = manager.testEmailConfiguration("test@mail.com");

        assertTrue(result);
        verify(notificationService).setRealMode(true);
    }
}
