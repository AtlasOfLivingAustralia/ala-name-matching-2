package au.org.ala.names;

import au.org.ala.bayesian.analysis.EnumAnalysis;
import au.org.ala.vocab.VernacularStatus;

/**
 * Analysis based on vernacular status.
 */
public class VernacularStatusAnalysis extends EnumAnalysis<VernacularStatus> {
    /**
     * Default constructor
     */
    public VernacularStatusAnalysis() {
        super(VernacularStatus.class);
    }
}
