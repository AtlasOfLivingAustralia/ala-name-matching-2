package au.org.ala.bayesian;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.gbif.dwc.terms.Term;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A list of issues associated with some operation.
 * <p>
 * Issues are generally regarded as warnings, rather than errors, indicating
 * possible problems with processing and matching evidence.
 * Issues are stored as URI-based {@link Term}s, to provide an extensible way
 * of recording issues.
 * </p>
 */
public class Issues extends HashSet<Term> {
    public Issues() {
        super();
    }

    /**
     * Merge two sets of issues.
     * <p>
     * If this issues or the other is empty or a subset, the other issue set is returned.
     * </p>
     *
     * @param other The other issues to merge
     *
     * @return An issues collection representing the merged issues list
     */
    public Issues merge(Issues other) {
        if (this.isEmpty() || other.containsAll(this))
            return other;
        if (other.isEmpty() || this.containsAll(other))
            return this;
        Issues merged = new Issues();
        merged.addAll(this);
        merged.addAll(other);
        return merged;
    }
}
