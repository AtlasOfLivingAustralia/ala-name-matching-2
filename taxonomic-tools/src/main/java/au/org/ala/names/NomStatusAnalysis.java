package au.org.ala.names;

import au.org.ala.bayesian.Analysis;
import au.org.ala.bayesian.Fidelity;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.StoreException;
import au.org.ala.bayesian.fidelity.SimpleFidelity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Analysis based on nomenclatural status.
 * <p>
 * This has some oddities that mean that aternative names in the {@link NomStatus} class
 * are not always parsed.
 * </p>
 */
public class NomStatusAnalysis extends Analysis<NomStatus, String, String, Object> {
    private static final Logger logger = LoggerFactory.getLogger(NomStatusAnalysis.class);

    /**
     * Default constructor
     */
    public NomStatusAnalysis() {
    }


    /**
     * Get the class of object that this analyser handles.
     *
     * @return The {@link NomStatus} class
     */
    @Override
    public Class<NomStatus> getType() {
        return NomStatus.class;
    }

    /**
     * Get the class of object that this analyser stores.
     *
     * @return The {@link String} class
     */
    @Override
    public Class<String> getStoreType() {
        return String.class;
    }

    /**
     * Analyse this object, providing any special interpretation
     * required.
     *
     * @param value The value to analyse
     * @return The value.
     * @throws InferenceException if unable to analyse the value
     */
    @Override
    public NomStatus analyse(NomStatus value) throws InferenceException {
        return value;
    }

    /**
     * Convert this object into a string for storage
     *
     * @param value The value to convert
     * @return The stringified value (null should return null)
      */
    @Override
    public String toStore(NomStatus value) {
        return value == null ? null : value.canonical();
    }

    /**
     * Convert this object into a value for query
     *
     * @param value The value to convert
     * @return The converted value (null should return null)
     * @throws StoreException if unable to convert to a query object
     */
    @Override
    public String toQuery(NomStatus value) throws StoreException {
        return value == null ? null : value.canonical();
    }

    /**
     * Parse a stored value and return a suitably interpreted object.
     *
     * @param value The value
     * @return The parsed value
     * @throws StoreException if unable to interpret the stored value
     */
    @Override
    public NomStatus fromStore(String value) throws StoreException {
        return this.fromString(value, null);
    }

    /**
     * Compute a fidelity measure for this type of object.
     *
     * @param original The original value
     * @param actual   The actual value
     * @return The computed fidelity
     */
    @Override
    public Fidelity<NomStatus> buildFidelity(NomStatus original, NomStatus actual) throws InferenceException {
        return original == null ? null : new SimpleFidelity<>(original, actual, original.equals(actual) ? 1.0 : 0.0);
    }

    /**
     * Parse this value and return a suitably interpreted object.
     *
     * @param value The value
     * @param context Unused context
     * @return The parsed value
     */
    @Override
    public NomStatus fromString(String value, Object context) {
        if (value == null || value.isEmpty())
            return null;
        NomStatus status = new NomStatus(value);
        return status.isEmpty() ? null : status;
    }
}
