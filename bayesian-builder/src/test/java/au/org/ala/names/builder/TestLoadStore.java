package au.org.ala.names.builder;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.*;
import au.org.ala.names.lucene.LuceneClassifier;
import org.gbif.dwc.terms.Term;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static au.org.ala.bayesian.ExternalContext.LUCENE;

/**
 * A test load store that just keeps a
 */
public class TestLoadStore extends LoadStore<LuceneClassifier> {
    private Map<String, LuceneClassifier> store;

    public TestLoadStore(String name) throws StoreException {
        super(name);
        this.store = new HashMap<>();
    }

    /**
     * Is this a temporary store?
     *
     * @return Always true.
     */
    @Override
    public boolean isTemporary() {
        return true;
    }

    /**
     * Get the store location
     *
     * @return This is an in-memory store
     */
    @Override
    public String getLocation() {
        return "memory";
    }

    /**
     * Get the observation that allows us to find annotations.
     *
     * @param annotation
     * @return The annotation observable
     */
    @Override
    public Observation getAnnotationObservation(Term annotation) {
        return new Observation(true, Observable.string(LuceneClassifier.ANNOTATION_FIELD), annotation.qualifiedName());
    }

    /**
     * Create a new, empty classifier
     *
     * @return The new classifier
     */
    @Override
    protected LuceneClassifier doNewClassifier() {
        return new LuceneClassifier();
    }

    public List<LuceneClassifier> getStore() {
        return new ArrayList<>(store.values());
    }


    @Override
    protected void doStore(LuceneClassifier classifier) {
        String id = classifier.identify();
        this.store.put(id, classifier);
    }

    @Override
    protected void doStore(LuceneClassifier classifier, Term type) throws BayesianException {
        String id = classifier.identify();
        classifier.setType(type);
        this.store.put(id, classifier);
    }

    @Override
    protected void doUpdate(LuceneClassifier classifier) throws StoreException {
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
    public ParameterAnalyser getParameterAnalyser(Network network, Observable weight, double defaultWeight) {
        return new ParameterAnalyser() {
            @Override
            public double getTotalWeight() {
                return 1.0;
            }

            @Override
            public double computePrior(Observation observation) {
                return 1.0;
            }

            @Override
            public double computeConditional(Observation observation, Observation... inputs) {
                return 1.0;
            }
        };
    }

    @Override
    protected LuceneClassifier doGet(Term type, Observable observable, String value) throws StoreException {
        final String field = observable.getExternal(LUCENE);
        final String typeValue = type.qualifiedName();
        Predicate<LuceneClassifier> test = classifier -> Objects.equals(classifier.getDocument().get(LuceneClassifier.TYPE_FIELD), typeValue) && Objects.equals(classifier.getDocument().get(field), value);
        return this.store.values().stream().filter(test).findAny().orElse(null);
    }

    @Override
    protected Iterable<LuceneClassifier> doGetAll(Term type, Observation... values) throws StoreException {
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
    public int count(Term type, Observation... values) throws StoreException {
         final String typeValue = type.qualifiedName();
         Predicate<LuceneClassifier> test = classifier -> Objects.equals(classifier.getDocument().get(LuceneClassifier.TYPE_FIELD), typeValue);
         for (Observation ob: values) {
             String field = ob.getObservable().getExternal(LUCENE);
             if (ob.isPositive())
                 test = test.and(classifier -> Objects.equals(classifier.getDocument().get(field), ob.getValue()));
             else
                 test = test.and(classifier -> !Objects.equals(classifier.getDocument().get(ob.getObservable().getExternal(LUCENE)), ob.getValue()));
         }
         return (int) this.store.values().stream().filter(test).count();
    }

    @Override
    public void commit() throws StoreException {
    }

    @Override
    public void close() throws StoreException {
    }
}
