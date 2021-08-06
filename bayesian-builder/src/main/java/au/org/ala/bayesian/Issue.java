package au.org.ala.bayesian;

import java.net.URI;

/**
 * An issue description.
 * <p>
 * This provides a label and a URI, along with any other information that is needed for a flag
 * indicating some sort of problem with processing.
 * The URI is the main feature of the issue and a valid issue requires a URI.
 * </p>
 */
public class Issue extends Identifiable {
    /**
     * Construct an empty issue with an arbitrary identifier.
     */
    public Issue() {
    }

    /**
     * Construct with a defined URI.
     *
     * @param uri The uri
     */
    public Issue(URI uri) {
        super(uri);
    }
}
