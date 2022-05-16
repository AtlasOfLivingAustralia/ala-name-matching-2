package au.org.ala.names.lucene;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.*;
import au.org.ala.vocab.BayesianTerm;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static au.org.ala.bayesian.ExternalContext.LUCENE;
import static au.org.ala.bayesian.ExternalContext.LUCENE_VARIANT;

/**
 * Lucene implementation of a classifier.
 * <p>
 * The classifier is backed by a lucene document that can be stored/retrieved as required.
 * </p>
 */
@Slf4j
@JsonPropertyOrder({ "identifier", "type", "names", "index" })
public class LuceneClassifier implements Classifier {
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
    /** The default field name for the left/right values */
    public static final String INDEX_FIELD = "_index";
    /** The default field name for classifier signature */
    public static final String SIGNATURE_FIELD = "_signature";
    /** The default field name for classifier trail */
    public static final String TRAIL_FIELD = "_trail";
    /** The trail separator */
    public static final String TRAIL_SEPARATOR = "|";
    /** The trail separator regular expression */
    public static final Pattern TRAIL_SEPARATOR_REGEX = Pattern.compile("\\|");

    /** The underlying lucene document */
    @Getter
    @JsonIgnore
    private final Document document;
    /** Has this document been retrieved from a store (in which case it is hard ti store again */
    @Getter
    @JsonIgnore
    private final boolean retrieved;
    /** The cached parameters */
    @Getter
    @JsonIgnore
    private Parameters cachedParameters;

    /**
     * Construct for an empty document
     *
     */
    public LuceneClassifier() {
        super();
        this.document = new Document();
        this.retrieved = false;
        this.cachedParameters = null;
    }

    /**
     * Construct for a lucene document
     *
     * @param document The document
     */
    public LuceneClassifier(Document document) {
        super();
        this.document = document;
        this.retrieved = true;
        this.cachedParameters = null;
    }

    /**
     * Make a copy of the document with all new fields.
     * <p>
     * This is a bit disgusting but needed to stop the index from doing odd things to
     * read-in document information.
     * In particular, fields that should not be analysed get analysed when stored, read and
     * stored again.
     * Additionally, stored int fields are normally associated with an int point in the index for
     * searching.
     * Sigh.
     * </p>
     */
    public Document makeDocumentCopy() {
        Document copy = new Document();
        for (IndexableField field: this.document) {
            if (!field.fieldType().stored()) {
                continue; // Assume added in by what is below
            }
            if (field.fieldType().indexOptions() == IndexOptions.NONE) {
                if (field.binaryValue() != null)
                    copy.add(new StoredField(field.name(), field.binaryValue()));
                else if (field.numericValue() != null) {
                    Number number = field.numericValue();
                    if (number instanceof Integer || number instanceof Short || number instanceof Byte) {
                        copy.add(new StoredField(field.name(), number.intValue()));
                        copy.add(new IntPoint(field.name(), number.intValue())); // Assume searchable as well
                    } else if (number instanceof Long) {
                        copy.add(new StoredField(field.name(), number.longValue()));
                    } else if (number instanceof Float) {
                        copy.add(new StoredField(field.name(), number.floatValue()));
                    } else {
                        copy.add(new StoredField(field.name(), number.doubleValue()));
                    }
                } else if (field.stringValue() != null)
                    copy.add(new StoredField(field.name(), field.stringValue()));
                else
                    throw new IllegalStateException("Unable to copy field :" + field);
            } else if (field.fieldType().indexOptions() == IndexOptions.DOCS) {
                if (field.stringValue() != null)
                    copy.add(new StringField(field.name(), field.stringValue(), Field.Store.YES));
                else
                    throw new IllegalStateException("Unable to copy field :" + field);
            } else {
                if (field.binaryValue() != null)
                    copy.add(new StoredField(field.name(), field.binaryValue()));
                else if (field.numericValue() != null) {
                    Number number = field.numericValue();
                    if (number instanceof Integer || number instanceof Short || number instanceof Byte) {
                        copy.add(new StoredField(field.name(), number.intValue()));
                        copy.add(new IntPoint(field.name(), number.intValue()));
                    } else if (number instanceof Long) {
                        copy.add(new StoredField(field.name(), number.longValue()));
                        copy.add(new LongPoint(field.name(), number.longValue()));
                    } else if (number instanceof Float) {
                        copy.add(new StoredField(field.name(), number.floatValue()));
                        copy.add(new FloatPoint(field.name(), number.floatValue()));
                    } else {
                        copy.add(new StoredField(field.name(), number.doubleValue()));
                        copy.add(new DoublePoint(field.name(), number.doubleValue()));
                    }
                } else if (field.stringValue() != null) {
                    copy.add(new TextField(field.name(), field.stringValue(), Field.Store.YES));
                } else {
                    throw new IllegalStateException("Unable to copy field :" + field);
                }
            }
        }
        return copy;
    }

