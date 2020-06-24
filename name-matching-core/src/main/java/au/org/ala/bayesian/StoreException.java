package au.org.ala.bayesian;

/**
 * An exception that indicates one of the information stores has encountered a problem.
 */
public class StoreException extends Exception {
    /**
     * Consurct with a message.
     *
     * @param message The message
     */
    public StoreException(String message) {
        super(message);
    }

    /**
     * Construct with a message and cause.
     *
     * @param message The message
     * @param cause The underlying cause
     */
    public StoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
