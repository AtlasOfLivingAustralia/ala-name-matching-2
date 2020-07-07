package au.org.ala.names.lucene;

import au.org.ala.bayesian.*;

import static au.org.ala.names.model.ExternalContext.LUCENE;

import au.org.ala.bayesian.Observable;
import lombok.Getter;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.io.IOException;
import java.util.*;

/**
 * Lucene implementation of a classifier.
 * <p>
 * The classifier is backed by a lucene document that can be stored/retrieved as required.
 * </p>
 */
public class LuceneClassifier extends Classifier {
    /** The default field name for identifiers */
    public static final String ID_FIELD = "_id";
    /** The default field name for types */
    public static final String TYPE_FIELD = "_type";
    /** The default field name for names */
    public static final String NAMES_FIELD = "_names";
    /** The default field name for annotations */
    public static final String ANNOTATION_FIELD = "_annotations";
    /** The default field name for parameters */
    public static final String PARAMETERS_FIELD = "_parameters";
    /** The default field name for the left value */
    public static final String INDEX_FIELD = "_index";

    /** The underlying lucene document */
    @Getter
    private Document document;

    /**
     * Construct for an empty document
     *
     */
    public LuceneClassifier() {
        super();
        this.document = new Document();
    }

    /**
     * Construct for a lucene document
     *
     * @param document The document
     */
    public LuceneClassifier(Document document) {
        super();
        this.document = document;
    }

    /**
     * Make a copy of the document with all new fields.
     * <p>
     * This is a bit disgusting but needed to stop the index from doing odd things to
     * read-in document information.
     * In particular, fields that should not be analysed get analysed when stored, read and
     * stored again.
     * Sigh.
     * </p>
     */
    public Document makeDocumentCopy() {
        Document copy = new Document();
        for (IndexableField field: this.document) {
            if (field.fieldType().indexOptions() == IndexOptions.NONE) {
                if (field.binaryValue() != null)
                    copy.add(new StoredField(field.name(), field.binaryValue()));
                else if (field.numericValue() != null) {
                    Number number = field.numericValue();
                    if (number instanceof Integer || number instanceof Short || number instanceof Byte)
                        copy.add(new StoredField(field.name(), number.intValue()));
                    else if (number instanceof Long)
                        copy.add(new StoredField(field.name(), number.longValue()));
                    else if (number instanceof Float)
                        copy.add(new StoredField(field.name(), number.floatValue()));
                    else
                        copy.add(new StoredField(field.name(), number.doubleValue()));
                } else if (field.stringValue() != null)
                    copy.add(new StoredField(field.name(), field.stringValue()));
                else
                    throw new IllegalStateException("Unable to copy field :" + field);
            } else if (field.fieldType().indexOptions() == IndexOptions.DOCS)
                if (field.stringValue() != null)
                    copy.add(new StringField(field.name(), field.stringValue(), Field.Store.YES));
                else
                    throw new IllegalStateException("Unable to copy field :" + field);
            else
                copy.add(field);
        }
        return copy;
    }

    /**
     * Does this classifier contain any information about this observable?
     *
     * @param observable The observable to test
     *
     * @return True if there are values in the classifier
     */
    @Override
    public boolean has(Observable observable) {
        return this.document.getField(observable.getExternal(LUCENE)) != null;
    }

    /**
     * Does this classifier have a matching term for an observable?
     * <p>
     * Integer types and double types are directly compared.
     * String types are compared according to the normalisation and style rules specified by the observable.
     * </p>
     *
     * @param observable The observable to match
     * @param value      The value to match against (may be null)
     * @return Null for nothing to match against (ie null value), or true for a match/false for a non-match
     * @throws InferenceException if there was a problem matching the result
     */
    @Override
    public Boolean match(Observable observable, Object value) throws InferenceException {
        if (value == null)
            return null;
        IndexableField[] fields = this.document.getFields(observable.getExternal(LUCENE));
        if (fields.length == 0)
            return false;

        if (observable.getType() == Integer.class || observable.getType() == Short.class || observable.getType() == Byte.class) {
            if (!(value instanceof Number))
                return false;
            int val = ((Number) value).intValue();
            for (IndexableField field: fields)
                if (field.numericValue() != null && field.numericValue().intValue() == val)
                    return true;
        } else if (observable.getType() == Double.class || observable.getType() == Float.class) {
            if (!(value instanceof Number))
                return false;
            double val = ((Number) value).doubleValue();
            for (IndexableField field: fields)
                if (field.numericValue() != null && field.numericValue().doubleValue() == val)
                    return true;
        } else {
            String val = value.toString();
            Normaliser normaliser = observable.getNormaliser();
            if (normaliser != null)
                val = normaliser.normalise(val);
            for (IndexableField field: this.document.getFields(observable.getExternal(LUCENE))) {
                switch (observable.getStyle()) {
                    case IDENTIFIER:
                    case CANONICAL:
                        return val.equals(field.stringValue());
                    default:
                        return val.equalsIgnoreCase(field.stringValue());
                }
            }
        }
        return false;
    }

