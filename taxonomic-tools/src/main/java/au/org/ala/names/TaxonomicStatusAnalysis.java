package au.org.ala.names;

import au.org.ala.bayesian.analysis.EnumAnalysis;
import au.org.ala.vocab.TaxonomicStatus;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Analysis based on taxonomic status.
 */
public class TaxonomicStatusAnalysis extends EnumAnalysis<TaxonomicStatus> {
    /** Taxonomic status to treat as equivalent */
    private static final List<List<TaxonomicStatus>> EQUIVALENCE_CLASSES = Arrays.asList(
            Arrays.asList(TaxonomicStatus.accepted, TaxonomicStatus.inferredAccepted),
            // Don't really care what sort of synonym
            Arrays.asList(TaxonomicStatus.synonym, TaxonomicStatus.inferredSynonym, TaxonomicStatus.heterotypicSynonym, TaxonomicStatus.homotypicSynonym, TaxonomicStatus.objectiveSynonym, TaxonomicStatus.subjectiveSynonym, TaxonomicStatus.proParteSynonym),
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
     * Parse this value and return a suitably interpreted object.
     * <p>
     * By default, tries the enum name to all upper case and
     * if that doesn't work, tries the name.
     * </p>
     *
     * @param value The value
     * @return The parsed value
     */
    @Override
    public TaxonomicStatus fromString(String value) {
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
        if (EQUIVALENCES.computeIfAbsent(value1, k -> new HashSet<>()).contains(value2))
            return true;
        return false;
    }

}
