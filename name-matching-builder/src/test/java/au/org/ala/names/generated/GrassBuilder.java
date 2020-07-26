package au.org.ala.names.generated;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.StoreException;
import au.org.ala.names.builder.Builder;

import java.util.Deque;


public class GrassBuilder implements Builder<GrassParameters> {

  public GrassBuilder() {
  }

  @Override
  public void infer(Classifier classifier) throws InferenceException, StoreException {
  }

    @Override
    public void expand(Classifier classifier, Deque<Classifier> parents) throws InferenceException, StoreException {
  }

  @Override
  public void calculate(GrassParameters parameters, ParameterAnalyser analyser, Classifier classifier) throws InferenceException, StoreException {
    parameters.prior_t$rain = analyser.computePrior(analyser.getObservation(true, GrassFactory.rain, classifier));
    parameters.inf_t_t$sprinkler = analyser.computeConditional(analyser.getObservation(true, GrassFactory.sprinkler, classifier) , analyser.getObservation(true, GrassFactory.rain, classifier));
    parameters.inf_t_f$sprinkler = analyser.computeConditional(analyser.getObservation(true, GrassFactory.sprinkler, classifier) , analyser.getObservation(false, GrassFactory.rain, classifier));
    parameters.inf_t_tt$wet = analyser.computeConditional(analyser.getObservation(true, GrassFactory.wet, classifier) , analyser.getObservation(true, GrassFactory.rain, classifier), analyser.getObservation(true, GrassFactory.sprinkler, classifier));
    parameters.inf_t_tf$wet = analyser.computeConditional(analyser.getObservation(true, GrassFactory.wet, classifier) , analyser.getObservation(true, GrassFactory.rain, classifier), analyser.getObservation(false, GrassFactory.sprinkler, classifier));
    parameters.inf_t_ft$wet = analyser.computeConditional(analyser.getObservation(true, GrassFactory.wet, classifier) , analyser.getObservation(false, GrassFactory.rain, classifier), analyser.getObservation(true, GrassFactory.sprinkler, classifier));
    parameters.inf_t_ff$wet = analyser.computeConditional(analyser.getObservation(true, GrassFactory.wet, classifier) , analyser.getObservation(false, GrassFactory.rain, classifier), analyser.getObservation(false, GrassFactory.sprinkler, classifier));
  }
}
