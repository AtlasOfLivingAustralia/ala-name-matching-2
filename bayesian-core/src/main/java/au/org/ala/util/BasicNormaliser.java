package au.org.ala.util;

import au.org.ala.bayesian.Normaliser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A basic normaliser that can be used to clean a string to varying degrees.
 * <p>
 * Strings may contain non-breaking spaces.
 * They may also contain punctuation that is a bit weird and
 * they may also contain accented characters and ligatures.
 * This class provides various levels of cleanliness for input strings.
 * </p>
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * @copyright Copyright (c) 2020 ALA
 */
public class BasicNormaliser extends Normaliser {
    /** The multiple space pattern */
    private static final Pattern SPACES = Pattern.compile("\\s+", Pattern.UNICODE_CHARACTER_CLASS);
    /** The diacritic pattern */
    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+", Pattern.UNICODE_CHARACTER_CLASS);
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
            Substitute.all("\u2018", "'"), // Single left quotation
            Substitute.all("\u2019", "'"), // Single right quotation
            Substitute.all("\u201a", "'"), // Single low quotation
            Substitute.all("\u201b", "'"), // Single high reversed quotation
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
    private static final List<Substitute> SYMBOL_TRANSLATE = Arrays.asList(
            Substitute.all("\u00a1", "!"), // Inverted exclamation
            Substitute.all("\u00a2", "c"), // Cent sign
            Substitute.all("\u00a3", "#"), // Pound sign
            Substitute.all("\u00a4", "#"), // Currency sign
            Substitute.all("\u00a5", "Y"), // Yen
            Substitute.all("\u00a6", "|"), // Borken bar
            Substitute.all("\u00a7", "\\$"), // Section sign
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

    /** Reduce runs of spaces to a single space */
    @JsonProperty
    private boolean normaliseSpaces;
    /** Turn things like open/close quotes into single direction quotes */
    @JsonProperty
    private boolean normalisePunctuation;
    /** Spell out unusual symbols */
    @JsonProperty
    private boolean normaliseSymbols;
    /** Turn accented characters into non-accented characters */
    @JsonProperty
    private boolean normaliseAccents;
    /** Convert case into upper/lower case */
    @JsonProperty
    private boolean normaliseCase;

    /**
     * Construct a normaliser
     *
     * @param id The normaliser id
     * @param normaliseSpaces Normalise spaces into a single space
     * @param normalisePunctuation Normalise open/close punctuation characters
     * @param normaliseSymbols Normalise symbols such as \beta
     * @param normaliseAccents Convert accented characters into unaccented characters
     * @param normaliseCase Ensure all lower-case
     */
    public BasicNormaliser(String id, boolean normaliseSpaces, boolean normalisePunctuation, boolean normaliseSymbols, boolean normaliseAccents, boolean normaliseCase) {
        super(id);
        this.normaliseSpaces = normaliseSpaces;
        this.normalisePunctuation = normalisePunctuation;
        this.normaliseSymbols = normaliseSymbols;
        this.normaliseAccents = normaliseAccents;
        this.normaliseCase = normaliseCase;
    }

    /**
     * Construct a default normaliser that performs all normalisations
     */
    public BasicNormaliser() {
        super();
        this.normaliseSpaces = true;
        this.normalisePunctuation = true;
        this.normaliseSymbols = true;
        this.normaliseAccents = true;
        this.normaliseCase = true;
    }

    /**
     * Normalise spaces.
     * <p>
     * Replace all sequences of whitespace with a single space.
     * Remove fancy whitespace.
     * </p>
     *
     * @param s The string to translate
     *
     * @return The normalised string
     */
    protected String normaliseSpaces(String s) {
        Matcher matcher = SPACES.matcher(s);
        return matcher.replaceAll(" ").trim();
    }


    /**
     * Strip accents.
     * <p>
     * Decompose any accented characters into diacritics and base characters and then remove the diacritics.
     * </p>
     *
     * @param s The string to translate
     *
     * @return The de-accented string
     */
    protected String removeAccents(String s) {
        s = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        Matcher matcher = DIACRITICS.matcher(s);
        return matcher.replaceAll("").trim();
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

    public String normalise(String s) {
        if (s == null)
            return null;
        s = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFKC); // Get into canonical form
        if (this.normalisePunctuation)
            s = this.translate(s, PUNCT_TRANSLATE);
        if (this.normaliseSymbols)
            s = this.translate(s, SYMBOL_TRANSLATE);
        if (this.normaliseAccents)
            s = removeAccents(s);
        if (this.normaliseSpaces)
            s = this.normaliseSpaces(s);
        if (this.normaliseCase)
            s = s.toLowerCase();
        return s.trim();
    }

    /**
     * Generate the code to create this normaliser
     *
     * @return normaliser code
     */
    @Override
    @JsonIgnore
    public String getCreator() {
        StringBuilder builder = new StringBuilder();
        builder.append("new ");
        builder.append(this.getClass().getName());
        builder.append("(\"");
        builder.append(this.getId());
        builder.append("\", ");
        builder.append(this.normaliseSpaces);
        builder.append(", ");
        builder.append(this.normalisePunctuation);
        builder.append(", ");
        builder.append(this.normaliseSpaces);
        builder.append(", ");
        builder.append(this.normaliseAccents);
        builder.append(", ");
        builder.append(this.normaliseCase);
        builder.append(")");
        return builder.toString();
    }
}