    /**
     * Add a value to the classifier.
     *
     * @param observable The observable to store
     * @param value      The value to store
     * @throws StoreException if unable to add this variable to the classifier
     */
    @Override
    public void add(Observable observable, Object value) throws StoreException {
        Field field = this.convert(observable, value);
        this.document.add(field);
    }

    /**
     * Copy all values from another classifier
     *
     * @param observable The observable for the values
     * @param classifier The classifier to copy from
     * @throws StoreException if unable to add this variable to the classifier
     */
    @Override
    public void addAll(Observable observable, Classifier classifier) throws StoreException {
        if (!(classifier instanceof LuceneClassifier))
            throw new IllegalArgumentException("Expecting instnce of LuceneClassifier");
        for (IndexableField field: ((LuceneClassifier) classifier).document.getFields(observable.getExternal(LUCENE)))
            this.document.add(field);
    }

    /**
     * Add a replace a value in the classifier.
     *
     * @param observable The observable to store
     * @param value      The value to store
     * @throws StoreException if unable to add this variable to the classifier
     */
    @Override
    public void replace(Observable observable, Object value) throws StoreException {
        this.document.removeFields(observable.getExternal(LUCENE));
        this.add(observable, value);
    }

    /**
     * Get the value for an observable.
     *
     * @param observable The observable
     * @return The associated value or null for not present
     */
    @Override
    public String get(Observable observable)  {
        return this.document.get(observable.getExternal(LUCENE));
    }

    /**
     * Get the all values for an observable.
     *
     * @param observable The observable
     * @return The a set of all present values
     */
    @Override
    public Set<String> getAll(Observable observable) {
        String[] vs = this.document.getValues(observable.getExternal(LUCENE));
        Set<String> values = new HashSet<>(vs.length);
        for (String v: vs)
            values.add(v);
        return values;
    }

    /**
     * Get the identifier for a classifier.
     *
     * @return The identifier, or null for no identifier found.
     */
    @Override
    public String getIdentifier() {
        return this.document.get(ID_FIELD);
    }

