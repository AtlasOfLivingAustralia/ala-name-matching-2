package au.org.ala.names;

import au.org.ala.util.Substitute;
import org.apache.commons.lang3.StringUtils;
import org.gbif.nameparser.api.NameType;
import org.gbif.nameparser.api.Rank;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A Java implementation of the sound ex algorithm supplied by Tony Rees
 * Copied from Taxamatch project. We don't need full taxamatch...
 */
public class TaxonNameSoundEx {
    private static final String RANK_MARKERS = Arrays.stream(Rank.values())
            .map(r -> r.getMarker())
            .filter(Objects::nonNull)
            .map(m -> m.endsWith(".") ? m.substring(0, m.length() - 1) : m)
            .collect(Collectors.joining("|"))
            + "|ssp|spp";
    private static final List<Substitute> BASIC_NORMALISE = Arrays.asList(
            // Correct HTMLised ampersand
            Substitute.allCI("&amp;", "&"),
            // Remove HTML
            Substitute.all("\\<[^>]+\\>", ""),
            // Cultivar gets rid of the sp. nov. in a cultivar since it's sort of implicit
            Substitute.allCI("\\s+(?:" + RANK_MARKERS + ")\\.?\\s+nov\\.?\\s+('[A-Za-z\\s]+'|\"[A-Za-z\\s]+\"|\\([A-Za-z\\s]+\\))(?=\\s+|$)", " $1"),
            // Preserve aff, cf. sp nov markers
            Substitute.allCI("\\s+(" + RANK_MARKERS + ")\\.?\\s+nov\\.?(?=\\s+|$)", " $1 nov"),
            Substitute.allCI("\\s+aff\\.?(?=\\s+|$)", " aff"),
            Substitute.allCI("\\s+(?:cf|cfr|conf)\\.?(?=\\s+|$)", " cf"),
            Substitute.allCI("\\s+(?:s\\.\\s*s\\.|s\\.\\s*str\\.?|sens\\.\\s*str\\.?|sensu\\s*stricto)(?=\\s+|$)", " "),
            Substitute.allCI("\\s+(?:s\\.\\s*l\\.|s\\.\\s*lat\\.?|sens\\.\\s*lat\\.?|sensu\\s*lato)(?=\\s+|$)", " "),
            Substitute.all("\\s+", " "),
            Substitute.all("[^A-Za-z0-9 .]", "")
    );
    private static final List<Substitute> MARKER_NORMALISE = Arrays.asList(
            // Correct HTMLised ampersand
            Substitute.allCI("&amp;", "&"),
            // Remove HTML
            Substitute.all("\\<[^>]+\\>", ""),
            // Cultivar gets rid of the sp. nov. in a cultivar since it's sort of implicit
            Substitute.allCI("\\s+(?:" + RANK_MARKERS + ")\\.?\\s+nov\\.?\\s+('[A-Za-z\\s]+'|\"[A-Za-z\\s]+\"|\\([A-Za-z\\s]+\\))(?=\\s+|$)", " $1"),
            // Preserve aff, cf. sp nov markers
            Substitute.allCI("\\s+(" + RANK_MARKERS + ")\\.?\\s+nov\\.?(?=\\s+|$)", " $1 nov"),
            Substitute.allCI("\\s+aff\\.?(?=\\s+|$)", " aff"),
            Substitute.allCI("\\s+(?:cf|cfr|conf)\\.?(?=\\s+|$)", " cf"),
            Substitute.allCI("\\s+(?:s\\.\\s*s\\.|s\\.\\s*str\\.?|sens\\.\\s*str\\.?|sensu\\s*stricto)(?=\\s+|$)", " "),
            Substitute.allCI("\\s+(?:s\\.\\s*l\\.|s\\.\\s*lat\\.?|sens\\.\\s*lat\\.?|sensu\\s*lato)(?=\\s+|$)", " "),
            // Remove embedded rank marker unless they're an integral part of the name
            Substitute.allCI("\\s+(?:" + RANK_MARKERS + ")\\.?(?!\\s+nov)(?=\\s+|$)", " "),
            Substitute.all("\\s+", " "),
            Substitute.all("[^A-Za-z .]", "")
    );

    private static String translate(String source, String transSource, String transTarget) {
        String result = source;

        while (transSource.length() > transTarget.length()) {
            transTarget += " ";
        }
        for (int i = 0; i < transSource.length(); i++) {
            result = result.replace(transSource.charAt(i), transTarget.charAt(i));
        }
        return result;
    }


