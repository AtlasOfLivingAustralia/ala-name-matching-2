package au.org.ala.names;

import au.org.ala.bayesian.Fidelity;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.analysis.StringAnalysis;
import au.org.ala.bayesian.fidelity.SimpleFidelity;
import org.gbif.utils.file.csv.CSVReader;
import org.gbif.utils.file.csv.CSVReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Analysis for kingdoms.
 * <p>
 * Normalised kingdom names
 * </p>
 */
public class KingdomAnalysis extends StringAnalysis {
    private static final Logger logger = LoggerFactory.getLogger(KingdomAnalysis.class);
    protected static final Map<String, String> KINGDOM_MAP = Collections.unmodifiableMap(buildInitialKingdomMap());
    protected static final Map<String, List<String>> KINGDOM_CLASS = Collections.unmodifiableMap(buildInitialClassMap());



    // Initialise the kingdom map
    private static Map<String, String> buildInitialKingdomMap() {
        Map<String, String> kingdomMap = new HashMap<>();
        try {
            CSVReader reader = CSVReaderFactory.build(KingdomAnalysis.class.getResourceAsStream("kingdoms.csv"), "UTF-8", ",", '"', 1);
            while (reader.hasNext()) {
                String[] row = reader.next();
                if (row.length < 3)
                    continue;
                if (row[0].startsWith("#"))
                    continue;
                kingdomMap.put(row[0].toUpperCase(), row[0]);
                for (int i = 3; i < row.length; i++)
                    kingdomMap.put(row[i].toUpperCase(), row[0]);
             }
        } catch (IOException ex) {
            logger.error("Unable to read kingdom map", ex);
        }
        logger.info("Initialised kingdom map with " + kingdomMap.size() + " entries");
        return kingdomMap;
    }

    // Initialise the classes of kingdom
    private static Map<String, List<String>>  buildInitialClassMap() {
        final Map<String, List<String>> classes = new HashMap<>();
        try {
            CSVReader reader = CSVReaderFactory.build(KingdomAnalysis.class.getResourceAsStream("kingdoms.csv"), "UTF-8", ",", '"', 1);
            while (reader.hasNext()) {
                String[] row = reader.next();
                if (row.length < 3)
                    continue;
                if (row[0].startsWith("#"))
                    continue;
                classes.put(row[0].toUpperCase(), Arrays.asList(row[2].split("\\s*\\|\\s*")));
             }
        } catch (IOException ex) {
            logger.error("Unable to read classes", ex);
        }
        logger.info("Initialised kingdom classes with " + classes.size() + " entries");
        return classes;
    }

    /**
     * Analyse this object, providing any special interpretation
     * required.
     * <p>
     * Convert values into standardised kingdom names
     * </p>
     *
     * @param value The value to analyse
     * @return The analysed value.
     * @throws InferenceException if unable to analyse the value
     */
    @Override
    public String analyse(String value) throws InferenceException {
        value = super.analyse(value);
        if (value == null)
            return null;
        return KINGDOM_MAP.getOrDefault(value.toUpperCase(), value);
    }

    /**
     * Compute a fidelity measure for this type of object.
     * <p>
     * If status is within the same class, then fidelity is at leaset 0.5
     * </p>
     *
     * @param original The original value
     * @param actual   The actual value
     * @return The computed fidelity
     */
    @Override
    public Fidelity<String> buildFidelity(String original, String actual) throws InferenceException {
        if (original == null)
            return null;
        double fidelity = 0.0;
        if (actual != null) {
            if (original.equalsIgnoreCase(actual))
                fidelity = 1.0;
            else {
                Boolean equivalent = this.equivalent(original, actual);
                if (equivalent != null && equivalent.booleanValue())
                    fidelity = 0.5;
            }
        }
        return new SimpleFidelity<>(original, actual, fidelity);
    }

    /**
     * Test to see if this is a valid kingdom.
     *
     * @param kingdom The kingdom name
     *
     * @return True if this is a recognised kingdom. False otherwise.
     */
    public static boolean testKingdom(String kingdom) {
        return KINGDOM_MAP.containsKey(kingdom.toUpperCase());
    }
}
