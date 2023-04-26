package au.org.ala.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Simple metadata object for inclusion in data products.
 * <p>
 * The fields are based on the dcterms dublin core terms.
 * </p>
 *
 * @see <a href="https://www.dublincore.org/specifications/dublin-core/dcmi-terms/">Dublin Core Terms</a>
 */
@Value
@Builder
@With
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({ "about", "identifier", "version", "type", "title", "description", "contact", "created", "creator", "publisher", "modified", "format", "rights", "rightsHolder", "license", "references", "sources" })
public class Metadata {
    /** The resource identifier for this metadata */
    private URI about;
    /** The product identifier @see org.gbif.dwc.terms.DCTerm#identifier */
    @JsonSerialize(using = EmptyStringSerializer.class) // Added to make XSLT transforms easier
    @JsonDeserialize(using = EmptyStringDeserializer.class)
    private String identifier;
    /** The version of the product */
    @JsonSerialize(using = EmptyStringSerializer.class)
    @JsonDeserialize(using = EmptyStringDeserializer.class)
    private String version;
    /** The data type, preferably drawn from the type vocabulary in https://www.dublincore.org/specifications/dublin-core/dcmi-terms @see org.gbif.dwc.terms.DCTerm#type */
    @JsonSerialize(using = EmptyStringSerializer.class)
    @JsonDeserialize(using = EmptyStringDeserializer.class)
    private String type;
    /** The product title @see org.gbif.dwc.terms.DCTerm#title */
    @JsonSerialize(using = EmptyStringSerializer.class)
    @JsonDeserialize(using = EmptyStringDeserializer.class)
    private String title;
    /** A description of the product @see org.gbif.dwc.terms.DCTerm#description */
    @JsonSerialize(using = EmptyStringSerializer.class)
    @JsonDeserialize(using = EmptyStringDeserializer.class)
    private String description;
    /** The date the product was created @see org.gbif.dwc.terms.DCTerm#created */
    private Date created;
    /** The creator of the product @see org.gbif.dwc.terms.DCTerm#creator */
    @JsonSerialize(using = EmptyStringSerializer.class)
    @JsonDeserialize(using = EmptyStringDeserializer.class)
    private String creator;
    /** The publisher of the product @see org.gbif.dwc.terms.DCTerm#publisher */
    @JsonSerialize(using = EmptyStringSerializer.class)
    @JsonDeserialize(using = EmptyStringDeserializer.class)
    private String publisher;
    /** The date the product was last modified @see org.gbif.dwc.terms.DCTerm#modified */
    private Date modified;
    /** The data format @see org.gbif.dwc.terms.DCTerm#modified */
    @JsonSerialize(using = EmptyStringSerializer.class)
    @JsonDeserialize(using = EmptyStringDeserializer.class)
    private String format;
    /** The rights the data has been published under @see org.gbif.dwc.terms.DCTerm#rights */
    @JsonSerialize(using = EmptyStringSerializer.class)
    @JsonDeserialize(using = EmptyStringDeserializer.class)
    private String rights;
    /** The rights holder to the product @see org.gbif.dwc.terms.DCTerm#rightsHolder */
    @JsonSerialize(using = EmptyStringSerializer.class)
    @JsonDeserialize(using = EmptyStringDeserializer.class)
    private String rightsHolder;
    /** The licence the product has been published under @see org.gbif.dwc.terms.DCTerm#license */
    @JsonSerialize(using = EmptyStringSerializer.class)
    @JsonDeserialize(using = EmptyStringDeserializer.class)
    private String license;
    /** Contact details, if available. */
    @JsonSerialize(using = EmptyStringSerializer.class)
    @JsonDeserialize(using = EmptyStringDeserializer.class)
    private String contact;
    /** A reference to another resource as a URI. @see org.gbif.dwc.terms.DCTerm#references  */
    private URI references;
    /** Any source information from other datasets  @see org.gbif.dwc.terms.DCTerm#source */
    private List<Metadata> sources;
    /** Any other properties */
    private Map<String, String> properties;

