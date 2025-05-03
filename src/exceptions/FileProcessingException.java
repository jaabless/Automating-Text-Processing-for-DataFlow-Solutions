package exceptions;

public class FileProcessingException extends RuntimeException {
    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(String messages, Throwable cause) {
        super(messages, cause);
    }
}
