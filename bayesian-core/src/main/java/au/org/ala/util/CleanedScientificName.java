package au.org.ala.util;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cleaned scientific names to varying degrees,
 * <p>
 * Scientific names may contain non-breaking spaces.
 * They may also contain punctuation that is a bit weird and
 * they may also contain accented characters and ligatures.
 * This class provides various levels of cleanliness for the names.
 * </p>
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * @copyright Copyright (c) 2016 ALA
 */
public class CleanedScientificName {
    /** The multiple space pattern */
    private static final Pattern SPACES = Pattern.compile("\\s+", Pattern.UNICODE_CHARACTER_CLASS);
    /** The punctuation translation table */
    private static final List<Substitute> PUNCT_TRANSLATE = Arrays.asList(
            Substitute.all("\u00a0", " "), // Non breaking space
            Substitute.all("\u00ad", "-"), // Soft hyphen
            Substitute.all("\u2010", "-"), // Hyphen
            Substitute.all("\u2011", "-"), // Non-breaking hyphen
            Substitute.all("\u2012", "-"), // Figure dash
            Substitute.all("\u2013", "-"), // En-dash
            Substitute.all("\u2014", "-"), // Em-dash
            Substitute.all("\u2015", "-"), // Horizontal bar
            Substitute.all("\u2018", "\'"), // Single left quotation
            Substitute.all("\u2019", "\'"), // Single right quotation
            Substitute.all("\u201a", "\'"), // Single low quotation
            Substitute.all("\u201b", "\'"), // Single high reversed quotation
            Substitute.all("\u201c", "\""), // Left quote
            Substitute.all("\u201d", "\""), // Right quote
            Substitute.all("\u201e", "\""), // Low quote
            Substitute.all("\u201f", "\""), // Reversed high quote
            Substitute.all("\u2027", ""), // Hyphenation point
            Substitute.all("\u2028", " "), // Line seperator
            Substitute.all("\u2029", " "), // Paragraph seperator
            Substitute.all("\u202a", ""), // Left to right embedding
            Substitute.all("\u202b", ""), // Right to left embeddiong
            Substitute.all("\u202c", ""), // Pop directional formatting
            Substitute.all("\u202d", ""), // Left to right override
            Substitute.all("\u202e", ""), // Right to left override
            Substitute.all("\u202f", " ") // Narrow no break space
    );

