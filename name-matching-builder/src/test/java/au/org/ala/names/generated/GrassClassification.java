package au.org.ala.names.generated;

import au.org.ala.bayesian.Classification;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Observation;

import java.util.ArrayList;
import java.util.Collection;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;


public class GrassClassification extends Classification {

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
      obs.add(new Observation(true, GrassObservables.rain, this.rain));
    if (this.sprinkler != null)
      obs.add(new Observation(true, GrassObservables.sprinkler, this.sprinkler));
    if (this.wet != null)
      obs.add(new Observation(true, GrassObservables.wet, this.wet));
    return obs;
  }

  @Override
  public void infer() throws InferenceException {
  }


  @Override
  public void populate(Classifier classifier, boolean overwrite) throws InferenceException {
    if (overwrite || this.rain == null) {
      this.rain = classifier.get(GrassObservables.rain);
    }
    if (overwrite || this.sprinkler == null) {
      this.sprinkler = classifier.get(GrassObservables.sprinkler);
    }
    if (overwrite || this.wet == null) {
      this.wet = classifier.get(GrassObservables.wet);
    }
  }

  public GrassInference.Evidence match(Classifier classifier) throws InferenceException {
    GrassInference.Evidence evidence = new GrassInference.Evidence();
    evidence.e$rain = classifier.match(GrassObservables.rain, this.rain);
    evidence.e$sprinkler = classifier.match(GrassObservables.sprinkler, this.sprinkler);
    evidence.e$wet = classifier.match(GrassObservables.wet, this.wet);
    return evidence;
  }

}