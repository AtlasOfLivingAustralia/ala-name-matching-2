package au.org.ala.names.generated;

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.Inferencer;

import java.util.HashMap;
import java.util.Map;


public class GrassInferencer implements Inferencer<GrassClassification> {
  private Map<String, Inferencer<GrassClassification>> subInferencers;

  // Assumed to be stateless
  private static final Inferencer<GrassClassification>[] INFERENCERS = new Inferencer[] {
    new GrassInferencer_()
  };

  public GrassInferencer() {
    this.subInferencers = new HashMap<>(INFERENCERS.length);
    for (Inferencer<GrassClassification> i: INFERENCERS)
      this.subInferencers.put(i.getSignature(), i);
  }

  @Override
  public String getSignature() {
    return null;
  }

  @Override
  public Inference probability(GrassClassification classification, Classifier classifier) throws BayesianException {
    Inferencer<GrassClassification> sub = this.subInferencers.get(classifier.getSignature());
    if (sub == null)
      throw new IllegalArgumentException("Signature '" + classifier.getSignature() + "' is not recognised");
    return sub.probability(classification, classifier);
  }

  public static class Evidence {
    public Boolean e$rain;
    public Boolean e$sprinkler;
    public Boolean e$wet;

    public boolean isT$e$rain() {
      return this.e$rain == null || this.e$rain;
    }

    public boolean isF$e$rain() {
      return this.e$rain == null || !this.e$rain;
    }

    public boolean isT$e$sprinkler() {
      return this.e$sprinkler == null || this.e$sprinkler;
    }

    public boolean isF$e$sprinkler() {
      return this.e$sprinkler == null || !this.e$sprinkler;
    }

    public boolean isT$e$wet() {
      return this.e$wet == null || this.e$wet;
    }

    public boolean isF$e$wet() {
      return this.e$wet == null || !this.e$wet;
    }

  }
}