    /** The special character translation table for spelling some things out and replacing interesting punctuation with basic latin versions */
    private static final List<Substitute> BASIC_TRANSLATE = Arrays.asList(
            Substitute.all("\u00a1", "!"), // Inverted exclamation
            Substitute.all("\u00a2", "c"), // Cent sign
            Substitute.all("\u00a3", "#"), // Pound sign
            Substitute.all("\u00a4", "#"), // Currency sign
            Substitute.all("\u00a5", "Y"), // Yen
            Substitute.all("\u00a6", "|"), // Borken bar
            Substitute.all("\u00a7", "$"), // Section sign
            Substitute.all("\u00a8", ""), // Diaresis
            Substitute.all("\u00a9", "c"), // Copyright
            Substitute.all("\u00aa", ""), // Feminine ordinal
            Substitute.all("\u00ab", "<<"), // Left angle quotation
            Substitute.all("\u00ac", "~"), // Not sign
            Substitute.all("\u00d7", " x "), // Multiplication sign
            Substitute.all("\u00ae", "r"), // Registerd
            Substitute.all("\u00af", " "), // Macron
            Substitute.all("\u00b0", "o"), // Degree
            Substitute.all("\u00b1", "+-"), // Plus-minus
            Substitute.all("\u00b2", "2"), // Superscipt 2
            Substitute.all("\u00b3", "3"), // Superscript 3
            Substitute.all("\u00b4", ""), // Acute accent
            Substitute.all("\u00b5", "u"), // Micro
            Substitute.all("\u00b6", "@"), // Pilcrow
            Substitute.all("\u00b7", "."), // Middle dot
            Substitute.all("\u00b8", ""), // Cedilla
            Substitute.all("\u00b9", "1"), // Superscript 1
            Substitute.all("\u00bb", ">>"), // Right angle quotation
            Substitute.all("\u00bf", "?"), // Inverted question mark
            Substitute.all("\u00df", "ss"), // Small sharp s
            Substitute.all("\u03b1", " alpha "),
            Substitute.all("\u03b2", " beta "),
            Substitute.all("\u03b3", " gamma "),
            Substitute.all("\u03b4", " delta "),
            Substitute.all("\u03b5", " epsilon "),
            Substitute.all("\u03b6", " zeta "),
            Substitute.all("\u03b7", " eta"),
            Substitute.all("\u03b8", " theta "),
            Substitute.all("\u03ba", " kappa "),
            Substitute.all("\u03bb", " lambda "),
            Substitute.all("\u03bc", " mu "),
            Substitute.all("\u03bd", " nu "),
            Substitute.all("\u03be", " xi "),
            Substitute.all("\u03bf", " omicron "),
            Substitute.all("\u03c0", " pi "),
            Substitute.all("\u03c1", " rho "),
            Substitute.all("\u03c2", " sigma "),
            Substitute.all("\u03c3", " sigma"),
            Substitute.all("\u03c4", " tau "),
            Substitute.all("\u03c5", " upsilon "),
            Substitute.all("\u03c6", " phi "),
            Substitute.all("\u03c7", " chi "),
            Substitute.all("\u03c8", " psi "),
            Substitute.all("\u03c9", " omega "),
            Substitute.all("\u1e9e", "SS"), // Capital sharp s
            Substitute.all("\u2016", "|"), // Double vertical line
            Substitute.all("\u2017", "-"), // Double low line
            Substitute.all("\u2020", "*"), // Dagger
            Substitute.all("\u2021", "*"), // Double dagger
            Substitute.all("\u2022", "*"), // Bullet
            Substitute.all("\u2023", "*"), // Triangular bullet
            Substitute.all("\u2024", "."), // One dot leader
            Substitute.all("\u2025", "."), // Two dot leader
            Substitute.all("\u2026", "."), // Three dot leader
            Substitute.all("\u2030", "%"), // Per mille
            Substitute.all("\u2031", "%"), // Per ten thousand
            Substitute.all("\u2032", "'"), // Prime
            Substitute.all("\u2033", "\""), // Double prime
            Substitute.all("\u2034", "\""), // Triple prime
            Substitute.all("\u2035", "'"), // Reversed prime
            Substitute.all("\u2036", "\""), // Reversed double prime
            Substitute.all("\u2037", "\""), // Reversed triple prime
            Substitute.all("\u2038", "^"), // Caret
            Substitute.all("\u2039", "<"), // Left angle quote
            Substitute.all("\u203a", ">"), // Right angle quote
            Substitute.all("\u203b", "*"), // Reference mark
            Substitute.all("\u203c", "!!"), // Double exclamation
            Substitute.all("\u203d", "?!"), // Interrobang
            Substitute.all("\u203e", "-"), // Overline
            Substitute.all("\u203f", "_"), // Undertie
            Substitute.all("\u2040", "-"), // Character tie
            Substitute.all("\u2041", "^"), // Caret insertion point
            Substitute.all("\u2042", "*"), // Asterism
            Substitute.all("\u2043", "*"), // Hyphen bullet
            Substitute.all("\u2044", "/"), // Fraction slash
            Substitute.all("\u2045", "["), // Left bracket with quill
            Substitute.all("\u2046", "]"), // Right bracket with quill
            Substitute.all("\u2047", "??"), // Double question mark
            Substitute.all("\u2715", " x "), // Multiplication x
            Substitute.all("\u2a09", " x "), // n-ary cross
            Substitute.all("\u2a7f", " x ") // Cross product
    );

