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

public class AlaNameAnalyser implements Analyser<AlaLinnaeanClassification> {
    private static final ThreadLocal<NameParser> PARSER = ThreadLocal.withInitial(NameParserGBIF::new);
    private static final String RANK_MARKERS = Arrays.stream(Rank.values())
            .filter(r -> !r.isInfrasubspecific()) // Allow var. and the like through
            .map(r -> r.getMarker())
            .filter(Objects::nonNull)
            .map(m -> m.endsWith("." ) ? m.substring(0, m.length() - 1) : m)
            .collect(Collectors.joining("|" ))
            + "|spp";
    private static final String INFRA_RANK_MARKERS = Arrays.stream(Rank.values())
            .filter(r -> !r.isLinnean() && !r.isInfraspecific())
            .map(r -> r.getMarker())
            .filter(Objects::nonNull)
            .map(m -> m.endsWith("." ) ? m.substring(0, m.length() - 1) : m)
            .collect(Collectors.joining("|" ));
    private static final Pattern MARKER_ENDING = Pattern.compile("(^|\\s+)(" + RANK_MARKERS + ")\\.?$" );
    private static final Pattern MARKER_INTERNAL = Pattern.compile("\\s+(" + RANK_MARKERS + ")\\.?\\s+" );
    private static final Pattern MARKER_ONLY = Pattern.compile("^\\s*(" + RANK_MARKERS + ")\\.?\\s*$" );
    private static final Pattern INDETERMINATE_MARKER = Pattern.compile("\\?" );
    private static final Pattern CONFER_SPECIES_MARKER = Pattern.compile("\\s+cf\\.?\\s+" );
    private static final Pattern AFFINITY_SPECIES_MARKER = Pattern.compile("\\s+aff\\.?\\s+" );
    private static final Pattern SP_NOV_MARKER = Pattern.compile("(?<=(" + RANK_MARKERS + ")\\.?)\\s+nov\\.?\\s+");
    private static final Pattern INFRA_RANK_PATTERN = Pattern.compile("^[A-Z][a-z]+\\s+(?:" + INFRA_RANK_MARKERS + ")\\.?\\s+[A-Z][a-z]+");

