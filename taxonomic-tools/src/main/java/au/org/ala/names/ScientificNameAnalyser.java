package au.org.ala.names;

import au.org.ala.bayesian.*;
import au.org.ala.util.BasicNormaliser;
import com.google.common.base.Enums;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.gbif.api.vocabulary.NomenclaturalCode;
import org.gbif.dwc.terms.Term;
import org.gbif.nameparser.AuthorshipParsingJob;
import org.gbif.nameparser.NameParserGBIF;
import org.gbif.nameparser.api.*;
import org.gbif.nameparser.util.NameFormatter;
import org.gbif.nameparser.util.RankUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Common analysis for scientific names
 *
 * @param <C> The classification
 */
abstract public class ScientificNameAnalyser<C extends Classification> implements Analyser<C> {
    protected static final ThreadLocal<NameParser> PARSER = ThreadLocal.withInitial(NameParserGBIF::new);

    /**
     * A string giving a regular expression for all rank markers
     */
    public static final String RANK_MARKERS = Arrays.stream(org.gbif.nameparser.api.Rank.values())
            .filter(r -> !r.isInfrasubspecific()) // Allow var. and the like through
            .map(r -> r.getMarker())
            .filter(Objects::nonNull)
            .map(m -> m.endsWith(".") ? m.substring(0, m.length() - 1) : m)
            .collect(Collectors.joining("|"))
            + "|ssp|spp";
    /**
     * A string giving a regular expression for the Linnaean rank markers
     */
    public static final String LINNAEAN_RANK_MARKERS = Arrays.stream(org.gbif.nameparser.api.Rank.values())
            .filter(r -> r.isLinnean()) // Allow var. and the like through
            .map(r -> r.getMarker())
            .filter(Objects::nonNull)
            .map(m -> m.endsWith(".") ? m.substring(0, m.length() - 1) : m)
            .collect(Collectors.joining("|"));
    /**
     * A string giving a regular expression for non-Linnaean, above species rank markers
     */
    public static final String INFRA_RANK_MARKERS = Arrays.stream(org.gbif.nameparser.api.Rank.values())
            .filter(r -> !r.isLinnean() && !r.isInfraspecific())
            .map(r -> r.getMarker())
            .filter(Objects::nonNull)
            .map(m -> m.endsWith(".") ? m.substring(0, m.length() - 1) : m)
            .collect(Collectors.joining("|"));
    /**
     * A string giving a regular expression for rank markers with capitalised first letter
     */
    public static final String CAPITALISED_RANK_MARKERS = Arrays.stream(org.gbif.nameparser.api.Rank.values())
            .map(r -> r.getMarker())
            .filter(Objects::nonNull)
            .map(m -> m.endsWith(".") ? m.substring(0, m.length() - 1) : m)
            .map(m -> m.substring(0, 1).toUpperCase() + m.substring(1))
            .collect(Collectors.joining("|"));
    /**
     * A sequence of rank markers and names, Eg. fam. Corbiculidae gen. Corbiculina
     */
    public static final Pattern ALTERNATING_RANK_NAME = Pattern.compile("^\\s*(" + RANK_MARKERS + ")\\.?\\s+([A-Za-z][a-z]+)(?:\\s|$)");
    /**
     * A name with a rank marker at the end of the name. Eg. Acacia sp.
     */
    public static final Pattern MARKER_ENDING = Pattern.compile("(^|\\s+)(" + RANK_MARKERS + ")\\.?$");
    /**
     * A name with a rank marker internally. Eg. Acacia sp. dealbata
     */
    public static final Pattern MARKER_INTERNAL = Pattern.compile("\\s+(" + RANK_MARKERS + ")\\.?\\s+");
    /**
     * A name that consists of a rank only. Eg. sp.
     */
    public static final Pattern MARKER_ONLY = Pattern.compile("^\\s*(" + RANK_MARKERS + ")\\.?\\s*$");
    /**
     * A name containing a query. Eg. Acacia dealbata?
     */
    public static final Pattern INDETERMINATE_MARKER = Pattern.compile("\\?");
    /**
     * A name with a confer species marker. Eg. Acacia cf. dealbata Also accepts Acacia dealbata cf.
     */
    public static final Pattern CONFER_SPECIES_MARKER = Pattern.compile("\\s+(?:cf|cfr|conf)\\.?(?:\\s+|$)");
    /**
     * A name with an affinity species marker. Eg. Acacia aff. dealbata Also accepts Acacia delabata aff.
     */
    public static final Pattern AFFINITY_SPECIES_MARKER = Pattern.compile("\\s+aff\\.?(?:\\s+|$)");
    /**
     * A name with a species novum marker. Eg. Acacia sp. nov. dealbata
     */
    public static final Pattern SP_NOV_MARKER = Pattern.compile("(?<=\\s)((?:" + RANK_MARKERS + ")\\.?)\\s+(nov\\.?|novum)\\s+");
    /**
     * A name with a Linnaean main name and an infraspecificno specivifc name. Eg. Acacia sect. Acacia
     */
    public static final Pattern INFRA_RANK_PATTERN = Pattern.compile("^[A-Z][a-z]+\\s+((?:" + INFRA_RANK_MARKERS + ")\\.?)\\s+[A-Z][a-z]+");
    /**
     * A name with specific epithets of the form "epithet 'Cultivar Name'" Eg. Acacia dealbata 'Yellow Dust'
     */
    public static final Pattern SUSPECTED_CULTIVAR_IN_SPECIFIC_EPITHET = Pattern.compile("['\"]([A-Za-z ]+)['\"]\\s*$");
    /**
     * A name with stuff that will probably cause the name parser to have a conniption
     */
    public static final Pattern UNPARSABLE_NAME = Pattern.compile("(?:" +
            "\\s+(" + CAPITALISED_RANK_MARKERS + ")\\.?\\s+" + "|" + // An internal capitalised rank marker
            "\\s+(" + INFRA_RANK_MARKERS + ")\\.?\\s+[A-Za-z]+-[A-Za-z]+(?:\\s+|$)" + // A hyphenated infra-rank name
            ")"
    );
    /**
     * A numeric or single letter placeholder name
     */
    public static final Pattern CODE_PLACEHOLDER = Pattern.compile("^[A-Z][a-z]+\\s((?:" + RANK_MARKERS + ")\\.?)\\s(?:[A-Z]|\\d+)(?:$|\\s)");
    /**
     * Multiple spaces
     */
    public static final Pattern MUTLI_SPACES = Pattern.compile("\\s{2,}");
    /**
     * The name comments resource bundle name
     */
    private static final String COMMENTS_BUNDLE_NAME = ScientificNameAnalyser.class.getPackage().getName() + ".ScientificNameComments";
    /**
     * The comments bundle
     */
    private static final ResourceBundle COMMENTS_BUNDLE = ResourceBundle.getBundle(COMMENTS_BUNDLE_NAME);
    /**
     * The comments list
     */
    private static final String COMMENTS = COMMENTS_BUNDLE.keySet().stream().map(k -> COMMENTS_BUNDLE.getString(k).trim()).collect(Collectors.joining("|"));
    /**
     * The commentary pattern
     */
    private static final Pattern COMMENTARY = Pattern.compile("(?:" + COMMENTS + ")", Pattern.CASE_INSENSITIVE);
    /**
     * The name invalid resource bundle name
     */
    private static final String INVALID_BUNDLE_NAME = ScientificNameAnalyser.class.getPackage().getName() + ".ScientificNameInvalid";
    /**
     * The invalid bundle
     */
    private static final ResourceBundle INVALID_BUNDLE = ResourceBundle.getBundle(INVALID_BUNDLE_NAME);
    /**
     * The invalid list
     */
    private static final String INVALID = INVALID_BUNDLE.keySet().stream().map(k -> INVALID_BUNDLE.getString(k).trim()).collect(Collectors.joining("|"));
    /**
     * The invalid pattern
     */
    private static final Pattern INVALID_PATTERN = Pattern.compile("(?:" + INVALID + ")", Pattern.CASE_INSENSITIVE);
    /**
     * An author pattern that allows the detection of a zoological nomenclatural code
     */
    private static final Pattern ZOOLOGICAL_AUTHOR = Pattern.compile("\\(?.+,\\s*[12]\\d\\d\\d\\s*\\)?");
    /**
     * Basic clean-up
     */
    public static final Normaliser BASIC_NORMALISER = new BasicNormaliser("basic", true, false, false, false, false);
    /**
     * Remove non-ascii punctuation
     */
    public static final Normaliser PUNCTUATION_NORMALISER = new BasicNormaliser("punctuation", true, true, true, false, false);
    /**
     * Replace non-ascii characters and accents
     */
    public static final Normaliser FULL_NORMALISER = new BasicNormaliser("full", true, true, true, true, false);
    /**
     * "Authors" that are actually part of the scientific name
     */
    public static final Set<String> DUD_AUTHORS = new HashSet<>(Arrays.asList("filius", "filia", "fil", "hort", "jun", "junior", "sen", "senior", "al"));

