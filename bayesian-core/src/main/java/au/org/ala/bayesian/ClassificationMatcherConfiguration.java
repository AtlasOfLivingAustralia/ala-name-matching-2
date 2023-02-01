package au.org.ala.bayesian;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Configuration information for a {@link au.org.ala.bayesian.ClassificationMatcher}
 */
@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ClassificationMatcherConfiguration {
    /** Make the classification matcher visible through JMX */
    @JsonProperty
    @Builder.Default
    private boolean enableJmx = false;
    /** Instrument the classification matcher and collect statistics */
    @JsonProperty
    @Builder.Default
    private boolean statistics = false;
    /** The size of secondary caches, sych as a quick kingdom lookup. Defaults to 100000 */
    @JsonProperty
    @Builder.Default
    private int secondaryCacheSize = 100000;
}
