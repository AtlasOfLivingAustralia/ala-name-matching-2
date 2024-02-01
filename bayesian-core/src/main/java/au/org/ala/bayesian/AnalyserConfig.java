package au.org.ala.bayesian;

import au.org.ala.util.JsonUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration options for a name analyser.
 */
@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class AnalyserConfig {
    private static final Logger logger = LoggerFactory.getLogger(AnalyserConfig.class);

    /**
     * The default file name to store configurations to
     */
    public static final String DEFAULT_CONFIG_FILE_NAME = "analyser-config.json";

    /**
     * The source of any special case data that the anayser might need.
     * <p>
     * A URL to an analyser-specific resource.
     * If null, the default, then no special case information is present.
     */
    @JsonProperty
    @Builder.Default
    private URL specialCases = null;

    /**
     * Store the configuration.
     *
     * @param writer The writer to store the configuration to
     * @throws IOException if unable to write the configuration
     */
    public void store(Writer writer) throws IOException {
        JsonUtils.createMapper().writeValue(writer, this);
    }

    /**
     * Create a configuration relative to a directory.
     *
     * @param directory The directory
     * @return The relativised configuration
     * @throws IOException if unable to relativise the configuration
     */
    public AnalyserConfig relative(File directory) throws IOException {
        URL rebasedSpecialCases = this.specialCases;
        if (rebasedSpecialCases != null && rebasedSpecialCases.getProtocol().equals("file")) {
            try {
                Path sp = Paths.get(rebasedSpecialCases.getPath()).normalize();
                Path base = directory.toPath().normalize();
                if (sp.startsWith(base))
                    rebasedSpecialCases = new URL("file:" + base.relativize(sp).toString());
            } catch (Exception ex) {
                throw new IOException("Unable to rebase " + rebasedSpecialCases + " against " + directory, ex);
            }
        }
        return AnalyserConfig.builder()
                .specialCases(rebasedSpecialCases)
                .build();
    }


    /**
     * Create a configuration with absolute paths.
     *
     *
     * @param directory The directory that forms the base
     *
     * @return The absolute
     * configuration
     * @throws IOException if unable to make the configuration absolute
     */
    public AnalyserConfig absolute(File directory) throws IOException {
        URL rebasedSpecialCases = this.specialCases;
        if (rebasedSpecialCases != null && rebasedSpecialCases.getProtocol().equals("file")) {
            try {
                Path sp = Paths.get(rebasedSpecialCases.getPath()).normalize();
                Path base = directory.toPath().normalize();
                rebasedSpecialCases = base.resolve(sp).toUri().toURL();
            } catch (Exception ex) {
                throw new IOException("Unable to rebase " + rebasedSpecialCases + " against " + directory, ex);
            }
        }
        return AnalyserConfig.builder()
                .specialCases(rebasedSpecialCases)
                .build();
    }

    /**
     * Load an analyser configuration from a directory.
     *
     * @param directory The directory
     *
     * @return Either the configuration, made absolute to the directory or a default configutation
     *
     * @throws IOException if unable to read the configuation
     */
    public static AnalyserConfig load(File directory) throws IOException {
        File configFile = new File(directory, DEFAULT_CONFIG_FILE_NAME);
        if (!configFile.canRead()) {
            logger.info("No analyser configuration in " + directory + " using default");
            return AnalyserConfig.builder().build();
        }
        ObjectMapper mapper = JsonUtils.createMapper();
        AnalyserConfig config = mapper.readValue(configFile, AnalyserConfig.class);
        return config.absolute(directory);
    }
}
