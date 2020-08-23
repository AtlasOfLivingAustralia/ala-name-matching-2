package au.org.ala.bayesian;

import au.org.ala.vocab.BayesianTerm;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.gbif.dwc.terms.Term;

import java.net.URI;

/**
 * A node of a bayseian network, representing some sort of observable element that can be reasoned about.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Observable extends Identifiable implements Comparable<Observable> {
    /** The domain of the observable if unspecified */
    public static final Class<?> DEFAULT_TYPE = String.class;
    /** The derivation of this observable, if this is a derived value */
    @JsonProperty
    @Getter
    @Setter
    private Derivation derivation;
    /** The base of the values for this observable, if this is not directly supplied */
    @JsonProperty
    @Getter
    @Setter
    private Derivation base;
    /** The base class of the values for this observable, if this is not directly supplied, a {@link String} is assumed by default */
    @JsonProperty
    @Getter
    @Setter
    private Class<?> type = DEFAULT_TYPE;
    /** the style of this observable. How to treat searching and canonicity */
    @JsonProperty
    @Getter
    @Setter
    private Style style = Style.CANONICAL;
    /** Is this a required observable, meaning that it must be present to proceed? */
    @JsonProperty
    @Getter
    @Setter
    private boolean required = false;
    /** The normaliser, if required */
    @JsonProperty
    @Getter
    @Setter
    private Normaliser normaliser;
    /**
     * The object that analyses this observable and provides equivalence.
     */
    @JsonProperty
    @Setter
    private Analysis analysis;

    // Ensure ALA Term vocabulary is properly loaded
    static {
        BayesianTerm.values();
    }

    /**
     * Default constructor.
     *
     * @see Identifiable#Identifiable()
     */
    public Observable() {
    }

    /**
     * Construct for an identifier and URI, along with a set of other useful attributes.
     *
     * @param id The identifier
     * @param uri The URI
     * @param type The type of values
     * @param style The style of value (how to search for the value)
     * @param normaliser Any normaliser
     * @param analysis The analysis object
     * @param required Is this a required observable
     */
    public Observable(String id, URI uri, Class type, Style style, Normaliser normaliser, Analysis analysis, boolean required) {
        super(id, uri);
        this.type = type;
        this.style = style;
        this.normaliser = normaliser;
        this.analysis = analysis;
        this.required = required;
    }

    /**
     * Construct for an identifier
     *
     * @param id The identifier
     *
     * @see Identifiable#Identifiable(String)
     */
    public Observable(String id) {
        super(id);
    }

    /**
     * Construct for a URI
     *
     * @param uri The URI
     *
     * @see Identifiable#Identifiable(String)
     */
    public Observable(URI uri) {
        super(uri);
    }

    /**
     * Construct from a term.
     *
     * @param term The term
     */
    public Observable(Term term) {
        this(term, String.class, Style.CANONICAL, null, null, false);
    }

    /**
     * Construct from a term with additional styling information.
     *
     * @param term The term
     * @param type The type of the value
     * @param style The style to usse
     * @param normaliser Any normaliser
     * @param analysis The analysis object
     * @param required Is this a required value
     */
    public Observable(Term term, Class<?> type, Style style, Normaliser normaliser, Analysis analysis, boolean required) {
        this(term.simpleName(), URI.create(term.qualifiedName()), type, style, normaliser, analysis, required);
    }

    /**
     * Get the analysis object.
     * <p>
     * If null, this is lazily initialised to the default
     * object for this type via {@link Analysis#defaultAnalyser(Class)}
     * </p>
     *
     * @return The analysis object.
     */
    public Analysis getAnalysis() {
        if (this.analysis == null) {
            synchronized (this) {
                if (this.analysis == null)
                    this.analysis = Analysis.defaultAnalyser(this.getType());
            }
        }
        return this.analysis;
    }

    /**
     * Compare with another vertex, based on id.
     *
     * @param o The vertex to compare to.
     *
     * @return A comparison in identifier order
     */
    @Override
    public int compareTo(Observable o) {
        return this.getId().compareTo(o.getId());
    }

    /**
     * Hash code, based on identifier.
     *
     * @return The hash code for the vertex
     */
    @Override
    public int hashCode() {
        return this.getUri() != null ? this.getUri().hashCode() : this.getId().hashCode();
    }

    /**
     * Equality test.
     * <p>
     * Two vertices are equal if their URIs (preferable) or ids match.
     * </p>
     *
     * @param obj The object to test against
     *
     * @return True if obj is a vertex and has the same URI or identifier
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Observable))
            return false;
        Observable o = (Observable) obj;
        URI uri1 = this.getUri();
        URI uri2 = o.getUri();
        if ((uri1 == null && uri2 != null) || (uri1 != null  && uri2 == null))
            return false;
        if (uri1 != null)
            return uri1.equals(uri2);
        return this.getId().equals(o.getId());
    }

    /**
     * The style of data in an observable.
     * <p>
     * This gets used to decide how to search for somethign
     * </p>
     */
    public enum Style {
        /** An identifier, treated as a single unit */
        IDENTIFIER,
        /** A canonical form */
        CANONICAL,
        /** A matchable pieces of text */
        PHRASE
    }

}