    /**
     * Analyse rank names
     */
    protected final RankAnalysis rankAnalysis = new RankAnalysis();

    /**
     * Normalse runs of spaces into a single spacv
     *
     * @param s
     * @return
     */
    protected String normaliseSpaces(String s) {
        return MUTLI_SPACES.matcher(s).replaceAll(" ").trim();
    }

    /**
     * Replace all matches with a replacement string and reduce any spaces that have crept in.
     *
     * @param matcher The matcher to examine
     * @param replace The replacement string
     * @return A replaced version
     */
    protected String replaceAll(@NonNull Matcher matcher, @NonNull String replace) {
        return this.normaliseSpaces(matcher.replaceAll(replace));
    }

    /**
     * Renmove stray surrpunding quotes on the name as a whole.
     *
     * @param analysis The name analysus
     * @param issues   Issues to add
     * @return True if surrounding quotes were detected
     */
    protected boolean removeSurroundingQuotes(@NonNull Analysis analysis, @Nullable Issues issues) {
        boolean detected = false;
        boolean found = true;
        String scientificName = analysis.getScientificName().trim();
        while (found) {
            found = false;
            if (scientificName.startsWith("\"") && scientificName.endsWith("\"")) {
                scientificName = scientificName.substring(1, scientificName.length() - 1).trim();
                detected = true;
                found = true;
            }
            if (scientificName.startsWith("'") && scientificName.endsWith("'")) {
                scientificName = scientificName.substring(1, scientificName.length() - 1).trim();
                detected = true;
                found = true;
            }
        }
        if (detected) {
            analysis.setScientificName(scientificName);
            analysis.addIssues(issues);
        }
        return detected;
    }

