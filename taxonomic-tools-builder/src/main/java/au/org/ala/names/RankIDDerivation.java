package au.org.ala.names;

import au.org.ala.bayesian.Condition;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.derivation.CopyDerivation;

import java.util.stream.Collectors;

/**
 * Derive a rank identifier based on a taxon rank
 */
public class RankIDDerivation extends CopyDerivation {
    /**
     * Construct a new derivation.
     */
    public RankIDDerivation() {
    }

    /**
     * Construct a derivation from a source.
     *
     * @param condition Any condition
     * @param source The source
     */
    public RankIDDerivation(Condition condition, Observable source) {
        super(condition, source);
    }

    /**
     * Does this have a transform?
     *
     * @return True
     */
    @Override
    public boolean hasTransform() {
        return true;
    }

    @Override
    public String generateBuilderTransform(String var, String extra, String documentVar) {
        return "au.org.ala.names.RankIDAnalysis.idFromRank((org.gbif.nameparser.api.Rank) " + var + ")";
    }

    @Override
    public String generateClassificationTransform() {
        return this.getSources().stream().map(s -> "au.org.ala.names.RankIDAnalysis.idFromRank(this." + s.getJavaVariable() + ")").collect(Collectors.joining(" + "));
    }

}
