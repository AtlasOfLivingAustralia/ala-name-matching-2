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
    private static final Map<String, String> KINGDOM_MAP = Collections.unmodifiableMap(buildInitialKingdomMap());
    private static final Set<String> KINGDOM_CLASS = Collections.unmodifiableSet(buildInitialClassMap(KINGDOM_MAP));

    // Initialise the classes of kingdom
    private static Set<String> buildInitialClassMap(Map<String, String> kingdomMap) {
        final Map<String, Set<String>> inverted = new HashMap<>();
        final Set<String> classes = new HashSet<>();
        kingdomMap.entrySet().forEach(e -> inverted.computeIfAbsent(e.getValue(), n -> new HashSet<>()).add(e.getKey()));
        try {
            CSVReader reader = CSVReaderFactory.build(KingdomAnalysis.class.getResourceAsStream("kingdoms.csv"), "UTF-8", ",", '"', 1);
            while (reader.hasNext()) {
                String row[] = reader.next();
                if (row.length < 3)
                    continue;
                if (row[0].startsWith("#"))
                    continue;
                for (String value : inverted.getOrDefault(row[0], Collections.emptySet())) {
                    for (String match : row[2].split("\\s*\\|\\s*")) {
                        for (String match2 : inverted.getOrDefault(match, Collections.emptySet())) {
                            classes.add(value + "|" + match2);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            logger.error("Unable to read classes", ex);
        }
        logger.info("Initialised kingdom classes with " + classes.size() + " entries");
        return classes;
    }


    // Initialise the kingdom map
    private static Map<String, String> buildInitialKingdomMap() {
        Map<String, String> kingdomMap = new HashMap<>();
        try {
            CSVReader reader = CSVReaderFactory.build(KingdomAnalysis.class.getResourceAsStream("kingdoms.csv"), "UTF-8", ",", '"', 1);
            while (reader.hasNext()) {
                String row[] = reader.next();
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
        if (value1.equalsIgnoreCase(value2))
            return true;
        value1 = value1.toUpperCase();
        value2 = value2.toUpperCase();
        String pattern = value1 + "|" + value2;
        return KINGDOM_CLASS.contains(pattern);
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
