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

public class SimpleLinnaeanBuilder implements Builder<SimpleLinnaeanParameters> {
  private TaxonNameSoundEx soundex;

  public SimpleLinnaeanBuilder() {
    this.soundex = new TaxonNameSoundEx();
  }

  @Override
  public void infer(Classifier classifier) throws InferenceException, StoreException {
    java.lang.String e_5 = classifier.get(SimpleLinnaeanFactory.taxonRank);
    for (Object v: classifier.getAll(SimpleLinnaeanFactory.scientificName)) {
      Object d = this.soundex.treatWord((String) v, e_5);
      classifier.add(SimpleLinnaeanFactory.soundexScientificName, d);
    }
  }

    @Override
    public void expand(Classifier classifier, Deque<Classifier> parents) throws InferenceException, StoreException {
    Optional<Classifier> d_6 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "genus".equals(x))).findFirst();
    if (d_6.isPresent()){
      for(Object v: d_6.get().getAll(SimpleLinnaeanFactory.scientificName)){
        Object d = v;
        classifier.add(SimpleLinnaeanFactory.genus, d);
      }
    }
    Optional<Classifier> d_7 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "family".equals(x))).findFirst();
    if (d_7.isPresent()){
      for(Object v: d_7.get().getAll(SimpleLinnaeanFactory.scientificName)){
        Object d = v;
        classifier.add(SimpleLinnaeanFactory.family, d);
      }
    }
    Optional<Classifier> d_8 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "order".equals(x))).findFirst();
    if (d_8.isPresent()){
      for(Object v: d_8.get().getAll(SimpleLinnaeanFactory.scientificName)){
        Object d = v;
        classifier.add(SimpleLinnaeanFactory.order, d);
      }
    }
    Optional<Classifier> d_9 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "class".equals(x))).findFirst();
    if (d_9.isPresent()){
      for(Object v: d_9.get().getAll(SimpleLinnaeanFactory.scientificName)){
        Object d = v;
        classifier.add(SimpleLinnaeanFactory.class_, d);
      }
    }
    Optional<Classifier> d_10 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "phylum".equals(x))).findFirst();
    if (d_10.isPresent()){
      for(Object v: d_10.get().getAll(SimpleLinnaeanFactory.scientificName)){
        Object d = v;
        classifier.add(SimpleLinnaeanFactory.phylum, d);
      }
    }
    Optional<Classifier> d_11 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "kingdom".equals(x))).findFirst();
    if (d_11.isPresent()){
      for(Object v: d_11.get().getAll(SimpleLinnaeanFactory.scientificName)){
        Object d = v;
        classifier.add(SimpleLinnaeanFactory.kingdom, d);
      }
    }
  }

  @Override
  public void calculate(SimpleLinnaeanParameters parameters, ParameterAnalyser analyser, Classifier classifier) throws InferenceException, StoreException {
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
  }
}
