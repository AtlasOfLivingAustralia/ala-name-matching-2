package au.org.ala.names.tools;

import old.au.org.ala.names.search.NameIndexField;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import java.util.function.Function;

abstract public class IndexConverter<NT, OT> {
    /** The field this index converter is for */
    protected NameIndexField field;
    /** The new stored type for the object */
    protected Class<NT> storeClass;
    /** The transform for the value */
    protected Function<NT, OT> transform;

    /**
     * Construct for a name index field
     *
     * @param field The field
     * @param storeClass How the value is stored in the new-style index
     * @param transform Any transform needed
     */
    public IndexConverter(NameIndexField field, Class<NT> storeClass, Function<NT, OT> transform) {
        this.field = field;
        this.storeClass = storeClass;
        this.transform = transform;
    }

    public void convert(String id, Document from, Document to, String name) {
        for (IndexableField fv: from.getFields(name)) {
            OT sv = null;
            if (Integer.class.isAssignableFrom(this.storeClass)) {
                Integer value = fv.numericValue().intValue();
                if (transform != null)
                    sv = this.transform.apply((NT) value);
                else
                    sv = (OT) value;
            } else if (Double.class.isAssignableFrom(storeClass)) {
                Double value = fv.numericValue().doubleValue();
                if (transform != null)
                    sv = this.transform.apply((NT) value);
                else
                    sv = (OT) value;
            } else {
                String value = fv.stringValue();
                if (transform != null)
                    sv = this.transform.apply((NT) value);
                else
                    sv = (OT) value;
            }
            this.field.store(sv, to);
        }
    }

    abstract public void convert(String id, Document from, Document to);

}
