package library.models;

import library.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

public class FineTest {

    @Test
    public void testDefaultConstructor() {
        Fine fine = new Fine();

        assertNull(fine.getId());
        assertNull(fine.getUserId());
        assertNull(fine.getLoanId());
        assertEquals(0.0, fine.getAmount(), 0.001);
        assertEquals(0.0, fine.getPaidAmount(), 0.001);
        assertNull(fine.getIssuedDate());
        assertNull(fine.getPaidDate());
        assertFalse(fine.isPaid());
    }

    @Test
    public void testParameterizedConstructor() {
        Fine fine = new Fine("U1", "L1", 50.0);

        assertEquals("U1", fine.getUserId());
        assertEquals("L1", fine.getLoanId());
        assertEquals(50.0, fine.getAmount(), 0.001);
        assertEquals(0.0, fine.getPaidAmount(), 0.001);
        assertNotNull(fine.getIssuedDate());
        assertFalse(fine.isPaid());
    }

    @Test
    public void testSettersAndGetters() {
        Fine fine = new Fine();

        fine.setId("F1");
        fine.setUserId("U1");
        fine.setLoanId("L1");
        fine.setAmount(40.0);
        fine.setPaidAmount(10.0);
        fine.setIssuedDate("2025-01-01T10:00");
        fine.setPaidDate("2025-01-02T10:00");
        fine.setPaid(true);

        assertEquals("F1", fine.getId());
        assertEquals("U1", fine.getUserId());
        assertEquals("L1", fine.getLoanId());
        assertEquals(40.0, fine.getAmount(), 0.001);
        assertEquals(10.0, fine.getPaidAmount(), 0.001);
        assertEquals("2025-01-01T10:00", fine.getIssuedDate());
        assertEquals("2025-01-02T10:00", fine.getPaidDate());
        assertTrue(fine.isPaid());
    }

    @Test
    public void testRemainingAmount() {
        Fine fine = new Fine("U1", "L1", 100);

        fine.setPaidAmount(30);

        assertEquals(70, fine.getRemainingAmount(), 0.001);
    }

    @Test
    public void testGetIssuedDateTime() {
        Fine fine = new Fine();
        fine.setIssuedDate("2024-12-25T12:30");

        assertEquals(
                LocalDateTime.of(2024, 12, 25, 12, 30),
                fine.getIssuedDateTime()
        );
    }

    @Test
    public void testGetPaidDateTimeAndSetter() {
        Fine fine = new Fine();

        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 15, 0);
        fine.setPaidDateTime(now);

        assertEquals(now, fine.getPaidDateTime());
    }

    @Test
    public void testMakePaymentSuccessPartial() {
        Fine fine = new Fine("U1", "L1", 100);

        boolean result = fine.makePayment(30);

        assertTrue(result);
        assertEquals(30, fine.getPaidAmount(), 0.001);
        assertFalse(fine.isPaid());
        assertNull(fine.getPaidDate());
    }

    @Test
    public void testMakePaymentSuccessFullPayment() {
        Fine fine = new Fine("U1", "L1", 80);

        boolean result = fine.makePayment(80);

        assertTrue(result);
        assertEquals(80, fine.getPaidAmount(), 0.001);
        assertTrue(fine.isPaid());
        assertNotNull(fine.getPaidDate());
    }

    @Test
    public void testMakePaymentFailNegative() {
        Fine fine = new Fine("U1", "L1", 50);

        boolean result = fine.makePayment(-10);

        assertFalse(result);
        assertEquals(0, fine.getPaidAmount(), 0.001);
    }

    @Test
    public void testMakePaymentFailZero() {
        Fine fine = new Fine("U1", "L1", 50);

        boolean result = fine.makePayment(0);

        assertFalse(result);
        assertEquals(0, fine.getPaidAmount(), 0.001);
    }

    @Test
    public void testMakePaymentFailExceedsAmount() {
        Fine fine = new Fine("U1", "L1", 40);

        boolean result = fine.makePayment(50);

        assertFalse(result);
        assertEquals(0, fine.getPaidAmount(), 0.001);
        assertFalse(fine.isPaid());
    }
}
