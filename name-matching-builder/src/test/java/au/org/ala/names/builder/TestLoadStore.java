package au.org.ala.names.builder;

import au.org.ala.bayesian.*;
import au.org.ala.bayesian.Observable;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.gbif.dwc.terms.Term;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A test load store that just keeps a
 */
public class TestLoadStore extends LoadStore {
    private Map<String, Document> store;

    public TestLoadStore(Annotator annotator) throws StoreException {
        super(annotator);
        this.store = new HashMap<>();
    }

    public List<Document> getStore() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void store(Document document, Term type) throws StoreException {
        String id = this.annotator.identify(document);
        this.annotator.type(document, type);
        this.annotator.annotate(document);
        this.store.put(id, document);
    }

    @Override
    public void update(Document document) throws StoreException {
        String id = this.annotator.getIdentifier(document);
        if (id == null)
            throw new StoreException("No identifier for " + document);
        this.store.put(id, document);
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
    public ParameterAnalyser<Document> getParameterAnalyser(Network network, Observable weight, double defaultWeight) throws InferenceException, StoreException {
        return new ParameterAnalyser<Document>() {
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

            @Override
            public Observation getObservation(boolean positive, String id, Document document) throws InferenceException {
                return null;
            }
        };
    }

    @Override
    public Document get(Term type, Observable observable, String value) throws StoreException {
        final String field = observable.getField();
        final String typeValue = type.qualifiedName();
        Predicate<Document> test = doc -> Objects.equals(doc.get(this.annotator.getTypeField()), typeValue) && Objects.equals(doc.get(field), value);
        return this.store.values().stream().filter(test).findAny().orElse(null);
    }

    @Override
    public Iterable<Document> getAll(Term type, Observation... values) throws StoreException {
        final String typeValue = type.qualifiedName();
        Predicate<Document> test = doc -> Objects.equals(doc.get(this.annotator.getTypeField()), typeValue);
        for (Observation ob: values) {
            if (ob.isPositive())
                test = test.and(doc -> Objects.equals(doc.get(ob.getObservable().getField()), ob.getValue()));
            else
                test = test.and(doc -> !Objects.equals(doc.get(ob.getObservable().getField()), ob.getValue()));
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
