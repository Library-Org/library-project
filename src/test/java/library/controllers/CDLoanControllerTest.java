package library.controllers;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class CDLoanControllerTest {

    @Test
    void testShortenString_nullInput() throws Exception {
        CDLoanController ctrl = createDummy();
        Method m = CDLoanController.class.getDeclaredMethod("shortenString", String.class, int.class);
        m.setAccessible(true);

        String result = (String) m.invoke(ctrl, null, 10);
        assertEquals("", result);
    }

    @Test
    void testShortenString_shorterThanMax() throws Exception {
        CDLoanController ctrl = createDummy();
        Method m = CDLoanController.class.getDeclaredMethod("shortenString", String.class, int.class);
        m.setAccessible(true);

        String result = (String) m.invoke(ctrl, "Hello", 10);
        assertEquals("Hello", result);
    }

    @Test
    void testShortenString_longerThanMax() throws Exception {
        CDLoanController ctrl = createDummy();
        Method m = CDLoanController.class.getDeclaredMethod("shortenString", String.class, int.class);
        m.setAccessible(true);

        String result = (String) m.invoke(ctrl, "ABCDEFGHIJK", 5);
        assertEquals("AB...", result);
    }

    private CDLoanController createDummy() {
        return new CDLoanController(null, null, null, null);
    }
}