    /**
     * Label a classifier with a unique identifier.
     * <p>
     * If the classifier is already identified, then return that identifier.
     * Otherwise, add a UUID identifier to the classifier.
     * </p>
     *
     * @return The identifier.
     */
    @Override
    public String identify() {
        String id = this.getIdentifier();

        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
            this.document.add(new StringField(ID_FIELD, id, Field.Store.YES));
        }
        return id;
    }

    /**
     * Get a clause that searches for a specific classifier type.
     *
     * @param type The type
     *
     * @return A type query claise
     */
    public static BooleanClause getTypeClause(Term type) {
        Query base = new TermQuery(new org.apache.lucene.index.Term(TYPE_FIELD, type.qualifiedName()));
        return new BooleanClause(base, BooleanClause.Occur.MUST);
    }

    /**
     * Get the classifier's type
     *
     * @return The document type
     * @throws StoreException If unable to create a type for some reason.
     */
    @Override
    public Term getType() throws StoreException {
        String type = this.document.get(TYPE_FIELD);

        if (type == null || type.isEmpty())
            return null;
        return TermFactory.instance().findTerm(type);
    }

    /**
     * Set the classifier's type
     *
     * @param type The new type
     * @return The document type
     * @throws StoreException If unable to create a type for some reason.
     */
    @Override
    public void setType(Term type) throws StoreException {
        String value = type.qualifiedName();
        this.document.removeFields(TYPE_FIELD);
        this.document.add(new StringField(TYPE_FIELD, value, Field.Store.YES));
    }

    /**
     * Is this document annotated with a specific annotation (flag)?
     * <p>
     * By default, the URI of the annotation is searched for.
     * </p>
     *
     * @param annotation The annotation to test for
     * @return True if the document has the annotation, false otherwise.
     * @throws StoreException if unable to test for the annotation
     */
    @Override
    public boolean hasAnnotation(Term annotation) throws StoreException {
        String value = annotation.qualifiedName();
        for (String v: this.document.getValues(ANNOTATION_FIELD)) {
            if (value.equals(v))
                return true;
        }
        return false;
    }

    /**
     * Add an annotation to a classifier.
     *
     * @param annotation The annotation to add
     * @throws StoreException If unable to annotate for some reason.
     */
    @Override
    public void annotate(Term annotation) throws StoreException {
        this.document.add(new StringField(ANNOTATION_FIELD, annotation.qualifiedName(), Field.Store.YES));
    }

    /**
     * Get the inference parameters associcated with a classifier.
     *
     * @return The parameters
     * @throws StoreException if unable to retrieve the parameters
     */
    @Override
    public void loadParameters(Parameters parameters) throws StoreException {
        BytesRef bytes = this.document.getBinaryValue(PARAMETERS_FIELD);

        if (bytes == null)
            return;
        try {
            parameters.loadFromBytes(bytes.bytes);
        } catch (IOException ex) {
            throw new StoreException("Unable to load parameters", ex);
        }
    }

    /**
     * Store the inference parameters in a classifier.
     *
     * @param parameters The inferred parameters
     * @throws StoreException if unable to store the parameters in the document
     */
    @Override
    public void storeParameters(Parameters parameters) throws StoreException {
        try {
            BytesRef bytes = new BytesRef(parameters.storeAsBytes());
            this.document.removeFields(PARAMETERS_FIELD);
            this.document.add(new StoredField(PARAMETERS_FIELD, bytes));
        } catch (IOException ex) {
            throw new StoreException("Unable to save parameters to document", ex);
        }
    }

    /**
     * Get the index values for the classifier.
     *
     * @return An array of [left, right] index integers
     * @throws StoreException if unable to retrieve the index
     */
    @Override
    public int[] getIndex() throws StoreException {
        IndexableField[] fields = this.document.getFields(INDEX_FIELD);

        if (fields.length == 0)
            return null;
        if (fields.length == 1) {
            int left = fields[0].numericValue().intValue();
            return new int[] { left, left };
        }
        int left = fields[0].numericValue().intValue();
        int right = fields[1].numericValue().intValue();
        return new int[] { Math.min(left, right), Math.max(left, right) };
    }

    /**
     * Set the index values for a classifier.
     * <p>
     * Index values place the classifier within a tree heirarchy
     * </p>
     *
     * @param left  The left-most index value
     * @param right The right-most index value
     * @throws StoreException If unable to store the index
     */
    @Override
    public void setIndex(int left, int right) throws StoreException {
        this.document.removeFields(INDEX_FIELD);
        this.document.add(new StoredField(INDEX_FIELD, left));
        this.document.add(new StoredField(INDEX_FIELD, right));
    }

    /**
     * Get the names for the classifier.
     * <p>
     * The names contains the primary list of names that will identify this classifier
     * </p>
     *
     * @return The names list
     * @throws StoreException
     */
    @Override
    public Collection<String> getNames() throws StoreException {
        return Arrays.asList(this.document.getValues(NAMES_FIELD));
    }

    /**
     * Set the names for a classifier.
     * <p>
     * The names contain the searchable list of names the classifier might be found under
     * </p>
     *
     * @param names The names for this classifier
     * @throws StoreException if unable to store the names in the classifier
     */
    @Override
    public void setNames(Collection<String> names) throws StoreException {
        this.document.removeFields(NAMES_FIELD);
        for (String name: names)
            document.add(new TextField(NAMES_FIELD, name, Field.Store.YES));
    }

    /**
     * Convert a observable/value pair into a lucene field
     *
     * @param observable The term
     * @param value The object value
     *
     * @return A corresponding field
     *
     * @throws StoreException if unable to store the data
     */
    protected Field convert(Observable observable, Object value) throws StoreException {
        if (value == null || ((value instanceof String) && ((String) value).isEmpty()))
            return null;
        String field = observable.getExternal(LUCENE);
        if (value instanceof Integer)
            return new StoredField(field, ((Number) value).intValue());
        else if (value instanceof Number)
            return new StoredField(field, ((Number) value).doubleValue());
        else {
            String val = value.toString();
            Normaliser normaliser = observable.getNormaliser();
            if (normaliser != null)
                val = normaliser.normalise(val);
            switch (observable.getStyle()) {
                case IDENTIFIER:
                case CANONICAL:
                    return new StringField(field, val, Field.Store.YES);
                default:
                    return new TextField(field, val, Field.Store.YES);
            }
        }
    }
}