    /**
     * Does this classifier contain any non-variant information about this observable?
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
     * Does this classifier contain any variant or non-variant information about this observable?
     *
     * @param observable The observable to test
     *
     * @return True if there are values in the classifier
     */
    @Override
    public boolean hasAny(Observable observable) {
        return this.document.getField(observable.getExternal(LUCENE)) != null || this.document.getField(observable.getExternal(LUCENE_VARIANT)) != null;
    }

    /**
     * Does this classifier have a matching term for an observable?
     * <p>
     * All variants are tested.
     * Integer types and double types are directly compared.
     * String types are compared according to the normalisation and style rules specified by the observable.
     * </p>
     *
     * @param observables The observables to match
     * @param value      The value to match against (may be null)
     *
     * @return Null for nothing to match against (ie null value), or true for a match/false for a non-match
     *
     * @throws StoreException if there was a problem matching the result or testing for equivalence.
     */
    @Override
    public <T> Boolean match(T value, Observable<T>... observables) throws StoreException {
        return this.match(value, true, observables);
    }

    /**
     * Does this classifier have a matching term for an observable?
     * <p>
     * All variants are tested.
     * Integer types and double types are directly compared.
     * String types are compared according to the normalisation and style rules specified by the observable.
     * </p>
     *
     * @param observables The observables to match
     * @param value      The value to match against (may be null)
     * @param variants  Look for variants
     *
     * @return Null for nothing to match against (ie null value), or true for a match/false for a non-match
     *
     * @throws StoreException if there was a problem matching the result or testing for equivalence.
     */
    public <T, C, Q> Boolean match(T value, boolean variants, Observable<T>... observables) throws StoreException {
        try {
            if (value == null)
                return null;
            boolean allNull = true;
            for (Observable observable: observables) {
                IndexableField[] fields = this.document.getFields(observable.getExternal(variants && observable.getMultiplicity().isMany() ? LUCENE_VARIANT : LUCENE));
                if (fields.length == 0)
                    continue;
                Analysis<T, C, Q> analysis = observable.getAnalysis();
                if (Number.class.isAssignableFrom(observable.getType())) {
                    if (!(value instanceof Number))
                        return false;
                    for (IndexableField field : fields) {
                        Number fv = field.numericValue();
                        Boolean match = analysis.equivalent(value, (T) fv);
                        if (match != null && match)
                            return true;
                        allNull = allNull && match == null;
                    }
                } else {
                    for (IndexableField field : fields) {
                        String sv = field.stringValue();
                        Object cv = analysis.fromString(sv);
                        Boolean match = analysis.equivalent(value, (T) cv);
                        if (match != null && match)
                            return true;
                        allNull = allNull && match == null;
                    }
                }
            }
            return allNull ? null : false;
        } catch (InferenceException ex) {
            throw new StoreException("Unable to match " + value + " against " + observables, ex);
        }
    }

    /**
     * Add a value to the classifier.
     * <p>
     * Values are not added if already present.
     * </p>
     *
     * @param observable The observable to store
     * @param value      The value to store
     * @param variant    Write as a variant only
     * @param replace Replace the non-variant value
     * @throws StoreException if unable to add this variable to the classifier
     */
    @Override
    public <T> void add(Observable<T> observable, T value, boolean variant, boolean replace) throws StoreException {
        if (replace)
            this.document.removeFields(observable.getExternal(LUCENE));
        if (value == null)
            return;
        boolean index = !observable.hasProperty(BayesianTerm.index, false); // True by default
        Boolean match = this.match(value, false, observable);
        if (match != null && match && !observable.getMultiplicity().isMany())
            throw new StoreException("Duplicate value " + value + " for " + observable);
        if (match == null && !variant) {
            this.store(observable, value, LUCENE, !observable.getMultiplicity().isMany() && index);
        }
        if (observable.getMultiplicity().isMany()) {
            match = this.match(value, true, observable);
            if (match == null || !match) {
                this.store(observable, value, LUCENE_VARIANT, index);
            }
        }
    }

