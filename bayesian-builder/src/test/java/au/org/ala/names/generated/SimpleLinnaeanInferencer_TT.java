package au.org.ala.names.generated;

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.Inferencer;
import au.org.ala.bayesian.Trace;

public class SimpleLinnaeanInferencer_TT implements Inferencer<SimpleLinnaeanClassification> {
  public final static String SIGNATURE = "TT";

  public SimpleLinnaeanInferencer_TT() {
  }

  @Override
  public String getSignature() {
    return SIGNATURE;
  }

  public double infer_t(SimpleLinnaeanInferencer.Evidence evidence, SimpleLinnaeanParameters_TT parameters, Trace trace) {
    double c$taxonId = 1.0;
    double nc$taxonId = 0.0;
    double c$taxonRank = 0.0;
    double nc$taxonRank = 0.0;
    double c$specificEpithet = evidence.isT$e$specificEpithet() ? 1.0 : 0.0;
    double nc$specificEpithet = evidence.isF$e$specificEpithet() ? 1.0 : 0.0;
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
    if (trace != null) {
        trace.add("taxonID", c$taxonId);
        trace.add("!taxonID", nc$taxonId);
    }
    if (evidence.isT$e$taxonRank()) {
      c$taxonRank += parameters.inf_taxonRank_t$t * c$taxonId;
    }
    if (evidence.isF$e$taxonRank()) {
      nc$taxonRank += parameters.inf_taxonRank_f$t * c$taxonId;
    }
    if (trace != null) {
        trace.add("taxonRank", c$taxonRank);
        trace.add("!taxonRank", nc$taxonRank);
    }
    // Ignoring non-base specificEpithet
    if (trace != null) {
        trace.add("specificEpithet", c$specificEpithet);
        trace.add("!specificEpithet", nc$specificEpithet);
    }
    if (evidence.isT$e$scientificNameAuthorship()) {
      c$scientificNameAuthorship += parameters.inf_scientificNameAuthorship_t$t * c$taxonId;
    }
    if (evidence.isF$e$scientificNameAuthorship()) {
      nc$scientificNameAuthorship += parameters.inf_scientificNameAuthorship_f$t * c$taxonId;
    }
    if (trace != null) {
        trace.add("scientificNameAuthorship", c$scientificNameAuthorship);
        trace.add("!scientificNameAuthorship", nc$scientificNameAuthorship);
    }
    if (evidence.isT$e$scientificName()) {
      c$scientificName += parameters.derived_scientificName_t$t_t * c$taxonId * c$specificEpithet;
      c$scientificName += parameters.derived_scientificName_t$t_f * c$taxonId * nc$specificEpithet;
    }
    if (evidence.isF$e$scientificName()) {
      nc$scientificName += parameters.derived_scientificName_f$t_t * c$taxonId * c$specificEpithet;
      nc$scientificName += parameters.derived_scientificName_f$t_f * c$taxonId * nc$specificEpithet;
    }
    if (trace != null) {
        trace.add("scientificName", c$scientificName);
        trace.add("!scientificName", nc$scientificName);
    }
    // Ignoring non-base soundexScientificName
    if (trace != null) {
        trace.add("soundexScientificName", c$soundexScientificName);
        trace.add("!soundexScientificName", nc$soundexScientificName);
    }
    if (evidence.isT$e$genus()) {
      c$genus += parameters.derived_genus_t$t_tt * c$taxonId * c$soundexScientificName * c$scientificName;
      c$genus += parameters.derived_genus_t$t_tf * c$taxonId * c$soundexScientificName * nc$scientificName;
      c$genus += parameters.derived_genus_t$t_ft * c$taxonId * nc$soundexScientificName * c$scientificName;
      c$genus += parameters.derived_genus_t$t_ff * c$taxonId * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isF$e$genus()) {
      nc$genus += parameters.derived_genus_f$t_tt * c$taxonId * c$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_genus_f$t_tf * c$taxonId * c$soundexScientificName * nc$scientificName;
      nc$genus += parameters.derived_genus_f$t_ft * c$taxonId * nc$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_genus_f$t_ff * c$taxonId * nc$soundexScientificName * nc$scientificName;
    }
    if (trace != null) {
        trace.add("genus", c$genus);
        trace.add("!genus", nc$genus);
    }
    if (evidence.isT$e$family()) {
      c$family += parameters.inf_family_t$t_t * c$taxonId * c$genus;
      c$family += parameters.inf_family_t$t_f * c$taxonId * nc$genus;
    }
    if (evidence.isF$e$family()) {
      nc$family += parameters.inf_family_f$t_t * c$taxonId * c$genus;
      nc$family += parameters.inf_family_f$t_f * c$taxonId * nc$genus;
    }
    if (trace != null) {
        trace.add("family", c$family);
        trace.add("!family", nc$family);
    }
    if (evidence.isT$e$order()) {
      c$order += parameters.inf_order_t$t_t * c$taxonId * c$family;
      c$order += parameters.inf_order_t$t_f * c$taxonId * nc$family;
    }
    if (evidence.isF$e$order()) {
      nc$order += parameters.inf_order_f$t_t * c$taxonId * c$family;
      nc$order += parameters.inf_order_f$t_f * c$taxonId * nc$family;
    }
    if (trace != null) {
        trace.add("order", c$order);
        trace.add("!order", nc$order);
    }
    if (evidence.isT$e$class_()) {
      c$class_ += parameters.inf_class__t$t_t * c$taxonId * c$order;
      c$class_ += parameters.inf_class__t$t_f * c$taxonId * nc$order;
    }
    if (evidence.isF$e$class_()) {
      nc$class_ += parameters.inf_class__f$t_t * c$taxonId * c$order;
      nc$class_ += parameters.inf_class__f$t_f * c$taxonId * nc$order;
    }
    if (trace != null) {
        trace.add("class", c$class_);
        trace.add("!class", nc$class_);
    }
    if (evidence.isT$e$phylum()) {
      c$phylum += parameters.inf_phylum_t$t_t * c$taxonId * c$class_;
      c$phylum += parameters.inf_phylum_t$t_f * c$taxonId * nc$class_;
    }
    if (evidence.isF$e$phylum()) {
      nc$phylum += parameters.inf_phylum_f$t_t * c$taxonId * c$class_;
      nc$phylum += parameters.inf_phylum_f$t_f * c$taxonId * nc$class_;
    }
    if (trace != null) {
        trace.add("phylum", c$phylum);
        trace.add("!phylum", nc$phylum);
    }
    if (evidence.isT$e$kingdom()) {
      c$kingdom += parameters.inf_kingdom_t$t_t * c$taxonId * c$phylum;
      c$kingdom += parameters.inf_kingdom_t$t_f * c$taxonId * nc$phylum;
    }
    if (evidence.isF$e$kingdom()) {
      nc$kingdom += parameters.inf_kingdom_f$t_t * c$taxonId * c$phylum;
      nc$kingdom += parameters.inf_kingdom_f$t_f * c$taxonId * nc$phylum;
    }
    if (trace != null) {
        trace.add("kingdom", c$kingdom);
        trace.add("!kingdom", nc$kingdom);
    }
    return (c$taxonRank + nc$taxonRank) * (c$scientificNameAuthorship + nc$scientificNameAuthorship) * (c$kingdom + nc$kingdom);
  }

