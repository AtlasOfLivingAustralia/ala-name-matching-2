package au.org.ala.location;

import au.org.ala.bayesian.Fidelity;
import au.org.ala.bayesian.analysis.EnumAnalysis;
import au.org.ala.bayesian.fidelity.SimpleFidelity;
import au.org.ala.vocab.GeographyType;
import au.org.ala.vocab.TaxonomicStatus;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Analysis based on taxonomic status.
 */
public class GeographyTypeAnalysis extends EnumAnalysis<GeographyType> {
    /** Geography types to treat as equivalent */
    private static final List<List<GeographyType>> EQUIVALENCE_CLASSES = Arrays.asList(
            Arrays.asList(GeographyType.municipality, GeographyType.county),
            Arrays.asList(GeographyType.county, GeographyType.stateProvince),
            Arrays.asList(GeographyType.stateProvince, GeographyType.country, GeographyType.island, GeographyType.islandGroup),
            Arrays.asList(GeographyType.islandGroup, GeographyType.waterBody),
            Arrays.asList(GeographyType.other, GeographyType.municipality, GeographyType.county, GeographyType.stateProvince, GeographyType.country, GeographyType.continent, GeographyType.island, GeographyType.islandGroup, GeographyType.waterBody)
    );
    /** Lookup for equivalence classes */
    private static final Map<GeographyType, Set<GeographyType>> EQUIVALENCES = new HashMap<>();

    static {
        for (List<GeographyType> eq: EQUIVALENCE_CLASSES) {
            GeographyType base = null;
            for (GeographyType gt: eq) {
                if (base == null) {
                    base = gt;
                } else {
                    EQUIVALENCES.computeIfAbsent(base, k -> new HashSet<>()).add(gt);
                    EQUIVALENCES.computeIfAbsent(gt, k -> new HashSet<>()).add(base);
                }
            }
        }
    }

    /** Map strings onto actual terms */
    private static final Map<String, GeographyType> NAME_MAP = Arrays.stream(GeographyType.values())
            .flatMap(gt -> {
                        Map<String, GeographyType> values = new HashMap<>();
                        values.put(gt.simpleName(), gt);
                        values.put(gt.simpleName().toLowerCase(), gt);
                        values.put(gt.simpleName().toUpperCase(), gt);
                        values.put(gt.qualifiedName(), gt);
                        for (String alt : gt.alternativeNames()) {
                            values.put(alt, gt);
                            values.put(alt.toLowerCase(), gt);
                            values.put(alt.toUpperCase(), gt);
                        }
                        return values.entrySet().stream();
                    }
            )
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

    /**
     * Default constructor
     */
    public GeographyTypeAnalysis() {
        super(GeographyType.class);
    }

    /**
     * Convert this object into a string for storage
     *
     * @param value The value to convert
     * @return The stringified value (null should return null)
      */
    @Override
    public String toStore(GeographyType value) {
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
    public Fidelity<GeographyType> buildFidelity(GeographyType original, GeographyType actual) {
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
     * @return The parsed value
     */
    @Override
    public GeographyType fromString(String value) {
        if (value == null || value.isEmpty())
            return null;
        GeographyType type = NAME_MAP.get(value);
        if (type != null)
            return type;
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
    public Boolean equivalent(GeographyType value1, GeographyType value2) {
        if (value1 == null || value2 == null)
            return null;
        if  (value1 == value2)
            return true;
        return EQUIVALENCES.computeIfAbsent(value1, k -> new HashSet<>()).contains(value2);
    }


}
