package au.org.ala.names;

import au.org.ala.bayesian.StoreException;
import au.org.ala.bayesian.analysis.EnumAnalysis;
import au.org.ala.bayesian.analysis.TermAnalysis;
import au.org.ala.vocab.TaxonomicStatus;
import org.gbif.api.vocabulary.NomenclaturalCode;
import org.gbif.common.parsers.NomCodeParser;

/**
 * Analysis based on taxonomic status.
 */
public class TaxonomicStatusAnalysis extends TermAnalysis {
    /**
     * Default constructor
     */
    public TaxonomicStatusAnalysis() {
        TaxonomicStatus.values(); // Ensure class is loaded
    }
}