    /**
     * Replace unprintable characters in a name with a replacement character.
     * These characters will usually cause havoc in parsing and the like.
     *
     * @param analysis An analysis to strip
     * @param replace  The character to replace non-determinate characters with
     * @param issues   Issues to indivate a problem with the name
     * @return True if any unprintable characters were deteced
     */
    protected boolean replaceUnprintable(@NonNull Analysis analysis, char replace, @Nullable Issues issues) {
        boolean detected = false;
        String scientificName = analysis.getScientificName();
        if (scientificName != null) {
            scientificName = scientificName.replace('\ufffd', replace);
            if (!scientificName.equals(analysis.getScientificName())) {
                analysis.setScientificName(scientificName);
                detected = true;
                analysis.addIssues(issues);
            }
        }
        String scientificNameAuthorship = analysis.getScientificNameAuthorship();
        if (scientificNameAuthorship != null) {
            scientificNameAuthorship = scientificNameAuthorship.replace('\ufffd', replace);
            if (!scientificNameAuthorship.equals(analysis.getScientificNameAuthorship())) {
                analysis.setScientificNameAuthorship(scientificNameAuthorship);
                detected = true;
                analysis.addIssues(issues);
            }
        }
        return detected;
    }

    /**
     * Detect a pattern of alternating ranks and names and, if they exist, retyrn a map of what we have
     *
     * @param analysis The anlaysis to use
     * @param issues   ANy issues to add if this is detected
     * @return True if the name is indeterminate
     */
    protected Map<Rank, String> detectAlternatingRankName(@NonNull Analysis analysis, @Nullable Issues issues) {
        Matcher matcher;
        String scientificName = analysis.getScientificName();

        if (scientificName == null)
            return null;
        matcher = ALTERNATING_RANK_NAME.matcher(scientificName);
        if (matcher.find()) {
            analysis.addIssues(issues);
            Map<Rank, String> names = new HashMap<>();
            do {
                Rank rank = this.rankAnalysis.fromString(matcher.group(1));
                String name = matcher.group(2);
                if (rank != null)
                    names.put(rank, name);
                scientificName = scientificName.substring(matcher.end()).trim();
                matcher = ALTERNATING_RANK_NAME.matcher(scientificName);
            } while (matcher.find());
            if (!names.isEmpty() && scientificName.isEmpty())
                return names;
        }
        return null;
    }