  public double infer_f(SimpleLinnaeanInferencer.Evidence evidence, SimpleLinnaeanParameters_TT parameters, Trace trace) {
    double c$taxonId = 0.0;
    double nc$taxonId = 1.0;
    double c$taxonRank = 0.0;
    double nc$taxonRank = 0.0;
    double c$specificEpithet = evidence.isT$e$specificEpithet() ? 1.0 : 0.0;
    double nc$specificEpithet = evidence.isF$e$specificEpithet() ? 1.0 : 0.0;
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
    if (trace != null) {
        trace.add("taxonID", c$taxonId);
        trace.add("!taxonID", nc$taxonId);
    }
    if (evidence.isT$e$taxonRank()) {
      c$taxonRank += parameters.inf_taxonRank_t$f * nc$taxonId;
    }
    if (evidence.isF$e$taxonRank()) {
      nc$taxonRank += parameters.inf_taxonRank_f$f * nc$taxonId;
    }
    if (trace != null) {
        trace.add("taxonRank", c$taxonRank);
        trace.add("!taxonRank", nc$taxonRank);
    }
    // Ignoring non-base specificEpithet
    if (trace != null) {
        trace.add("specificEpithet", c$specificEpithet);
        trace.add("!specificEpithet", nc$specificEpithet);
    }
    if (evidence.isT$e$scientificNameAuthorship()) {
      c$scientificNameAuthorship += parameters.inf_scientificNameAuthorship_t$f * nc$taxonId;
    }
    if (evidence.isF$e$scientificNameAuthorship()) {
      nc$scientificNameAuthorship += parameters.inf_scientificNameAuthorship_f$f * nc$taxonId;
    }
    if (trace != null) {
        trace.add("scientificNameAuthorship", c$scientificNameAuthorship);
        trace.add("!scientificNameAuthorship", nc$scientificNameAuthorship);
    }
    if (evidence.isT$e$scientificName()) {
      c$scientificName += parameters.derived_scientificName_t$f_t * nc$taxonId * c$specificEpithet;
      c$scientificName += parameters.derived_scientificName_t$f_f * nc$taxonId * nc$specificEpithet;
    }
    if (evidence.isF$e$scientificName()) {
      nc$scientificName += parameters.derived_scientificName_f$f_t * nc$taxonId * c$specificEpithet;
      nc$scientificName += parameters.derived_scientificName_f$f_f * nc$taxonId * nc$specificEpithet;
    }
    if (trace != null) {
        trace.add("scientificName", c$scientificName);
        trace.add("!scientificName", nc$scientificName);
    }
    // Ignoring non-base soundexScientificName
    if (trace != null) {
        trace.add("soundexScientificName", c$soundexScientificName);
        trace.add("!soundexScientificName", nc$soundexScientificName);
    }
    if (evidence.isT$e$genus()) {
      c$genus += parameters.derived_genus_t$f_tt * nc$taxonId * c$soundexScientificName * c$scientificName;
      c$genus += parameters.derived_genus_t$f_tf * nc$taxonId * c$soundexScientificName * nc$scientificName;
      c$genus += parameters.derived_genus_t$f_ft * nc$taxonId * nc$soundexScientificName * c$scientificName;
      c$genus += parameters.derived_genus_t$f_ff * nc$taxonId * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isF$e$genus()) {
      nc$genus += parameters.derived_genus_f$f_tt * nc$taxonId * c$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_genus_f$f_tf * nc$taxonId * c$soundexScientificName * nc$scientificName;
      nc$genus += parameters.derived_genus_f$f_ft * nc$taxonId * nc$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_genus_f$f_ff * nc$taxonId * nc$soundexScientificName * nc$scientificName;
    }
    if (trace != null) {
        trace.add("genus", c$genus);
        trace.add("!genus", nc$genus);
    }
    if (evidence.isT$e$family()) {
      c$family += parameters.inf_family_t$f_t * nc$taxonId * c$genus;
      c$family += parameters.inf_family_t$f_f * nc$taxonId * nc$genus;
    }
    if (evidence.isF$e$family()) {
      nc$family += parameters.inf_family_f$f_t * nc$taxonId * c$genus;
      nc$family += parameters.inf_family_f$f_f * nc$taxonId * nc$genus;
    }
    if (trace != null) {
        trace.add("family", c$family);
        trace.add("!family", nc$family);
    }
    if (evidence.isT$e$order()) {
      c$order += parameters.inf_order_t$f_t * nc$taxonId * c$family;
      c$order += parameters.inf_order_t$f_f * nc$taxonId * nc$family;
    }
    if (evidence.isF$e$order()) {
      nc$order += parameters.inf_order_f$f_t * nc$taxonId * c$family;
      nc$order += parameters.inf_order_f$f_f * nc$taxonId * nc$family;
    }
    if (trace != null) {
        trace.add("order", c$order);
        trace.add("!order", nc$order);
    }
    if (evidence.isT$e$class_()) {
      c$class_ += parameters.inf_class__t$f_t * nc$taxonId * c$order;
      c$class_ += parameters.inf_class__t$f_f * nc$taxonId * nc$order;
    }
    if (evidence.isF$e$class_()) {
      nc$class_ += parameters.inf_class__f$f_t * nc$taxonId * c$order;
      nc$class_ += parameters.inf_class__f$f_f * nc$taxonId * nc$order;
    }
    if (trace != null) {
        trace.add("class", c$class_);
        trace.add("!class", nc$class_);
    }
    if (evidence.isT$e$phylum()) {
      c$phylum += parameters.inf_phylum_t$f_t * nc$taxonId * c$class_;
      c$phylum += parameters.inf_phylum_t$f_f * nc$taxonId * nc$class_;
    }
    if (evidence.isF$e$phylum()) {
      nc$phylum += parameters.inf_phylum_f$f_t * nc$taxonId * c$class_;
      nc$phylum += parameters.inf_phylum_f$f_f * nc$taxonId * nc$class_;
    }
    if (trace != null) {
        trace.add("phylum", c$phylum);
        trace.add("!phylum", nc$phylum);
    }
    if (evidence.isT$e$kingdom()) {
      c$kingdom += parameters.inf_kingdom_t$f_t * nc$taxonId * c$phylum;
      c$kingdom += parameters.inf_kingdom_t$f_f * nc$taxonId * nc$phylum;
    }
    if (evidence.isF$e$kingdom()) {
      nc$kingdom += parameters.inf_kingdom_f$f_t * nc$taxonId * c$phylum;
      nc$kingdom += parameters.inf_kingdom_f$f_f * nc$taxonId * nc$phylum;
    }
    if (trace != null) {
        trace.add("kingdom", c$kingdom);
        trace.add("!kingdom", nc$kingdom);
    }
    return (c$taxonRank + nc$taxonRank) * (c$scientificNameAuthorship + nc$scientificNameAuthorship) * (c$kingdom + nc$kingdom);
  }


