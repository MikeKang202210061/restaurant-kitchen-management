package exception;

// Custom exception for invalid order operations
public class InvalidOrderException extends Exception {
    public InvalidOrderException(String message) {
        super(message);
    }
}
