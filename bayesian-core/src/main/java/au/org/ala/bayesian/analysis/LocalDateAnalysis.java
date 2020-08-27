package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Analysis;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.StoreException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Calendar;

/**
 * The default date analysis for a local date (no time part)
 */
public class LocalDateAnalysis extends Analysis<LocalDate> {
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
     * @throws StoreException if unable to convert to a string
     */
    @Override
    public String toString(LocalDate value) throws StoreException {
        return value == null ? null : DateTimeFormatter.ISO_LOCAL_DATE.format((LocalDate) value);
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
