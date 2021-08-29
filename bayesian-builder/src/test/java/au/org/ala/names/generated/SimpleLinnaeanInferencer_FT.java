package au.org.ala.names.generated;

import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Inferencer;
import au.org.ala.bayesian.StoreException;

public class SimpleLinnaeanInferencer_FT implements Inferencer<SimpleLinnaeanClassification> {
  public final static String SIGNATURE = "FT";

  private ThreadLocal<SimpleLinnaeanParameters_FT> parameters = ThreadLocal.withInitial(() -> new SimpleLinnaeanParameters_FT());

  public SimpleLinnaeanInferencer_FT() {
  }

  @Override
  public String getSignature() {
    return SIGNATURE;
  }

  public double infer_t(SimpleLinnaeanInferencer.Evidence evidence, SimpleLinnaeanParameters_FT parameters) {
    double c$taxonId = 1.0;
    double nc$taxonId = 0.0;
    double c$taxonRank = 0.0;
    double nc$taxonRank = 0.0;
    double c$scientificNameAuthorship = 0.0;
    double nc$scientificNameAuthorship = 0.0;
    double c$scientificName = 0.0;
    double nc$scientificName = 0.0;
    double c$soundexScientificName = evidence.isT$e$soundexScientificName() ? 1.0 : 0.0;
    double nc$soundexScientificName = evidence.isF$e$soundexScientificName() ? 1.0 : 0.0;
    double c$genus = 0.0;
    double nc$genus = 0.0;
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
    if (evidence.isT$e$scientificName()) {
      c$scientificName += parameters.inf_t_t$scientificName$t * c$taxonId;
      c$scientificName += parameters.inf_t_f$scientificName$t * nc$taxonId;
    }
    if (evidence.isF$e$scientificName()) {
      nc$scientificName += parameters.inf_f_t$scientificName$t * c$taxonId;
      nc$scientificName += parameters.inf_f_f$scientificName$t * nc$taxonId;
    }
    // Ignoring non-base soundexScientificName
    if (evidence.isT$e$genus()) {
      c$genus += parameters.derived_t_tt$genus$t * c$soundexScientificName * c$scientificName;
      c$genus += parameters.derived_t_tf$genus$t * c$soundexScientificName * nc$scientificName;
      c$genus += parameters.derived_t_ft$genus$t * nc$soundexScientificName * c$scientificName;
      c$genus += parameters.derived_t_ff$genus$t * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isF$e$genus()) {
      nc$genus += parameters.derived_f_tt$genus$t * c$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_f_tf$genus$t * c$soundexScientificName * nc$scientificName;
      nc$genus += parameters.derived_f_ft$genus$t * nc$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_f_ff$genus$t * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isT$e$family()) {
      c$family += parameters.inf_t_t$family$t * c$genus;
      c$family += parameters.inf_t_f$family$t * nc$genus;
    }
    if (evidence.isF$e$family()) {
      nc$family += parameters.inf_f_t$family$t * c$genus;
      nc$family += parameters.inf_f_f$family$t * nc$genus;
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

  public double infer_f(SimpleLinnaeanInferencer.Evidence evidence, SimpleLinnaeanParameters_FT parameters) {
    double c$taxonId = 0.0;
    double nc$taxonId = 1.0;
    double c$taxonRank = 0.0;
    double nc$taxonRank = 0.0;
    double c$scientificNameAuthorship = 0.0;
    double nc$scientificNameAuthorship = 0.0;
    double c$scientificName = 0.0;
    double nc$scientificName = 0.0;
    double c$soundexScientificName = evidence.isT$e$soundexScientificName() ? 1.0 : 0.0;
    double nc$soundexScientificName = evidence.isF$e$soundexScientificName() ? 1.0 : 0.0;
    double c$genus = 0.0;
    double nc$genus = 0.0;
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
    if (evidence.isT$e$scientificName()) {
      c$scientificName += parameters.inf_t_t$scientificName$f * c$taxonId;
      c$scientificName += parameters.inf_t_f$scientificName$f * nc$taxonId;
    }
    if (evidence.isF$e$scientificName()) {
      nc$scientificName += parameters.inf_f_t$scientificName$f * c$taxonId;
      nc$scientificName += parameters.inf_f_f$scientificName$f * nc$taxonId;
    }
    // Ignoring non-base soundexScientificName
    if (evidence.isT$e$genus()) {
      c$genus += parameters.derived_t_tt$genus$f * c$soundexScientificName * c$scientificName;
      c$genus += parameters.derived_t_tf$genus$f * c$soundexScientificName * nc$scientificName;
      c$genus += parameters.derived_t_ft$genus$f * nc$soundexScientificName * c$scientificName;
      c$genus += parameters.derived_t_ff$genus$f * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isF$e$genus()) {
      nc$genus += parameters.derived_f_tt$genus$f * c$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_f_tf$genus$f * c$soundexScientificName * nc$scientificName;
      nc$genus += parameters.derived_f_ft$genus$f * nc$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_f_ff$genus$f * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isT$e$family()) {
      c$family += parameters.inf_t_t$family$f * c$genus;
      c$family += parameters.inf_t_f$family$f * nc$genus;
    }
    if (evidence.isF$e$family()) {
      nc$family += parameters.inf_f_t$family$f * c$genus;
      nc$family += parameters.inf_f_f$family$f * nc$genus;
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


  public Inference probability(SimpleLinnaeanInferencer.Evidence evidence, SimpleLinnaeanParameters_FT parameters) {
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
    SimpleLinnaeanParameters_FT params = this.parameters.get();
    classifier.loadParameters(params);
    return this.probability(evidence, params);
  }

}