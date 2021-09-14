package au.org.ala.util;

import au.org.ala.bayesian.Identifiable;
import org.apache.commons.lang3.StringUtils;
import org.gbif.dwc.terms.Term;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class TermIdentifierConverter implements IdentifierConverter {
    /** A bit set of allowed characters */
    private BitSet characterCheck;
    /** The maximum character seen */
    private int maxCh;
    /** The allowed character set */
    private Predicate<String> allowedCharacter;
    /** The replacement for invalid characters */
    private String invalidReplacement;
    /** Append an index to avoid collisions */
    private boolean avoidCollisions;
    /** The prefix to add */
    private String prefix;
    /** The suffix to add */
    private String suffix;
    /** The map of existing conversions */
    private Map<Term, String> conversions;

    public TermIdentifierConverter(String allowedCharactersRegex, String invalidReplacement, boolean avoidCollisions, String prefix, String suffix) {
        this.characterCheck = new BitSet(127);
        this.maxCh = -1;
        this.allowedCharacter = Pattern.compile(allowedCharactersRegex).asPredicate();
        this.invalidReplacement = invalidReplacement;
        this.avoidCollisions = avoidCollisions;
        this.prefix = prefix;
        this.suffix = suffix;
        this.conversions = new HashMap<>();
    }

    @Override
    public String convert(Identifiable id) {
        Term term = id.getTerm();
        synchronized (this.conversions) {
            String identifier = this.conversions.get(term);
            if (identifier != null)
                return identifier;
            identifier = term.prefix() == null ? term.simpleName() : term.prefixedName();
            identifier = StringUtils.stripAccents(identifier);
            Reader r = new StringReader(identifier);
            StringWriter w = new StringWriter(term.prefixedName().length());
            if (this.prefix != null)
                w.append(this.prefix);
            boolean skip = false;
            int ch;
            try {
                while ((ch = r.read()) >= 0) {
                    if (this.maxCh < ch) {
                        for (int i = this.maxCh + 1; i <= ch; i++) {
                            if (allowedCharacter.test(Character.toString((char) i)))
                                this.characterCheck.set(i);
                            else
                                this.characterCheck.clear(i);
                        }
                        this.maxCh = ch;
                    }
                    if (this.characterCheck.get(ch)) {
                        w.append((char) ch);
                        skip = false;
                    } else {
                        if (!skip)
                            w.append(this.invalidReplacement);
                        skip = true;
                    }
                }
            } catch (IOException e) {
                if (w.toString().isEmpty())
                    w.append("invalid");
            }
            if (this.suffix != null)
                w.append(this.suffix);
            String base = w.toString();
            String discriminator = "";
            int index = 0;
            do {
                identifier = base + discriminator;
                discriminator = this.invalidReplacement + (index > 0 ? Integer.toString(index) : "");
                index++;
            } while (this.avoidCollisions && this.conversions.containsValue(identifier));
            this.conversions.put(term, identifier);
            return identifier;
        }
    }
}
