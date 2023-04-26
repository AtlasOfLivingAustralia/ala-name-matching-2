package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Analysis;
import au.org.ala.bayesian.Fidelity;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.StoreException;
import au.org.ala.bayesian.fidelity.SimpleFidelity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

/**
 * The default date analysis for a local date (no time part)
 */
public class LocalDateAnalysis extends Analysis<LocalDate, String, String> {
    /**
     * Get the class of object that this analyser handles.
     *
     * @return The local date class
     */
    @Override
    public Class<LocalDate> getType() {
        return LocalDate.class;
    }

    /**
     * Get the class of object that this analyser stores.
     *
     * @return The string class
     */
    @Override
    public Class<String> getStoreType() {
        return String.class;
    }

    /**
     * Analyse this object, providing any special interpretation
     * required.
     * <p>
     * Returns the value as-is.
     * </p>
     *
     * @param value The value to analyse
     * @return The analysed value.
     * @throws InferenceException if unable to analyse the value
     */
    @Override
    public LocalDate analyse(LocalDate value) throws InferenceException {
        return value;
    }

    /**
     * Convert this object into a string for storage
     *
     * @param value The value to convert
     * @return The stringified value (null should return null)
     */
    @Override
    public String toStore(LocalDate value) {
        return value == null ? null : DateTimeFormatter.ISO_LOCAL_DATE.format(value);
    }


    /**
     * Convert this object into a string for storage
     *
     * @param value The value to convert
     * @return The stringified value (null should return null)
     */
    @Override
    public String toQuery(LocalDate value) {
        return this.toStore(value);
    }

    /**
     * Parse this value and return a suitably interpreted object.
     * <p>
     * Ideally, this is first ISO date format.
     * Otherwise, parse based on locale.
     * </p>
     *
     * @param value The value
     * @return The parsed value
     * @throws StoreException if unable to interpret the string
     */
    @Override
    public LocalDate fromStore(String value) throws StoreException {
        return this.fromString(value);
    }
    /**
     * Compute a fidelity measure for this type of object.
     *
     * @param original The original value
     * @param actual   The actual value
     * @return The computed fidelity
     */
    @Override
    public Fidelity<LocalDate> buildFidelity(LocalDate original, LocalDate actual) throws InferenceException {
        return original == null ? null : new SimpleFidelity<>(
                original,
                actual,
                actual == null ?
                        0.0 :
                        1.0 - Math.min(1.0, Math.abs(original.until(actual).getDays()) / this.getFidelityScale())
        );
    }

    /**
     * How far away an actual value can be from an original value before zero
     * fidelity is reported.
     * <p>
     * This value defaults to 30.0 (a month) but can be overridden by subclasses.
     * </p>
     *
     * @return The fi
     */
    @JsonIgnore
    public double getFidelityScale() {
        return 30.0;
    }

    /**
     * Parse this value and return a suitably interpreted object.
     * <p>
     * Ideally, this is first ISO date format.
     * Otherwise, parse based on locale.
     * </p>
     *
     * @param value The value
     * @return The parsed value
     * @throws StoreException if unable to interpret the string
     */
    @Override
    public LocalDate fromString(String value) throws StoreException {
        if (value == null || value.isEmpty())
            return null;
        try {
            DateTimeFormatter.ISO_LOCAL_DATE.parse(value);
        } catch (DateTimeParseException ex) {
        }
        try {
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).parse(value);
        } catch (DateTimeParseException ex) {
        }
        try {
            DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).parse(value);
        } catch (DateTimeParseException ex) {
        }
        throw new StoreException("Unbale to parse as date " + value);
    }


}
