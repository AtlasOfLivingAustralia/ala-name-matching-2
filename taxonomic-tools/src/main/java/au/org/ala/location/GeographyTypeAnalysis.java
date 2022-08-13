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

    @Override
    public GeographyType fromString(String value) {
        if (value == null || value.isEmpty())
            return null;
        GeographyType type = NAME_MAP.get(value);
        if (type != null)
            return type;
        return NAME_MAP.get(value.toUpperCase());
    }
}
