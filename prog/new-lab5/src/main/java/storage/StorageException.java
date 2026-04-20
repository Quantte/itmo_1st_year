package storage;

/**
 * Thrown when a storage operation fails (e.g., write permission denied).
 */
public class StorageException extends RuntimeException {
    /**
     * @param message description of the failure
     * @param cause   underlying cause
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