  public Inference probability(SimpleLinnaeanInferencer.Evidence evidence, SimpleLinnaeanParameters_TT parameters, Trace trace) {
    double p;
    double prior = parameters.prior_taxonId_t;
    double ph = 0.0;
    double pe = 0.0;

    if (trace != null)
        trace.push("t");
    try {
        if (evidence.isT$e$taxonId()) {
          p = this.infer_t(evidence, parameters, trace) * parameters.prior_taxonId_t;
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
        if (evidence.isF$e$taxonId()) {
          p = this.infer_f(evidence, parameters, trace) * parameters.prior_taxonId_f;
          pe += p;
        }
    } finally {
        if (trace != null)
            trace.pop();
    }
    return Inference.forPEH(prior, pe, ph);
  }

  @Override
  public Inference probability(SimpleLinnaeanClassification classification, Classifier classifier, Trace trace) throws BayesianException {
    if (trace != null)
        trace.push("inference");
    try {
        SimpleLinnaeanInferencer.Evidence evidence = classification.match(classifier);
        if (trace != null)
            trace.add("evidence", evidence);
        SimpleLinnaeanParameters_TT params = (SimpleLinnaeanParameters_TT) classifier.getCachedParameters();
        if (params == null) {
          params = new SimpleLinnaeanParameters_TT();
          classifier.loadParameters(params);
        }
        return this.probability(evidence, params, trace);
    } finally {
        if (trace != null)
            trace.pop();
    }
  }
}