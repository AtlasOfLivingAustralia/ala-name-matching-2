package au.org.ala.names;

import au.org.ala.bayesian.StoreException;
import au.org.ala.bayesian.analysis.EnumAnalysis;
import org.gbif.api.vocabulary.NomenclaturalCode;
import org.gbif.common.parsers.NomCodeParser;
import org.gbif.nameparser.api.NomCode;
import org.gbif.nameparser.api.Rank;
import org.gbif.nameparser.util.RankUtils;

/**
 * Analysis based on nomenclatureal code.
 */
public class NomenclaturalCodeAnalysis extends EnumAnalysis<NomenclaturalCode> {
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
     * @throws StoreException if unable to convert to a string
     */
    @Override
    public String toString(NomenclaturalCode value) throws StoreException {
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
        if (value == null || value.isEmpty())
            return null;
        NomenclaturalCode code = super.fromString(value);
        if (code == null)
            code = this.parser.parse(value).getPayload();
        return code;
    }
}
