package au.org.ala.names.generated;

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.Inferencer;

public class SimpleLinnaeanInferencer_FF implements Inferencer<SimpleLinnaeanClassification> {
  public final static String SIGNATURE = "FF";

  private ThreadLocal<SimpleLinnaeanParameters_FF> parameters = ThreadLocal.withInitial(() -> new SimpleLinnaeanParameters_FF());

  public SimpleLinnaeanInferencer_FF() {
  }

  @Override
  public String getSignature() {
    return SIGNATURE;
  }

  public double infer_t(SimpleLinnaeanInferencer.Evidence evidence, SimpleLinnaeanParameters_FF parameters) {
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
      c$taxonRank += parameters.inf_taxonRank_t$t * c$taxonId;
    }
    if (evidence.isF$e$taxonRank()) {
      nc$taxonRank += parameters.inf_taxonRank_f$t * c$taxonId;
    }
    if (evidence.isT$e$scientificNameAuthorship()) {
      c$scientificNameAuthorship += parameters.inf_scientificNameAuthorship_t$t * c$taxonId;
    }
    if (evidence.isF$e$scientificNameAuthorship()) {
      nc$scientificNameAuthorship += parameters.inf_scientificNameAuthorship_f$t * c$taxonId;
    }
    if (evidence.isT$e$scientificName()) {
      c$scientificName += parameters.inf_scientificName_t$t * c$taxonId;
    }
    if (evidence.isF$e$scientificName()) {
      nc$scientificName += parameters.inf_scientificName_f$t * c$taxonId;
    }
    // Ignoring non-base soundexScientificName
    if (evidence.isT$e$family()) {
      c$family += parameters.derived_family_t$t_tt * c$taxonId * c$soundexScientificName * c$scientificName;
      c$family += parameters.derived_family_t$t_tf * c$taxonId * c$soundexScientificName * nc$scientificName;
      c$family += parameters.derived_family_t$t_ft * c$taxonId * nc$soundexScientificName * c$scientificName;
      c$family += parameters.derived_family_t$t_ff * c$taxonId * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isF$e$family()) {
      nc$family += parameters.derived_family_f$t_tt * c$taxonId * c$soundexScientificName * c$scientificName;
      nc$family += parameters.derived_family_f$t_tf * c$taxonId * c$soundexScientificName * nc$scientificName;
      nc$family += parameters.derived_family_f$t_ft * c$taxonId * nc$soundexScientificName * c$scientificName;
      nc$family += parameters.derived_family_f$t_ff * c$taxonId * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isT$e$order()) {
      c$order += parameters.inf_order_t$t_t * c$taxonId * c$family;
      c$order += parameters.inf_order_t$t_f * c$taxonId * nc$family;
    }
    if (evidence.isF$e$order()) {
      nc$order += parameters.inf_order_f$t_t * c$taxonId * c$family;
      nc$order += parameters.inf_order_f$t_f * c$taxonId * nc$family;
    }
    if (evidence.isT$e$class_()) {
      c$class_ += parameters.inf_class__t$t_t * c$taxonId * c$order;
      c$class_ += parameters.inf_class__t$t_f * c$taxonId * nc$order;
    }
    if (evidence.isF$e$class_()) {
      nc$class_ += parameters.inf_class__f$t_t * c$taxonId * c$order;
      nc$class_ += parameters.inf_class__f$t_f * c$taxonId * nc$order;
    }
    if (evidence.isT$e$phylum()) {
      c$phylum += parameters.inf_phylum_t$t_t * c$taxonId * c$class_;
      c$phylum += parameters.inf_phylum_t$t_f * c$taxonId * nc$class_;
    }
    if (evidence.isF$e$phylum()) {
      nc$phylum += parameters.inf_phylum_f$t_t * c$taxonId * c$class_;
      nc$phylum += parameters.inf_phylum_f$t_f * c$taxonId * nc$class_;
    }
    if (evidence.isT$e$kingdom()) {
      c$kingdom += parameters.inf_kingdom_t$t_t * c$taxonId * c$phylum;
      c$kingdom += parameters.inf_kingdom_t$t_f * c$taxonId * nc$phylum;
    }
    if (evidence.isF$e$kingdom()) {
      nc$kingdom += parameters.inf_kingdom_f$t_t * c$taxonId * c$phylum;
      nc$kingdom += parameters.inf_kingdom_f$t_f * c$taxonId * nc$phylum;
    }
    return (c$taxonRank + nc$taxonRank) * (c$scientificNameAuthorship + nc$scientificNameAuthorship) * (c$kingdom + nc$kingdom);
  }

  public double infer_f(SimpleLinnaeanInferencer.Evidence evidence, SimpleLinnaeanParameters_FF parameters) {
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
      c$taxonRank += parameters.inf_taxonRank_t$f * nc$taxonId;
    }
    if (evidence.isF$e$taxonRank()) {
      nc$taxonRank += parameters.inf_taxonRank_f$f * nc$taxonId;
    }
    if (evidence.isT$e$scientificNameAuthorship()) {
      c$scientificNameAuthorship += parameters.inf_scientificNameAuthorship_t$f * nc$taxonId;
    }
    if (evidence.isF$e$scientificNameAuthorship()) {
      nc$scientificNameAuthorship += parameters.inf_scientificNameAuthorship_f$f * nc$taxonId;
    }
    if (evidence.isT$e$scientificName()) {
      c$scientificName += parameters.inf_scientificName_t$f * nc$taxonId;
    }
    if (evidence.isF$e$scientificName()) {
      nc$scientificName += parameters.inf_scientificName_f$f * nc$taxonId;
    }
    // Ignoring non-base soundexScientificName
    if (evidence.isT$e$family()) {
      c$family += parameters.derived_family_t$f_tt * nc$taxonId * c$soundexScientificName * c$scientificName;
      c$family += parameters.derived_family_t$f_tf * nc$taxonId * c$soundexScientificName * nc$scientificName;
      c$family += parameters.derived_family_t$f_ft * nc$taxonId * nc$soundexScientificName * c$scientificName;
      c$family += parameters.derived_family_t$f_ff * nc$taxonId * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isF$e$family()) {
      nc$family += parameters.derived_family_f$f_tt * nc$taxonId * c$soundexScientificName * c$scientificName;
      nc$family += parameters.derived_family_f$f_tf * nc$taxonId * c$soundexScientificName * nc$scientificName;
      nc$family += parameters.derived_family_f$f_ft * nc$taxonId * nc$soundexScientificName * c$scientificName;
      nc$family += parameters.derived_family_f$f_ff * nc$taxonId * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isT$e$order()) {
      c$order += parameters.inf_order_t$f_t * nc$taxonId * c$family;
      c$order += parameters.inf_order_t$f_f * nc$taxonId * nc$family;
    }
    if (evidence.isF$e$order()) {
      nc$order += parameters.inf_order_f$f_t * nc$taxonId * c$family;
      nc$order += parameters.inf_order_f$f_f * nc$taxonId * nc$family;
    }
    if (evidence.isT$e$class_()) {
      c$class_ += parameters.inf_class__t$f_t * nc$taxonId * c$order;
      c$class_ += parameters.inf_class__t$f_f * nc$taxonId * nc$order;
    }
    if (evidence.isF$e$class_()) {
      nc$class_ += parameters.inf_class__f$f_t * nc$taxonId * c$order;
      nc$class_ += parameters.inf_class__f$f_f * nc$taxonId * nc$order;
    }
    if (evidence.isT$e$phylum()) {
      c$phylum += parameters.inf_phylum_t$f_t * nc$taxonId * c$class_;
      c$phylum += parameters.inf_phylum_t$f_f * nc$taxonId * nc$class_;
    }
    if (evidence.isF$e$phylum()) {
      nc$phylum += parameters.inf_phylum_f$f_t * nc$taxonId * c$class_;
      nc$phylum += parameters.inf_phylum_f$f_f * nc$taxonId * nc$class_;
    }
    if (evidence.isT$e$kingdom()) {
      c$kingdom += parameters.inf_kingdom_t$f_t * nc$taxonId * c$phylum;
      c$kingdom += parameters.inf_kingdom_t$f_f * nc$taxonId * nc$phylum;
    }
    if (evidence.isF$e$kingdom()) {
      nc$kingdom += parameters.inf_kingdom_f$f_t * nc$taxonId * c$phylum;
      nc$kingdom += parameters.inf_kingdom_f$f_f * nc$taxonId * nc$phylum;
    }
    return (c$taxonRank + nc$taxonRank) * (c$scientificNameAuthorship + nc$scientificNameAuthorship) * (c$kingdom + nc$kingdom);
  }


  public Inference probability(SimpleLinnaeanInferencer.Evidence evidence, SimpleLinnaeanParameters_FF parameters) {
    double p;
    double prior = parameters.prior_taxonId_t;
    double ph = 0.0;
    double pe = 0.0;

    if (evidence.isT$e$taxonId()) {
      p = this.infer_t(evidence, parameters) * parameters.prior_taxonId_t;
      ph += p;
      pe += p;
    }
    if (evidence.isF$e$taxonId()) {
      p = this.infer_f(evidence, parameters) * parameters.prior_taxonId_f;
      pe += p;
    }
    return Inference.forPEH(prior, pe, ph);
  }

  @Override
  public Inference probability(SimpleLinnaeanClassification classification, Classifier classifier) throws BayesianException {
    SimpleLinnaeanInferencer.Evidence evidence = classification.match(classifier);
    SimpleLinnaeanParameters_FF params = this.parameters.get();
    classifier.loadParameters(params);
    return this.probability(evidence, params);
  }

}