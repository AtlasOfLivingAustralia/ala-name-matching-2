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
    parameters.inf_t_t$taxonRank$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.taxonRank, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$taxonRank$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.taxonRank, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$taxonRank$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.taxonRank, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$taxonRank$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.taxonRank, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$specificEpithet$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.specificEpithet, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$specificEpithet$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.specificEpithet, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$specificEpithet$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.specificEpithet, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$specificEpithet$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.specificEpithet, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$scientificNameAuthorship$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificNameAuthorship, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$scientificNameAuthorship$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificNameAuthorship, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$scientificNameAuthorship$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificNameAuthorship, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$scientificNameAuthorship$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificNameAuthorship, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_tt$scientificName$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.specificEpithet, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_tt$scientificName$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.specificEpithet, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_tf$scientificName$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.specificEpithet, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_tf$scientificName$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.specificEpithet, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_ft$scientificName$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.specificEpithet, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_ft$scientificName$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.specificEpithet, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_ff$scientificName$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.specificEpithet, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_ff$scientificName$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.specificEpithet, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$soundexScientificName$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.soundexScientificName, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$soundexScientificName$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.soundexScientificName, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$soundexScientificName$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.soundexScientificName, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.scientificName, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$soundexScientificName$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.soundexScientificName, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.scientificName, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_tt$genus$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.genus, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.soundexScientificName, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_tt$genus$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.genus, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.soundexScientificName, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_tf$genus$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.genus, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.soundexScientificName, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_tf$genus$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.genus, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.scientificName, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.soundexScientificName, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_ft$genus$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.genus, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.scientificName, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.soundexScientificName, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_ft$genus$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.genus, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.scientificName, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.soundexScientificName, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_ff$genus$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.genus, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.scientificName, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.soundexScientificName, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_ff$genus$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.genus, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.scientificName, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.soundexScientificName, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$family$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.family, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.genus, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$family$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.family, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.genus, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$family$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.family, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.genus, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$family$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.family, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.genus, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$order$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.order, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.family, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$order$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.order, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.family, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$order$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.order, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.family, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$order$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.order, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.family, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$class_$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.class_, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.order, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$class_$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.class_, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.order, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$class_$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.class_, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.order, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$class_$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.class_, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.order, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$phylum$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.phylum, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.class_, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$phylum$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.phylum, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.class_, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$phylum$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.phylum, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.class_, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$phylum$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.phylum, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.class_, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$kingdom$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.kingdom, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.phylum, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_t$kingdom$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.kingdom, classifier) , analyser.getObservation(true, SimpleLinnaeanFactory.phylum, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$kingdom$t = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.kingdom, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.phylum, classifier), analyser.getObservation(true, SimpleLinnaeanFactory.taxonId, classifier));
    parameters.inf_t_f$kingdom$f = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanFactory.kingdom, classifier) , analyser.getObservation(false, SimpleLinnaeanFactory.phylum, classifier), analyser.getObservation(false, SimpleLinnaeanFactory.taxonId, classifier));
    return parameters;
  }
}
