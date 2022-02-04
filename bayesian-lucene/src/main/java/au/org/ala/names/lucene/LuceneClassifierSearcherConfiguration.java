package au.org.ala.names.lucene;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

/**
 * Configuration and tuning parameters for the {@link LuceneClassifierSearcher}
 */
@Value
@Builder
public class LuceneClassifierSearcherConfiguration {
    /** Instrument this searcher with JMX */
    @JsonProperty
    @Builder.Default
    private boolean enableJmx = false;
    /** The minimum score for a candidate */
    @JsonProperty
    @Builder.Default
    private float scoreCutoff = 1.0f;
    /** The limit to the number of results returned by a query */
    @JsonProperty
    @Builder.Default
    private int queryLimit = 20;
    /** Cache the classifiers for a specific document ID */
    @JsonProperty
    @Builder.Default
    private boolean cache = true;
    /** The cache size */
    @JsonProperty
    @Builder.Default
    private int cacheSize = 10000;
}