    /** The source name */
    private String source;
    /** The basic name, with spaces reduced */
    private String name;
    /** The name with normalised punctuation and ligatures */
    private String normalised;
    /** The name with basic latin characters (ie. no accents and funny stuff */
    private String basic;

    /**
     * Normalise spaces.
     * <p>
     * Replace all sequences of whitespace with a single space.
     * Remove fancy whitespace.
     * </p>
     *
     * @param name The name to translate
     *
     * @return The normalised name
     */
    protected String normaliseSpaces(String name) {
        Matcher matcher = SPACES.matcher(name);

        return matcher.replaceAll(" ").trim();
    }

    /**
     * Translate a string according to a translation map.
     *
     * @param name The string
     * @param map The translation map
     *
     * @return The translated string
     */
    protected String translate(String name, List<Substitute> map) {
        return map.stream().reduce(name, (s, m) -> m.apply(s), (a, b) -> b);
    }

    /**
     * Construct for a source name.
     * @param source
     */
    public CleanedScientificName(String source) {
        assert source != null;
        this.source = source;
        this.name = null;
        this.normalised = null;
        this.basic = null;
    }

    /**
     * Build a name from a source string.
     * <p>
     * By default, all sequences of whitespace are replaced by a single space character.
     * </p>
     *
     * @param source The reduced source
     *
     * @return The norm
     */
    protected String buildName(String source) {
        return this.normaliseSpaces(source);
    }

    /**
     * Get the name.
     * <p>
     * The name is the source name, with spaces reduced by {@link #buildName(String)}
     * </p>
     *
     * @return The basic name
     */
    public String getName() {
        if (this.name == null)
            this.name = this.buildName(this.source);
        return this.name;
    }

    /**
     * Build a normalised name.
     * <p>
     * By default, most unicode punctuation is replaced by ASCII equivalents.
     * The string is also decomposed so that ligatures and the like are replaced by character sequences.
     * </p>
     *
     * @param name The name to normalise
     *
     * @return The built name
     */
    protected String buildNormalised(String name) {
        name = Normalizer.normalize(name, Normalizer.Form.NFKC);
        name = this.translate(name, PUNCT_TRANSLATE);
        return this.normaliseSpaces(name);
    }

    /**
     * Get the normalised name.
     * <p>
     * The normalised name is the name from {@link #getName()} normaised by {@link #buildNormalised(String)}
     *
     * @return The
     */
    public String getNormalised() {
        if (this.normalised == null)
            this.normalised = this.buildNormalised(this.getName());
        return this.normalised;
    }

    /**
     * Has this got a normalised version that is different from the basic name?
     *
     * @return True if the normalised version is different
     */
    public boolean hasNormalised() {
        return !this.getName().equals(this.getNormalised());
    }

    /**
     * Build a basic latin version of a name.
     * <p>
     * Any accented characters are replaced by non-accented equivalents.
     * Any non-basic latin characters are removed.
     * </p>
     * @param name The name to make basic
     *
     * @return The basic name
     */
    protected String buildBasic(String name) {
        name = Normalizer.normalize(name, Normalizer.Form.NFD);
        name = this.translate(name, BASIC_TRANSLATE);
        name = this.normaliseSpaces(name);

        int i, len = name.length();
        StringBuilder builder = new StringBuilder();

        for (i = 0; i < len; i++) {
            char ch = name.charAt(i);
            if (ch < 128)
                builder.append(ch);
        }
        return builder.toString();
    }

    public String getBasic() {
        if (this.basic == null)
            this.basic = this.buildBasic(this.getNormalised());
        return this.basic;
    }


    /**
     * Has this got a normalised version that is different from the basic name?
     *
     * @return True if the normalised version is different
     */
    public boolean hasBasic() {
        return !this.getNormalised().equals(this.getBasic());
    }
}