    /**
     * Get the currency of the resource.
     *
     * @return Either the latest of created or modified dates or null if no currency
     */
    @JsonIgnore
    public Date getCurrency() {
        if (this.created != null && this.modified != null) {
            return this.modified.after(this.created) ? this.modified : this.created;
        } else if (this.modified != null) {
            return this.modified;
        }
        return this.created;
    }

    /**
     * Build a new metadata object with a value changed.
     * <p>
     * Type converstion between strings and URIs or dates (in ISO format) is handled automatically.
     * Empty strings are treated as nulls.
     * </p>
     *
     * @param term The name of the term (one of the field names)
     * @param value The value to set
     *
     * @return The new metadata instance
     */
    public Metadata with(String term, Object value) {
        try {
            Field field = this.getClass().getDeclaredField(term);

            if (value == null) {
            } else if (field.getType() == Date.class && value instanceof String) {
                value = Date.from(OffsetDateTime.parse((String) value, DateTimeFormatter.ISO_DATE_TIME).toInstant());
            } else if (field.getType() == URI.class && value instanceof String) {
                value = URI.create((String) value);
            } else if (field.getType() == String.class && !(value instanceof String)) {
                value = value.toString();
            }
            if ((value instanceof String) && ((String) value).isEmpty()) {
                value = null;
            }
            Method withMethod = this.getClass().getMethod("with" + term.substring(0, 1).toUpperCase() + term.substring(1), field.getType());
            return (Metadata) withMethod.invoke(this, value);
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalArgumentException("Can't set term " + term + " to " + value, ex);
        }
    }

    /**
     * Overwrite or merge a this metadata with any valid fields from another metadata source.
     * <p>
     * Sources and properties are merged.
     * </p>
     *
     * @param other The other (higher priority) metadata.
     *
     * @return The merged metadata
     */
    public Metadata with(Metadata other) {
        Map<String, String> resultProperties = new HashMap<>();
        if (this.properties != null)
            resultProperties.putAll(this.properties);
        if (other.properties != null)
            resultProperties.putAll(other.properties);
        List<Metadata> resultSources = new ArrayList<>();
        if (other.sources != null)
            resultSources.addAll(other.sources);
        if (this.sources != null) {
            final Set<String> ids = other.sources == null ? Collections.emptySet() : other.sources.stream().map(Metadata::getIdentifier).collect(Collectors.toSet());
            resultSources.addAll(this.sources.stream().filter(s -> !ids.contains(s.getIdentifier())).collect(Collectors.toList()));
        }
        return new Metadata(
                other.about != null ? other.about : this.about,
                other.identifier != null ? other.identifier : this.identifier,
                other.version != null ? other.version : this.version,
                other.type != null ? other.type : this.type,
                other.title != null ? other.title : this.title,
                other.description != null ? other.description : this.description,
                other.created != null ? other.created : this.created,
                other.creator != null ? other.creator : this.creator,
                other.publisher != null ? other.publisher : this.publisher,
                other.modified != null ? other.modified : this.modified,
                other.format != null ? other.format : this.format,
                other.rights != null ? other.rights : this.rights,
                other.rightsHolder != null ? other.rightsHolder : this.rightsHolder,
                other.license != null ? other.license : this.license,
                other.contact != null ? other.contact : this.contact,
                other.references != null ? other.references : this.references,
                resultSources.isEmpty() ? null : resultSources,
                resultProperties.isEmpty() ? null : resultProperties
        );
    }

    /**
     * Read metadata from a source
     *
     * @param source The source
     * @return The read instance
     *
     * @throws Exception If unable to read
     */
    public static Metadata read(URL source) throws Exception {
        ObjectMapper mapper = JsonUtils.createMapper();
        return mapper.readValue(source, Metadata.class);
    }


    /**
     * Read metadata from a file
     *
     * @param source The source
     * @return The read instance
     *
     * @throws Exception If unable to read
     */
    public static Metadata read(File source) throws Exception {
        ObjectMapper mapper = JsonUtils.createMapper();
        return mapper.readValue(source, Metadata.class);
    }

}
