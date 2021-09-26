package au.org.ala.names;

import au.org.ala.bayesian.analysis.EnumAnalysis;
import au.org.ala.vocab.TaxonomicStatus;
import au.org.ala.vocab.VernacularStatus;

import java.util.*;
import java.util.stream.Collectors;

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
