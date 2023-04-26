package au.org.ala.bayesian.derivation;

import org.apache.commons.codec.language.Soundex;

import java.text.Normalizer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Generate a soundexed version of a phrase, taking care of multiple words and accents.
 */
public class SoundexGenerator {
    private final Pattern SPLITTER = Pattern.compile("[^A-Za-z]+");

    private final Soundex soundex = new Soundex();

    /**
     * Convert a stream of words into concatenated soundexed values.
     *
     * @param value The value
     *
     * @return The soundexed form
     */
    public String soundex(String value) {
        if (value == null || value.isEmpty())
            return null;
        value = Normalizer.normalize(value, Normalizer.Form.NFD); // Break out accents
        int i, len = value.length();
        StringBuilder builder = new StringBuilder();

        for (i = 0; i < len; i++) {
            char ch = value.charAt(i);
            if (ch < 128)
                builder.append(ch);
        }
        return SPLITTER.splitAsStream(builder.toString()).map(n -> this.soundex.soundex(n)).collect(Collectors.joining());
    }
}
