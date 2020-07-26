package au.org.ala.names.generated;

import au.org.ala.bayesian.*;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

import java.util.ArrayList;
import java.util.Collection;


public class GrassClassification implements Classification {

  public java.lang.String rain;
  public java.lang.String sprinkler;
  public java.lang.String wet;

  public GrassClassification() {
  }

  public GrassClassification(Classifier classifier) throws InferenceException {
    this();
    this.populate(classifier, true);
    this.infer();
  }

  @Override
  public Term getType() {
    return DwcTerm.Taxon;
  }

  @Override
  public Collection<Observation> toObservations() {
    Collection<Observation> obs = new ArrayList(3);

    if (this.rain != null)
      obs.add(new Observation(true, GrassFactory.rain, this.rain));
    if (this.sprinkler != null)
      obs.add(new Observation(true, GrassFactory.sprinkler, this.sprinkler));
    if (this.wet != null)
      obs.add(new Observation(true, GrassFactory.wet, this.wet));
    return obs;
  }

  @Override
  public void infer() throws InferenceException {
  }


  @Override
  public void populate(Classifier classifier, boolean overwrite) throws InferenceException {
    if (overwrite || this.rain == null) {
      this.rain = classifier.get(GrassFactory.rain);
    }
    if (overwrite || this.sprinkler == null) {
      this.sprinkler = classifier.get(GrassFactory.sprinkler);
    }
    if (overwrite || this.wet == null) {
      this.wet = classifier.get(GrassFactory.wet);
    }
  }

  public GrassInferencer.Evidence match(Classifier classifier) throws InferenceException {
    GrassInferencer.Evidence evidence = new GrassInferencer.Evidence();
    evidence.e$rain = classifier.match(GrassFactory.rain, this.rain);
    evidence.e$sprinkler = classifier.match(GrassFactory.sprinkler, this.sprinkler);
    evidence.e$wet = classifier.match(GrassFactory.wet, this.wet);
    return evidence;
  }

  @Override
  public void translate(Classifier classifier) throws InferenceException, StoreException {
    classifier.add(GrassFactory.rain, this.rain);
    classifier.add(GrassFactory.sprinkler, this.sprinkler);
    classifier.add(GrassFactory.wet, this.wet);
  }
}