package au.org.ala.names;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.*;
import au.org.ala.util.CleanedScientificName;
import au.org.ala.vocab.ALATerm;
import au.org.ala.vocab.VernacularStatus;
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
    /** The default set of weights to apply */
    private static final Map<VernacularStatus, Double> STATUS_WEIGHT_MAP;

    private static final Pattern DASH = Pattern.compile("(?<=\\w)\\s*-\\s*(?=\\w)");
    private static final Pattern POSSESSIVE = Pattern.compile("(?<=\\w)'(?=s?\\s)");

    static {
        STATUS_WEIGHT_MAP = new HashMap<>();
        STATUS_WEIGHT_MAP.put(VernacularStatus.legislated, 1000.0);
        STATUS_WEIGHT_MAP.put(VernacularStatus.standard, 100.0);
        STATUS_WEIGHT_MAP.put(VernacularStatus.preferred, 20.0);
        STATUS_WEIGHT_MAP.put(VernacularStatus.common, 10.0);
        STATUS_WEIGHT_MAP.put(VernacularStatus.traditionalKnowledge, 20.0);
        STATUS_WEIGHT_MAP.put(VernacularStatus.local, 5.0);
    }
    /**
     * Analyse the information in a classifier and extend the classifier
     * as required for indexing.
     *
     * @param classification The classification
     */
    @Override
    public void analyseForIndex(AlaVernacularClassification classification) {
        if (classification.weight == null) {
            double weight = STATUS_WEIGHT_MAP.getOrDefault(classification.vernacularStatus, 1.0);
            if (classification.taxonRank == Rank.SPECIES)
                weight *= 10;
            else if (classification.taxonRank == Rank.GENUS)
                weight *= 2;
            else if (classification.taxonRank != null && classification.taxonRank != Rank.FAMILY && classification.taxonRank != Rank.SUBSPECIES)
                weight /= 10.0;
            if (classification.taxonomicStatus != null && !classification.taxonomicStatus.isAcceptedFlag())
                weight /= 10.0;
            classification.weight = weight;
        }
        if (classification.acceptedNameUsageId != null)
            classification.taxonId = classification.acceptedNameUsageId;
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
     * Sort out variations on hyphenation and possessives.
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
            String nnm = n.getNormalised();
            Set<String> moreNames = new HashSet<>();
            moreNames.add(nnm);
            Matcher matcher = POSSESSIVE.matcher(nnm);
            if (matcher.find())
                moreNames.add(matcher.replaceAll(""));
            for (String mnm: moreNames) {
                names.add(mnm);
                matcher = DASH.matcher(mnm);
                if (matcher.find()) {
                    names.add(matcher.replaceAll(" "));
                    names.add(matcher.replaceAll(""));
                }
            }
        }
        return names;
    }
}
