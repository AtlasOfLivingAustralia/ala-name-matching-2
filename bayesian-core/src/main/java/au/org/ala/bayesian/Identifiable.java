package au.org.ala.bayesian;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * A node in a Bayesian inference graph.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIdentityReference(alwaysAsId = false)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
abstract public class Identifiable {
    /** The pattern for a valid ifentifier part */
    public static final String VALID_IDENTIFIER_PART = "\\p{L}\\p{Digit}:_\\-\\.";
    /** The pattern for a valid identifier. */
    public static final Pattern VALID_IDENTIFIER = Pattern.compile("[\\p{L}_][" + VALID_IDENTIFIER_PART + "]*");
    /** The pattern for removing any unusable identifer part */
    public static final Pattern INVALID_SEQUENCE = Pattern.compile("[^" + VALID_IDENTIFIER_PART + "]+");

    private static AtomicInteger ID_SEQUENCE = new AtomicInteger();

    /** The identifier */
    @JsonProperty
    @Getter
    private String id;
    /** An optional short label */
    @JsonProperty
    @Setter
    private String label;
    /** The description */
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Getter
    @Setter
    private String description;
    /** A URI describing the concept that this noderepresents */
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Getter
    @Setter
    private URI uri;
    /** A GBIF term corresponding to the URI/identifier */
    @JsonIgnore
    private Term term;
    /** External names for things like lucene index entries */
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<ExternalContext, String> external;
    /** Cache of external names, including computed ones */
    @JsonIgnore
    private Map<ExternalContext, String> externalCache;
    /** Any property flags that this observable has */
    // See getter/setter for json properties
    private SortedMap<Term, Object> properties;


    /**
     * Construct an empty identifiable object with an arbitrary identifier.
     */
    public Identifiable() {
        this("object_" + ID_SEQUENCE.incrementAndGet());
    }

    /**
     * Construct for a supplied identifier and URI.
     *
     * @param id The identifier
     * @param uri The URI (may be null)
     */
    public Identifiable(String id, URI uri) {
        if (id == null || !VALID_IDENTIFIER.matcher(id).matches())
            throw new IllegalArgumentException("Invalid identifier");
        this.id = id;
        this.uri = uri;
        this.external = new HashMap<>();
        this.externalCache = new HashMap<>();
        this.properties = new TreeMap<>((t1, t2) -> t1.qualifiedName().compareTo(t2.qualifiedName()));
    }
    /**
     * Construct with a defined identifier.
     *
     * @param id The identifier
     */
    public Identifiable(String id) {
        this(id, null);
    }

    /**
     * Construct with a defined URI.
     *
     * @param uri The uri
     */
    public Identifiable(URI uri) {
        this(makeIdFromURI(uri), uri);
    }

    /**
     * Get the term associated with this identifiable.
     * <p>
     * The term is constructed either from the URI, if available, or from the identifier
     * and returns a term corresponding to the observable.
     * </p>
     *
     * @return The corresponding term
     */
    public Term getTerm() {
        if (this.term == null) {
            if (this.getUri() != null)
                this.term = TermFactory.instance().findTerm(this.getUri().toASCIIString());
            if (this.term == null)
                this.term = TermFactory.instance().findTerm(this.getId());
        }
        return this.term;
    }

    /**
     * Get the label for this identifiable.
     * <p>
     * Labels are short forms of the identifier, useful for labelling things where you don't want to take up
     * too much space.
     * </p>
     *
     * @return The label, if set, or the identifier otherwise
     */
    @JsonIgnore
    public String getLabel() {
        return this.label != null ? this.label : this.getId();
    }

    /**
     * Get the external name for this context.
     * <p>
     * If specified, then use that one. Otherwise, use the converter to build a default form
     * </p>
     *
     * @param context The context for this identifier
     *
     * @return The external name of this thing
     */
    public String getExternal(ExternalContext context) {
        return this.externalCache.computeIfAbsent(context, k -> k.getConverter().convert(this));
    }

