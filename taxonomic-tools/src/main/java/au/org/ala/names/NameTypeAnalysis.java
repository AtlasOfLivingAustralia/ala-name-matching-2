package au.org.ala.names;

import au.org.ala.bayesian.analysis.EnumAnalysis;
import org.gbif.nameparser.api.NameType;

/**
 * Analysis based on parsed name type
 */
public class NameTypeAnalysis extends EnumAnalysis<NameType> {
    public NameTypeAnalysis() {
        super(NameType.class);
    }
}
