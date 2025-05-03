package exceptions;

public class InvalidRegexPatternException extends RuntimeException {
    public InvalidRegexPatternException(String message) {
        super(message);
    }

    public InvalidRegexPatternException(String messages, Throwable cause) {
        super(messages, cause);
    }
}