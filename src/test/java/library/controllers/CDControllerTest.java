package library.controllers;

import library.models.CD;
import library.models.CDLoan;
import library.models.CDFine;
import library.repositories.CDRepository;
import library.repositories.UserRepository;
import library.services.CDLoanService;
import library.services.CDFineService;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CDLoanControllerTest {

    private CDLoanService cdLoanService;
    private CDFineService cdFineService;
    private UserRepository userRepository;
    private CDRepository cdRepository;

    private CDLoanController controller;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        cdLoanService = mock(CDLoanService.class);
        cdFineService = mock(CDFineService.class);
        userRepository = mock(UserRepository.class);
        cdRepository = mock(CDRepository.class);

        controller = new CDLoanController(cdLoanService, cdFineService, userRepository, cdRepository);

        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // ============================================================
    // TEST viewUserCDLoans
    // ============================================================

    @Test
    void testViewUserCDLoans_NoLoans() {
        when(cdLoanService.getUserCDLoans("user1")).thenReturn(Collections.emptyList());

        controller.viewUserCDLoans("user1");

        String output = outputStream.toString();
        assertTrue(output.contains("No CD loans found."));
    }

    @Test
    void testViewUserCDLoans_WithLoans() {
        CDLoan loan = mock(CDLoan.class);
        when(loan.getId()).thenReturn("LOAN123456789");
        when(loan.getCdId()).thenReturn("CD001");
        when(loan.getDueDate()).thenReturn("2025-12-31T00:00");
        when(loan.getDueDateTime()).thenReturn(LocalDateTime.now().plusDays(5));
        when(loan.isReturned()).thenReturn(false);
        when(loan.isOverdue()).thenReturn(false);

        CD cd = new CD();
        cd.setTitle("Best Hits");
        cd.setArtist("John Doe");

        when(cdLoanService.getUserCDLoans("user1")).thenReturn(List.of(loan));
        when(cdRepository.findById("CD001")).thenReturn(cd);

        controller.viewUserCDLoans("user1");

        String output = outputStream.toString();
        assertTrue(output.contains("My CD Loans"));
        assertTrue(output.contains("Best Hits"));
        assertTrue(output.contains("John Doe"));
    }

    // ============================================================
    // TEST viewUserCDFines
    // ============================================================

    @Test
    void testViewUserCDFines_NoFines() {
        when(cdFineService.getUserCDFines("user1")).thenReturn(Collections.emptyList());

        controller.viewUserCDFines("user1");

        String output = outputStream.toString();
        assertTrue(output.contains("No CD fines found."));
    }

    @Test
    void testViewUserCDFines_WithFines() {
        CDFine fine = mock(CDFine.class);
        when(fine.getId()).thenReturn("FINE123456789");
        when(fine.getAmount()).thenReturn(15.0);
        when(fine.getPaidAmount()).thenReturn(5.0);
        when(fine.getRemainingAmount()).thenReturn(10.0);
        when(fine.isPaid()).thenReturn(false);

        when(cdFineService.getUserCDFines("user1")).thenReturn(List.of(fine));

        controller.viewUserCDFines("user1");

        String output = outputStream.toString();
        assertTrue(output.contains("My CD Fines"));
        assertTrue(output.contains("$15.00"));
        assertTrue(output.contains("$5.00"));
        assertTrue(output.contains("$10.00"));
    }

    // ============================================================
    // TEST borrowCD
    // ============================================================

    @Test
    void testBorrowCD_Success() {
        when(cdLoanService.borrowCD("user1", "cd1")).thenReturn(true);

        controller.borrowCD("user1", "cd1");

        String output = outputStream.toString();
        assertTrue(output.contains("CD borrowed successfully"));
    }

    @Test
    void testBorrowCD_Fail() {
        when(cdLoanService.borrowCD("user1", "cd1")).thenReturn(false);

        controller.borrowCD("user1", "cd1");

        String output = outputStream.toString();
        assertTrue(output.contains("Failed to borrow CD"));
    }

    // ============================================================
    // TEST returnCD
    // ============================================================

    @Test
    void testReturnCD_Success() {
        when(cdLoanService.returnCD("loan1")).thenReturn(true);

        controller.returnCD("loan1");

        String output = outputStream.toString();
        assertTrue(output.contains("CD returned successfully"));
    }

    @Test
    void testReturnCD_Fail() {
        when(cdLoanService.returnCD("loan1")).thenReturn(false);

        controller.returnCD("loan1");

        String output = outputStream.toString();
        assertTrue(output.contains("Failed to return CD"));
    }

    // ============================================================
    // TEST payCDFine
    // ============================================================

    @Test
    void testPayCDFine_InvalidAmount() {
        controller.payCDFine("fine1", -5);

        String output = outputStream.toString();
        assertTrue(output.contains("Invalid payment amount"));
    }

    @Test
    void testPayCDFine_Success() {
        when(cdFineService.payCDFine("fine1", 10.0)).thenReturn(true);

        controller.payCDFine("fine1", 10.0);

        String output = outputStream.toString();
        assertTrue(output.contains("CD fine payment processed successfully"));
    }

    @Test
    void testPayCDFine_Fail() {
        when(cdFineService.payCDFine("fine1", 10.0)).thenReturn(false);

        controller.payCDFine("fine1", 10.0);

        String output = outputStream.toString();
        assertTrue(output.contains("Failed to process CD fine payment"));
    }
}
