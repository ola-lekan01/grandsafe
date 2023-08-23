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
        String phoneNumber = inputStr;
        phoneNumber = phoneNumber.replaceAll("[^0-9]", ""); // remove all non-digit characters
        if (phoneNumber.startsWith("234")) {
            phoneNumber = phoneNumber.substring(3); // remove prefix "234"
        } else if (phoneNumber.startsWith("0")) {
            phoneNumber = phoneNumber.substring(1); // remove prefix "0"
        }
        return phoneNumber;
    }
}