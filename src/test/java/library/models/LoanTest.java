package library.models;

import library.utils.DateUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class LoanTest {

    @Test
    void testStringSettersAndGetters() {
        Loan loan = new Loan();

        loan.setId("L1");
        loan.setUserId("U1");
        loan.setBookId("B1");
        loan.setBorrowDate("2025-01-01T10:00:00");
        loan.setDueDate("2025-01-10T10:00:00");
        loan.setReturnDate("2025-01-05T10:00:00");
        loan.setReturned(true);
        loan.setFineAmount(12.5);

        assertEquals("L1", loan.getId());
        assertEquals("U1", loan.getUserId());
        assertEquals("B1", loan.getBookId());
        assertEquals("2025-01-01T10:00:00", loan.getBorrowDate());
        assertEquals("2025-01-10T10:00:00", loan.getDueDate());
        assertEquals("2025-01-05T10:00:00", loan.getReturnDate());
        assertTrue(loan.isReturned());
        assertEquals(12.5, loan.getFineAmount());
    }

    @Test
    void testBorrowDateTimeConversion() {
        Loan loan = new Loan();

        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 12, 0);
        loan.setBorrowDateTime(now);

        assertEquals(now, loan.getBorrowDateTime());
    }

    @Test
    void testDueDateTimeConversion() {
        Loan loan = new Loan();

        LocalDateTime due = LocalDateTime.of(2025, 1, 10, 12, 0);
        loan.setDueDateTime(due);

        assertEquals(due, loan.getDueDateTime());
    }

    @Test
    void testReturnDateTimeConversion() {
        Loan loan = new Loan();

        LocalDateTime ret = LocalDateTime.of(2025, 1, 5, 12, 0);
        loan.setReturnDateTime(ret);

        assertEquals(ret, loan.getReturnDateTime());
    }

    @Test
    void testIsOverdue_WhenReturned() {
        Loan loan = new Loan();
        loan.setReturned(true);

        assertFalse(loan.isOverdue());
    }

    @Test
    void testIsOverdue_NoDueDate() {
        Loan loan = new Loan();
        loan.setReturned(false);
        loan.setDueDate(null);

        assertFalse(loan.isOverdue());
    }

    @Test
    void testIsOverdue_NotOverdue() {
        Loan loan = new Loan();
        loan.setReturned(false);

        LocalDateTime futureDue = LocalDateTime.now().plusDays(3);
        loan.setDueDateTime(futureDue);

        assertFalse(loan.isOverdue());
    }

    @Test
    void testIsOverdue_Overdue() {
        Loan loan = new Loan();
        loan.setReturned(false);

        LocalDateTime pastDue = LocalDateTime.now().minusDays(3);
        loan.setDueDateTime(pastDue);

        assertTrue(loan.isOverdue());
    }

    @Test
    void testGetOverdueDays_NoOverdue() {
        Loan loan = new Loan();
        loan.setReturned(false);
        loan.setDueDateTime(LocalDateTime.now().plusDays(2));

        assertEquals(0, loan.getOverdueDays());
    }

    @Test
    void testGetOverdueDays_Overdue() {
        Loan loan = new Loan();
        loan.setReturned(false);

        LocalDateTime pastDue = LocalDateTime.now().minusDays(5);
        loan.setDueDateTime(pastDue);

        int days = loan.getOverdueDays();
        assertTrue(days >= 5);  // depending on exact time difference
    }
}