    public static String normalize(String str, NameType nameType) {

        if (str == null) return null;

        String output = str;

        // Common letter substitutes
        output = output.toUpperCase();
        output = translate(output, "\u00c1\u00c9\u00cd\u00d3\u00da\u00c0\u00c8\u00cc\u00d2\u00d9" +
                "\u00c2\u00ca\u00ce\u00d4\u00db\u00c4\u00cb\u00cf\u00d6\u00dc\u00c3\u00d1\u00d5" +
                "\u00c5\u00c7\u00d8", "AEIOUAEIOUAEIOUAEIOUANOACO");
        output = output.replace("\u00c6", "AE");

        // Normalise markers if a scientific name
        if (nameType == NameType.SCIENTIFIC)
            output = MARKER_NORMALISE.stream().reduce(output, (s, m) -> m.apply(s), (a, b) -> b).trim();
        else
            output = BASIC_NORMALISE.stream().reduce(output, (s, m) -> m.apply(s), (a, b) -> b).trim();
        output = output.toUpperCase();

        output = StringUtils.trimToNull(output);

        return output;
    }

    public static String treatWord(String str, Rank rank, NameType nameType, boolean epithet) {
        char startLetter;
        str = normalize(str, nameType);
        if (StringUtils.isBlank(str))
            return null;
        // Do some selective replacement on the leading letter/s only:
        StringBuilder builder = new StringBuilder(str.length());
        String[] segments = str.split(" ");
        for (String temp : segments) {
            if (StringUtils.isBlank(temp))
                continue;
            if (temp.startsWith("AE")) {
                temp = "E" + temp.substring(2);
            } else if (temp.startsWith("CN")) {
                temp = "N" + temp.substring(2);
            } else if (temp.startsWith("CT")) {
                temp = "T" + temp.substring(2);
            } else if (temp.startsWith("CZ")) {
                temp = "C" + temp.substring(2);
            } else if (temp.startsWith("DJ")) {
                temp = "J" + temp.substring(2);
            } else if (temp.startsWith("EA")) {
                temp = "E" + temp.substring(2);
            } else if (temp.startsWith("EU")) {
                temp = "U" + temp.substring(2);
            } else if (temp.startsWith("GN")) {
                temp = "N" + temp.substring(2);
            } else if (temp.startsWith("KN")) {
                temp = "N" + temp.substring(2);
            } else if (temp.startsWith("MC")) {
                temp = "MAC" + temp.substring(2);
            } else if (temp.startsWith("MN")) {
                temp = "N" + temp.substring(2);
            } else if (temp.startsWith("OE")) {
                temp = "E" + temp.substring(2);
            } else if (temp.startsWith("QU")) {
                temp = "Q" + temp.substring(2);
            } else if (temp.startsWith("PS")) {
                temp = "S" + temp.substring(2);
            } else if (temp.startsWith("PT")) {
                temp = "T" + temp.substring(2);
            } else if (temp.startsWith("TS")) {
                temp = "S" + temp.substring(2);
            } else if (temp.startsWith("WR")) {
                temp = "R" + temp.substring(2);
            } else if (temp.startsWith("X")) {
                temp = "Z" + temp.substring(1);
            }
            // Now keep the leading character, then do selected "soundalike" replacements. The
            // following letters are equated: AE, OE, E, U, Y and I; IA and A are equated;
            // K and C; Z and S; and H is dropped. Also, A and O are equated, MAC and MC are equated, and SC and S.
            startLetter = temp.charAt(0); // quarantine the leading letter
            temp = temp.substring(1); // snip off the leading letter
            // now do the replacements
            temp = temp.replaceAll("AE", "I");
            temp = temp.replaceAll("IA", "A");
            temp = temp.replaceAll("OE", "I");
            temp = temp.replaceAll("OI", "A");
            temp = temp.replaceAll("SC", "S");
            temp = temp.replaceAll("E", "I");
            temp = temp.replaceAll("O", "A");
            temp = temp.replaceAll("U", "I");
            temp = temp.replaceAll("Y", "I");
            temp = temp.replaceAll("K", "C");
            temp = temp.replaceAll("Z", "C");
            temp = temp.replaceAll("H", "");
            // add back the leading letter
            temp = startLetter + temp;
            // now drop any repeated characters (AA becomes A, BB or BBB becomes B, etc.)
            temp = temp.replaceAll("(\\w)\\1+", "$1");

            // Specific or subspecific epithets get endings
            if (epithet) {
                if (temp.endsWith("IS")) {
                    temp = temp.substring(0, temp.length() - 2) + "A";
                } else if (temp.endsWith("IM")) {
                    temp = temp.substring(0, temp.length() - 2) + "A";
                } else if (temp.endsWith("AS")) {
                    temp = temp.substring(0, temp.length() - 2) + "A";
                }
                //temp = temp.replaceAll("(\\w)\\1+", "$1");
            }
            // Following words for species level ranks are treated as epithets
            if (builder.length() > 0)
                builder.append(' ');
            builder.append(temp);
            epithet = epithet || ((rank == null || rank.isSpeciesOrBelow()) && builder.length() > 0);
        }
        return builder.length() == 0 ? null : builder.toString();
    }