    /**
     * Copy all values from another classifier
     *
     * @param observable The observable for the values
     * @param classifier The classifier to copy from
     */
    @Override
    public void addAll(Observable observable, Classifier classifier) {
        if (!(classifier instanceof LuceneClassifier))
            throw new IllegalArgumentException("Expecting instnce of LuceneClassifier");
        String fieldName = observable.getExternal(LUCENE);
        for (IndexableField field: ((LuceneClassifier) classifier).document.getFields(fieldName))
            this.document.add(field);
        fieldName = observable.getExternal(LUCENE_VARIANT);
        for (IndexableField field: ((LuceneClassifier) classifier).document.getFields(fieldName))
            this.document.add(field);
    }

    /**
     * Clear and observable in the classifier.
     *
     * @param observable The observable to store
     * @throws StoreException if unable to add this variable to the classifier
     */
    @Override
    public void clear(Observable observable) throws StoreException {
        this.document.removeFields(observable.getExternal(LUCENE));
        this.document.removeFields(observable.getExternal(LUCENE_VARIANT));
    }

    /**
     * Get the value for an observable.
     *
     * @param observable The observable
     * @return The associated value or null for not present
     */
    @Override
    public <T> T get(Observable<T> observable) {
        IndexableField field = this.document.getField(observable.getExternal(LUCENE));
        return this.convert(observable, field);
    }

