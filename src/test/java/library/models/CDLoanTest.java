package library.models;

import library.models.CDLoan;
import library.utils.DateUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CDLoanTest {

    @Test
    void testConstructorAndGetters() {
        CDLoan loan = new CDLoan("L1", "U1", "CD1", "2025-12-10T10:00:00");

        assertEquals("L1", loan.getId());
        assertEquals("U1", loan.getUserId());
        assertEquals("CD1", loan.getCdId());
        assertEquals("2025-12-10T10:00:00", loan.getDueDate());
    }

    @Test
    void testSetters() {
        CDLoan loan = new CDLoan();

        loan.setId("L2");
        loan.setUserId("U2");
        loan.setCdId("CD2");
        loan.setDueDate("2030-01-01T12:00:00");
        loan.setReturned(true);

        assertEquals("L2", loan.getId());
        assertEquals("U2", loan.getUserId());
        assertEquals("CD2", loan.getCdId());
        assertEquals("2030-01-01T12:00:00", loan.getDueDate());
        assertTrue(loan.isReturned());
    }

    @Test
    void testGetDueDateTime() {
        CDLoan loan = new CDLoan();
        loan.setDueDate("2030-01-01T12:00:00");

        LocalDateTime dt = loan.getDueDateTime();
        assertEquals(2030, dt.getYear());
        assertEquals(1, dt.getMonthValue());
        assertEquals(1, dt.getDayOfMonth());
    }

    @Test
    void testIsOverdue_True() {
        CDLoan loan = new CDLoan();
        loan.setDueDate(DateUtils.toString(LocalDateTime.now().minusDays(2)));

        assertTrue(loan.isOverdue());
    }

    @Test
    void testIsOverdue_False() {
        CDLoan loan = new CDLoan();
        loan.setDueDate(DateUtils.toString(LocalDateTime.now().plusDays(2)));

        assertFalse(loan.isOverdue());
    }

    @Test
    void testReturnCD() {
        CDLoan loan = new CDLoan();

        assertFalse(loan.isReturned());

        loan.returnCD();

        assertTrue(loan.isReturned());
    }

    @Test
    void testExtendLoan() {
        CDLoan loan = new CDLoan();
        loan.setDueDate("2030-01-01T12:00:00");

        loan.extendLoan(5);

        LocalDateTime expected = LocalDateTime.of(2030, 1, 6, 12, 0);

        assertEquals(expected, loan.getDueDateTime());
    }

    @Test
    void testToStringNotNull() {
        CDLoan loan = new CDLoan("L5", "U5", "CD5", "2030-01-01T12:00:00");

        assertNotNull(loan.toString());
    }
}
