package au.org.ala.names;

import au.org.ala.bayesian.analysis.EnumAnalysis;
import org.gbif.api.vocabulary.NomenclaturalCode;
import org.gbif.common.parsers.NomCodeParser;
import org.gbif.utils.file.csv.CSVReader;
import org.gbif.utils.file.csv.CSVReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Analysis based on nomenclatural code.
 */
public class NomenclaturalCodeAnalysis extends EnumAnalysis<NomenclaturalCode> {
    private static final Logger logger = LoggerFactory.getLogger(NomenclaturalCodeAnalysis.class);
    private static final Map<String, NomenclaturalCode> KINGDOM_CODE = Collections.synchronizedMap(buildInitialCodeMap());

    // Initialise the nomenclatural codes of kingdom
    private static Map<String, NomenclaturalCode> buildInitialCodeMap() {
        Map<String, NomenclaturalCode> codeMap = new HashMap<>();
        NomCodeParser parser = NomCodeParser.getInstance();
        try {
            CSVReader reader = CSVReaderFactory.build(KingdomAnalysis.class.getResourceAsStream("kingdoms.csv"), "UTF-8", ",", '"', 1);
            while (reader.hasNext()) {
                String row[] = reader.next();
                if (row.length < 3)
                    continue;
                if (row[0].startsWith("#"))
                    continue;
                NomenclaturalCode code = parser.parse(row[1]).getPayload();
                codeMap.put(row[0].toUpperCase(), code);
                for (int i = 3; i < row.length; i++) {
                    codeMap.put(row[i].toUpperCase(), code);
                }
            }
        } catch (IOException ex) {
            logger.error("Unable to read kingdom map", ex);
        }
        logger.info("Initialised kingdom dictionary with " + codeMap.size() + " entries");
        return codeMap;
    }

    private NomCodeParser parser = NomCodeParser.getInstance();

    public NomenclaturalCodeAnalysis() {
        super(NomenclaturalCode.class);
    }

    /**
     * Convert this object into a string for storage
     * <p>
     * This uses the nomenclatural code acronym.
     * </p>
     *
     * @param value The value to convert
     * @return The stringified value (null should return null)
     */
    @Override
    public String toStore(NomenclaturalCode value) {
        return value== null ? null : value.getAcronym();
    }

    /**
     * Parse this value and return a suitably interpreted object.
     *
     * @param value The value
     * @return The parsed value
     */
    @Override
    public NomenclaturalCode fromString(String value) {
        if (value == null)
            return null;
        value = value.trim();
        if (value.isEmpty())
            return null;
        if (value.equals("ICN")) // Grr
            return NomenclaturalCode.BOTANICAL;
        NomenclaturalCode code = super.fromString(value);
        if (code == null)
            code = this.parser.parse(value).getPayload();
        return code;
    }

    /**
     * Estimate the nomenclatural code from a kingdom name.
     *
     * @param kingdom The kingdom
     * @return The matching code, or null for not found
     */
    public NomenclaturalCode estimateFromKingdom(String kingdom) {
        if (kingdom == null)
            return null;
        kingdom = kingdom.trim().toUpperCase();
        return KINGDOM_CODE.get(kingdom);
    }

}
