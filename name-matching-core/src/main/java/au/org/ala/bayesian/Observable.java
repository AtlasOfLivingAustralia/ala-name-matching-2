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
public class Observable extends Identifiable implements Comparable<Observable> {
    /** The derivation of this observable, if this is a derived value */
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Derivation derivation;
    /** The base of the values for this observable, if this is not directly supplied */
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Derivation base;

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
     * Construct for an identifier and URI.
     *
     * @param id The identifier
     * @param uri The URI
     */
    public Observable(String id, URI uri) {
        super(id, uri);
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
        this(term.simpleName(), URI.create(term.qualifiedName()));
    }

    /**
     * Get the derivatrion for this observable
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
     * Get the field name for this observable.
     * <p>
     * Field names are suitable names for storing in lucene documents and corresponds to
     * <code>this.getExternal(ExternalContext.LUCENE)</code>
     * </p>
     *
     * @return The field name
     *
     * @see #getExternal
     */
    @JsonIgnore
    public String getField() {
        return this.getExternal(ExternalContext.LUCENE);
    }

    /**
     * Get the java variable name for this observable.
     * <p>
     * Variable names are suitable base names for using in java code and corresponds to
     * <code>this.getExternal(ExternalContext.JAVA_VARIABLE)</code>
     * </p>
     *
     * @return The java variable name
     *
     * @see #getExternal
     */
    @JsonIgnore
    public String getJavaVariable() {
        return this.getExternal(ExternalContext.JAVA_VARIABLE);
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

}
