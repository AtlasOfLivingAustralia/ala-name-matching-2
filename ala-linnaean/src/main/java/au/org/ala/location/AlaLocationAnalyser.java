package au.org.ala.location;

import au.org.ala.bayesian.*;
import au.org.ala.util.BasicNormaliser;
import au.org.ala.vocab.GeographyType;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AlaLocationAnalyser implements Analyser<AlaLocationClassification> {
    /** Unaccented version of the name */
    private static final BasicNormaliser CLEAN_NORMALISER = new BasicNormaliser("clean", true, true, false, false, true, false);

    @Override
    public void analyseForIndex(Classifier classifier) throws InferenceException, StoreException {
    }

    @Override
    public void analyseForSearch(AlaLocationClassification classification, MatchOptions options) throws InferenceException {
        classification.island = this.removeNA(classification.island);
        classification.islandGroup = this.removeNA(classification.islandGroup);
        classification.stateProvince = this.removeNA(classification.stateProvince);
        classification.country = this.removeNA(classification.country);
        classification.countryCode = this.removeNA(classification.countryCode);
        classification.continent = this.removeNA(classification.continent);
        classification.waterBody = this.removeNA(classification.waterBody);
        GeographyType geographyType = null;
        String locality = null;
        if (classification.island != null) {
            locality = classification.island;
            geographyType = GeographyType.island;
         } else if (classification.islandGroup != null) {
            locality = classification.islandGroup;
            geographyType = GeographyType.islandGroup;
        } else if (classification.stateProvince != null) {
            locality = classification.stateProvince;
            geographyType = GeographyType.stateProvince;
        } else if (classification.country != null) {
            locality = classification.country;
            geographyType = GeographyType.country;
          } else if (classification.continent != null) {
            locality = classification.continent;
            geographyType = GeographyType.continent;
        } else if (classification.waterBody != null) {
            locality = classification.waterBody;
            geographyType = GeographyType.waterBody;
        }
        if (classification.locality == null) {
            classification.locality = locality;
            classification.geographyType = geographyType;
        }
    }

    /**
     * Remove "not applicable" type values from a name
     *
     * @param locality The locality
     *
     * @return The locality with NA turned into null
     */
    protected String removeNA(String locality) {
        if (locality == null)
            return null;
        locality = AlaLocationFactory.localityNormaliser.normalise(locality);
        if (locality.isEmpty())
            return null;
        if (locality.equalsIgnoreCase("unknown"))
            return null;
        if (locality.equalsIgnoreCase("unknown or invalid"))
            return null;
        if (locality.equalsIgnoreCase("invalid"))
            return null;
        if (locality.equalsIgnoreCase("not applicable"))
            return null;
        if (locality.equalsIgnoreCase("not recorded"))
            return null;
        if (locality.equalsIgnoreCase("na"))
            return null;
        if (locality.equalsIgnoreCase("n/a"))
            return null;
        if (locality.equalsIgnoreCase("zz"))
            return null;
        if (locality.equalsIgnoreCase("null"))
            return null;
        return locality;
   }

    @Override
    public Set<String> analyseNames(Classifier classifier, Observable<String> name, Optional<Observable<String>> complete, Optional<Observable<String>> disambiguator, boolean canonical) throws InferenceException {
        final Set<String> names = new LinkedHashSet<>();
        this.addNames(names, classifier.getAll(name));
        complete.ifPresent(o -> this.addNames(names, classifier.getAll(o)));
        disambiguator.ifPresent(o -> this.addNames(names, classifier.getAll(o)));
        return names;
    }

    /**
     * Add all variants of names
     *
     * @param names The collected names
     * @param add The base names to add
     */
    protected void addNames(Set<String> names, Set<String> add) {
        names.addAll(add);
        names.addAll(add.stream().map(n -> CLEAN_NORMALISER.normalise(n)).collect(Collectors.toList()));
    }

    @Override
    public boolean acceptSynonym(Classifier base, Classifier candidate) {
        return false;
    }
}