    /**
     * Get rid of and flag indeterminate names (eg. ? in the name)
     *
     * @param analysis The analysis to strip
     * @param replace  If not null, the string to replace the indeterminate flag
     * @return True if the name is indeterminate
     * @pqram issues The issues to add if the name is indeterminate
     */
    protected boolean processIndeterminate(@NonNull Analysis analysis, @Nullable String replace, @Nullable Issues issues) {
        Matcher matcher;

        matcher = INDETERMINATE_MARKER.matcher(analysis.getScientificName());
        if (matcher.find()) {
            if (replace != null)
                analysis.setScientificName(this.replaceAll(matcher, replace));
            analysis.addIssues(issues);
            analysis.flagIndeterminate();
        }
        return analysis.isIndeterminate();
    }

    /**
     * Process an affinity species marker (aff.)
     *
     * @param analysis The analysis to flag
     * @param replace  Replace the marker with this value, null for no replacement
     * @param issues   Flag the analysis with these issues
     * @return True if an affinity species has been flagged (not necessarily by this method)
     */
    protected boolean processAffinitySpecies(@NonNull Analysis analysis, @Nullable String replace, @Nullable Issues issues) {
        Matcher matcher;

        matcher = AFFINITY_SPECIES_MARKER.matcher(analysis.getScientificName());
        if (matcher.find()) {
            analysis.addIssues(issues);
            if (replace != null)
                analysis.setScientificName(matcher.replaceAll(replace).trim());
            analysis.flagAffinitySpecies();
        }
        return analysis.isAffinitySpecies();
    }

    /**
     * Process a confer species marker (cf., conf. etc.)
     *
     * @param analysis The analysis to flag
     * @param replace  Replace the marker with this value, null for no replacement
     * @param issues   Flag the analysis with these issues
     * @return True if a confer species has been flagged (not necessarily by this method)
     */
    protected boolean processConferSpecies(@NonNull Analysis analysis, @Nullable String replace, @Nullable Issues issues) {
        Matcher matcher;

        matcher = CONFER_SPECIES_MARKER.matcher(analysis.getScientificName());
        if (matcher.find()) {
            analysis.addIssues(issues);
            if (replace != null)
                analysis.setScientificName(matcher.replaceAll(replace).trim());
            analysis.flagConferSpecies();
        }
        return analysis.isConferSpecies();
    }

