package au.org.ala.names.tools;

import old.au.org.ala.names.search.NameIndexField;
import org.apache.lucene.document.Document;

import java.util.function.Function;

/**
 * Convert data from
 */
public class NameIndexConverter<NT, OT> extends IndexConverter<NT, OT> {
    /** The observable to get from the new index */
    protected String name;

    /**
     * Construct for an observable and index field
     *
     * @param name The lucene field name
     * @param field The old name index field
     */
    public NameIndexConverter(NameIndexField field, String name, Class<NT> class_) {
        this(field, name,  class_, null);
    }

    /**
     * Construct for an observable, index field and transform
     *
     * @param name The lucene field name
     * @param field The old name index field
     * @param transform The transform to use, null if none
     */
    public NameIndexConverter(NameIndexField field, String name, Class<NT> class_, Function<NT, OT> transform) {
        super(field, class_, transform);
        this.name = name;
    }

    public void convert(String id, Document from, Document to) {
        this.convert(id, from, to, this.name);
    }
}
