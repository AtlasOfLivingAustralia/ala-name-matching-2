package au.org.ala.names;

import au.org.ala.bayesian.Fidelity;
import au.org.ala.bayesian.analysis.EnumAnalysis;
import au.org.ala.bayesian.fidelity.SimpleFidelity;
import au.org.ala.vocab.TaxonomicStatus;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Analysis based on taxonomic status.
 */
public class TaxonomicStatusAnalysis extends EnumAnalysis<TaxonomicStatus, Object> {
    /** Taxonomic status to treat as equivalent */
    private static final List<List<TaxonomicStatus>> EQUIVALENCE_CLASSES = Arrays.asList(
            Arrays.asList(TaxonomicStatus.accepted, TaxonomicStatus.unreviewed, TaxonomicStatus.inferredAccepted),
            // Don't really care what sort of synonym
            Arrays.asList(TaxonomicStatus.synonym, TaxonomicStatus.unreviewedSynonym, TaxonomicStatus.inferredSynonym, TaxonomicStatus.heterotypicSynonym, TaxonomicStatus.homotypicSynonym, TaxonomicStatus.objectiveSynonym, TaxonomicStatus.subjectiveSynonym, TaxonomicStatus.proParteSynonym),
            Arrays.asList(TaxonomicStatus.excluded, TaxonomicStatus.inferredExcluded),
            Arrays.asList(TaxonomicStatus.unplaced, TaxonomicStatus.inferredUnplaced, TaxonomicStatus.incertaeSedis, TaxonomicStatus.speciesInquirenda),
            Arrays.asList(TaxonomicStatus.invalid, TaxonomicStatus.inferredInvalid),
            Arrays.asList(TaxonomicStatus.misapplied),
            Arrays.asList(TaxonomicStatus.miscellaneousLiterature)
    );
    /** Lookup for equivalence classes */
    private static final Map<TaxonomicStatus, Set<TaxonomicStatus>> EQUIVALENCES = EQUIVALENCE_CLASSES.stream()
            .collect(
                    HashMap::new,
                    (m, eq) -> eq.forEach(s -> m.computeIfAbsent(s, k -> new HashSet<>()).addAll(eq)),
                    (m1, m2) -> m2.entrySet().forEach(e -> m1.computeIfAbsent(e.getKey(), k -> new HashSet<>()).addAll(e.getValue()))
                    );
    /** Map strings onto actual terms */
    private static final Map<String, TaxonomicStatus> NAME_MAP = Arrays.stream(TaxonomicStatus.values())
            .collect(Collectors.toMap(s -> s.name().toLowerCase(), s -> s));

    /**
     * Default constructor
     */
    public TaxonomicStatusAnalysis() {
        super(TaxonomicStatus.class);
    }

    /**
     * Convert this object into a string for storage
     *
     * @param value The value to convert
     * @return The stringified value (null should return null)
      */
    @Override
    public String toStore(TaxonomicStatus value) {
        if (value == null)
            return null;
        return value.name();
    }

    /**
     * Compute a fidelity measure for this type of object.
     * <p>
     * If status is within the same class, then fidelity is at leaset 0.5
     * </p>
     *
     * @param original The original value
     * @param actual   The actual value
     * @return The computed fidelity
     */
    @Override
    public Fidelity<TaxonomicStatus> buildFidelity(TaxonomicStatus original, TaxonomicStatus actual) {
        if (original == null)
            return null;
        double fidelity = 0.0;
        if (actual != null) {
            if (original.equals(actual))
                fidelity = 1.0;
            else {
                Boolean equivalent = this.equivalent(original, actual);
                if (equivalent != null && equivalent.booleanValue())
                    fidelity = 0.5;
            }
        }
        return new SimpleFidelity<>(original, actual, fidelity);
    }

    /**
     * Parse this value and return a suitably interpreted object.
     * <p>
     * By default, tries the enum name to all upper case and
     * if that doesn't work, tries the name.
     * </p>
     *
     * @param value The value
     * @param context Unused context
     * @return The parsed value
     */
    @Override
    public TaxonomicStatus fromString(String value, Object context) {
        if (value == null || value.isEmpty())
            return null;
        value = value.replaceAll("\\s", "").toLowerCase();
        return NAME_MAP.get(value);
    }

    /**
     * Test for equivalence.
     * <p>
     * At the moment, two enums being equal is equivalent.
     * </p>
     *
     * @param value1 The first value to test
     * @param value2 The second value to test
     *
     * @return Null if not comparable, true if equivalent, false otherwise.
     */
    @Override
    public Boolean equivalent(TaxonomicStatus value1, TaxonomicStatus value2) {
        if (value1 == null || value2 == null)
            return null;
        if  (value1 == value2)
            return true;
        return EQUIVALENCES.computeIfAbsent(value1, k -> new HashSet<>()).contains(value2);
    }

}