    /**
     * Process a species novum marker (sp, nov., gen. nov. etc.)
     *
     * @param analysis  The analysis to flag
     * @param normalise Convert into standard form
     * @param issues    Flag the analysis with these issues
     * @return True if an species novum has been flagged (not necessarily by this method)
     */
    protected boolean processSpeciesNovum(@NonNull Analysis analysis, boolean normalise, @Nullable Issues issues) {
        Matcher matcher;
        String scientificName = analysis.getScientificName();

        matcher = SP_NOV_MARKER.matcher(scientificName);
        if (matcher.find()) {
            analysis.addIssues(issues);
            if (normalise) {
                Rank rank = RankUtils.inferRank(matcher.group(1));
                if (rank != null) {
                    scientificName = scientificName.substring(0, matcher.start())
                            + rank.getMarker()
                            + " nov. "
                            + scientificName.substring(matcher.end());
                    scientificName = this.normaliseSpaces(scientificName);
                    analysis.setScientificName(scientificName);
                }
            }
            analysis.flagSpeciesNovum();
        }
        return analysis.isSpeciesNovum();
    }

    /**
     * Process a phrase name.
     * <p>
     * This detects the presence of a phrase name
     * </p>
     *
     * @param analysis The analysis object to use
     * @param issues   If non-null, flag these issues
     * @return True if a phrase name (not necessarily from this process)
     */
    protected boolean processPhraseName(@NonNull Analysis analysis, @Nullable Issues issues) {
        Matcher matcher;

        matcher = AuthorshipParsingJob.PHRASE_NAME.matcher(analysis.getScientificName());
        if (matcher.matches()) {
            analysis.addIssues(issues);
            analysis.flagPhraseName();
        }
        return analysis.isPhraseName();
    }


    /**
     * Look for an embedded authorship in the name and remove it.
     * <p>
     * The authorship may or may not include combination parentheses and a year.
     * Look for them anyway.
     * </p>
     *
     * @param analysis The analysis object to use
     * @param issues   If non-null, flag these issues
     * @return True if the authorship is repeated in the name
     */
    protected boolean processEmbeddedAuthor(@NonNull Analysis analysis, @Nullable Issues issues) {
        String scientificName = analysis.getScientificName();
        String scientificNameAuthorship = analysis.getScientificNameAuthorship();

        if (scientificNameAuthorship != null && !scientificNameAuthorship.isEmpty()) {
            int p = scientificName.indexOf(scientificNameAuthorship);
            if (p >= 0) {
                scientificNameAuthorship = scientificNameAuthorship.replaceAll("[\\[\\]\\(\\)\\.\\+\\*\\?\\{\\}]", "\\\\$0");
                scientificNameAuthorship = "\\(\\s*" + scientificNameAuthorship + "(?:\\s*,\\s*\\d{4}\\s*)?\\)" +
                        "|" + scientificNameAuthorship + "(?:\\s*,\\s*\\d{4}\\s*)?";
                scientificName = scientificName.replaceFirst(scientificNameAuthorship, " ");
                scientificName = this.normaliseSpaces(scientificName);
                analysis.setScientificName(scientificName);
                analysis.addIssues(issues);
                return true;
            }
        }
        return false;
    }


    /**
     * Look for daft comments in the supplied name and remove them.
     *
     * @param analysis The analysis object to use
     * @param issues   If non-null, flag these issues
     * @return True if comments were found
     */
    protected boolean processCommentary(@NonNull Analysis analysis, @Nullable Issues issues) {
        String scientificName = analysis.getScientificName();
        if (scientificName == null)
            return false;
        Matcher matcher = COMMENTARY.matcher(scientificName);

        if (matcher.find()) {
            scientificName = this.normaliseSpaces(matcher.replaceAll(" "));
            analysis.setScientificName(scientificName);
            analysis.addIssues(issues);
            return true;
        }
        return false;
    }

