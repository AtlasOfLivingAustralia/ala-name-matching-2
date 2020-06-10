package au.org.ala.util;

import au.org.ala.names.model.Identifiable;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

public class SimpleIdentifierConverter implements IdentifierConverter {
    public static final IdentifierConverter JAVA_CLASS = new SimpleIdentifierConverter(Style.CAMEL_CASE, true);
    public static final IdentifierConverter JAVA_VARIABLE = new SimpleIdentifierConverter(Style.CAMEL_CASE, false);
    public static final IdentifierConverter LUCENE_FIELD = new SimpleIdentifierConverter(Style.UNDERSCORE, false);

    /** The style to use when converting */
    private Style style;
    /** Should the first letter be captialised */
    private boolean firstLetterUpperCase;

    /**
     * Identifier converter
     *
     * @param style The style for work separation, etc.
     * @param firstLetterUpperCase Is the first letter upper case?
     */
    public SimpleIdentifierConverter(Style style, boolean firstLetterUpperCase) {
        this.style = style;
        this.firstLetterUpperCase = firstLetterUpperCase;
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
        return w.toString();
    }


    public enum Style {
        /** Use camel case to deliniate words */
        CAMEL_CASE,
        /** Use underscores to deliniate words */
        UNDERSCORE
    }
}
