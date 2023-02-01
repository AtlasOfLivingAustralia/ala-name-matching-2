package au.org.ala.names;

import au.org.ala.bayesian.ClassificationMatcherConfiguration;
import au.org.ala.names.lucene.LuceneClassifierSearcherConfiguration;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collections;
import java.util.List;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ALANameSearcherConfiguration {
    /** The index version */
    @JsonProperty
    @Builder.Default
    private String version = null;
    /** The index source directory */
    @JsonProperty
    @Builder.Default
    private File index = new File("/data/namematching");
    /** The work directory */
    @JsonProperty
    @Builder.Default
    private File work = new File("/data/tmp");
    /** The location of the Linnaean name index, constructed from the  */
    @JsonProperty
    @Builder.Default
    private File linnaean = null;
    /** The location of the vernacular name index */
    @JsonProperty
    @Builder.Default
    private File vernacular = null;
    /** The location of the location index */
    @JsonProperty
    @Builder.Default
    private File location = null;
    /** The location of the suggestion index */
    @JsonProperty
    @Builder.Default
    private File suggester = null;
    /** The searcher configuration */
    @JsonProperty
    @Builder.Default
    private LuceneClassifierSearcherConfiguration searcherConfiguration = LuceneClassifierSearcherConfiguration.builder().build();
    /** The matcher configuration */
    @JsonProperty
    @Builder.Default
    private ClassificationMatcherConfiguration matcherConfiguration = ClassificationMatcherConfiguration.builder().build();
    /** The list of localities for which we have accurate distribution information */
    @JsonProperty
    @Builder.Default
    private List<String> localities = Collections.emptyList();

    /**
     * Build a directory from a supplied base, type and version number.
     *
     * @param base The base directory
     * @param type The type of directory
     * @param version The version, if present
     *
     * @return A directory path
     */
    private File buildDirectory(@Nullable File dir, @NonNull File base, @NonNull String type, String version) {
        if (dir == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(type);
            if (version != null) {
                sb.append("-");
                sb.append(version);
            }
            dir = new File(sb.toString());
        }
        return dir.isAbsolute() ? dir : new File(base, dir.getPath());
    }

    /**
     * Get the linnaean index location.
     * <p>
     * If not explicitly set, this is built from the source directory and the version.
     * </p>
     *
     * @return The linnaean index location
     */
    @JsonIgnore
    public File getLinnaean() {
        return this.buildDirectory(this.linnaean, this.index, "linnaean", this.version);
    }

    /**
     * Get the vernacular index location.
     * <p>
     * If not explicitly set, this is built from the source directory and the version.
     * </p>
     *
     * @return The vernacular index location
     */
    @JsonIgnore
    public File getVernacular() {
        return this.buildDirectory(this.vernacular, this.index, "vernacular", this.version);
    }

    /**
     * Get the location index location.
     * <p>
     * If not explicitly set, this is built from the source directory and the version.
     * </p>
     *
     * @return The location index location
     */
    @JsonIgnore
    public File getLocation() {
        return this.buildDirectory(this.location, this.index, "location", this.version);
    }

    /**
     * Get the suggestion index location.
     * <p>
     * If not explicitly set, this is built from the source directory and the version.
     * </p>
     *
     * @return The suggestion index location
     */
    @JsonIgnore
    public File getSuggester() {
        return this.buildDirectory(this.suggester, this.work, "suggester", this.version);
    }
}
