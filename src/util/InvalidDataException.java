package util;

/**
 * Класс-исключение для некорректно введенных данных
 */

public class InvalidDataException extends Exception {
    public InvalidDataException(String message) {
        super(message);
    }
}
