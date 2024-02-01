package au.org.ala.bayesian;

import java.io.File;
import java.io.IOException;

/**
 * Something that handles special cases of analysis
 *
 * @param <R> the type of thing that the analysis returs.
 */
public interface SpecialCase<R> {
    /**
     * Get a special case for a supplied name, if needed
     *
     * @param name The name
     *
     * @return The special case or null for not a special case
     */
    public R get(String name);

    /**
     * Store this special case analysis in a directory.
     * <p>
     * Used to propagate special case information into a matching index.
     * The special case can
     * </p>
     * @param directory The directory to place any stored files.
     *
     * @return The file that contains the configuration/data
     * @throws IOException
     */
    public File store(File directory) throws IOException;
}
