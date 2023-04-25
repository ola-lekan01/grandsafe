package africa.grandsafe.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static africa.grandsafe.utils.Utils.*;
import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {
    @Test
    void testIsValidTokenWithFutureExpiryDate() {
        LocalDateTime futureExpiryDate = LocalDateTime.now().plusMinutes(10);
        assertTrue(isValidToken(futureExpiryDate));
    }

    @Test
    void testIsValidTokenWithPastExpiryDate() {
        LocalDateTime pastExpiryDate = LocalDateTime.now().minusMinutes(10);
        assertFalse(isValidToken(pastExpiryDate));
    }

    @Test
    void testExtractString(){
        assertEquals("8069580949", extractSubstring("08069580949"));
        assertEquals("8069580949", extractSubstring("+2348069580949"));
        assertEquals("8069580949", extractSubstring("2348069580949"));
    }

    @Test
    public void testIsNullOrEmpty() {
        // Test null value
        assertTrue(isNullOrEmpty(null));

        // Test empty string
        assertTrue(isNullOrEmpty(""));

        // Test string with only whitespaces
        assertTrue(isNullOrEmpty("   "));

        // Test non-empty string without whitespaces
        assertFalse(isNullOrEmpty("hello"));

        // Test non-empty string with whitespaces
        assertFalse(isNullOrEmpty(" hello "));
    }

}