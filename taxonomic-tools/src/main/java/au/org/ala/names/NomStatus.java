package au.org.ala.names;

import lombok.NonNull;
import lombok.Value;
import org.gbif.api.vocabulary.NomenclaturalStatus;
import org.gbif.common.parsers.NomStatusParser;
import org.gbif.common.parsers.core.ParseResult;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A grouop of nomenclatural status indicators.
 */
@Value
public class NomStatus {
    private static final Pattern DIVIDER = Pattern.compile("[,|;]");
    private static NomStatusParser PARSER = NomStatusParser.getInstance();

    private List<NomenclaturalStatus> status;

    /**
     * Construct for a list of status elements.
     *
     * @param status The status elements
     */
    public NomStatus(@NonNull List<NomenclaturalStatus> status) {
        this.status = status;
    }

    /**
     * Construct for a list of status codes.
     *
     * @param status The status codes
     */
    public NomStatus(NomenclaturalStatus... status) {
        this(Arrays.asList(status));
    }

    /**
     * Construct from a string
     *
     * @param status A list of status elements, separated by commas, bars or semi-colons
     */
    public NomStatus(String status) {
        this.status = DIVIDER.splitAsStream(status)
                .map(NomStatus::parse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Emptyness check.
     *
     * @return True if there are no status elements
     */
    public boolean isEmpty() {
        return this.status.isEmpty();
    }

    /**
     * Equality test.
     *
     * @param o The other object
     *
     * @return True if a nom status and the status lists match.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NomStatus nomStatus = (NomStatus) o;
        return status.equals(nomStatus.status);
    }

    /**
     * Hash code
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    /**
     * Build the canonical form of the status set
     *
     * @return A string built out of status names
     */
    public String canonical() {
        return this.status.stream()
                .map(s -> s.getAbbreviatedLabel() != null ? s.getAbbreviatedLabel() : s.name().toLowerCase())
                .collect(Collectors.joining(", "));
    }

    /**
     * Try and parse a nomenclatural status string, including the
     *
     * @param value The nomenclatural status string
     *
     * @return An optional
     */
    public static Optional<NomenclaturalStatus> parse(String value) {
        if (value == null)
            return Optional.empty();
        value = value.trim();
        if (value.isEmpty())
            return Optional.empty();
        try {
            return Optional.of(NomenclaturalStatus.valueOf(value.toUpperCase()));
        } catch (IllegalArgumentException _ex) {
        }
        ParseResult<NomenclaturalStatus> pr = PARSER.parse(value);
        return pr.isSuccessful() ? Optional.of(pr.getPayload()) : Optional.empty();
    }
}
