package au.org.ala.bayesian;

import au.org.ala.util.BasicNormaliser;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

import java.util.*;

public class TestClassification implements Classification {
    public static final Normaliser NORMALISER = new BasicNormaliser("basic", true, true, true, true, false);
    public static final Observable CLASS_ = new Observable(DwcTerm.class_);
    public static final Observable SCIENTIFIC_NAME = new Observable(DwcTerm.scientificName);
    public static final Observable VERNACULAR_NAME = new Observable(DwcTerm.vernacularName);
    public static final List<Observable> OBSERVABLES = Collections.unmodifiableList(Arrays.asList(
            CLASS_,
            SCIENTIFIC_NAME,
            VERNACULAR_NAME
    ));

    static {
        CLASS_.setNormaliser(NORMALISER);
        CLASS_.setStyle(Observable.Style.PHRASE);
        SCIENTIFIC_NAME.setRequired(true);
        SCIENTIFIC_NAME.setStyle(Observable.Style.PHRASE);
        SCIENTIFIC_NAME.setNormaliser(NORMALISER);
        VERNACULAR_NAME.setNormaliser(NORMALISER);
        VERNACULAR_NAME.setStyle(Observable.Style.PHRASE);
    }

    public String class_;
    public String scientificName;
    public String vernacularName;


    @Override
    public Term getType() {
        return DwcTerm.Taxon;
    }

    @Override
    public Collection<Observation> toObservations() {
        Collection<Observation> obs = new ArrayList<>(12);

        if (this.class_ != null)
            obs.add(new Observation(true, CLASS_, this.class_));
        if (this.scientificName != null)
            obs.add(new Observation(true, SCIENTIFIC_NAME, this.scientificName));
        if (this.vernacularName != null)
            obs.add(new Observation(true, VERNACULAR_NAME, this.vernacularName));
        return obs;
    }

     @Override
    public void infer() {

    }

    @Override
    public void populate(Classifier classifier, boolean overwrite) throws InferenceException {
        if (overwrite || this.class_ == null)
            this.class_ = classifier.get(CLASS_);
        if (overwrite || this.scientificName == null)
            this.scientificName = classifier.get(SCIENTIFIC_NAME);
        if (overwrite || this.vernacularName == null)
            this.vernacularName = classifier.get(VERNACULAR_NAME);
    }

    @Override
    public void translate(Classifier classifier) throws StoreException {
        classifier.add(CLASS_, this.class_);
        classifier.add(SCIENTIFIC_NAME, this.scientificName);
        classifier.add(VERNACULAR_NAME, this.vernacularName);
    }
}
