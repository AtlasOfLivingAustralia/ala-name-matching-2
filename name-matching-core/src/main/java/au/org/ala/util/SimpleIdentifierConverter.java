package au.org.ala.util;

import au.org.ala.names.model.Identifiable;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SimpleIdentifierConverter implements IdentifierConverter {
    public static final IdentifierConverter JAVA_CLASS = new SimpleIdentifierConverter(Style.CAMEL_CASE, true, false);
    public static final IdentifierConverter JAVA_VARIABLE = new SimpleIdentifierConverter(Style.CAMEL_CASE, false, true);
    public static final IdentifierConverter LUCENE_FIELD = new SimpleIdentifierConverter(Style.UNDERSCORE, false, false);

    private static final Set<String> RESERVED_WORDS = new HashSet<>(Arrays.asList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
            "class", "const", "continue", "default", "double", "do", "else", "enum",
            "extends", "false", "final", "finally", "float", "for", "goto", "if",
            "implements", "import", "instanceof", "int", "interface", "long", "native", "new",
            "null", "package", "private", "protected", "public", "return", "short", "static",
            "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
            "transient", "true", "try", "void", "volatile", "while"
    ));

    /** The style to use when converting */
    private Style style;
    /** Should the first letter be captialised */
    private boolean firstLetterUpperCase;
    /** Should java keywords be avoided */
    private boolean noKeywords;

    /**
     * Identifier converter
     *
     * @param style The style for work separation, etc.
     * @param firstLetterUpperCase Is the first letter upper case?
     * @param noKeywords True to avoid generating java keywords
     */
    public SimpleIdentifierConverter(Style style, boolean firstLetterUpperCase, boolean noKeywords) {
        this.style = style;
        this.firstLetterUpperCase = firstLetterUpperCase;
        this.noKeywords = noKeywords;
    }

    /**
     * Convert an identifier into the style specified by the converter
     *
     * @param id The identifiable object
     *
     * @return The converted identifier
     */
    @Override
    public String convert(Identifiable id) {
        Reader r = new StringReader(id.getId());
        StringWriter w = new StringWriter(id.getId().length());

        try {
            int ch = r.read();
            boolean capitalise = this.firstLetterUpperCase;
            boolean separate = false;

            if (!Character.isLetter(ch)) {
                w.append(this.firstLetterUpperCase ? 'X' : 'x');
                separate = true;
                capitalise = false;
            }
            while (ch >= 0) {
                if (!Character.isLetterOrDigit(ch)) {
                    separate = true;
                    ch = r.read();
                    continue;
                }
                if (separate == true) {
                   switch (this.style) {
                       case CAMEL_CASE:
                           capitalise = true;
                           break;
                       case UNDERSCORE:
                           w.append('_');
                           break;
                   }
                   separate = false;
                }
                if (Character.isLetter(ch) && capitalise) {
                    w.append((char) Character.toUpperCase(ch));
                    capitalise = false;
                } else
                    w.append((char) Character.toLowerCase(ch));
                int old = ch;
                ch = r.read();
                if (
                        Character.isUpperCase(ch) && (Character.isLowerCase(old) || Character.isDigit(old)) ||
                        (Character.isDigit(old) && !Character.isDigit(ch) ||
                        (Character.isDigit(ch) && !Character.isDigit(old)))) {
                     separate = true;
                }
            }
        } catch (IOException e) {
            w.append("Error");
        }
        if (this.noKeywords) {
            while (RESERVED_WORDS.contains(w.toString().toLowerCase()))
                w.append('_');
        }
        return w.toString();
    }


    public enum Style {
        /** Use camel case to deliniate words */
        CAMEL_CASE,
        /** Use underscores to deliniate words */
        UNDERSCORE
    }
}
