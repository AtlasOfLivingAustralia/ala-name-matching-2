package au.org.ala.names.tools;

import au.org.ala.bayesian.ExternalContext;
import au.org.ala.bayesian.Observable;
import old.au.org.ala.names.search.NameIndexField;
import org.apache.lucene.document.Document;

import java.util.function.Function;

/**
 * Convert data from
 */
public class ObservableIndexConverter<NT, OT> extends IndexConverter<NT, OT> {
    /** The observable to get from the new index */
    protected Observable observable;

    /**
     * Construct for an observable and index field
     *
     * @param observable The bayesian observable
     * @param field The old name index field
     */
    public ObservableIndexConverter(NameIndexField field, Observable observable) {
        this(field, observable, null);
    }

    /**
     * Construct for an observable, index field and transform
     *
     * @param observable The bayesian observable
     * @param field The old name index field
     * @param transform The transform to use, null if none
     */
    public ObservableIndexConverter(NameIndexField field, Observable observable, Function<NT, OT> transform) {
        super(field, observable.getAnalysis().getStoreType(), transform);
        this.observable = observable;
    }

    public void convert(String id, Document from, Document to) {
        this.convert(id, from, to, this.observable.getExternal(ExternalContext.LUCENE));
    }
}
