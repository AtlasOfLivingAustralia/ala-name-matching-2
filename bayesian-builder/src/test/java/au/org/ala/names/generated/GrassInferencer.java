package au.org.ala.names.generated;

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.Inferencer;
import au.org.ala.bayesian.Trace;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
  public Inference probability(GrassClassification classification, Classifier classifier, Trace trace) throws BayesianException {
    Inferencer<GrassClassification> sub = this.subInferencers.get(classifier.getSignature());
    if (sub == null)
      throw new IllegalArgumentException("Signature '" + classifier.getSignature() + "' is not recognised");
    return sub.probability(classification, classifier, trace);
  }

  @JsonPropertyOrder(alphabetic = true)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class Evidence {
    public Boolean e$rain;
    public Boolean e$sprinkler;
    public Boolean e$wet;

    @JsonIgnore
    public boolean isT$e$rain() {
      return this.e$rain == null || this.e$rain;
    }

    @JsonIgnore
    public boolean isF$e$rain() {
      return this.e$rain == null || !this.e$rain;
    }

    @JsonIgnore
    public boolean isT$e$sprinkler() {
      return this.e$sprinkler == null || this.e$sprinkler;
    }

    @JsonIgnore
    public boolean isF$e$sprinkler() {
      return this.e$sprinkler == null || !this.e$sprinkler;
    }

    @JsonIgnore
    public boolean isT$e$wet() {
      return this.e$wet == null || this.e$wet;
    }

    @JsonIgnore
    public boolean isF$e$wet() {
      return this.e$wet == null || !this.e$wet;
    }

  }
}