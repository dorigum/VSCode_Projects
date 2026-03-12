package exception;

public class CafeKioskException extends RuntimeException {
    public CafeKioskException(String message) {
        super(message);
    }

    public CafeKioskException(String message, Throwable cause) {
        super(message, cause);
    }
}