    /**
     * Get the all values for observables.
     * <p>
     * Ensures values are ordered first by reference value and then by variant.
     * </p>
     *
     * @param observables The observable
     * @return The a set of all present values
     */
    @Override
    public <T> LinkedHashSet<T> getAll(Observable<T>... observables) {
        LinkedHashSet<T> values = new LinkedHashSet<>(observables.length);
        for (Observable<T> observable: observables) {
            IndexableField[] fs = this.document.getFields(observable.getExternal(LUCENE));
            for (IndexableField f : fs) {
                T v = this.convert(observable, f);
                if (v != null)
                    values.add(v);
            }
        }
        for (Observable<T> observable : observables) {
            if (!observable.getMultiplicity().isMany())
                continue;
            IndexableField[] fs = this.document.getFields(observable.getExternal(LUCENE_VARIANT));
            for (IndexableField f : fs) {
                T v = this.convert(observable, f);
                if (v != null)
                    values.add(v);
            }
        }
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
     */
    @Override
    public void setType(Term type) {
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
     */
    @Override
    public boolean hasAnnotation(Term annotation) {
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
     */
    @Override
    public void annotate(Term annotation) {
        this.document.add(new StringField(ANNOTATION_FIELD, annotation.qualifiedName(), Field.Store.YES));
    }

    /**
     * Get the inference parameters associcated with a classifier.
     *
     * @param parameters  The parameters
     * @throws StoreException if unable to retrieve the parameters
     */
    @Override
    public void loadParameters(Parameters parameters) throws StoreException {
        BytesRef bytes = this.document.getBinaryValue(PARAMETERS_FIELD);

        this.cachedParameters = null;
        if (bytes == null)
            return;
        try {
            parameters.loadFromBytes(bytes.bytes);
        } catch (IOException ex) {
            throw new StoreException("Unable to load parameters", ex);
        }
        this.cachedParameters = parameters;
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
            this.cachedParameters = parameters;
        } catch (IOException ex) {
            throw new StoreException("Unable to save parameters to document", ex);
        }
    }

    /**
     * Get the index values for the classifier.
     *
     * @return An array of [left, right] index integers
     */
    @Override
    public int[] getIndex() {
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
     */
    @Override
    public void setIndex(int left, int right) {
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
     */
    @Override
    public Collection<String> getNames() {
        return Arrays.asList(this.document.getValues(NAMES_FIELD));
    }

    /**
     * Set the names for a classifier.
     * <p>
     * The names contain the searchable list of names the classifier might be found under
     * </p>
     *
     * @param names The names for this classifier
     */
    @Override
    public void setNames(Collection<String> names) {
        this.document.removeFields(NAMES_FIELD);
        for (String name: names)
            document.add(new TextField(NAMES_FIELD, name, Field.Store.YES));
    }

    /**
     * Get the classifier signature
     *
     * @return The signature, or null for no identifier found.
     */
    @Override
    @JsonIgnore
    public String getSignature() {
        return this.document.get(SIGNATURE_FIELD);
    }

    /**
     * Set the signature for the classifier.
     * <p>
     * Replaces any existing signature.
     * </p>
     *
     * @param signature The classifier signature
     */
    @Override
    public void setSignature(String signature) {
        this.document.removeFields(SIGNATURE_FIELD);
        if (signature != null)
            this.document.add(new StringField(SIGNATURE_FIELD, signature, Field.Store.YES));
    }

    /**
     * Get the trail of identifiers used in hierarchical classifiers.
     * <p>
     * The trail represents the heirarchy of possible identifiers, from most general to least general (this classifier).
     * </p>
     *
     * @return The trail
     */
    @Override
    @JsonIgnore
    public List<String> getTrail() {
        String trails = this.document.get(TRAIL_FIELD);
        if (trails == null)
            return null;
        return TRAIL_SEPARATOR_REGEX.splitAsStream(trails).collect(Collectors.toList());
    }

    /**
     * Set the trail of identifiers used in the classifier hierarchy.
     *
     * @param trail The new trail
     */
    @Override
    public void setTrail(List<String> trail) {
        this.document.removeFields(TRAIL_FIELD);
        if (trail != null) {
            String trails = String.join(TRAIL_SEPARATOR, trail);
            this.document.add(new StoredField(TRAIL_FIELD, trails));
        }
    }

    /**
     * Convert a observable/value pair into a lucene field
     *
     * @param observable The term
     * @param value The object value
     * @param context The context to use
     * @param index Index this term
     *
     * @throws StoreException if unable to store the data
     *
     * @see #convert(Observable, IndexableField)
     */
    protected <T, S, Q> void store(Observable<T> observable, T value, ExternalContext context, boolean index) throws StoreException {
        if (value == null || ((value instanceof String) && ((String) value).isEmpty()))
            return;
        String field = observable.getExternal(context);
        Analysis<T, S, Q> analysis = (Analysis<T, S, Q>) observable.getAnalysis();
        S val = analysis.toStore(value);
        if (val == null)
            return;
        Normaliser normaliser = observable.getNormaliser();
        if (normaliser != null && val instanceof String)
            val = (S) normaliser.normalise((String) val);
        if (val instanceof Integer) {
            int iVal = ((Number) val).intValue();
            this.document.add(new StoredField(field, iVal));
            if (index)
                this.document.add(new IntPoint(field, iVal));
        } else if (val instanceof Number) {
            double dVal = ((Number) val).doubleValue();
            this.document.add(new StoredField(field, dVal));
            if (index)
                this.document.add(new DoublePoint(field, dVal));
        } else {
            String sv = val.toString();
            if (!index) {
                this.document.add(new StoredField(field, sv));
                return;
            }
            switch (observable.getStyle()) {
                case IDENTIFIER:
                case CANONICAL:
                    this.document.add(new StringField(field, sv, Field.Store.YES));
                    break;
                default:
                    this.document.add(new TextField(field, sv, Field.Store.YES));
                    break;
            }
        }
    }

    /**
     * Convert a field into an appropriate value for an observable.
     * <p>
     * The field is assumed to be normalised
     * </p>
     *
     * @param observable The observable
     * @param field The field
     *
     * @param <T> The implicit observable type
     *
     * @return The converted field
     *
     * @see #store(Observable, Object, ExternalContext, boolean)
     */
    protected <T> T convert(Observable observable, IndexableField field) {
        if (field == null)
            return null;
        if (observable.getType() == Integer.class || observable.getType() == Short.class || observable.getType() == Byte.class) {
            Number number = field.numericValue();
            if (number == null)
                return null;
            return (T) Integer.valueOf(number.intValue());
        }
        if (observable.getType() == Double.class || observable.getType() == Float.class) {
            Number number = field.numericValue();
            if (number == null)
                return null;
            return (T) Double.valueOf(number.doubleValue());
        }
        try {
            return (T) observable.getAnalysis().fromString(field.stringValue());
        } catch (StoreException ex) {
            log.error("Unable to convert " + field + " to " + observable + " returning null instead", ex);
            return null;
        }
    }

    /**
     * Get a list of all the values set in the classifier.
     * <p>
     * This can be used to dump the classifer during debugging.
     * </p>
     *
     * @return The values in the form of key -> value pairs, with the keys a useful internal representation
     */
    @Override
    @JsonIgnore
    public Collection<String[]> getAllValues() {
        return this.document.getFields().stream().map(f -> new String[] {f.name(), f.stringValue() }).collect(Collectors.toList());
    }
}