    /**
     * If a classification does not have a direct scientific name/rank combination then infer it.
     * Sub-species ranks are generalised to be a genric infraspecific rank, since there's too
     * much disagreement about what is actually valid.
     *
     * @param analysis The analysis state
     */
    protected void inferRank(Analysis analysis) {
        AlaLinnaeanClassification classification = analysis.classification;
        
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

        // Generalise sub-species ranks
        if (analysis.rank.notOtherOrUnranked()) {
            if (Rank.SPECIES.higherThan(analysis.rank)) {
                classification.taxonRank = analysis.rank = Rank.INFRASPECIFIC_NAME;
            } else if (classification.taxonRank == null && analysis.rank.notOtherOrUnranked())
                classification.taxonRank = analysis.rank;
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
     * @param name The name observable
     *             
     * @return A set of base names
     */
    protected Set<String> generateBaseNames(Analysis analysis, Observable name) {
        Classifier classifier = analysis.classifier;
        Set<String> names = new LinkedHashSet<>(classifier.getAll(name));
        Rank rank = analysis.rank;
        
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
        if (analysis.rank == Rank.UNRANKED)
            analysis.rank = rank;
        return names;
    }

    /**
     * Get rid of and flag indeterminate names (eg. ?, cf, aff etc.)
     * 
     * @param analysis The analysis to strip
     */
    protected void stripIndeterminate(Analysis analysis) {
        Matcher matcher;

        matcher = INDETERMINATE_MARKER.matcher(analysis.scientificName);
        if (matcher.find()) {
            analysis.setScientificName(matcher.replaceAll(" ").trim());
            analysis.addIssue(AlaLinnaeanFactory.INDETERMINATE_NAME);
            analysis.indeterminate = true;
        }
        matcher = AFFINITY_SPECIES_MARKER.matcher(analysis.scientificName);
        if (matcher.find()) {
            analysis.setScientificName(matcher.replaceAll(" ").trim());
            analysis.addIssue(AlaLinnaeanFactory.AFFINITY_SPECIES_NAME);
            analysis.indeterminate = true;
        }
        matcher = CONFER_SPECIES_MARKER.matcher(analysis.scientificName);
        if (matcher.find()) {
            analysis.setScientificName(matcher.replaceAll(" ").trim());
            analysis.addIssue(AlaLinnaeanFactory.CONFER_SPECIES_NAME);
            analysis.indeterminate = true;
        }
        matcher = SP_NOV_MARKER.matcher(analysis.scientificName);
        if (matcher.find()) {
            analysis.setScientificName(matcher.replaceAll(" ").trim());
            analysis.indeterminate = true;
        }
    }
    
    protected boolean stripRankMarkers(Analysis analysis) {
        Matcher matcher;
        boolean changed = false;
        
        // Phrase names should not have rank markers removed
        matcher = AuthorshipParsingJob.PHRASE_NAME.matcher(analysis.scientificName);
        if (!matcher.matches()) {
            // Remove rank marker, if present and flag
            matcher = MARKER_ENDING.matcher(analysis.scientificName);
            if (matcher.find()) {
                changed = true;
                analysis.setScientificName(analysis.scientificName.substring(0, matcher.start()).trim());
                analysis.addIssue(AlaLinnaeanFactory.INDETERMINATE_NAME);
                analysis.addIssue(ALATerm.canonicalMatch);
                analysis.rank = Rank.UNRANKED;
                if (analysis.classification != null)
                    analysis.classification.taxonRank = null;
            }
            // Leave names that can only be recognised as Uninomial rank. Uninomial alone
            matcher = INFRA_RANK_PATTERN.matcher(analysis.scientificName);
            if (!matcher.matches()) {
                matcher = MARKER_INTERNAL.matcher(analysis.scientificName);
                if (matcher.find()) {
                    changed = true;
                    analysis.setScientificName(matcher.replaceAll(" ").trim());
                    analysis.addIssue(ALATerm.canonicalMatch);
                }
            }
        }
        return changed;
    }

    protected void fillOutClassification(Analysis analysis, ParsedName name) throws InferenceException {
        AlaLinnaeanClassification classification = analysis.classification;

        if (classification == null)
            return;
        if (name.getState() == ParsedName.State.COMPLETE) {
            if (name.getType() == NameType.SCIENTIFIC) {
                if (name.hasAuthorship()) {
                    analysis.addIssue(ALATerm.canonicalMatch);
                    if (classification.scientificNameAuthorship == null) {
                        classification.scientificNameAuthorship = NameFormatter.authorshipComplete(name);
                    }
                }
            }
            if (name.getType() == NameType.PHRASE) {
                if (classification.nominatingParty == null && name.getNominatingParty() != null) {
                    classification.nominatingParty = name.getNominatingParty();
                }
                if (classification.voucher == null && name.getVoucher() != null) {
                    classification.voucher = (String) AlaLinnaeanFactory.voucher.getAnalysis().analyse(name.getVoucher());
                }
                if (classification.phraseName == null && name.getPhrase() != null) {
                    classification.phraseName = (String) AlaLinnaeanFactory.phraseName.getAnalysis().analyse(name.getPhrase());
                }
            }
            if (name.getType() == NameType.SCIENTIFIC || name.getType() == NameType.INFORMAL || name.getType() == NameType.PLACEHOLDER) {
                // Only do this for non-synonyms to avoid dangling speciesID
                if (classification.acceptedNameUsageId == null && classification.specificEpithet == null && name.getSpecificEpithet() != null && !analysis.indeterminate)
                    classification.specificEpithet = name.getSpecificEpithet();

            }
            if (classification.acceptedNameUsageId == null && classification.genus == null && analysis.rank != null && !analysis.rank.higherThan(Rank.GENUS) && name.getGenus() != null)
                classification.genus = name.getGenus();
            if (classification.cultivarEpithet == null && name.getCultivarEpithet() != null)
                classification.cultivarEpithet = name.getCultivarEpithet();
        }

    }

    protected void parseName(Analysis analysis) throws InferenceException {
        try {
            NameParser parser = PARSER.get();
            ParsedName name = parser.parse(analysis.scientificName, analysis.rank, analysis.nomCode);
            if (name.getRank() != null)
                analysis.estimateRank(name.getRank());
            if (name.getState() == ParsedName.State.COMPLETE) {
                if (name.getType() == NameType.SCIENTIFIC || name.getType() == NameType.PHRASE) {
                    analysis.setScientificName(NameFormatter.canonicalWithoutAuthorship(name));
                }
                if (name.getType() == NameType.SCIENTIFIC) {
                    // More minimal version if a recognised linnaean rank
                    if (name.getRank() != null && name.getRank().isLinnean()) {
                        analysis.addName(name.canonicalNameMinimal());
                    }
                    // Add infrageneric name without enclosing genus
                    if (name.getInfragenericEpithet() != null && name.getRank().isInfrageneric() && name.getGenus() != null && !name.getInfragenericEpithet().equals(name.getGenus())) {
                        analysis.addName(name.getInfragenericEpithet());
                    }
                }
                if (name.getType() == NameType.PHRASE) {
                    // Add bare phrase name without voucher
                    analysis.addName(name.canonicalNameMinimal() + " " + name.getRank().getMarker() + " " + name.getPhrase());
                }
            }
            this.fillOutClassification(analysis, name);
        } catch (UnparsableNameException e) {
            analysis.addIssue(ALATerm.unparsableName);
        }
    }

    /**
     * Analyse the information in a classifier and extend the classifier
     * as required for indexing.
     *
     * @param classification The classification
     */
    @Override
    public void analyseForIndex(AlaLinnaeanClassification classification) throws InferenceException {
        Analysis analysis = new Analysis(classification);

        this.inferRank(analysis);
        if (analysis.scientificName != null) {
            this.parseName(analysis);
            classification.taxonRank = analysis.rank.notOtherOrUnranked() ? analysis.rank : null;
        }

        // Remove loops in taxonomy
        if (classification.parentNameUsageId != null && classification.parentNameUsageId.equals(classification.taxonId)) {
            classification.addIssue(ALATerm.taxonParentLoop);
            classification.parentNameUsageId = null;
        }
        if (classification.acceptedNameUsageId != null && classification.acceptedNameUsageId.equals(classification.taxonId)) {
            classification.addIssue(ALATerm.taxonAcceptedLoop);
            classification.acceptedNameUsageId = null;
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
        Analysis analysis = new Analysis(classification);

        this.inferRank(analysis);
        if (analysis.scientificName == null)
            return;
        if (MARKER_ONLY.matcher(analysis.scientificName).matches())
            throw new InferenceException("Name is rank only.");
        this.stripIndeterminate(analysis);
        this.stripRankMarkers(analysis);
        this.parseName(analysis);
        classification.scientificName = analysis.scientificName;
        classification.taxonRank = analysis.rank.notOtherOrUnranked() ? analysis.rank : null;
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
        Analysis analysis = new Analysis(classifier);

        Set<String> allScientificNameAuthorship = !canonical && additional.isPresent() ? classifier.getAll(additional.get()) : Collections.emptySet();
        Set<String> allCompleteNames = !canonical && complete.isPresent() ? classifier.getAll(complete.get()) : Collections.emptySet();
        Set<String> allScientificNames = this.generateBaseNames(analysis, name);
 
        if (allScientificNames.isEmpty())
            throw new InferenceException("No scientific name for " + classifier.get(AlaLinnaeanFactory.taxonId));
        for (String nm : allScientificNames) {
            CleanedScientificName n = new CleanedScientificName(nm);
            analysis.names.add(n.getName());
            analysis.names.add(n.getBasic());
            analysis.names.add(n.getNormalised());

            // From now on, only use the normalised version
            Analysis sub = analysis.with(n.getNormalised());

            this.stripIndeterminate(sub);
            this.parseName(sub);
            if (this.stripRankMarkers(sub))
                this.parseName(sub);
        }

        // Add name/author pairs
        if (allCompleteNames.isEmpty() && !allScientificNameAuthorship.isEmpty()) {
            allCompleteNames = allScientificNames.stream().flatMap(n -> allScientificNameAuthorship.stream().map(a -> n + " " + a)).collect(Collectors.toSet());
        }
        for (String nm : allCompleteNames) {
            CleanedScientificName n = new CleanedScientificName(nm);
            analysis.names.add(n.getName());
            analysis.names.add(n.getBasic());
            analysis.names.add(n.getNormalised());
        }
        return analysis.names;
    }

    /**
     * Basic information about the current state of the analysis.
     * <p>
     * Used so that we can factor out the 
     * </p>
     */
    private class Analysis implements Cloneable {
        // The current view of the scientific name
        @Getter
        private String scientificName;
        // The estimated rank
        public Rank rank;
        // The estimated nomenclatural code
        public NomCode nomCode;
        // The classification to manipulate (null if not relevant)
        public AlaLinnaeanClassification classification;
        // The classifier to interrogate (null if not relevant)
        public Classifier classifier;
        // The set of names derived from that name
        public Set<String> names;
        // Is this an indeterminate name
        public boolean indeterminate = false;
        
        public Analysis(AlaLinnaeanClassification classification) {
            this.scientificName = classification.scientificName;
            this.classification = classification;
            this.rank = classification.taxonRank != null ? classification.taxonRank : Rank.UNRANKED;
            final NomenclaturalCode nomenclaturalCode = classification.nomenclaturalCode;
            this.nomCode = nomenclaturalCode == null ? null : Enums.getIfPresent(NomCode.class, nomenclaturalCode.name()).orNull();
        }
        
        public Analysis(Classifier classifier) {
            this.names = new LinkedHashSet<>();
            this.classifier = classifier;
            this.rank = classifier.has(AlaLinnaeanFactory.taxonRank) ? classifier.get(AlaLinnaeanFactory.taxonRank) : Rank.UNRANKED;
            final NomenclaturalCode nomenclaturalCode = classifier.get(AlaLinnaeanFactory.nomenclaturalCode);
            this.nomCode = nomenclaturalCode == null ? null : Enums.getIfPresent(NomCode.class, nomenclaturalCode.name()).orNull();
        }
        
        @Override
        @SneakyThrows
        public Analysis clone() {
            return (Analysis) super.clone();
        }
        
        public Analysis with(String scientificName) {
            Analysis clone = this.clone();
            clone.scientificName = scientificName;
            return clone;
        }
        
        public void estimateRank(Rank rank) {
            if (this.rank.otherOrUnranked())
                this.rank = rank;
        }
        
        public void addIssue(Term issue) {
            if (this.classification != null)
                this.classification.addIssue(issue);
        }

        public void addName(String name) {
            if (this.names == null || name == null || name.isEmpty())
                return;
            this.names.add(name);
        }
        
        public void setScientificName(String name) {
            this.scientificName = name;
            this.addName(name);
        }
    }
}
