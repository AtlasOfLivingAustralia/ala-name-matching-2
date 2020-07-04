package au.org.ala.bayesian;

import au.org.ala.names.model.ExternalContext;
import au.org.ala.names.model.Identifiable;
import au.org.ala.vocab.ALATerm;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.net.URI;

/**
 * A node of a bayseian network, representing some sort of observable element that can be reasoned about.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Observable extends Identifiable implements Comparable<Observable> {
    /** The derivation of this observable, if this is a derived value */
    @JsonProperty
    private Derivation derivation;
    /** The base of the values for this observable, if this is not directly supplied */
    @JsonProperty
    private Derivation base;
    /** The base of the values for this observable, if this is not directly supplied, a string if null */
    @JsonProperty
    private Class<?> type = String.class;
    /** the style of this observable. How to treat searching and canonicity */
    @JsonProperty
    private Style style = Style.CANONICAL;
    /** Is this a required observable, meaning that it must be present to proceed? */
    @JsonProperty
    private boolean required = false;
   /** The normaliser, if required */
    @JsonProperty
    private Normaliser normaliser;

    // Ensure ALA Term vocabulary is properly loaded
    static {
        ALATerm.values();
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
     * @param required Is this a required observable
     *
     */
    public Observable(String id, URI uri, Class<?> type, Style style, Normaliser normaliser, boolean required) {
        super(id, uri);
        this.type = type;
        this.style = style;
        this.normaliser = normaliser;
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
        this(term, String.class, Style.CANONICAL, null, false);
    }

    /**
     * Construct from a term with additional styling information.
     *
     * @param term The term
     * @param type The type of the value
     * @param style The style to usse
     * @param normaliser Any normaliser
     * @param required Is this a requred value
     */
    public Observable(Term term, Class<?> type, Style style, Normaliser normaliser, boolean required) {
        this(term.simpleName(), URI.create(term.qualifiedName()), type, style, normaliser, required);
    }

    /**
     * Get the derivation for this observable
     *
     * @return The derivartion
     */
    public Derivation getDerivation() {
        return this.derivation;
    }

    /**
     * Set the derivation for this observable
     *
     * @param derivation The derivation
     */
    public void setDerivation(Derivation derivation) {
        this.derivation = derivation;
    }

    /**
     * Get the base of derived values
     *
     * @return The base of derived values
     */
    public Derivation getBase() {
        return base;
    }

    /**
     * Set the base of derived values
     *
     * @param base The base of derived values
     */
    public void setBase(Derivation base) {
        this.base = base;
    }

    /**
     * Get the type of this observable
     *
     * @return The type (defaults to {@link String})
     */
    public Class<?> getType() {
        return this.type;
    }

    /**
     * Set the type of this observable
     *
     * @param type The new type
     */
    public void setType(Class<?> type) {
        this.type = type;
    }

    /**
     * Get the style of information contained in this observable.
     * <p>
     * Styles describe how tight or sloppy searches for terms and equality
     * should be handles.
     * </p>
     *
     * @return The style of the observable
     */
    public Style getStyle() {
        return style;
    }

    /**
     * Set the style of the observable
     *
     * @param style
     */
    public void setStyle(Style style) {
        this.style = style;
    }

    /**
     * Is this a required variable for querying.
     *
     * @return True if the variable is required
     */
    public boolean isRequired() {
        return this.required;
    }

    /**
     * Set the required flag.
     *
     * @param required True if this is a required variable
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * Get any normaliser associated with this observable.
     * <p>
     * Normalisers define how a value needs to be treated to get into canonical form.
     * </p>
     *
     * @return The normaliser or null for not used
     */
    public Normaliser getNormaliser() {
        return this.normaliser;
    }

    /**
     * Set the normaliser.
     *
     * @param normaliser The new normaliser
     */
    public void setNormaliser(Normaliser normaliser) {
        this.normaliser = normaliser;
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
    public static enum Style {
        /** An identifier, treated as a single unit */
        IDENTIFIER,
        /** A canonical form */
        CANONICAL,
        /** A matchable pieces of text */
        PHRASE
    }

}
