package library.models;

import library.utils.DateUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CDFineTest {

    

    @Test
    void testConstructor_Valid() {
        CDFine fine = new CDFine("U1", "CD10", 50);

        assertEquals("U1", fine.getUserId());
        assertEquals("CD10", fine.getCdLoanId());
        assertEquals(50, fine.getAmount());
        assertEquals(50, fine.getRemainingAmount());
        assertFalse(fine.isPaid());
        assertNotNull(fine.getIssuedDate());
    }

    @Test
    void testConstructor_InvalidParams() {
        assertThrows(IllegalArgumentException.class, () -> new CDFine(null, "C", 10));
        assertThrows(IllegalArgumentException.class, () -> new CDFine("U", null, 10));
        assertThrows(IllegalArgumentException.class, () -> new CDFine("U", "C", 0));
        assertThrows(IllegalArgumentException.class, () -> new CDFine("U", "C", -5));
    }


    

    @Test
    void testSetId_UserLoanIssuedPaidDates() {
        CDFine fine = new CDFine("U1", "L1", 30);

        fine.setId("F100");
        fine.setUserId("UU");
        fine.setCdLoanId("LL");
        fine.setIssuedDate("2025-01-01");
        fine.setPaidDate("2025-02-01");
        fine.setPaid(true);

        assertEquals("F100", fine.getId());
        assertEquals("UU", fine.getUserId());
        assertEquals("LL", fine.getCdLoanId());
        assertEquals("2025-01-01", fine.getIssuedDate());
        assertEquals("2025-02-01", fine.getPaidDate());
        assertTrue(fine.isPaid());
    }


    
    @Test
    void testSetAmount_Valid() {
        CDFine fine = new CDFine("U1", "C1", 100);

        fine.setAmount(60); // reduces remaining
        assertEquals(60, fine.getAmount());
        assertEquals(60, fine.getRemainingAmount());
    }

    @Test
    void testSetAmount_Invalid() {
        CDFine fine = new CDFine("U1", "C1", 20);
        assertThrows(IllegalArgumentException.class, () -> fine.setAmount(-1));
    }


   

    @Test
    void testSetPaidAmount_Valid() {
        CDFine fine = new CDFine("U1", "C1", 50);

        fine.setPaidAmount(10);
        assertEquals(10, fine.getPaidAmount());
        assertEquals(40, fine.getRemainingAmount());
    }

    @Test
    void testSetPaidAmount_Invalid() {
        CDFine fine = new CDFine("U1", "C1", 50);
        assertThrows(IllegalArgumentException.class, () -> fine.setPaidAmount(-5));
    }


    

    @Test
    void testSetRemainingAmount_Valid() {
        CDFine fine = new CDFine("U1", "C1", 50);

        fine.setRemainingAmount(30);
        assertEquals(30, fine.getRemainingAmount());
    }

    @Test
    void testSetRemainingAmount_Invalid() {
        CDFine fine = new CDFine("U1", "C1", 50);
        assertThrows(IllegalArgumentException.class, () -> fine.setRemainingAmount(-1));
    }


    

    @Test
    void testRecalcRemaining_AfterAmountChange() {
        CDFine fine = new CDFine("U1", "C1", 100);

        fine.setPaidAmount(30);
        fine.setAmount(80); // triggers recalc

        assertEquals(50, fine.getRemainingAmount());
    }


    

    @Test
    void testMakePayment_InvalidAmounts() {
        CDFine fine = new CDFine("U1", "C1", 40);

        assertFalse(fine.makePayment(0));
        assertFalse(fine.makePayment(-5));
        assertFalse(fine.makePayment(50)); // > remaining
    }

    @Test
    void testMakePayment_PartialPayment() {
        CDFine fine = new CDFine("U1", "C1", 40);

        assertTrue(fine.makePayment(10));
        assertEquals(10, fine.getPaidAmount());
        assertEquals(30, fine.getRemainingAmount());
        assertFalse(fine.isPaid());
    }

    @Test
    void testMakePayment_FullPayment() {
        CDFine fine = new CDFine("U1", "C1", 40);

        assertTrue(fine.makePayment(40));

        assertEquals(40, fine.getPaidAmount());
        assertEquals(0, fine.getRemainingAmount());
        assertTrue(fine.isPaid());
        assertNotNull(fine.getPaidDate());
    }

    @Test
    void testMakePayment_OverMultipleSteps() {
        CDFine fine = new CDFine("U1", "C1", 100);

        assertTrue(fine.makePayment(30));
        assertTrue(fine.makePayment(70));

        assertEquals(100, fine.getPaidAmount());
        assertEquals(0, fine.getRemainingAmount());
        assertTrue(fine.isPaid());
    }

}
