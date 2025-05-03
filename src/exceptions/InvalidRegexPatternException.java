package exceptions;

public class InvalidRegexPatternException extends RuntimeException {
    public InvalidRegexPatternException(String message) {
        super(message);
    }

    public InvalidRegexPatternException(String message, Throwable cause) {
        super(message, cause);
    }
}