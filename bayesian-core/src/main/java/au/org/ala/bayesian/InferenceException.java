package au.org.ala.bayesian;

/**
 * And exception thrown when attempting to make an inference about something.
 * <p>
 * This can be generated either at runtime or during network construction.
 * </p>
 */
public class InferenceException extends BayesianException {
    /**
     * Construct an empty inference exception
     */
    public InferenceException() {
        super();
    }

    /**
     * Constructr an inference exception with a message.
     *
     * @param message The message
     */
    public InferenceException(String message) {
        super(message);
    }

    /**
     * Construct an inference exception with a message and cause
     *
     * @param message The message
     * @param cause The underlying cause
     */
    public InferenceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct an inference exception with a cause
     *
     * @param cause The underlying cause
     */
    public InferenceException(Throwable cause) {
        super(cause);
    }
}