    /**
     * Returns the SoundEx for the source string
     *
     * @param source String to get the sound ex of
     * @return The sound ex string
     */
    public String soundEx(String source) {
        String temp = source.toUpperCase();
        temp = selectiveReplaceFirstChar(temp);
        temp = selectiveReplaceWithoutFirstChar(temp);
        temp = removeRepeatedChars(temp);
        temp = alphabetiseWordsIgnoringFirstLetter(temp);

        return temp;
    }

    /**
     * Ignoring the first letter, alphabetise each word
     */
    String alphabetiseWordsIgnoringFirstLetter(String source) {
        StringTokenizer st = new StringTokenizer(source, " ");
        StringBuffer sb = new StringBuffer();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            char[] chars = token.toCharArray();
            List<Character> charList = new LinkedList<Character>();
            for (int i = 1; i < chars.length; i++) {
                charList.add(chars[i]);
            }
            Collections.sort(charList);
            sb.append(chars[0]);
            for (Character c : charList) {
                sb.append(c);
            }
            if (st.hasMoreTokens()) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * Removes repeated characters
     * Can't get the regex version working so pretty primitive...
     */
    String removeRepeatedChars(String source) {
        StringBuffer sb = new StringBuffer();
        char c = ' ';
        for (int i = 0; i < source.length(); i++) {
            char sourceC = source.charAt(i);
            if (sourceC != c) {
                sb.append(sourceC);
            }
            c = sourceC;
        }
        return sb.toString();
    }

    /**
     * Ignoring the first character, selectively replace sound alikes
     */
    String selectiveReplaceWithoutFirstChar(String source) {
        if (source.length() > 1) {
            String temp = source.substring(1);
            temp = temp.replaceAll("AE", "I");
            temp = temp.replaceAll("IA", "A");
            temp = temp.replaceAll("OE", "I");
            temp = temp.replaceAll("OI", "A");
            temp = temp.replaceAll("MC", "MAC");
            temp = temp.replaceAll("SC", "S");
            temp = temp.replaceAll("EOUYKZH", "IAIICS");

            return source.charAt(0) + temp;
        } else {
            return source;
        }
    }

    /**
     * Selectively replaces the first character
     */
    String selectiveReplaceFirstChar(String source) {
        if (source.startsWith("Æ")) {
            return source.replaceFirst("Æ", "E");

        } else if (source.startsWith("AE")) {
            return source.replaceFirst("AE", "E");

        } else if (source.startsWith("CN")) {
            return source.replaceFirst("CN", "N");

        } else if (source.startsWith("CT")) {
            return source.replaceFirst("CT", "T");

        } else if (source.startsWith("CZ")) {
            return source.replaceFirst("CZ", "C");

        } else if (source.startsWith("DJ")) {
            return source.replaceFirst("DJ", "J");

        } else if (source.startsWith("EA")) {
            return source.replaceFirst("EA", "E");

        } else if (source.startsWith("EU")) {
            return source.replaceFirst("EU", "U");

        } else if (source.startsWith("GN")) {
            return source.replaceFirst("GN", "N");

        } else if (source.startsWith("KN")) {
            return source.replaceFirst("KN", "N");

        } else if (source.startsWith("MN")) {
            return source.replaceFirst("MN", "N");

        } else if (source.startsWith("OE")) {
            return source.replaceFirst("OE", "E");

        } else if (source.startsWith("QU")) {
            return source.replaceFirst("QU", "Q");

        } else if (source.startsWith("PS")) {
            return source.replaceFirst("PS", "S");

        } else if (source.startsWith("PT")) {
            return source.replaceFirst("PT", "T");

        } else if (source.startsWith("TS")) {
            return source.replaceFirst("TS", "S");

        } else if (source.startsWith("X")) {
            return source.replaceFirst("X", "Z");

        } else return source;
    }
}