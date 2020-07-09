package au.org.ala.names.generated;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.StoreException;
import au.org.ala.names.builder.Builder;

import java.util.Arrays;
import java.util.Deque;
import java.util.Optional;

import au.org.ala.util.TaxonNameSoundEx;

public class SimpleLinnaeanBuilder extends Builder<SimpleLinnaeanParameters> {
  private TaxonNameSoundEx soundex;

  public SimpleLinnaeanBuilder() {
    this.soundex = new TaxonNameSoundEx();
  }

  @Override
  public void infer(Classifier classifier) throws InferenceException, StoreException {
    java.lang.String e_5 = classifier.get(SimpleLinnaeanObservables.taxonRank);
    for (Object v: classifier.getAll(SimpleLinnaeanObservables.scientificName)) {
      Object d = this.soundex.treatWord((String) v, e_5);
      classifier.add(SimpleLinnaeanObservables.soundexScientificName, d);
    }
  }

    @Override
    public void expand(Classifier classifier, Deque<Classifier> parents) throws InferenceException, StoreException {
    Optional<Classifier> d_6 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanObservables.taxonRank).stream().anyMatch(x -> "genus".equals(x))).findFirst();
    if (d_6.isPresent()){
      for(Object v: d_6.get().getAll(SimpleLinnaeanObservables.scientificName)){
        Object d = v;
        classifier.add(SimpleLinnaeanObservables.genus, d);
      }
    }
    Optional<Classifier> d_7 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanObservables.taxonRank).stream().anyMatch(x -> "family".equals(x))).findFirst();
    if (d_7.isPresent()){
      for(Object v: d_7.get().getAll(SimpleLinnaeanObservables.scientificName)){
        Object d = v;
        classifier.add(SimpleLinnaeanObservables.family, d);
      }
    }
    Optional<Classifier> d_8 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanObservables.taxonRank).stream().anyMatch(x -> "order".equals(x))).findFirst();
    if (d_8.isPresent()){
      for(Object v: d_8.get().getAll(SimpleLinnaeanObservables.scientificName)){
        Object d = v;
        classifier.add(SimpleLinnaeanObservables.order, d);
      }
    }
    Optional<Classifier> d_9 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanObservables.taxonRank).stream().anyMatch(x -> "class".equals(x))).findFirst();
    if (d_9.isPresent()){
      for(Object v: d_9.get().getAll(SimpleLinnaeanObservables.scientificName)){
        Object d = v;
        classifier.add(SimpleLinnaeanObservables.class_, d);
      }
    }
    Optional<Classifier> d_10 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanObservables.taxonRank).stream().anyMatch(x -> "phylum".equals(x))).findFirst();
    if (d_10.isPresent()){
      for(Object v: d_10.get().getAll(SimpleLinnaeanObservables.scientificName)){
        Object d = v;
        classifier.add(SimpleLinnaeanObservables.phylum, d);
      }
    }
    Optional<Classifier> d_11 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanObservables.taxonRank).stream().anyMatch(x -> "kingdom".equals(x))).findFirst();
    if (d_11.isPresent()){
      for(Object v: d_11.get().getAll(SimpleLinnaeanObservables.scientificName)){
        Object d = v;
        classifier.add(SimpleLinnaeanObservables.kingdom, d);
      }
    }
  }

  @Override
  public SimpleLinnaeanParameters createParameters() {
      return new SimpleLinnaeanParameters();
  }

  @Override
  public void calculate(SimpleLinnaeanParameters parameters, ParameterAnalyser analyser, Classifier classifier) throws InferenceException, StoreException {
    parameters.prior_t$taxonId = analyser.computePrior(analyser.getObservation(true, SimpleLinnaeanObservables.taxonId, classifier));
    parameters.inf_t_t$taxonRank = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.taxonRank, classifier) , analyser.getObservation(true, SimpleLinnaeanObservables.taxonId, classifier));
    parameters.inf_t_f$taxonRank = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.taxonRank, classifier) , analyser.getObservation(false, SimpleLinnaeanObservables.taxonId, classifier));
    parameters.inf_t_t$specificEpithet = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.specificEpithet, classifier) , analyser.getObservation(true, SimpleLinnaeanObservables.taxonId, classifier));
    parameters.inf_t_f$specificEpithet = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.specificEpithet, classifier) , analyser.getObservation(false, SimpleLinnaeanObservables.taxonId, classifier));
    parameters.inf_t_t$scientificNameAuthorship = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.scientificNameAuthorship, classifier) , analyser.getObservation(true, SimpleLinnaeanObservables.taxonId, classifier));
    parameters.inf_t_f$scientificNameAuthorship = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.scientificNameAuthorship, classifier) , analyser.getObservation(false, SimpleLinnaeanObservables.taxonId, classifier));
    parameters.inf_t_tt$scientificName = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.scientificName, classifier) , analyser.getObservation(true, SimpleLinnaeanObservables.taxonId, classifier), analyser.getObservation(true, SimpleLinnaeanObservables.specificEpithet, classifier));
    parameters.inf_t_tf$scientificName = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.scientificName, classifier) , analyser.getObservation(true, SimpleLinnaeanObservables.taxonId, classifier), analyser.getObservation(false, SimpleLinnaeanObservables.specificEpithet, classifier));
    parameters.inf_t_ft$scientificName = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.scientificName, classifier) , analyser.getObservation(false, SimpleLinnaeanObservables.taxonId, classifier), analyser.getObservation(true, SimpleLinnaeanObservables.specificEpithet, classifier));
    parameters.inf_t_ff$scientificName = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.scientificName, classifier) , analyser.getObservation(false, SimpleLinnaeanObservables.taxonId, classifier), analyser.getObservation(false, SimpleLinnaeanObservables.specificEpithet, classifier));
    parameters.inf_t_t$soundexScientificName = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.soundexScientificName, classifier) , analyser.getObservation(true, SimpleLinnaeanObservables.scientificName, classifier));
    parameters.inf_t_f$soundexScientificName = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.soundexScientificName, classifier) , analyser.getObservation(false, SimpleLinnaeanObservables.scientificName, classifier));
    parameters.inf_t_tt$genus = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.genus, classifier) , analyser.getObservation(true, SimpleLinnaeanObservables.scientificName, classifier), analyser.getObservation(true, SimpleLinnaeanObservables.soundexScientificName, classifier));
    parameters.inf_t_tf$genus = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.genus, classifier) , analyser.getObservation(true, SimpleLinnaeanObservables.scientificName, classifier), analyser.getObservation(false, SimpleLinnaeanObservables.soundexScientificName, classifier));
    parameters.inf_t_ft$genus = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.genus, classifier) , analyser.getObservation(false, SimpleLinnaeanObservables.scientificName, classifier), analyser.getObservation(true, SimpleLinnaeanObservables.soundexScientificName, classifier));
    parameters.inf_t_ff$genus = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.genus, classifier) , analyser.getObservation(false, SimpleLinnaeanObservables.scientificName, classifier), analyser.getObservation(false, SimpleLinnaeanObservables.soundexScientificName, classifier));
    parameters.inf_t_t$family = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.family, classifier) , analyser.getObservation(true, SimpleLinnaeanObservables.genus, classifier));
    parameters.inf_t_f$family = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.family, classifier) , analyser.getObservation(false, SimpleLinnaeanObservables.genus, classifier));
    parameters.inf_t_t$order = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.order, classifier) , analyser.getObservation(true, SimpleLinnaeanObservables.family, classifier));
    parameters.inf_t_f$order = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.order, classifier) , analyser.getObservation(false, SimpleLinnaeanObservables.family, classifier));
    parameters.inf_t_t$class_ = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.class_, classifier) , analyser.getObservation(true, SimpleLinnaeanObservables.order, classifier));
    parameters.inf_t_f$class_ = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.class_, classifier) , analyser.getObservation(false, SimpleLinnaeanObservables.order, classifier));
    parameters.inf_t_t$phylum = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.phylum, classifier) , analyser.getObservation(true, SimpleLinnaeanObservables.class_, classifier));
    parameters.inf_t_f$phylum = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.phylum, classifier) , analyser.getObservation(false, SimpleLinnaeanObservables.class_, classifier));
    parameters.inf_t_t$kingdom = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.kingdom, classifier) , analyser.getObservation(true, SimpleLinnaeanObservables.phylum, classifier));
    parameters.inf_t_f$kingdom = analyser.computeConditional(analyser.getObservation(true, SimpleLinnaeanObservables.kingdom, classifier) , analyser.getObservation(false, SimpleLinnaeanObservables.phylum, classifier));
  }
}
