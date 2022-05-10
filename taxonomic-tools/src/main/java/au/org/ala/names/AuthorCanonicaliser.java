package au.org.ala.names;

import au.org.ala.bayesian.Normaliser;
import au.org.ala.util.BasicNormaliser;
import org.gbif.nameparser.NameParserGBIF;
import org.gbif.nameparser.api.Authorship;
import org.gbif.nameparser.api.NameParser;
import org.gbif.nameparser.api.ParsedAuthorship;
import org.gbif.nameparser.api.UnparsableNameException;
import org.gbif.utils.file.csv.CSVReader;
import org.gbif.utils.file.csv.CSVReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convert an author string into a canonical representation.
 * <p>
 * This allows authors to be campared without need to parse components each time.
 * Removes abbreviations, stems names and does other strange stuff.
 * </p>
 * <p>
 * Makes use of the GBIF {@link org.gbif.checklistbank.authorship.AuthorComparator} list of abbreviations.
 * </p>
 */
public class AuthorCanonicaliser {
    private static final Logger logger = LoggerFactory.getLogger(AuthorCanonicaliser.class);

    protected static final ThreadLocal<NameParser> PARSER = ThreadLocal.withInitial(NameParserGBIF::new);

    protected static final Normaliser NORMALISER = new BasicNormaliser("author", true, true, true, true, false);
    protected static final Pattern NON_LETTER = Pattern.compile("\\W");
    protected static final Pattern INITIAL_ABBREV = Pattern.compile("(?<=^|\\s|[A-Z]\\.)([A-Z])(?:\\.|\\.\\s+|\\s+)");
    protected static final Pattern QUESTION_MARKS = Pattern.compile("\\?");
    protected static final Pattern LEADING_EX = Pattern.compile("^\\(ex\\s");
    protected static final Pattern LOWER_CASE_BASIONYM = Pattern.compile("[\\(\\[]\\s*(\\p{Lower}+)\\s*[\\)\\]]");

    /** The map of abbreviations and strings used to canonicalise names */
    private static final Map<String, String> CANONICAL = Collections.synchronizedMap(buildInitialCanonicalMap());

    // Initialise all the possible abbreviations
    private static Map<String, String> buildInitialCanonicalMap() {
        Map<String, String> canonicalMap = new HashMap<>(40000);
        try {
            CSVReader reader = CSVReaderFactory.buildUtf8TabReader(AuthorCanonicaliser.class.getResourceAsStream("/authorship/authormap.txt"));
            while (reader.hasNext()) {
                String[] row = reader.next();
                if (row.length < 1)
                    continue;
                String canonical = canonicaliseAuthorString(row[row.length - 1]);
                if (canonical == null)
                    continue;
                for (int i = 0; i < row.length; i++) {
                    String version = row[i];
                    canonicalMap.put(version, canonical);
                    canonicalMap.put(NORMALISER.normalise(version), canonical);
                    version = INITIAL_ABBREV.matcher(version).replaceAll("$1.");
                    canonicalMap.put(version, canonical);
                    canonicalMap.put(NORMALISER.normalise(version), canonical);
                }
            }
        } catch (IOException ex) {
            logger.error("Unable to read author map", ex);
        }
        logger.info("Initialised canonicalisation dictionary with " + canonicalMap.size() + " entries");
        return canonicalMap;
    }

    private static String canonicaliseAuthorString(String name) {
        name = NORMALISER.normalise(name);
        if (name == null)
            return null;
        name = INITIAL_ABBREV.matcher(name).replaceAll("");
        name = NON_LETTER.matcher(name).replaceAll( "");
        return name;
    }

    private String canonicaliseAuthor(String name) {
        return CANONICAL.computeIfAbsent(name, AuthorCanonicaliser::canonicaliseAuthorString);
    }

    private void appendAuthors(StringBuilder sb, List<String> authors) {
        if (authors.size() == 0) {
            sb.append("?");
            return;
        }
        sb.append(this.canonicaliseAuthor(authors.get(0)));
        if (authors.size() > 1) {
            sb.append(" ");
            if (authors.get(1).equals("al.")) {
                sb.append("+");
            } else {
                sb.append(this.canonicaliseAuthor(authors.get(1)));
            }
        }
        if (authors.size() > 2) {
            sb.append(" +");
        }
    }

    private void appendAuthorship(StringBuilder sb, Authorship auth, boolean includeYear) {
        if (auth != null && auth.exists()) {
            if (auth.hasAuthors()) {
                this.appendAuthors(sb, auth.getAuthors());
            } else if (auth.hasExAuthors()) {
                this.appendAuthors(sb, auth.getExAuthors());
            }
            if (auth.getYear() != null && includeYear) {
                sb.append(" ");
                sb.append(auth.getYear());
            }
        }
    }

    /**
     * Deal with the random junk that appears in author strings.
     *
     * @param authorship The author string
     *
     * @return
     */
    protected String condition(String authorship) {
        if (authorship == null)
            return null;
        authorship = QUESTION_MARKS.matcher(authorship).replaceAll("");
        authorship = LEADING_EX.matcher(authorship).replaceFirst("(");
        Matcher matcher = LOWER_CASE_BASIONYM.matcher(authorship);
        if (matcher.find()) {
            String replace = matcher.group(1);
            replace = "(" + replace.substring(0, 1).toUpperCase() + replace.substring(1) + ")";
            authorship = matcher.replaceFirst(replace);
        }
        authorship = NORMALISER.normalise(authorship);
        return authorship;
    }

    public String canonicalise(String authorship) {
        authorship = this.condition(authorship);
        if (authorship == null || authorship.equals("-"))
            return null;
        try {
            ParsedAuthorship parsed = PARSER.get().parseAuthorship(authorship);
            StringBuilder sb = new StringBuilder(authorship.length());
            if (parsed.hasCombinationAuthorship()) {
                this.appendAuthorship(sb, parsed.getCombinationAuthorship(), true);
            } else if (parsed.hasBasionymAuthorship()) {
                this.appendAuthorship(sb, parsed.getBasionymAuthorship(), true);
            } else if (parsed.getSanctioningAuthor() != null) {
                sb.append(this.canonicaliseAuthor(parsed.getSanctioningAuthor()));
            }
            return sb.toString();
        } catch (UnparsableNameException e) {
            logger.warn("Unparseable author string \"{}\", leaving as-is", authorship);
            return authorship;
         }
    }
}
