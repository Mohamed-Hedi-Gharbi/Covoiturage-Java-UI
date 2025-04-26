package covoiturage.ui.validator;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class InputValidator {
    private static final Pattern EMAIL_PATTERN                  = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern TELEPHONE_PATTERN              = Pattern.compile("^[0-9]{8}$");
    private static final DateTimeFormatter DATE_FORMATTER       = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER  = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidTelephone(String telephone) {
        return telephone != null && TELEPHONE_PATTERN.matcher(telephone).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    public static boolean isValidDateTime(String dateTimeStr) {
        try {
            LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
    }

    public static boolean isValidDouble(String doubleStr) {
        try {
            Double.parseDouble(doubleStr);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidInteger(String intStr) {
        try {
            Integer.parseInt(intStr);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveDouble(String doubleStr) {
        try {
            return Double.parseDouble(doubleStr) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveInteger(String intStr) {
        try {
            return Integer.parseInt(intStr) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
