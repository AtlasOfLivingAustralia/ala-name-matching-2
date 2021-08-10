package au.org.ala.names.generated;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.Parameters;
import au.org.ala.bayesian.StoreException;
import au.org.ala.names.builder.Builder;

import java.util.Arrays;
import java.util.Deque;
import java.util.Optional;

import au.org.ala.bayesian.analysis.StringAnalysis;
import au.org.ala.bayesian.analysis.DoubleAnalysis;

public class SimpleLinnaeanBuilder_TT implements Builder {
  public final static String SIGNATURE = "TT";

  public  SimpleLinnaeanBuilder_TT() {
  }

  @Override
  public String getSignature() {
    return SIGNATURE;
  }

  @Override
  public void infer(Classifier classifier) throws InferenceException, StoreException {
    throw new UnsupportedOperationException("Sub-builders do not support this operation");
  }

  @Override
  public void expand(Classifier classifier, Deque<Classifier> parents) throws InferenceException, StoreException {
    throw new UnsupportedOperationException("Sub-builders do not support this operation");
  }

  @Override
  public String buildSignature(Classifier classifier) {
    return SIGNATURE;
  }

  @Override
  public Parameters calculate(ParameterAnalyser analyser, Classifier classifier) throws InferenceException, StoreException {
    SimpleLinnaeanParameters_TT parameters = new SimpleLinnaeanParameters_TT();
    parameters.prior_t$taxonId = analyser.computePrior(analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$taxonRank = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.taxonRank, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$taxonRank = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.taxonRank, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$specificEpithet = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.specificEpithet, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$specificEpithet = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.specificEpithet, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$scientificNameAuthorship = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificNameAuthorship, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$scientificNameAuthorship = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificNameAuthorship, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_tt$scientificName = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.specificEpithet, classifier));
    parameters.inf_t_tf$scientificName = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.specificEpithet, classifier));
    parameters.inf_t_ft$scientificName = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.specificEpithet, classifier));
    parameters.inf_t_ff$scientificName = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.specificEpithet, classifier));
    parameters.inf_t_t$soundexScientificName = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.soundexScientificName, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier));
    parameters.inf_t_f$soundexScientificName = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.soundexScientificName, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.scientificName, classifier));
    parameters.inf_t_tt$genus = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.genus, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.soundexScientificName, classifier));
    parameters.inf_t_tf$genus = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.genus, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.soundexScientificName, classifier));
    parameters.inf_t_ft$genus = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.genus, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.scientificName, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.soundexScientificName, classifier));
    parameters.inf_t_ff$genus = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.genus, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.scientificName, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.soundexScientificName, classifier));
    parameters.inf_t_t$family = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.family, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.genus, classifier));
    parameters.inf_t_f$family = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.family, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.genus, classifier));
    parameters.inf_t_t$order = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.order, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.family, classifier));
    parameters.inf_t_f$order = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.order, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.family, classifier));
    parameters.inf_t_t$class_ = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.class_, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.order, classifier));
    parameters.inf_t_f$class_ = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.class_, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.order, classifier));
    parameters.inf_t_t$phylum = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.phylum, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.class_, classifier));
    parameters.inf_t_f$phylum = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.phylum, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.class_, classifier));
    parameters.inf_t_t$kingdom = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.kingdom, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.phylum, classifier));
    parameters.inf_t_f$kingdom = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.kingdom, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.phylum, classifier));
    return parameters;
  }
}
