package au.org.ala.names.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.SimpleCollector;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.DoubleAdder;

public class SumCollector extends SimpleCollector {
    private IndexSearcher searcher;
    private ConcurrentMap<Integer, Double> cache;
    private String field;
    private double defaultValue;
    private Set<String> fields;
    private int base;
    private DoubleAdder sum;

    public SumCollector(IndexSearcher searcher, ConcurrentMap<Integer, Double> cache, String field, double defaultValue) {
        this.searcher = searcher;
        this.cache = cache;
        this.field = field;
        this.defaultValue = defaultValue;
        this.fields = Collections.singleton(field);
        this.sum = new DoubleAdder();
    }

    /**
     * Get the accumulated sum
     *
     * @return The sum
     */
    public double getSum() {
        return this.sum.sum();
    }

    @Override
    protected void doSetNextReader(LeafReaderContext context) throws IOException {
        super.doSetNextReader(context);
        this.base = context.docBase;
    }

    @Override
    public void collect(int doc) {
        int docID = this.base + doc;
        double value = this.cache.computeIfAbsent(docID, key -> {
            Document document = null;
            try {
                document = this.searcher.doc(key, this.fields);
            } catch (IOException ex) {
                throw new IllegalStateException("Unable to retrieve document " + key, ex);
            }
            IndexableField val = document == null ? null : document.getField(this.field);
            return val == null ? this.defaultValue : val.numericValue().doubleValue();
        });
        this.sum.add(value);
    }

    @Override
    public ScoreMode scoreMode() {
        return ScoreMode.COMPLETE_NO_SCORES;
    }
}
