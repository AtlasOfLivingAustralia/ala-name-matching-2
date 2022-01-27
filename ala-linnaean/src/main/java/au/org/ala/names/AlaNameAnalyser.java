package au.org.ala.names;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.*;
import au.org.ala.vocab.BayesianTerm;
import com.google.common.base.Enums;
import org.gbif.api.vocabulary.NomenclaturalCode;
import org.gbif.nameparser.api.NameType;
import org.gbif.nameparser.api.ParsedName;
import org.gbif.nameparser.api.Rank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AlaNameAnalyser extends ScientificNameAnalyser<AlaLinnaeanClassification> {
    private Logger logger = LoggerFactory.getLogger(AlaNameAnalyser.class);

    /** Invalid authorship */
    public static final Pattern INVALID_AUTHORSHIP = Pattern.compile("\\d+");
    /** The distance in ranks allowable when looking at synonyms */
    public static final int SYNONYM_RANK_DISTANCE = 2000;

    private static final Issues INDETERMINATE_ISSUES = Issues.of(AlaLinnaeanFactory.INDETERMINATE_NAME);
    private static final Issues AFFINTIY_ISSUES = Issues.of(AlaLinnaeanFactory.AFFINITY_SPECIES_NAME);
    private static final Issues CONFER_ISSUES = Issues.of(AlaLinnaeanFactory.CONFER_SPECIES_NAME);
    private static final Issues UNPARSABLE_ISSUES = Issues.of(AlaLinnaeanFactory.UNPARSABLE_NAME);
    private static final Issues CANONICAL_ISSUES = Issues.of(AlaLinnaeanFactory.CANONICAL_NAME);
    private static final Issues DATA_ISSUES = Issues.of(BayesianTerm.illformedData);
    private static final Issues KINGDOM_ISSUES = Issues.of(AlaLinnaeanFactory.INVALID_KINGDOM, AlaLinnaeanFactory.REMOVED_KINGDOM);

    /**
     * A numeric or single letter placeholder name. We can extract the genus from this if we haven't already
     */
    public static final Pattern NUMERIC_PLACEHOLDER = Pattern.compile("^([A-Z][a-z]+)\\s(?:(?:" + RANK_MARKERS + ")\\.?\\s?)?(?:[A-Z]|\\d+)$");

    /**
     * Remove any invalid entries from the classification before processing it.
     *
     * @param analysis The current analysis
     * @param classification The classification
     */
    protected void removeInvalid(Analysis analysis, AlaLinnaeanClassification classification) {
        classification.kingdom = this.checkInvalid(classification.kingdom, analysis, DATA_ISSUES);
        classification.phylum = this.checkInvalid(classification.phylum, analysis, DATA_ISSUES);
        classification.class_ = this.checkInvalid(classification.class_, analysis, DATA_ISSUES);
        classification.order = this.checkInvalid(classification.order, analysis, DATA_ISSUES);
        classification.family = this.checkInvalid(classification.family, analysis, DATA_ISSUES);
        classification.genus = this.checkInvalid(classification.genus, analysis, DATA_ISSUES);
        classification.specificEpithet = this.checkInvalid(classification.specificEpithet, analysis, DATA_ISSUES);
        classification.scientificName = this.checkInvalid(classification.scientificName, analysis, DATA_ISSUES);
        analysis.setScientificName(this.checkInvalid(analysis.getScientificName(), analysis, DATA_ISSUES));
    }
    /**
     * This is a really, really annoyting thiung where we split apart names of
     * the form <em>fam. Gracilariaceae gen. Gracilaria</em> I mean,
     * come on people!
     *
     * @param analysis Thw analyser
     * @param classification The classifiction to fill out
     *
     * @return True if detected
     */
    protected boolean inferClassification(Analysis analysis, AlaLinnaeanClassification classification) {
        Map<Rank, String> map = this.detectAlternatingRankName(analysis, CANONICAL_ISSUES);
        if (map == null)
            return false;
        classification.scientificName = null;
        analysis.setScientificName(null);
        if (classification.kingdom == null) {
            classification.kingdom = map.get(Rank.KINGDOM);
        }
        if (classification.phylum == null) {
            classification.phylum = map.get(Rank.PHYLUM);
        }
        if (classification.class_ == null) {
            classification.class_ = map.get(Rank.CLASS);
        }
        if (classification.order == null) {
            classification.order = map.get(Rank.ORDER);
        }
        if (classification.family == null) {
            classification.family = map.get(Rank.FAMILY);
        }
        if (classification.genus == null) {
            classification.genus = map.get(Rank.GENUS);
        }
        if (classification.specificEpithet == null) {
            classification.specificEpithet = map.get(Rank.SPECIES);
        }
        return true;
    }

    /**
     * If a classification does not have a direct scientific name/rank combination then infer it.
     * Sub-species ranks are generalised to be a genric infraspecific rank, since there's too
     * much disagreement about what is actually valid.
     *
     * @param analysis       The analysis state
     * @param classification The classification
     */
    protected void inferRank(Analysis analysis, AlaLinnaeanClassification classification) {
        if (classification == null)
            return;
        if (classification.scientificName == null) {
            if (classification.specificEpithet != null && classification.genus != null) {
                classification.scientificName = classification.genus + " " + classification.specificEpithet;
                analysis.setScientificName(classification.scientificName);
                analysis.estimateRank(Rank.SPECIES);
            } else if (classification.genus != null) {
                classification.scientificName = classification.genus;
                analysis.setScientificName(classification.scientificName);
                analysis.estimateRank(Rank.GENUS);
            } else if (classification.family != null) {
                classification.scientificName = classification.family;
                analysis.setScientificName(classification.scientificName);
                analysis.estimateRank(Rank.FAMILY);
            } else if (classification.order != null) {
                classification.scientificName = classification.order;
                analysis.setScientificName(classification.scientificName);
                analysis.estimateRank(Rank.ORDER);
            } else if (classification.class_ != null) {
                classification.scientificName = classification.class_;
                analysis.setScientificName(classification.scientificName);
                analysis.estimateRank(Rank.CLASS);
            } else if (classification.phylum != null) {
                classification.scientificName = classification.phylum;
                analysis.setScientificName(classification.scientificName);
                analysis.estimateRank(Rank.PHYLUM);
            } else if (classification.kingdom != null) {
                classification.scientificName = classification.kingdom;
                analysis.setScientificName(classification.scientificName);
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
     * @param analysis   The analysis state
     * @param classifier The classifier
     * @param name       The name observable
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
        ParsedName name = analysis.getParsedName();
        if (classification == null || name == null)
            return;
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


    protected void fillOutClassifier(Analysis analysis, Classifier classifier) throws InferenceException, StoreException {
        ParsedName name = analysis.getParsedName();
        if (classifier == null || name == null)
            return;
        if (name.isPhraseName()) {
            if (!classifier.has(AlaLinnaeanFactory.nominatingParty) && name.getNominatingParty() != null) {
                classifier.add(AlaLinnaeanFactory.nominatingParty, name.getNominatingParty(), false, true);
            }
            if (!classifier.has(AlaLinnaeanFactory.voucher) && name.getVoucher() != null) {
                classifier.add(AlaLinnaeanFactory.voucher, AlaLinnaeanFactory.voucher.getAnalysis().analyse(name.getVoucher()), false, true);
            }
            if (!classifier.has(AlaLinnaeanFactory.phraseName) && name.getStrain() != null) {
                classifier.add(AlaLinnaeanFactory.phraseName, AlaLinnaeanFactory.phraseName.getAnalysis().analyse(name.getStrain()), false, true);
            }
        }
        if (name.getType() == NameType.SCIENTIFIC || name.getType() == NameType.INFORMAL || name.getType() == NameType.PLACEHOLDER) {
            // Only do this for non-synonyms to avoid dangling speciesID
            if (!classifier.has(AlaLinnaeanFactory.acceptedNameUsageId) && !classifier.has(AlaLinnaeanFactory.specificEpithet)  && name.getSpecificEpithet() != null && !analysis.isUncertain()) {
                classifier.add(AlaLinnaeanFactory.specificEpithet, name.getSpecificEpithet(), false, true);
            }
        }
        if (!classifier.has(AlaLinnaeanFactory.acceptedNameUsageId) && !classifier.has(AlaLinnaeanFactory.genus)  && !analysis.isUnranked() && !analysis.getRank().higherThan(Rank.GENUS) && name.getGenus() != null) {
            classifier.add(AlaLinnaeanFactory.genus, name.getGenus(), false, true);
        }
        if (!classifier.has(AlaLinnaeanFactory.cultivarEpithet) && name.getCultivarEpithet() != null) {
            classifier.add(AlaLinnaeanFactory.cultivarEpithet, name.getCultivarEpithet(), false, true);
        }
    }

    /**
     * Look for a scientific name of the form "Genus A" or "Genus 453"
     *
     * @param analysis The analysis
     * @param classification The classification
     */
    protected void detectNumericPlaceholder(Analysis analysis, AlaLinnaeanClassification classification) {
        Matcher numericPlaceholder = NUMERIC_PLACEHOLDER.matcher(analysis.getScientificName());
        if (numericPlaceholder.matches()) {
            if (classification.genus == null)
                classification.genus = numericPlaceholder.group(1);
            analysis.setNameType(NameType.PLACEHOLDER);
        }
    }

    /**
     * Look for a scientific name of the form "Genus A" or "Genus 453"
     *
     * @param analysis The analysis
     * @param classifier The classification
     */
    protected void detectNumericPlaceholder(Analysis analysis, Classifier classifier) throws StoreException {
        Matcher numericPlaceholder = NUMERIC_PLACEHOLDER.matcher(analysis.getScientificName());
        if (numericPlaceholder.matches()) {
            if (!classifier.has(AlaLinnaeanFactory.genus))
                classifier.add(AlaLinnaeanFactory.genus, numericPlaceholder.group(1), false, true);
            analysis.setNameType(NameType.PLACEHOLDER);
        }
    }

    /**
     * Analyse the information in a classifier and extend the classifier
     * as required for indexing.
     *
     * @param classifier The classifier
     */
    @Override
    public void analyseForIndex(Classifier classifier) throws InferenceException, StoreException {
        String id = classifier.get(AlaLinnaeanFactory.taxonId);
        Analysis analysis = new Analysis(
                classifier.get(AlaLinnaeanFactory.scientificName),
                classifier.get(AlaLinnaeanFactory.scientificNameAuthorship),
                classifier.get(AlaLinnaeanFactory.taxonRank),
                classifier.get(AlaLinnaeanFactory.nomenclaturalCode)
                );
        analysis.setKingdom(classifier.get(AlaLinnaeanFactory.kingdom));
        if (!this.checkKingdom(analysis, KINGDOM_ISSUES))
            logger.warn("Unrecognised kingdom " + analysis.getKingdom() + " on " + id);
        if (this.replaceUnprintable(analysis, 'x', DATA_ISSUES))
            throw new InferenceException("Replacement character in name while indexing for " + id + ": " + classifier.get(AlaLinnaeanFactory.scientificName) + " " + classifier.get(AlaLinnaeanFactory.scientificNameAuthorship));
        this.processIndeterminate(analysis, null, INDETERMINATE_ISSUES);
        this.processAffinitySpecies(analysis, " aff. ", AFFINTIY_ISSUES);
        this.processConferSpecies(analysis, " cf. ", CONFER_ISSUES);
        this.processSpeciesNovum(analysis, true, null);
        this.processPhraseName(analysis, null);
        this.processEmbeddedAuthor(analysis, CANONICAL_ISSUES);
        this.parseName(analysis, UNPARSABLE_ISSUES);
        this.processParsedScientificName(analysis, CANONICAL_ISSUES);
        this.fillOutClassifier(analysis, classifier);
        this.detectNumericPlaceholder(analysis, classifier);
        classifier.add(AlaLinnaeanFactory.scientificName, analysis.getScientificName(), false, true);
        classifier.add(AlaLinnaeanFactory.scientificNameAuthorship, analysis.getScientificNameAuthorship(), false, true);
        classifier.add(AlaLinnaeanFactory.taxonRank, analysis.isUnranked() ? null : analysis.getRank(), false, true);
        classifier.add(AlaLinnaeanFactory.nameType, analysis.getNameType(), false, true);
        classifier.add(AlaLinnaeanFactory.kingdom, analysis.getKingdom(), false, true);
        if (classifier.has(AlaLinnaeanFactory.cultivarEpithet) && classifier.has(AlaLinnaeanFactory.specificEpithet)) {
            // Check case where cultivar epithet is on specific epithet
            String cultivarEpithet = classifier.get(AlaLinnaeanFactory.cultivarEpithet);
            String specificEpithet = classifier.get(AlaLinnaeanFactory.specificEpithet);
            Matcher matcher = SUSPECTED_CULTIVAR_IN_SPECIFIC_EPITHET.matcher(specificEpithet);
            if (matcher.find() && matcher.group(1).equals(cultivarEpithet)) {
                classifier.add(AlaLinnaeanFactory.specificEpithet, specificEpithet.substring(0, matcher.start()).trim(), false, true);
                analysis.addIssues(CANONICAL_ISSUES);
            }
        }

        // Remove loops in taxonomy
        if (classifier.has(AlaLinnaeanFactory.parentNameUsageId) && classifier.get(AlaLinnaeanFactory.parentNameUsageId).equals(id)) {
            classifier.clear(AlaLinnaeanFactory.parentNameUsageId);
        }
        if (classifier.has(AlaLinnaeanFactory.acceptedNameUsageId) && classifier.get(AlaLinnaeanFactory.acceptedNameUsageId).equals(id)) {
            classifier.clear(AlaLinnaeanFactory.acceptedNameUsageId);
        }
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

        this.removeInvalid(analysis, classification);
        analysis.setKingdom(classification.kingdom);
        this.checkKingdom(analysis, KINGDOM_ISSUES);
        this.inferClassification(analysis, classification);
        this.inferRank(analysis, classification);
        if (this.processCommentary(analysis, CANONICAL_ISSUES)) {
            if (analysis.getScientificName() == null || analysis.getScientificName().isEmpty()) {
                analysis.setScientificName(null);
                this.inferRank(analysis, classification);
            }
        }
        if (analysis.getScientificName() == null)
            return;
        if (MARKER_ONLY.matcher(analysis.getScientificName()).matches())
            throw new InferenceException("Supplied scientific name is a rank marker.");
        this.removeSurroundingQuotes(analysis, CANONICAL_ISSUES);
        this.replaceUnprintable(analysis, 'x', DATA_ISSUES);
        this.processIndeterminate(analysis, " ", INDETERMINATE_ISSUES);
        this.processAffinitySpecies(analysis, " aff. ", AFFINTIY_ISSUES);
        this.processConferSpecies(analysis, " cf. ", CONFER_ISSUES);
        this.processSpeciesNovum(analysis, true, null);
        this.processPhraseName(analysis, null);
        this.processRankEnding(analysis, true, INDETERMINATE_ISSUES);
        this.processRankMarker(analysis, true, CANONICAL_ISSUES);
        this.processEmbeddedAuthor(analysis, CANONICAL_ISSUES);
        this.parseName(analysis, UNPARSABLE_ISSUES);
        this.processParsedScientificName(analysis, CANONICAL_ISSUES);
        this.fillOutClassification(analysis, classification);
        this.detectNumericPlaceholder(analysis, classification);
        classification.scientificName = analysis.getScientificName();
        classification.scientificNameAuthorship = analysis.getScientificNameAuthorship();
        classification.taxonRank = analysis.isUnranked() ? null : analysis.getRank();
        classification.nameType = analysis.getNameType();
        classification.kingdom = analysis.getKingdom();
        classification.addIssues(analysis.getIssues());
        if (classification.scientificNameAuthorship != null && INVALID_AUTHORSHIP.matcher(classification.scientificNameAuthorship).matches()) {
            classification.scientificNameAuthorship = null;
            classification.addIssues(CANONICAL_ISSUES);
        }
        if (classification.nomenclaturalCode == null && analysis.getNomCode() != null) {
            NomenclaturalCode code = Enums.getIfPresent(NomenclaturalCode.class, analysis.getNomCode().name()).orNull();
            if (code != null)
                classification.addHint(AlaLinnaeanFactory.nomenclaturalCode, code);
        }
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
    public Set<String> analyseNames(Classifier classifier, Observable<String> name, Optional<Observable<String>> complete, Optional<Observable<String>> additional, boolean canonical) throws InferenceException {
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
            this.processEmbeddedAuthor(analysis, null);
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

    /**
     * Decide whether to accept a synonym or not.
     * <p>
     * Only accept synonyms where the classifier is within two linnaean ranks distance of the classification.
     * The Species and sub-species range always pass because they are too fluid for accurate distance measurements.
     * </p>
     *
     * @param base The classifier the synonym is for
     * @param candidate      The classifier for the synonym
     * @return True if this is an acceptable synonym.
     */
    @Override
    public boolean acceptSynonym(Classifier base, Classifier candidate) {
        Rank baseRank = base.has(AlaLinnaeanFactory.taxonRank) ? base.get(AlaLinnaeanFactory.taxonRank) : Rank.UNRANKED;
        if (baseRank.otherOrUnranked())
            return true;
        Rank candidateRank = candidate.has(AlaLinnaeanFactory.taxonRank) ? candidate.get(AlaLinnaeanFactory.taxonRank) : Rank.UNRANKED;
        if (candidateRank.otherOrUnranked())
            return false;
        if (baseRank.isSpeciesOrBelow() && candidateRank.isSpeciesOrBelow())
            return true;
        int classificationRankID = RankIDAnalysis.idFromRank(baseRank);
        int candidateRankID = RankIDAnalysis.idFromRank(candidateRank);
        return Math.abs(classificationRankID - candidateRankID) < SYNONYM_RANK_DISTANCE;
    }
}