    /**
     * Process names with a rank ending.
     * <p>
     * This indicates an ideterminate identification
     * </p>
     *
     * @param analysis The anlaysis to use
     * @param remove   Remove the rank ending if detected
     * @param issues   ANy issues to add if this is detected
     * @return True if the name is indeterminate
     */
    protected boolean processRankEnding(@NonNull Analysis analysis, boolean remove, @Nullable Issues issues) {
        Matcher matcher;

        matcher = MARKER_ENDING.matcher(analysis.getScientificName());
        if (matcher.find()) {
            analysis.addIssues(issues);
            if (remove)
                analysis.setScientificName(analysis.scientificName.substring(0, matcher.start()).trim());
            analysis.flagIndeterminate();
            analysis.estimateRank(Rank.UNRANKED);
        }
        return analysis.isIndeterminate();
    }


    /**
     * Process names with an internal rank marker. Eg. Acacia sp. delabata
     * <p>
     * Don't process phrase names or names with a "Linnaean rank Infrarank" pattern.
     * The {@link #processPhraseName(Analysis, Issues)} and {@link #processSpeciesNovum(Analysis, boolean, Issues)}
     * methods should be run before this method.
     * </p>
     *
     * @param analysis The anlaysis to use
     * @param remove   Remove the rank marker
     * @param issues   ANy issues to add if this is detected
     * @return True if the name has a rank marker
     */
    protected boolean processRankMarker(@NonNull Analysis analysis, boolean remove, @Nullable Issues issues) {
        Matcher matcher;

        if (analysis.isPhraseName())
            return false;
        if (analysis.isSpeciesNovum())
            return false;
        matcher = CODE_PLACEHOLDER.matcher(analysis.getScientificName());
        if (matcher.matches()) {
            analysis.estimateRank(this.rankAnalysis.fromString(matcher.group(1)));
            return false;
        }
        matcher = INFRA_RANK_PATTERN.matcher(analysis.getScientificName());
        if (matcher.matches()) {
            analysis.estimateRank(this.rankAnalysis.fromString(matcher.group(1)));
            return false;
        }
        matcher = MARKER_INTERNAL.matcher(analysis.getScientificName());
        if (matcher.find()) {
            analysis.estimateRank(this.rankAnalysis.fromString(matcher.group()));
            if (remove)
                analysis.setScientificName(matcher.replaceAll(" ").trim());
            analysis.addIssues(issues);
            return true;
        }
        return false;
    }


    /**
     * Generate a reduced version of the name.
     *
     * @param name The name
     * @return A name without weird additions
     */
    protected String reducedName(ParsedName name) {
        return NameFormatter.buildName(
                name,
                true, // hybrid marker
                false, // rank marker
                false, // authorship
                true, // genus for infrageneric
                false, // infrageneric
                true, // decomposition
                false, // ascii only
                true, // qualifier
                true, // indeterminate
                false, // nom note
                false, // sensu
                true, // cultivar
                true, // phrase
                true, // vocuher
                false, // nominating party
                true, // strain
                false //html
        );
    }

    protected void parseName(Analysis analysis, Issues unparsable) throws InferenceException {
        // Skip cases which look like trouble
        Matcher matcher = UNPARSABLE_NAME.matcher(analysis.scientificName);
        if (matcher.find()) {
            analysis.setNameType(NameType.INFORMAL);
            analysis.setParsedName(null);
            analysis.addIssues(unparsable);
            return;
        }
        try {
            NameParser parser = PARSER.get();
            ParsedName name = parser.parse(analysis.scientificName, analysis.getRank(), analysis.getNomCode());
            analysis.setParsedName(name);
        } catch (UnparsableNameException e) {
            analysis.setParsedName(null);
            analysis.addIssues(unparsable);
        }
    }

