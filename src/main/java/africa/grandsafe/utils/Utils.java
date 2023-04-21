package africa.grandsafe.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
    public static boolean isValidToken(LocalDateTime expiryDate) {
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), expiryDate);
        return minutes >= 0;
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }
    public static String extractSubstring(String inputStr) {
        if (inputStr.startsWith("+")) {
            inputStr = inputStr.substring(1);
        }
        if (inputStr.startsWith("0")) {
            inputStr = inputStr.substring(1);
        }
        return inputStr;
    }
}