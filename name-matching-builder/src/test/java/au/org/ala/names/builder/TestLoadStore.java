package au.org.ala.names.builder;

import au.org.ala.bayesian.*;
import au.org.ala.bayesian.Observable;
import au.org.ala.names.lucene.LuceneClassifier;
import au.org.ala.names.model.ExternalContext;
import org.apache.lucene.document.Document;
import org.gbif.dwc.terms.Term;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static au.org.ala.names.model.ExternalContext.LUCENE;

/**
 * A test load store that just keeps a
 */
public class TestLoadStore extends LoadStore<LuceneClassifier> {
    private Map<String, LuceneClassifier> store;

    public TestLoadStore(Annotator annotator) throws StoreException {
        super(annotator);
        this.store = new HashMap<>();
    }

    /**
     * Get the observation that allows us to find annotations.
     *
     * @param annotation
     * @return The annotation observable
     */
    @Override
    public Observation getAnnotationObservation(Term annotation) {
        return new Observation(true, new Observable(LuceneClassifier.ANNOTATION_FIELD), annotation.qualifiedName());
    }

    /**
     * Create a new, empty classifier
     *
     * @return The new classifier
     */
    @Override
    public LuceneClassifier newClassifier() {
        return new LuceneClassifier();
    }

    public List<LuceneClassifier> getStore() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void store(LuceneClassifier classifier, Term type) throws StoreException {
        String id = classifier.identify();
        classifier.setType(type);
        this.annotator.annotate(classifier);
        this.store.put(id, classifier);
    }

    @Override
    public void update(LuceneClassifier classifier) throws StoreException {
        String id = classifier.getIdentifier();
        if (id == null)
            throw new StoreException("No identifier for " + classifier);
        this.store.put(id, classifier);
    }

    /**
     * Get a parameter analyser for this store.
     *
     * @param network       The underlying network
     * @param weight        The weight observable
     * @param defaultWeight The default weight
     * @return The parameter analyser
     */
    @Override
    public ParameterAnalyser getParameterAnalyser(Network network, Observable weight, double defaultWeight) throws InferenceException, StoreException {
        return new ParameterAnalyser() {
            @Override
            public double getTotalWeight() {
                return 1.0;
            }

            @Override
            public double computePrior(Observation observation) throws InferenceException {
                return 1.0;
            }

            @Override
            public double computeConditional(Observation observation, Observation... inputs) throws InferenceException {
                return 1.0;
            }
        };
    }

    @Override
    public LuceneClassifier get(Term type, Observable observable, String value) throws StoreException {
        final String field = observable.getExternal(LUCENE);
        final String typeValue = type.qualifiedName();
        Predicate<LuceneClassifier> test = classifier -> Objects.equals(classifier.getDocument().get(LuceneClassifier.TYPE_FIELD), typeValue) && Objects.equals(classifier.getDocument().get(field), value);
        return this.store.values().stream().filter(test).findAny().orElse(null);
    }

    @Override
    public Iterable<LuceneClassifier> getAll(Term type, Observation... values) throws StoreException {
        final String typeValue = type.qualifiedName();
        Predicate<LuceneClassifier> test = classifier -> Objects.equals(classifier.getDocument().get(LuceneClassifier.TYPE_FIELD), typeValue);
        for (Observation ob: values) {
            String field = ob.getObservable().getExternal(LUCENE);
            if (ob.isPositive())
                test = test.and(classifier -> Objects.equals(classifier.getDocument().get(field), ob.getValue()));
            else
                test = test.and(classifier -> !Objects.equals(classifier.getDocument().get(ob.getObservable().getExternal(LUCENE)), ob.getValue()));
        }
        return this.store.values().stream().filter(test).collect(Collectors.toList());
    }

    @Override
    public void commit() throws StoreException {
    }

    @Override
    public void close() throws StoreException {
    }
}
