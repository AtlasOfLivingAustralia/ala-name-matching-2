package au.org.ala.util;


import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Substitutions look for a pattern and replace that pattern with a replacement string.
 */
@Value
public class Substitute implements Function<String, String> {
    private static final Logger logger = LoggerFactory.getLogger(Substitute.class);

    Pattern orginal;
    String replace;
    boolean first;

    protected Substitute(Pattern orginal, String replace, boolean first) {
        this.orginal = orginal;
        this.replace = replace;
        this.first = first;
    }

    /**
     * Create a substitute that will replace the first matching
     *
     * @param original The regular expresion to search for.
     * @param replace The string to replace it with
     *
     * @return A new first-match substritute
     */
    public static Substitute first(String original, String replace) {
        return new Substitute(Pattern.compile(original), replace, true);
    }

    /**
     * Create a substitute that will replace the first matching without case sensitivity
     *
     * @param original The regular expresion to search for.
     * @param replace The string to replace it with
     *
     * @return A new first-match substritute
     */
    public static Substitute firstCI(String original, String replace) {
        return new Substitute(Pattern.compile(original, Pattern.CASE_INSENSITIVE), replace, true);
    }

    /**
     * Create a substitute that will replace all matching instances
     *
     *
     * @param original The regular expresion to search for.
     * @param replace The string to replace it with
     *
     * @return A new all-match substritute
     */
    public static Substitute all(String original, String replace) {
        return new Substitute(Pattern.compile(original), replace, false);
    }


    /**
     * Create a substitute that will replace all matching instances
     *
     *
     * @param original The regular expresion to search for.
     * @param replace The string to replace it with
     *
     * @return A new all-match substritute
     */
    public static Substitute allCI(String original, String replace) {
        return new Substitute(Pattern.compile(original, Pattern.CASE_INSENSITIVE), replace, false);
    }

    /**
     * If the pattern matches then replace all elements in the
     *
     * @param source the function argument
     * @return the function result
     */
    @Override
    public String apply(String source) {
        try {
            if (source == null)
                return null;
            Matcher matcher = this.orginal.matcher(source);
            if (!matcher.find())
                return source;
            return this.first ? matcher.replaceFirst(this.replace) : matcher.replaceAll(this.replace);
        } catch (Exception ex) {
            logger.error("Unable to handle replacement of \"" + source + "\" with \"" + this.replace + "\" on " + this.orginal);
            throw ex;
        }
    }
}
