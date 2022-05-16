package au.org.ala.names.generated;

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.Inferencer;
import au.org.ala.bayesian.Trace;

public class GrassInferencer_ implements Inferencer<GrassClassification> {
  public final static String SIGNATURE = "";

  public GrassInferencer_() {
  }

  @Override
  public String getSignature() {
    return SIGNATURE;
  }

  public double infer_t(GrassInferencer.Evidence evidence, GrassParameters_ parameters, Trace trace) {
    double c$rain = 1.0;
    double nc$rain = 0.0;
    double c$sprinkler = evidence.isT$e$sprinkler() ? 1.0 : 0.0;
    double nc$sprinkler = evidence.isF$e$sprinkler() ? 1.0 : 0.0;
    double c$wet = 0.0;
    double nc$wet = 0.0;
    if (trace != null) {
        trace.add("rain", c$rain);
        trace.add("!rain", nc$rain);
    }
    // Ignoring non-base sprinkler
    if (trace != null) {
        trace.add("sprinkler", c$sprinkler);
        trace.add("!sprinkler", nc$sprinkler);
    }
    if (evidence.isT$e$wet()) {
      c$wet += parameters.derived_wet_t$t_t * c$rain * c$sprinkler;
      c$wet += parameters.derived_wet_t$t_f * c$rain * nc$sprinkler;
    }
    if (evidence.isF$e$wet()) {
      nc$wet += parameters.derived_wet_f$t_t * c$rain * c$sprinkler;
      nc$wet += parameters.derived_wet_f$t_f * c$rain * nc$sprinkler;
    }
    if (trace != null) {
        trace.add("wet", c$wet);
        trace.add("!wet", nc$wet);
    }
    return (c$wet + nc$wet);
  }

  public double infer_f(GrassInferencer.Evidence evidence, GrassParameters_ parameters, Trace trace) {
    double c$rain = 0.0;
    double nc$rain = 1.0;
    double c$sprinkler = evidence.isT$e$sprinkler() ? 1.0 : 0.0;
    double nc$sprinkler = evidence.isF$e$sprinkler() ? 1.0 : 0.0;
    double c$wet = 0.0;
    double nc$wet = 0.0;
    if (trace != null) {
        trace.add("rain", c$rain);
        trace.add("!rain", nc$rain);
    }
    // Ignoring non-base sprinkler
    if (trace != null) {
        trace.add("sprinkler", c$sprinkler);
        trace.add("!sprinkler", nc$sprinkler);
    }
    if (evidence.isT$e$wet()) {
      c$wet += parameters.derived_wet_t$f_t * nc$rain * c$sprinkler;
      c$wet += parameters.derived_wet_t$f_f * nc$rain * nc$sprinkler;
    }
    if (evidence.isF$e$wet()) {
      nc$wet += parameters.derived_wet_f$f_t * nc$rain * c$sprinkler;
      nc$wet += parameters.derived_wet_f$f_f * nc$rain * nc$sprinkler;
    }
    if (trace != null) {
        trace.add("wet", c$wet);
        trace.add("!wet", nc$wet);
    }
    return (c$wet + nc$wet);
  }


  public Inference probability(GrassInferencer.Evidence evidence, GrassParameters_ parameters, Trace trace) {
    double p;
    double prior = parameters.prior_rain_t;
    double ph = 0.0;
    double pe = 0.0;

    if (trace != null)
        trace.push("t");
    try {
        if (evidence.isT$e$rain()) {
          p = this.infer_t(evidence, parameters, trace) * parameters.prior_rain_t;
          ph += p;
          pe += p;
        }
    } finally {
        if (trace != null)
            trace.pop();
    }
    if (trace != null)
        trace.push("f");
    try {
        if (evidence.isF$e$rain()) {
          p = this.infer_f(evidence, parameters, trace) * parameters.prior_rain_f;
          pe += p;
        }
    } finally {
        if (trace != null)
            trace.pop();
    }
    return Inference.forPEH(prior, pe, ph);
  }

  @Override
  public Inference probability(GrassClassification classification, Classifier classifier, Trace trace) throws BayesianException {
    if (trace != null)
        trace.push("inference");
    try {
        GrassInferencer.Evidence evidence = classification.match(classifier);
        if (trace != null)
            trace.add("evidence", evidence);
        GrassParameters_ params = (GrassParameters_) classifier.getCachedParameters();
        if (params == null) {
          params = new GrassParameters_();
          classifier.loadParameters(params);
        }
        return this.probability(evidence, params, trace);
    } finally {
        if (trace != null)
            trace.pop();
    }
  }
}