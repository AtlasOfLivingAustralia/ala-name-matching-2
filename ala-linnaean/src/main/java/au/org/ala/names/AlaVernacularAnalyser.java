package au.org.ala.names;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.*;
import au.org.ala.util.CleanedScientificName;
import au.org.ala.vocab.ALATerm;
import com.google.common.base.Enums;
import lombok.Getter;
import lombok.SneakyThrows;
import org.gbif.api.vocabulary.NomenclaturalCode;
import org.gbif.dwc.terms.Term;
import org.gbif.nameparser.AuthorshipParsingJob;
import org.gbif.nameparser.NameParserGBIF;
import org.gbif.nameparser.api.*;
import org.gbif.nameparser.util.NameFormatter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AlaVernacularAnalyser implements Analyser<AlaVernacularClassification> {
    /**
     * Analyse the information in a classifier and extend the classifier
     * as required for indexing.
     *
     * @param classification The classification
     */
    @Override
    public void analyseForIndex(AlaVernacularClassification classification) {
    }

    /**
     * Analyse the information in a classifier and extend the classifier
     * as required for searching.
     *
     * @param classification The classification
      */
    @Override
    public void analyseForSearch(AlaVernacularClassification classification)  {
    }

    /**
     * Build a collection of base names for the classification.
     * <p>
     * If a classification can be referred to in multiple ways, this method
     * builds the various ways of referring to the classification.
     * </p>
     *
     * @param classifier The classification
     * @param name       The observable that gives the name
     * @param complete   The observable that gives the complete name
     * @param additional The observable that gives additional disambiguation, geneerally complete = name + ' ' + additional
     * @param canonical  Only include canonical names
     * @return All the names that refer to the classification
     */
    @Override
    public Set<String> analyseNames(Classifier classifier, Observable name, Optional<Observable> complete, Optional<Observable> additional, boolean canonical) throws InferenceException {
        Set<String> allNames = new HashSet<>(classifier.getAll(name));
        if (complete.isPresent())
            allNames.addAll(classifier.getAll(complete.get()));
        Set<String> names = new HashSet<>(allNames);
 
        if (allNames.isEmpty())
            throw new InferenceException("No name for " + classifier.get(AlaVernacularFactory.nameId));
        for (String nm : allNames) {
            CleanedScientificName n = new CleanedScientificName(nm);
            names.add(n.getName());
            names.add(n.getBasic());
            names.add(n.getNormalised());
        }
        return names;
    }
}
