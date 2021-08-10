package au.org.ala.names.generated;

import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Inferencer;
import au.org.ala.bayesian.StoreException;

public class SimpleLinnaeanInferencer_FF implements Inferencer<SimpleLinnaeanClassification> {
  public final static String SIGNATURE = "FF";

  private ThreadLocal<SimpleLinnaeanParameters_FF> parameters = ThreadLocal.withInitial(() -> new SimpleLinnaeanParameters_FF());

  public SimpleLinnaeanInferencer_FF() {
  }

  @Override
  public String getSignature() {
    return SIGNATURE;
  }

  public double infer(SimpleLinnaeanInferencer.Evidence evidence, SimpleLinnaeanParameters_FF parameters, double c$taxonId) {
    double nc$taxonId = 1.0 - c$taxonId;
    double c$taxonRank = 0.0;
    double nc$taxonRank = 0.0;
    double c$scientificNameAuthorship = 0.0;
    double nc$scientificNameAuthorship = 0.0;
    double c$scientificName = 0.0;
    double nc$scientificName = 0.0;
    double c$soundexScientificName = evidence.isT$e$soundexScientificName() ? 1.0 : 0.0;
    double nc$soundexScientificName = evidence.isF$e$soundexScientificName() ? 1.0 : 0.0;
    double c$family = 0.0;
    double nc$family = 0.0;
    double c$order = 0.0;
    double nc$order = 0.0;
    double c$class_ = 0.0;
    double nc$class_ = 0.0;
    double c$phylum = 0.0;
    double nc$phylum = 0.0;
    double c$kingdom = 0.0;
    double nc$kingdom = 0.0;
    if (evidence.isT$e$taxonRank()) {
      c$taxonRank += parameters.inf_t_t$taxonRank * c$taxonId;
      c$taxonRank += parameters.inf_t_f$taxonRank * nc$taxonId;
    }
    if (evidence.isF$e$taxonRank()) {
      nc$taxonRank += parameters.inf_f_t$taxonRank * c$taxonId;
      nc$taxonRank += parameters.inf_f_f$taxonRank * nc$taxonId;
    }
    if (evidence.isT$e$scientificNameAuthorship()) {
      c$scientificNameAuthorship += parameters.inf_t_t$scientificNameAuthorship * c$taxonId;
      c$scientificNameAuthorship += parameters.inf_t_f$scientificNameAuthorship * nc$taxonId;
    }
    if (evidence.isF$e$scientificNameAuthorship()) {
      nc$scientificNameAuthorship += parameters.inf_f_t$scientificNameAuthorship * c$taxonId;
      nc$scientificNameAuthorship += parameters.inf_f_f$scientificNameAuthorship * nc$taxonId;
    }
    if (evidence.isT$e$scientificName()) {
      c$scientificName += parameters.inf_t_t$scientificName * c$taxonId;
      c$scientificName += parameters.inf_t_f$scientificName * nc$taxonId;
    }
    if (evidence.isF$e$scientificName()) {
      nc$scientificName += parameters.inf_f_t$scientificName * c$taxonId;
      nc$scientificName += parameters.inf_f_f$scientificName * nc$taxonId;
    }
    // Ignoring non-base soundexScientificName
    if (evidence.isT$e$family()) {
      c$family += parameters.derived_t_tt$family * c$soundexScientificName * c$scientificName;
      c$family += parameters.derived_t_tf$family * c$soundexScientificName * nc$scientificName;
      c$family += parameters.derived_t_ft$family * nc$soundexScientificName * c$scientificName;
      c$family += parameters.derived_t_ff$family * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isF$e$family()) {
      nc$family += parameters.derived_f_tt$family * c$soundexScientificName * c$scientificName;
      nc$family += parameters.derived_f_tf$family * c$soundexScientificName * nc$scientificName;
      nc$family += parameters.derived_f_ft$family * nc$soundexScientificName * c$scientificName;
      nc$family += parameters.derived_f_ff$family * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isT$e$order()) {
      c$order += parameters.inf_t_t$order * c$family;
      c$order += parameters.inf_t_f$order * nc$family;
    }
    if (evidence.isF$e$order()) {
      nc$order += parameters.inf_f_t$order * c$family;
      nc$order += parameters.inf_f_f$order * nc$family;
    }
    if (evidence.isT$e$class_()) {
      c$class_ += parameters.inf_t_t$class_ * c$order;
      c$class_ += parameters.inf_t_f$class_ * nc$order;
    }
    if (evidence.isF$e$class_()) {
      nc$class_ += parameters.inf_f_t$class_ * c$order;
      nc$class_ += parameters.inf_f_f$class_ * nc$order;
    }
    if (evidence.isT$e$phylum()) {
      c$phylum += parameters.inf_t_t$phylum * c$class_;
      c$phylum += parameters.inf_t_f$phylum * nc$class_;
    }
    if (evidence.isF$e$phylum()) {
      nc$phylum += parameters.inf_f_t$phylum * c$class_;
      nc$phylum += parameters.inf_f_f$phylum * nc$class_;
    }
    if (evidence.isT$e$kingdom()) {
      c$kingdom += parameters.inf_t_t$kingdom * c$phylum;
      c$kingdom += parameters.inf_t_f$kingdom * nc$phylum;
    }
    if (evidence.isF$e$kingdom()) {
      nc$kingdom += parameters.inf_f_t$kingdom * c$phylum;
      nc$kingdom += parameters.inf_f_f$kingdom * nc$phylum;
    }
    return (c$taxonRank + nc$taxonRank) * (c$scientificNameAuthorship + nc$scientificNameAuthorship) * (c$kingdom + nc$kingdom) * (parameters.prior_t$taxonId * c$taxonId + parameters.prior_f$taxonId * nc$taxonId);
  }

  public Inference probability(SimpleLinnaeanInferencer.Evidence evidence, SimpleLinnaeanParameters_FF parameters) {
    double p;
    double prior = parameters.prior_t$taxonId;
    double ph = 0.0;
    double pe = 0.0;

    p = (evidence.isT$e$taxonId() ? 1.0 : 0.0) * this.infer(evidence, parameters, 1.0);
    ph += p;
    pe += p;
    p = (evidence.isF$e$taxonId() ? 1.0 : 0.0) * this.infer(evidence, parameters, 0.0);
    pe += p;
    return Inference.forPEH(prior, pe, ph);
  }

  @Override
  public Inference probability(SimpleLinnaeanClassification classification, Classifier classifier) throws StoreException, InferenceException {
    SimpleLinnaeanInferencer.Evidence evidence = classification.match(classifier);
    SimpleLinnaeanParameters_FF params = this.parameters.get();
    classifier.loadParameters(params);
    return this.probability(evidence, params);
  }

}