    /**
     * Process a parsed scientific name.
     * <p>
     * If the name has an embedded author, split the name and author.
     * See if we can estimate the nomenclatural code.
     * </p>
     *
     * @param analysis  The analysis to use
     * @param canonical The issues to add if the name was canonicalised
     * @return True if processing took place
     */
    protected boolean processParsedScientificName(@NonNull Analysis analysis, @Nullable Issues canonical) {
        ParsedName name = analysis.getParsedName();
        if (name == null)
            return false;
        NameType nameType = name.getType();
        if (name.isPhraseName())
            analysis.flagPhraseName();
        // Do not use higher ranks from parsed name as it guesses based on endings. Eeeewww!
        if (name.getRank() != null && Rank.GENUS.higherOrEqualsTo(name.getRank()))
            analysis.estimateRank(name.getRank());
        if (!analysis.hasFullyParsedName())
            return false;
        analysis.setNameType(nameType);
        if (nameType == NameType.SCIENTIFIC) {
            if (name.hasAuthorship() && analysis.getScientificNameAuthorship() == null && !DUD_AUTHORS.contains(name.authorshipComplete())) {
                analysis.setScientificName(NameFormatter.canonicalWithoutAuthorship(name));
                analysis.setScientificNameAuthorship(name.authorshipComplete());
                analysis.addIssues(canonical);
            }
            analysis.estimateNomCode(name.getCode());
            if (analysis.getScientificNameAuthorship() != null && ZOOLOGICAL_AUTHOR.matcher(analysis.getScientificNameAuthorship()).matches())
                analysis.estimateNomCode(NomCode.ZOOLOGICAL);
        } else if (analysis.isPhraseName()) {
            analysis.setScientificName(this.reducedName(name));
            analysis.estimateNomCode(NomCode.BOTANICAL);
        }
        return true;
    }

    /**
     * Add variants of the parsed name.
     * <p>
     * Canonicalised and simple names are added.
     * Linnaean names get a minimum version.
     * Names with an infragenric epithet gets the bare epithet added
     * Phrase names have a canonicalised version added.
     * </p>
     *
     * @param analysis The analysis
     * @return True if processing has taken place
     */
    protected boolean processAdditionalScientificNames(@NonNull Analysis analysis) {
        ParsedName name = analysis.getParsedName();
        if (!analysis.hasFullyParsedName())
            return false;
        if (name.getType() != NameType.SCIENTIFIC && !name.isPhraseName())
            return false;
        if (!name.hasAuthorship() || !DUD_AUTHORS.contains(name.authorshipComplete())) {
            analysis.addName(this.reducedName(name));
            analysis.addName(NameFormatter.canonicalWithoutAuthorship(name));
        }
        if (name.getType() == NameType.SCIENTIFIC) {
            // More minimal version if a recognised linnaean rank
            if ((name.getRank() != null && name.getRank().isLinnean()) || name.isTrinomial()) {
                analysis.addName(name.canonicalNameMinimal());
            }
            // Add infrageneric name without enclosing genus
            if (name.getInfragenericEpithet() != null && name.getRank().isInfragenericStrictly() && name.getGenus() != null && !name.getInfragenericEpithet().equals(name.getGenus())) {
                analysis.addName(name.getInfragenericEpithet());
            }
        }
        if (name.isPhraseName()) {
            // Add bare phrase name without voucher
            analysis.addName(name.canonicalNameMinimal() + " " + name.getRank().getMarker() + " " + name.getStrain());
        }
        return true;
    }

    /**
     * Test to ensure that we have a valid kingdom.
     *
     * @param analysis The analysis
     * @param issues Any issues to add if the kingdom is invalid
     *
     * @return True if the kingdom is null or passes, false if there is a problem
     */
    protected boolean checkKingdom(@NonNull Analysis analysis, @Nullable Issues issues) {
        if (analysis.getKingdom() == null)
            return true;
        if (!KingdomAnalysis.testKingdom(analysis.getKingdom())) {
            analysis.setKingdom(null);
            analysis.addIssues(issues);
            return false;
        }
        return true;
    }

    /**
     * Decide whether to accept a synonym or not.
     * <p>
     * By default, accept a synonym.
     * </p>
     *
     * @param classification The classification the synonym is for
     * @param candidate      The classifier for the synonym
     * @return True if this is an acceptable synonym.
     */
    @Override
    public boolean acceptSynonym(Classifier base, Classifier candidate) {
        return true;
    }

