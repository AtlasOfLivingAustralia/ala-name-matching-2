package au.org.ala.names;

import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.analysis.StringAnalysis;
import au.org.ala.vocab.TaxonomicStatus;
import org.gbif.utils.file.csv.CSVReader;
import org.gbif.utils.file.csv.CSVReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Analysis for kingdoms.
 * <p>
 * Some kingdoms move about and need to be treated as equivalent when matching.
 * This also handles the soundex equivalents for kingdoms.
 * </p>
 */
public class KingdomAnalysis extends StringAnalysis {
    private static final Logger logger = LoggerFactory.getLogger(KingdomAnalysis.class);
    private static final Map<String, String> KINGDOM_CLASS = Collections.synchronizedMap(buildInitialClassMap());

    // Initialise the classes of kingdom
    private static Map<String, String> buildInitialClassMap() {
        Map<String, String> classMap = new HashMap<>();
        try {
            CSVReader reader = CSVReaderFactory.build(KingdomAnalysis.class.getResourceAsStream("kingdoms.csv"), "UTF-8", ",", '"', 1);
            while (reader.hasNext()) {
                String row[] = reader.next();
                if (row.length < 3)
                    continue;
                if (row[0].startsWith("#"))
                    continue;
                classMap.put(row[0].toUpperCase(), row[2]);
                for (int i = 3; i < row.length; i++) {
                    classMap.put(row[i].toUpperCase(), row[2]);
                }
            }
        } catch (IOException ex) {
            logger.error("Unable to read kingdom map", ex);
        }
        logger.info("Initialised kingdom dictionary with " + classMap.size() + " entries");
        return classMap;
    }

    /**
     * Test for equivalence.
     * <p>
     * Kingdoms are tested to see if they are in equivalence classes.
     * </p>
     *
     * @param value1 The first value to test
     * @param value2 The second value to test
     * @return Null if not comparable, true if equivalent, false otherwise.
     * @throws InferenceException if unable to determine equivalence
     */
    @Override
    public Boolean equivalent(String value1, String value2) throws InferenceException {
        if (value1 == null || value2 == null)
            return null;
        value1 = KINGDOM_CLASS.getOrDefault(value1.toUpperCase(), value1);
        value2 = KINGDOM_CLASS.getOrDefault(value2.toUpperCase(), value2);
        return value1.equalsIgnoreCase(value2);
    }
}