    /**
     * Set
     * @param context
     * @param external
     */
    public void setExternal(ExternalContext context, String external) {
        if (external == null) {
            this.external.remove(context);
            this.externalCache.remove(context);
        } else {
            this.externalCache.put(context, external);
            this.external.put(context, external);
        }
    }


    /**
     * Get the java variable name for this identifiable item.
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
     * Make an identifier out of a URI
     * <p>
     * The identifier is constructed from the last bit of the path or the fragment.
     * For example, <code>http://id.ala.org.au/terms/subgenus</code> becomes <code>subgenus</code>,
     * <code>http://www.w3.org/1999/02/22-rdf-syntax-ns#type</code> becomes <code>type</code> or
     * <code>urn:isbn:978-0143035008</code> is <code>978-0143035008</code>
     * </p>
     * <p>
     * The resulting identifier will meet the requirements of {@link #VALID_IDENTIFIER}.
     * </p>
     *
     * @param uri The URI
     *
     * @return The constructed id or "unknown" if there isn't a valid id.
     */
    public static String makeIdFromURI(URI uri) {
        final String SEPS = "/?#:";
        String id = null;

        String u = uri.toASCIIString();
        int h = u.lastIndexOf('#');
        if (h != -1) {
            if (h == u.length() - 1)
                u = u.substring(0, h);
            else {
                id = u.substring(h + 1).trim();
                if (id.isEmpty())
                    id = null;
            }
        }
        if (id == null) {
            int q = u.lastIndexOf('?');
            if (q != -1) {
                u = u.substring(0, q);
            }
        }
        if (id == null) {
            int s = u.lastIndexOf('/');
            if (s >= 0 && s == u.length() - 1) {
                u = u.substring(0, s);
                s = u.lastIndexOf('/');
            }
            if (s != -1) {
                id = u.substring(s + 1).trim();
                if (id.isEmpty())
                    id = null;
            }
        }
        if (id == null) {
            int c = u.lastIndexOf(':');
            if (c >= 0 && c == u.length() - 1) {
                u = u.substring(0, c);
                c = u.lastIndexOf(':');
            }
            if (c != -1) {
                id = u.substring(c + 1).trim();
                if (id.isEmpty())
                    id = null;
            }
        }
        if (id == null)
            id = u.trim();
        if (id.isEmpty())
            id = "unknown";
        if (!Character.isAlphabetic(id.codePointAt(0)))
            id = "_" + id;
        id = INVALID_SEQUENCE.matcher(id).replaceAll("_");
        return id;
     }

     /** Getter for json serialisation */
     @JsonGetter("properties")
     @JsonInclude(JsonInclude.Include.NON_EMPTY)
     protected Map<Term, Object> getProperties() {
        return this.properties;
     }

     /** Setter for json deserialisation */
     @JsonSetter("properties")
     protected void setProperties(Map<Term, Object> properties) {
        this.properties.clear();
        this.properties.putAll(properties);
     }

    /**
     * Get the value of a property.
     *
     * @param property The property
     *
     * @return The property value
     */
     public Object getProperty(Term property) {
        return this.properties.get(property);
     }

    /**
     * Set the value of a property (remove if null)
     *
     * @param property The property
     * @param value The property value
     */
     public void setProperty(Term property, Object value) {
         if (value == null)
             this.properties.remove(property);
         else
            this.properties.put(property, value);
     }

    /**
     * Test to see whether this observable has a property value.
     *
     * @param property The property
     * @param value The value (null for not set)
     *
     * @return True if the values match
     */
     public boolean hasProperty(Term property, Object value) {
         return Objects.equals(this.properties.get(property), value);
    }

    @Override
    public String toString() {
         StringBuilder builder = new StringBuilder();
         builder.append(this.getClass().getSimpleName());
         builder.append("{");
         builder.append(this.id);
         if (this.uri != null) {
             builder.append(", ");
             builder.append(this.uri);
         }
         builder.append("}");
         return builder.toString();
    }
}