    /**
     * Check for an invalid value in a name.
     *
     * @param name The name
     * @param analysis The current analysis
     * @param issues Any issues to add if an invalid name is detected
     *
     * @return The name or null if the name is invalid
     */
    protected String checkInvalid(@Nullable String name, @NonNull Analysis analysis, @Nullable Issues issues) {
        if (name == null)
            return null;
        if (INVALID_PATTERN.matcher(name).matches()) {
            analysis.addIssues(issues);
            return null;
        }
        return name;
    }

    protected static class Analysis implements Cloneable {
        @Getter
        @Setter
        private NomCode nomCode;
        @Getter
        @Setter
        private String kingdom;
        @Getter
        private String scientificName;
        @Getter
        @Setter
        private String scientificNameAuthorship;
        @Getter
        private Rank rank;
        @Getter
        @Setter
        private NameType nameType;
        @Getter
        @Setter
        private ParsedName parsedName;
        @Getter
        private Set<String> names;
        @Getter
        private boolean indeterminate;
        @Getter
        private boolean affinitySpecies;
        @Getter
        private boolean conferSpecies;
        @Getter
        private boolean speciesNovum;
        @Getter
        private boolean phraseName;
        @Getter
        private Issues issues;

        @Override
        @SneakyThrows
        public Analysis clone() {
            return (Analysis) super.clone();
        }

        public Analysis(String scientificName, String scientificNameAuthorship, Rank rank, NomenclaturalCode nomenclaturalCode) {
            this.scientificName = scientificName;
            this.scientificNameAuthorship = scientificNameAuthorship;
            this.rank = rank != null ? rank : Rank.UNRANKED;
            this.nameType = NameType.INFORMAL;
            this.nomCode = nomenclaturalCode == null ? null : Enums.getIfPresent(NomCode.class, nomenclaturalCode.name()).orNull();
            this.names = new LinkedHashSet<>(); // Keep order
            this.addName(scientificName);
            this.indeterminate = false;
            this.affinitySpecies = false;
            this.conferSpecies = false;
            this.speciesNovum = false;
            this.phraseName = false;
            this.issues = new Issues();
        }


        public Analysis with(String scientificName) {
            Analysis clone = this.clone();
            clone.scientificName = scientificName;
            return clone;
        }

        public void setScientificName(String scientificName) {
            this.scientificName = scientificName;
            this.addName(scientificName);
        }

        public void estimateRank(Rank rank) {
            if (rank != null && rank != Rank.UNRANKED && rank != Rank.OTHER && (this.isUnranked() || this.rank.higherOrEqualsTo(rank)))
                this.rank = rank;
        }

        public void estimateNomCode(NomCode code) {
            if (this.nomCode == null && code != null)
                this.nomCode = code;
        }

        public boolean hasFullyParsedName() {
            return this.parsedName != null &&
                    this.parsedName.getState() == ParsedName.State.COMPLETE &&
                    !this.parsedName.getWarnings().contains(Warnings.QUESTION_MARKS_REMOVED);
        }

        public void addName(String name) {
            if (this.names != null && name != null && !name.isEmpty())
                this.names.add(name);
        }

        public boolean isUnranked() {
            return this.rank == null || this.rank == Rank.UNRANKED || this.rank == Rank.OTHER;
        }

        public void flagIndeterminate() {
            this.indeterminate = true;
        }


        public void flagAffinitySpecies() {
            this.affinitySpecies = true;
        }

        public void flagConferSpecies() {
            this.conferSpecies = true;
        }

        public void flagSpeciesNovum() {
            this.speciesNovum = true;
        }

        public void flagPhraseName() {
            this.phraseName = true;
        }

        public boolean isUncertain() {
            return this.isIndeterminate() || this.isConferSpecies() || this.isAffinitySpecies();
        }

        public void addIssue(Term issue) {
            if (issue != null && this.issues != null)
                this.issues = this.issues.with(issue);
        }

        public void addIssues(Issues issues) {
            if (issues != null && this.issues != null)
                this.issues = this.issues.merge(issues);
        }

    }
}
