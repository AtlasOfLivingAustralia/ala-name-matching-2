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
import java.util.concurrent.atomic.DoubleAdder;

public class SumCollector extends SimpleCollector {
    private IndexSearcher searcher;
    private String field;
    private double defaultValue;
    private Set<String> fields;
    private int base;
    private DoubleAdder sum;

    public SumCollector(IndexSearcher searcher, String field, double defaultValue) {
        this.searcher = searcher;
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
    public void collect(int doc) throws IOException {
        Document document = this.searcher.doc(this.base + doc, this.fields);
        IndexableField value = document == null ? null : document.getField(this.field);

        this.sum.add(value == null ? this.defaultValue : value.numericValue().doubleValue());
    }

    @Override
    public ScoreMode scoreMode() {
        return ScoreMode.COMPLETE_NO_SCORES;
    }
}
