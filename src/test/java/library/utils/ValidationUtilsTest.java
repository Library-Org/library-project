package library.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    @Nested
    @DisplayName("Password Validation Tests")
    class PasswordTests {

        @Test
        @DisplayName("Valid password should pass")
        void testValidPassword() {
            assertTrue(ValidationUtils.isValidPassword("strongpass"));
        }

        @Test
        @DisplayName("Null password should fail")
        void testNullPassword() {
            assertFalse(ValidationUtils.isValidPassword(null));
        }

        @Test
        @DisplayName("Short password should fail")
        void testShortPassword() {
            assertFalse(ValidationUtils.isValidPassword("123"));
        }
    }

    @Nested
    @DisplayName("ISBN Validation Tests")
    class IsbnTests {

        @Test
        @DisplayName("Valid ISBN should pass")
        void testValidIsbn() {
            assertTrue(ValidationUtils.isValidIsbn("9781234567897"));
        }

        @Test
        @DisplayName("Null ISBN should fail")
        void testNullIsbn() {
            assertFalse(ValidationUtils.isValidIsbn(null));
        }

        @Test
        @DisplayName("Empty ISBN should fail")
        void testEmptyIsbn() {
            assertFalse(ValidationUtils.isValidIsbn(""));
        }

        @Test
        @DisplayName("ISBN with only spaces should fail")
        void testBlankIsbn() {
            assertFalse(ValidationUtils.isValidIsbn("   "));
        }
    }

    @Nested
    @DisplayName("Name Validation Tests")
    class NameTests {

        @Test
        @DisplayName("Valid name should pass")
        void testValidName() {
            assertTrue(ValidationUtils.isValidName("John Doe"));
        }

        @Test
        @DisplayName("Null name should fail")
        void testNullName() {
            assertFalse(ValidationUtils.isValidName(null));
        }

        @Test
        @DisplayName("Name too short should fail")
        void testShortName() {
            assertFalse(ValidationUtils.isValidName("A"));
        }

        @Test
        @DisplayName("Name too long should fail")
        void testLongName() {
            String longName = "A".repeat(51);
            assertFalse(ValidationUtils.isValidName(longName));
        }
    }
}
