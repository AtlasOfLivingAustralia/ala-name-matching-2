package au.org.ala.names.generated;

import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Inferencer;
import au.org.ala.bayesian.StoreException;

public class GrassInferencer implements Inferencer<GrassClassification, GrassParameters> {

  public double infer(Evidence evidence, GrassParameters parameters, double c$rain) {
    double nc$rain = 1.0 - c$rain;
    double c$sprinkler = evidence.isT$e$sprinkler() ? 1.0 : 0.0;
    double nc$sprinkler = evidence.isF$e$sprinkler() ? 1.0 : 0.0;
    double c$wet = 0.0;
    double nc$wet = 0.0;
    // Ignoring non-base sprinkler
    if (evidence.isT$e$wet()) {
      c$wet += parameters.derived_t_tt$wet * c$sprinkler * c$rain;
      c$wet += parameters.derived_t_tf$wet * c$sprinkler * nc$rain;
      c$wet += parameters.derived_t_ft$wet * nc$sprinkler * c$rain;
      c$wet += parameters.derived_t_ff$wet * nc$sprinkler * nc$rain;
    }
    if (evidence.isF$e$wet()) {
      nc$wet += parameters.derived_f_tt$wet * c$sprinkler * c$rain;
      nc$wet += parameters.derived_f_tf$wet * c$sprinkler * nc$rain;
      nc$wet += parameters.derived_f_ft$wet * nc$sprinkler * c$rain;
      nc$wet += parameters.derived_f_ff$wet * nc$sprinkler * nc$rain;
    }
    return (c$wet + nc$wet) * (parameters.prior_t$rain * c$rain + parameters.prior_f$rain * nc$rain);
  }

  public Inference probability(Evidence evidence, GrassParameters parameters) {
    double p;
    double prior = parameters.prior_t$rain;
    double ph = 0.0;
    double pe = 0.0;

    p = (evidence.isT$e$rain() ? 1.0 : 0.0) * this.infer(evidence, parameters, 1.0);
    ph += p;
    pe += p;
    p = (evidence.isF$e$rain() ? 1.0 : 0.0) * this.infer(evidence, parameters, 0.0);
    pe += p;
    return Inference.forPEH(prior, pe, ph);
  }

  @Override
  public Inference probability(GrassClassification classification, Classifier classifier, GrassParameters parameters) throws StoreException, InferenceException {
    Evidence evidence = classification.match(classifier);
    return this.probability(evidence, parameters);
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