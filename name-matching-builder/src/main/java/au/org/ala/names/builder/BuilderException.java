package au.org.ala.names.builder;

/**
 * An exception caused by using the builder
 */
public class BuilderException extends Exception {
    /**
     * Construct for a specific message.
     *
     * @param message The explanation message
     */
    public BuilderException(String message) {
        super(message);
    }

    /**
     * Construct for an explanation and an underlying cause.
     *
     * @param message The explanation message
     * @param cause The underlying cause
     */
    public BuilderException(String message, Throwable cause) {
        super(message, cause);
    }
}
