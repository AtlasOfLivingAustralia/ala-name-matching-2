package au.org.ala.names;

import au.org.ala.bayesian.*;
import au.org.ala.vocab.BayesianTerm;
import org.gbif.api.vocabulary.NomenclaturalCode;
import org.gbif.nameparser.api.NameType;
import org.gbif.nameparser.api.ParsedName;
import org.gbif.nameparser.api.Rank;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class AlaNameAnalyser extends ScientificNameAnalyser<AlaLinnaeanClassification> {
    private static final Issues INDETERMINATE_ISSUES = Issues.of(AlaLinnaeanFactory.INDETERMINATE_NAME);
    private static final Issues AFFINTIY_ISSUES = Issues.of(AlaLinnaeanFactory.AFFINITY_SPECIES_NAME);
    private static final Issues CONFER_ISSUES = Issues.of(AlaLinnaeanFactory.CONFER_SPECIES_NAME);
    private static final Issues UNPARSABLE_ISSUES = Issues.of(AlaLinnaeanFactory.UNPARSABLE_NAME);
    private static final Issues CANONICAL_ISSUES = Issues.of(AlaLinnaeanFactory.CANONICAL_NAME);

    /**
     * If a classification does not have a direct scientific name/rank combination then infer it.
     * Sub-species ranks are generalised to be a genric infraspecific rank, since there's too
     * much disagreement about what is actually valid.
     *
     * @param analysis The analysis state
     * @param classification The classification
     */
    protected void inferRank(Analysis analysis, AlaLinnaeanClassification classification) {
        if (classification == null)
            return;
        if (classification.scientificName == null) {
            if (classification.specificEpithet != null && classification.genus != null) {
                classification.scientificName = classification.genus + " " + classification.specificEpithet;
                analysis.estimateRank(Rank.SPECIES);
            } else if (classification.genus != null) {
                classification.scientificName = classification.genus;
                analysis.estimateRank(Rank.GENUS);
            } else if (classification.family != null) {
                classification.scientificName = classification.family;
                analysis.estimateRank(Rank.FAMILY);
            } else if (classification.order != null) {
                classification.scientificName = classification.order;
                analysis.estimateRank(Rank.ORDER);
            } else if (classification.class_ != null) {
                classification.scientificName = classification.class_;
                analysis.estimateRank(Rank.CLASS);
            } else if (classification.phylum != null) {
                classification.scientificName = classification.phylum;
                analysis.estimateRank(Rank.PHYLUM);
            } else if (classification.kingdom != null) {
                classification.scientificName = classification.kingdom;
                analysis.estimateRank(Rank.KINGDOM);
            }
        }
        if (!analysis.isUnranked() && (classification.taxonRank == null || classification.taxonRank.otherOrUnranked())) {
            classification.taxonRank = analysis.getRank();
        }
    }

    /**
     * Generate base names from a classifier.
     * <p>
     * If an explicit scientific name is not specified, then see if we can track down a higher level name
     * and deduce the rank.
     * </p>
     * 
     * @param analysis The analysis state
     * @param classifier The classifier
     * @param name The name observable
     *             
     * @return A set of base names
     */
    protected Set<String> generateBaseNames(Analysis analysis, Classifier classifier, Observable name) {
         Set<String> names = new LinkedHashSet<>(classifier.getAll(name));

        if (names.isEmpty() && classifier.has(AlaLinnaeanFactory.genus) && classifier.has(AlaLinnaeanFactory.specificEpithet)) {
            names = classifier.getAll(AlaLinnaeanFactory.genus).stream().flatMap(g -> classifier.getAll(AlaLinnaeanFactory.specificEpithet).stream().map(s -> g + " " + s)).collect(Collectors.toSet());
            analysis.estimateRank(Rank.SPECIES);
        }
        if (names.isEmpty() && classifier.has(AlaLinnaeanFactory.genus)) {
            names = classifier.getAll(AlaLinnaeanFactory.genus);
            analysis.estimateRank(Rank.GENUS);
        }
        if (names.isEmpty() && classifier.has(AlaLinnaeanFactory.family)) {
            names = classifier.getAll(AlaLinnaeanFactory.family);
            analysis.estimateRank(Rank.FAMILY);
        }
        if (names.isEmpty() && classifier.has(AlaLinnaeanFactory.order)) {
            names = classifier.getAll(AlaLinnaeanFactory.order);
            analysis.estimateRank(Rank.ORDER);
        }
        if (names.isEmpty() && classifier.has(AlaLinnaeanFactory.class_)) {
            names = classifier.getAll(AlaLinnaeanFactory.class_);
            analysis.estimateRank(Rank.CLASS);
        }
        if (names.isEmpty() && classifier.has(AlaLinnaeanFactory.phylum)) {
            names = classifier.getAll(AlaLinnaeanFactory.phylum);
            analysis.estimateRank(Rank.PHYLUM);
        }
        if (names.isEmpty() && classifier.has(AlaLinnaeanFactory.kingdom)) {
            names = classifier.getAll(AlaLinnaeanFactory.kingdom);
            analysis.estimateRank(Rank.KINGDOM);
        }
        return names;
    }

    protected void fillOutClassification(Analysis analysis, AlaLinnaeanClassification classification) throws InferenceException {

        if (classification == null || !analysis.hasFullyParsedName())
            return;
        ParsedName name = analysis.getParsedName();
            if (name.isPhraseName()) {
                if (classification.nominatingParty == null && name.getNominatingParty() != null) {
                    classification.nominatingParty = name.getNominatingParty();
                }
                if (classification.voucher == null && name.getVoucher() != null) {
                    classification.voucher = (String) AlaLinnaeanFactory.voucher.getAnalysis().analyse(name.getVoucher());
                }
                if (classification.phraseName == null && name.getStrain() != null) {
                    classification.phraseName = (String) AlaLinnaeanFactory.phraseName.getAnalysis().analyse(name.getStrain());
                }
            }
            if (name.getType() == NameType.SCIENTIFIC || name.getType() == NameType.INFORMAL || name.getType() == NameType.PLACEHOLDER) {
                // Only do this for non-synonyms to avoid dangling speciesID
                if (classification.acceptedNameUsageId == null && classification.specificEpithet == null && name.getSpecificEpithet() != null && !analysis.isUncertain())
                    classification.specificEpithet = name.getSpecificEpithet();

            }
            if (classification.acceptedNameUsageId == null && classification.genus == null && !analysis.isUnranked() && !analysis.getRank().higherThan(Rank.GENUS) && name.getGenus() != null)
                classification.genus = name.getGenus();
            if (classification.cultivarEpithet == null && name.getCultivarEpithet() != null)
                classification.cultivarEpithet = name.getCultivarEpithet();
    }

    /**
     * Analyse the information in a classifier and extend the classifier
     * as required for indexing.
     *
     * @param classification The classification
     */
    @Override
    public void analyseForIndex(AlaLinnaeanClassification classification) throws InferenceException {
        Analysis analysis = new Analysis(classification.scientificName, classification.scientificNameAuthorship, classification.taxonRank, classification.nomenclaturalCode);

        this.inferRank(analysis, classification);
        this.processIndeterminate(analysis, null, INDETERMINATE_ISSUES);
        this.processAffinitySpecies(analysis, " aff. ", AFFINTIY_ISSUES);
        this.processConferSpecies(analysis, " cf. ", CONFER_ISSUES);
        this.processSpeciesNovum(analysis, true, null);
        this.processPhraseName(analysis, null);
        this.parseName(analysis, UNPARSABLE_ISSUES);
        this.processParsedScientificName(analysis, CANONICAL_ISSUES);
        this.fillOutClassification(analysis, classification);
        classification.scientificName = analysis.getScientificName();
        classification.scientificNameAuthorship = analysis.getScientificNameAuthorship();
        classification.taxonRank = analysis.isUnranked() ? null: analysis.getRank();
        if (classification.cultivarEpithet != null && classification.specificEpithet != null) {
            // Check case where cultivar epithet is on specific epithet
            Matcher matcher = SUSPECTED_CULTIVAR_IN_SPECIFIC_EPITHET.matcher(classification.specificEpithet);
            if (matcher.find() && matcher.group(1).equals(classification.cultivarEpithet)) {
                classification.specificEpithet = classification.specificEpithet.substring(0, matcher.start()).trim();
                analysis.addIssues(CANONICAL_ISSUES);
            }
        }

        // Remove loops in taxonomy
        if (classification.parentNameUsageId != null && classification.parentNameUsageId.equals(classification.taxonId)) {
            classification.addIssue(BayesianTerm.parentLoop);
            classification.parentNameUsageId = null;
        }
        if (classification.acceptedNameUsageId != null && classification.acceptedNameUsageId.equals(classification.taxonId)) {
            classification.addIssue(BayesianTerm.acceptedLoop);
            classification.acceptedNameUsageId = null;
        }
        classification.addIssues(analysis.getIssues());
    }

    /**
     * Analyse the information in a classifier and extend the classifier
     * as required for searching.
     *
     * @param classification The classification
     * @throws StoreException if an error occurs updating the classification
     */
    @Override
    public void analyseForSearch(AlaLinnaeanClassification classification) throws InferenceException {
        Analysis analysis = new Analysis(classification.scientificName, classification.scientificNameAuthorship, classification.taxonRank, classification.nomenclaturalCode);

        this.inferRank(analysis, classification);
        if (analysis.getScientificName() == null)
            return;
        if (MARKER_ONLY.matcher(analysis.getScientificName()).matches())
            throw new InferenceException("Supplied scientific name is a rank marker.");
        this.processIndeterminate(analysis, " ", INDETERMINATE_ISSUES);
        this.processAffinitySpecies(analysis, " aff. ", AFFINTIY_ISSUES);
        this.processConferSpecies(analysis, " cf. ", CONFER_ISSUES);
        this.processSpeciesNovum(analysis, true, null);
        this.processPhraseName(analysis, null);
        this.processRankEnding(analysis, true, INDETERMINATE_ISSUES);
        this.processRankMarker(analysis, true, CANONICAL_ISSUES);
        this.parseName(analysis, UNPARSABLE_ISSUES);
        this.processParsedScientificName(analysis, CANONICAL_ISSUES);
        this.fillOutClassification(analysis, classification);
        classification.scientificName = analysis.getScientificName();
        classification.scientificNameAuthorship = analysis.getScientificNameAuthorship();
        classification.taxonRank = analysis.isUnranked() ? null: analysis.getRank();
        classification.addIssues(analysis.getIssues());
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
        Rank rank = classifier.get(AlaLinnaeanFactory.taxonRank);
        NomenclaturalCode code = classifier.get(AlaLinnaeanFactory.nomenclaturalCode);
        String scientificName = classifier.get(name);
        String scientificNameAuthorship = classifier.get(AlaLinnaeanFactory.scientificNameAuthorship);

        Analysis analysis = new Analysis(scientificName, scientificNameAuthorship, rank, code);

        Set<String> allScientificNameAuthorship = !canonical && additional.isPresent() ? classifier.getAll(additional.get()) : Collections.emptySet();
        Set<String> allCompleteNames = !canonical && complete.isPresent() ? classifier.getAll(complete.get()) : Collections.emptySet();
        Set<String> allScientificNames = this.generateBaseNames(analysis, classifier, name);
 
        if (allScientificNames.isEmpty())
            throw new InferenceException("No scientific name for " + classifier.get(AlaLinnaeanFactory.taxonId));
        for (String nm : allScientificNames) {
            analysis.addName(nm);
            analysis.addName(BASIC_NORMALISER.normalise(nm));
            analysis.addName(PUNCTUATION_NORMALISER.normalise(nm));
            nm = FULL_NORMALISER.normalise(nm);
            analysis.addName(nm);

            // From now on, only use the normalised version
            Analysis sub = analysis.with(nm);
            
            this.processIndeterminate(sub, " ", null);
            this.processAffinitySpecies(sub, " aff. ", null);
            this.processConferSpecies(sub, " cf. ", null);
            this.processSpeciesNovum(sub, true, null);
            this.processPhraseName(sub, null);
            this.parseName(sub, null);
            if (sub.getParsedName() != null) {
                this.processParsedScientificName(sub, null);
                this.processAdditionalScientificNames(sub);
                if (this.processRankMarker(sub, true, null)) {
                    this.parseName(sub, null);
                    this.processParsedScientificName(sub, null);
                    this.processAdditionalScientificNames(sub);
                }
            }
        }

        // Add name/author pairs
        if (allCompleteNames.isEmpty() && !allScientificNameAuthorship.isEmpty()) {
            allCompleteNames = allScientificNames.stream().flatMap(n -> allScientificNameAuthorship.stream().map(a -> n + " " + a)).collect(Collectors.toSet());
        }
        for (String nm : allCompleteNames) {
            analysis.addName(BASIC_NORMALISER.normalise(nm));
            analysis.addName(PUNCTUATION_NORMALISER.normalise(nm));
            analysis.addName(FULL_NORMALISER.normalise(nm));
        }
        return analysis.getNames();
    }
}
