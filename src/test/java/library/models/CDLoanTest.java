package library.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CDLoanTest {

    @Test
    void testSetReturnDate() {
        CDLoan loan = new CDLoan();
        LocalDateTime now = LocalDateTime.now();
        loan.setReturnDateTime(now);

        assertEquals(now, loan.getReturnDateTime());
    }

    @Test
    void testIsOverdue() {
        CDLoan loan = new CDLoan();
        loan.setDueDateTime(LocalDateTime.now().minusDays(1)); // yesterday

        assertTrue(loan.isOverdue());
    }

    @Test
    public void testIsReturned() {
        // استخدام الكونستركتور الموجود فعليًا في كودك
        CDLoan loan = new CDLoan("USER1", "CD1");

        // بالبداية مفروض يكون غير مُعاد
        assertFalse(loan.isReturned());
        assertNotNull(loan.getBorrowDate());

        // تغيير الإرجاع
        loan.setReturned(true);

        // التحقق
        assertTrue(loan.isReturned());
    }
}
