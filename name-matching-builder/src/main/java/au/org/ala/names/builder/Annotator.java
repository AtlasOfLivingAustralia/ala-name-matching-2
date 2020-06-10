package au.org.ala.names.builder;

import au.org.ala.bayesian.Parameters;
import org.apache.lucene.document.*;
import org.apache.lucene.util.BytesRef;
import org.gbif.dwc.terms.Term;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

/**
 * An annotator annotaters a document with additional information
 * derived from
 */
public interface Annotator {
    /** The default field name for identifiers */
    public static final String DEFAULT_ID_FIELD = "_id";
    /** The default field name for types */
    public static final String DEFAULT_TYPE_FIELD = "_type";
    /** The default field name for names */
    public static final String DEFAULT_NAMES_FIELD = "_names";
    /** The default field name for annotations */
    public static final String DEFAULT_ANNOTATION_FIELD = "_annotations";
    /** The default field name for parameters */
    public static final String DEFAULT_PARAMETERS_FIELD = "_parameters";
    /** The default field name for the left value */
    public static final String DEFAULT_INDEX_FIELD = "_index";

    /**
     * Get the field name for the identifier
     *
     * @return The identifier field name, by default {@link #DEFAULT_ID_FIELD}
     */
    public default String getIdentifierField() {
        return DEFAULT_ID_FIELD;
    }

    /**
     * Get the identifier for a document.
     *
     * @param document The document
     *
     * @return The identifier, or null for no identifier found.
     */
    public default String getIdentifier(Document document) {
        String field = this.getIdentifierField();

        return document.get(this.getIdentifierField());
    }

    /**
     * Label a document with a unique identifier.
     * <p>
     * Add the identifier to the document.
     * If the document is already identified, then return that identifier.
     * </p>
     * <p>
     * By default, a UUID is added as an identifier
     * </p>
     *
     * @param document The document to annotate
     *
     * @return The identifier.
     *
     * @throws StoreException If unable to create an identifier for some reason.
     */
    public default String identify(Document document) throws StoreException {
        String field = this.getIdentifierField();
        String id = this.getIdentifier(document);

        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
            document.add(new StringField(field, id, Field.Store.YES));
        }
        return id;
    }

    /**
     * Get the field name for the type
     *
     * @return The type field name, by default {@link #DEFAULT_TYPE_FIELD}
     */
    public default String getTypeField() {
        return DEFAULT_TYPE_FIELD;
    }

    /**
     * Associate a document with a type.
     * <p>
     * By default, the default type is added.
     * </p>
     *
     * @param document The document
     * @param defaultType The default type, if one is not provided by the annotator.
     *
     * @return The document type
     *
     * @throws StoreException If unable to create a type for some reason.
     */
    public default Term type(Document document, Term defaultType) throws StoreException {
        String field = this.getTypeField();
        document.removeFields(field);
        document.add(new StringField(field, defaultType.qualifiedName(), Field.Store.YES));
        return defaultType;
    }

    /**
     * Get the value associated with a specific type.
     * <p>
     * By default, this is the term URI.
     * </p>
     *
     * @param type The type to search for
     *
     * @return The type value
     *
     * @throws StoreException if unable
     */
    public default String getTypeValue(Term type) throws StoreException {
        return type.qualifiedName();
    }

    /**
     * Get the field name for the annotations list
     *
     * @return The annotation field name, by default {@link #DEFAULT_ANNOTATION_FIELD}
     */
    public default String getAnnodationField() {
        return DEFAULT_ANNOTATION_FIELD;
    }

    /**
     * Is this document annotated with a specific annotation (flag)?
     * <p>
     * By default, the URI of the annotation is searched for.
     * </p>
     *
     * @param document The document
     * @param annotation The annotation to test for
     *
     * @return True if the document has the annotation, false otherwise.
     */
    public default boolean hasAnnotation(Document document, Term annotation) {
        String field = this.getAnnodationField();
        String value = annotation.qualifiedName();

        for (String v: document.getValues(field)) {
            if (value.equals(v))
                return true;
        }
        return false;
    }

    /**
     * Get the value associated with a specific annotation.
     * <p>
     * By default, this is the URI of the annotation term
     * </p>
     *
     * @param annotation The annotation to search for
     *
     * @return The annotation value
     *
     * @throws StoreException if unable to get the value
     */
    public default String getAnnotationValue(Term annotation) throws StoreException {
        return annotation.qualifiedName();
     }

    /**
     * Add an annotation to a document.
     * <p>
     * By default, the URI of the term is added to the annotation field.
     * </p>
     *
     * @param document The document
     * @param annotation The annotation to add
     *
     * @throws StoreException If unable to annotate for some reason.
     */
    public default void annotate(Document document, Term annotation) throws StoreException {
        String field = this.getAnnodationField();
        String value = annotation.qualifiedName();

        document.add(new StringField(field, value, Field.Store.YES));
    }

    /**
     * Annotate a document with additional information.
     * <p>
     * This does not include the type or identifier, which are added separately
     * but includes any terms that should be added to the annotationas list.
     * </p>
     *
     * @param document The document
     *
     * @throws StoreException If unable to create an annotation for some reason.
     */
    public void annotate(Document document) throws StoreException;

    /**
     * Get the field name for the paramters array
     *
     * @return The parameters field name, by default {@link #DEFAULT_PARAMETERS_FIELD}
     */
    public default String getParametersField() {
        return DEFAULT_PARAMETERS_FIELD;
    }

    /**
     * Set the inference parameters in a document.
     * <p>
     * By default, this encodes the parameters as a byte array
     * of doubles.
     * </p>
     *
     * @param document The doucment to set parameters for
     * @param parameters The inferred parameters
     *
     * @throws StoreException if unable to store the parameters in the document
     */
    public default void setParameters(Document document, Parameters parameters) throws StoreException {
        try {
            String field = this.getParametersField();
            BytesRef bytes = new BytesRef(parameters.storeAsBytes());
            document.removeFields(field);
            document.add(new StoredField(field, bytes));
        } catch (IOException ex) {
            throw new StoreException("Unable to save parameters to document", ex);
        }
    }

    /**
     * Get the field name for the left-right index
     *
     * @return The left index field name, by default {@link #DEFAULT_INDEX_FIELD}
     */
    public default String getIndexField() {
        return DEFAULT_INDEX_FIELD;
    }

    /**
     * Set the index values for a document.
     *
     * @param document The document
     * @param left The left-most index value
     * @param right The right-most index value
     *
     * @throws StoreException If unable to store the index
     */
     public default void setIndex(Document document, int left, int right) throws StoreException {
        String field = this.getIndexField();
        document.removeFields(field);
        document.add(new StoredField(field, left));
        document.add(new StoredField(field, right));
        document.add(new IntRange(field, new int[] { left }, new int[] { right }));
    }


    /**
     * Get the field name for the names list
     *
     * @return The names field name, by default {@link #DEFAULT_NAMES_FIELD}
     */
    public default String getNamesField() {
        return DEFAULT_NAMES_FIELD;
    }

    /**
     * Set the names for a document.
     *
     * @param document The document to set names for
     * @param names The names for this document
     *
     * @throws StoreException if unable to store the names in the document
     */
    public default void setNames(Document document, Collection<String> names) throws StoreException {
        String field = this.getNamesField();
        document.removeFields(field);
        for (String name: names)
            document.add(new StringField(field, name, Field.Store.YES));
    }

}
