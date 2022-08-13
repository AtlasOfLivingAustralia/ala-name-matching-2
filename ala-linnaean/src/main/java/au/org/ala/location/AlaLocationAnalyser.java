package au.org.ala.location;

import au.org.ala.bayesian.*;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class AlaLocationAnalyser implements Analyser<AlaLocationClassification> {
    @Override
    public void analyseForIndex(Classifier classifier) throws InferenceException, StoreException {

    }

    @Override
    public void analyseForSearch(AlaLocationClassification classification, MatchOptions options) throws InferenceException {
        if (classification.locality == null) {
            if (classification.islandGroup != null)
                classification.locality = classification.islandGroup;
            else if (classification.stateProvince != null)
                classification.locality = classification.stateProvince;
            else if (classification.country != null)
                classification.locality = classification.country;
            else if (classification.continent != null)
                classification.locality = classification.continent;
            else if (classification.stateProvince != null)
                classification.waterBody = classification.waterBody;
        }
    }

    @Override
    public Set<String> analyseNames(Classifier classifier, Observable<String> name, Optional<Observable<String>> complete, Optional<Observable<String>> additional, boolean canonical) throws InferenceException {
        Set<String> names = new LinkedHashSet<>();
        names.addAll(classifier.getAll(name));
        complete.ifPresent(o -> names.addAll(classifier.getAll(o)));
        additional.ifPresent(o -> names.addAll(classifier.getAll(o)));
        return names;
    }

    @Override
    public boolean acceptSynonym(Classifier base, Classifier candidate) {
        return false;
    }
}
