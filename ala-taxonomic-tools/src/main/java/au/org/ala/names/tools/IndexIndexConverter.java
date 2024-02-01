package au.org.ala.names.tools;

import old.au.org.ala.names.search.NameIndexField;
import org.apache.lucene.document.Document;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Convert the tree index positions.
 */
public class IndexIndexConverter<OT> extends NameIndexConverter<Integer, Integer> {
    /** The observable to get from the new index */
    private int position;

    /**
     * Construct for an observable and index field
     *
     * @param name The lucene field name
     * @param field The old name index field
     */
    public IndexIndexConverter(NameIndexField field, String name, int position) {
        this(field, name, position, null);
    }

    /**
     * Construct for an observable, index field and transform
     *
     * @param field The old name index field
     * @param transform The transform to use, null if none
     */
    public IndexIndexConverter(NameIndexField field, String name, int position, Function<Integer, Integer> transform) {
        super(field, name, Integer.class, transform);
        this.position = position;
    }

    public void convert(String id, Document from, Document to) {
        List<Integer> indexes = Arrays.asList(from.getFields(this.name)).stream().map(fv -> fv.numericValue().intValue()).sorted().collect(Collectors.toList());
        Integer value = indexes.size() > this.position ? indexes.get(this.position) : null;
        this.field.store(value, to);
    }
}
