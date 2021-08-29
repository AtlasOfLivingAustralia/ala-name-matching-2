package au.org.ala.names.generated;

import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Inferencer;
import au.org.ala.bayesian.StoreException;

public class SimpleLinnaeanInferencer_TF implements Inferencer<SimpleLinnaeanClassification> {
  public final static String SIGNATURE = "TF";

  private ThreadLocal<SimpleLinnaeanParameters_TF> parameters = ThreadLocal.withInitial(() -> new SimpleLinnaeanParameters_TF());

  public SimpleLinnaeanInferencer_TF() {
  }

  @Override
  public String getSignature() {
    return SIGNATURE;
  }

  public double infer_t(SimpleLinnaeanInferencer.Evidence evidence, SimpleLinnaeanParameters_TF parameters) {
    double c$taxonId = 1.0;
    double nc$taxonId = 0.0;
    double c$taxonRank = 0.0;
    double nc$taxonRank = 0.0;
    double c$scientificNameAuthorship = 0.0;
    double nc$scientificNameAuthorship = 0.0;
    double c$specificEpithet = evidence.isT$e$specificEpithet() ? 1.0 : 0.0;
    double nc$specificEpithet = evidence.isF$e$specificEpithet() ? 1.0 : 0.0;
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
      c$taxonRank += parameters.inf_t_t$taxonRank$t * c$taxonId;
      c$taxonRank += parameters.inf_t_f$taxonRank$t * nc$taxonId;
    }
    if (evidence.isF$e$taxonRank()) {
      nc$taxonRank += parameters.inf_f_t$taxonRank$t * c$taxonId;
      nc$taxonRank += parameters.inf_f_f$taxonRank$t * nc$taxonId;
    }
    if (evidence.isT$e$scientificNameAuthorship()) {
      c$scientificNameAuthorship += parameters.inf_t_t$scientificNameAuthorship$t * c$taxonId;
      c$scientificNameAuthorship += parameters.inf_t_f$scientificNameAuthorship$t * nc$taxonId;
    }
    if (evidence.isF$e$scientificNameAuthorship()) {
      nc$scientificNameAuthorship += parameters.inf_f_t$scientificNameAuthorship$t * c$taxonId;
      nc$scientificNameAuthorship += parameters.inf_f_f$scientificNameAuthorship$t * nc$taxonId;
    }
    // Ignoring non-base specificEpithet
    if (evidence.isT$e$scientificName()) {
      c$scientificName += parameters.derived_t_tt$scientificName$t * c$specificEpithet * c$taxonId;
      c$scientificName += parameters.derived_t_tf$scientificName$t * c$specificEpithet * nc$taxonId;
      c$scientificName += parameters.derived_t_ft$scientificName$t * nc$specificEpithet * c$taxonId;
      c$scientificName += parameters.derived_t_ff$scientificName$t * nc$specificEpithet * nc$taxonId;
    }
    if (evidence.isF$e$scientificName()) {
      nc$scientificName += parameters.derived_f_tt$scientificName$t * c$specificEpithet * c$taxonId;
      nc$scientificName += parameters.derived_f_tf$scientificName$t * c$specificEpithet * nc$taxonId;
      nc$scientificName += parameters.derived_f_ft$scientificName$t * nc$specificEpithet * c$taxonId;
      nc$scientificName += parameters.derived_f_ff$scientificName$t * nc$specificEpithet * nc$taxonId;
    }
    // Ignoring non-base soundexScientificName
    if (evidence.isT$e$family()) {
      c$family += parameters.derived_t_tt$family$t * c$soundexScientificName * c$scientificName;
      c$family += parameters.derived_t_tf$family$t * c$soundexScientificName * nc$scientificName;
      c$family += parameters.derived_t_ft$family$t * nc$soundexScientificName * c$scientificName;
      c$family += parameters.derived_t_ff$family$t * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isF$e$family()) {
      nc$family += parameters.derived_f_tt$family$t * c$soundexScientificName * c$scientificName;
      nc$family += parameters.derived_f_tf$family$t * c$soundexScientificName * nc$scientificName;
      nc$family += parameters.derived_f_ft$family$t * nc$soundexScientificName * c$scientificName;
      nc$family += parameters.derived_f_ff$family$t * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isT$e$order()) {
      c$order += parameters.inf_t_t$order$t * c$family;
      c$order += parameters.inf_t_f$order$t * nc$family;
    }
    if (evidence.isF$e$order()) {
      nc$order += parameters.inf_f_t$order$t * c$family;
      nc$order += parameters.inf_f_f$order$t * nc$family;
    }
    if (evidence.isT$e$class_()) {
      c$class_ += parameters.inf_t_t$class_$t * c$order;
      c$class_ += parameters.inf_t_f$class_$t * nc$order;
    }
    if (evidence.isF$e$class_()) {
      nc$class_ += parameters.inf_f_t$class_$t * c$order;
      nc$class_ += parameters.inf_f_f$class_$t * nc$order;
    }
    if (evidence.isT$e$phylum()) {
      c$phylum += parameters.inf_t_t$phylum$t * c$class_;
      c$phylum += parameters.inf_t_f$phylum$t * nc$class_;
    }
    if (evidence.isF$e$phylum()) {
      nc$phylum += parameters.inf_f_t$phylum$t * c$class_;
      nc$phylum += parameters.inf_f_f$phylum$t * nc$class_;
    }
    if (evidence.isT$e$kingdom()) {
      c$kingdom += parameters.inf_t_t$kingdom$t * c$phylum;
      c$kingdom += parameters.inf_t_f$kingdom$t * nc$phylum;
    }
    if (evidence.isF$e$kingdom()) {
      nc$kingdom += parameters.inf_f_t$kingdom$t * c$phylum;
      nc$kingdom += parameters.inf_f_f$kingdom$t * nc$phylum;
    }
    return (c$taxonRank + nc$taxonRank) * (c$scientificNameAuthorship + nc$scientificNameAuthorship) * (c$kingdom + nc$kingdom);
  }

  public double infer_f(SimpleLinnaeanInferencer.Evidence evidence, SimpleLinnaeanParameters_TF parameters) {
    double c$taxonId = 0.0;
    double nc$taxonId = 1.0;
    double c$taxonRank = 0.0;
    double nc$taxonRank = 0.0;
    double c$scientificNameAuthorship = 0.0;
    double nc$scientificNameAuthorship = 0.0;
    double c$specificEpithet = evidence.isT$e$specificEpithet() ? 1.0 : 0.0;
    double nc$specificEpithet = evidence.isF$e$specificEpithet() ? 1.0 : 0.0;
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
      c$taxonRank += parameters.inf_t_t$taxonRank$f * c$taxonId;
      c$taxonRank += parameters.inf_t_f$taxonRank$f * nc$taxonId;
    }
    if (evidence.isF$e$taxonRank()) {
      nc$taxonRank += parameters.inf_f_t$taxonRank$f * c$taxonId;
      nc$taxonRank += parameters.inf_f_f$taxonRank$f * nc$taxonId;
    }
    if (evidence.isT$e$scientificNameAuthorship()) {
      c$scientificNameAuthorship += parameters.inf_t_t$scientificNameAuthorship$f * c$taxonId;
      c$scientificNameAuthorship += parameters.inf_t_f$scientificNameAuthorship$f * nc$taxonId;
    }
    if (evidence.isF$e$scientificNameAuthorship()) {
      nc$scientificNameAuthorship += parameters.inf_f_t$scientificNameAuthorship$f * c$taxonId;
      nc$scientificNameAuthorship += parameters.inf_f_f$scientificNameAuthorship$f * nc$taxonId;
    }
    // Ignoring non-base specificEpithet
    if (evidence.isT$e$scientificName()) {
      c$scientificName += parameters.derived_t_tt$scientificName$f * c$specificEpithet * c$taxonId;
      c$scientificName += parameters.derived_t_tf$scientificName$f * c$specificEpithet * nc$taxonId;
      c$scientificName += parameters.derived_t_ft$scientificName$f * nc$specificEpithet * c$taxonId;
      c$scientificName += parameters.derived_t_ff$scientificName$f * nc$specificEpithet * nc$taxonId;
    }
    if (evidence.isF$e$scientificName()) {
      nc$scientificName += parameters.derived_f_tt$scientificName$f * c$specificEpithet * c$taxonId;
      nc$scientificName += parameters.derived_f_tf$scientificName$f * c$specificEpithet * nc$taxonId;
      nc$scientificName += parameters.derived_f_ft$scientificName$f * nc$specificEpithet * c$taxonId;
      nc$scientificName += parameters.derived_f_ff$scientificName$f * nc$specificEpithet * nc$taxonId;
    }
    // Ignoring non-base soundexScientificName
    if (evidence.isT$e$family()) {
      c$family += parameters.derived_t_tt$family$f * c$soundexScientificName * c$scientificName;
      c$family += parameters.derived_t_tf$family$f * c$soundexScientificName * nc$scientificName;
      c$family += parameters.derived_t_ft$family$f * nc$soundexScientificName * c$scientificName;
      c$family += parameters.derived_t_ff$family$f * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isF$e$family()) {
      nc$family += parameters.derived_f_tt$family$f * c$soundexScientificName * c$scientificName;
      nc$family += parameters.derived_f_tf$family$f * c$soundexScientificName * nc$scientificName;
      nc$family += parameters.derived_f_ft$family$f * nc$soundexScientificName * c$scientificName;
      nc$family += parameters.derived_f_ff$family$f * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isT$e$order()) {
      c$order += parameters.inf_t_t$order$f * c$family;
      c$order += parameters.inf_t_f$order$f * nc$family;
    }
    if (evidence.isF$e$order()) {
      nc$order += parameters.inf_f_t$order$f * c$family;
      nc$order += parameters.inf_f_f$order$f * nc$family;
    }
    if (evidence.isT$e$class_()) {
      c$class_ += parameters.inf_t_t$class_$f * c$order;
      c$class_ += parameters.inf_t_f$class_$f * nc$order;
    }
    if (evidence.isF$e$class_()) {
      nc$class_ += parameters.inf_f_t$class_$f * c$order;
      nc$class_ += parameters.inf_f_f$class_$f * nc$order;
    }
    if (evidence.isT$e$phylum()) {
      c$phylum += parameters.inf_t_t$phylum$f * c$class_;
      c$phylum += parameters.inf_t_f$phylum$f * nc$class_;
    }
    if (evidence.isF$e$phylum()) {
      nc$phylum += parameters.inf_f_t$phylum$f * c$class_;
      nc$phylum += parameters.inf_f_f$phylum$f * nc$class_;
    }
    if (evidence.isT$e$kingdom()) {
      c$kingdom += parameters.inf_t_t$kingdom$f * c$phylum;
      c$kingdom += parameters.inf_t_f$kingdom$f * nc$phylum;
    }
    if (evidence.isF$e$kingdom()) {
      nc$kingdom += parameters.inf_f_t$kingdom$f * c$phylum;
      nc$kingdom += parameters.inf_f_f$kingdom$f * nc$phylum;
    }
    return (c$taxonRank + nc$taxonRank) * (c$scientificNameAuthorship + nc$scientificNameAuthorship) * (c$kingdom + nc$kingdom);
  }


  public Inference probability(SimpleLinnaeanInferencer.Evidence evidence, SimpleLinnaeanParameters_TF parameters) {
    double p;
    double prior = parameters.prior_t$taxonId;
    double ph = 0.0;
    double pe = 0.0;

    if (evidence.isT$e$taxonId()) {
      p = this.infer_t(evidence, parameters) * parameters.prior_t$taxonId;
      ph += p;
      pe += p;
    }
    if (evidence.isF$e$taxonId()) {
      p = this.infer_f(evidence, parameters) * parameters.prior_f$taxonId;
      pe += p;
    }
    return Inference.forPEH(prior, pe, ph);
  }

  @Override
  public Inference probability(SimpleLinnaeanClassification classification, Classifier classifier) throws StoreException, InferenceException {
    SimpleLinnaeanInferencer.Evidence evidence = classification.match(classifier);
    SimpleLinnaeanParameters_TF params = this.parameters.get();
    classifier.loadParameters(params);
    return this.probability(evidence, params);
  }

}