package au.org.ala.names.generated;

import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Inferencer;
import au.org.ala.bayesian.StoreException;

public class SimpleLinnaeanInferencer implements Inferencer<SimpleLinnaeanClassification, SimpleLinnaeanParameters> {

  public double infer(Evidence evidence, SimpleLinnaeanParameters parameters, double c$taxonId) {
    double nc$taxonId = 1.0 - c$taxonId;
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
    if (evidence.isT$e$taxonRank()) {
      c$taxonRank += parameters.inf_t_t$taxonRank * c$taxonId;
      c$taxonRank += parameters.inf_t_f$taxonRank * nc$taxonId;
    }
    if (evidence.isF$e$taxonRank()) {
      nc$taxonRank += parameters.inf_f_t$taxonRank * c$taxonId;
      nc$taxonRank += parameters.inf_f_f$taxonRank * nc$taxonId;
    }
    // Ignoring non-base specificEpithet
    if (evidence.isT$e$scientificNameAuthorship()) {
      c$scientificNameAuthorship += parameters.inf_t_t$scientificNameAuthorship * c$taxonId;
      c$scientificNameAuthorship += parameters.inf_t_f$scientificNameAuthorship * nc$taxonId;
    }
    if (evidence.isF$e$scientificNameAuthorship()) {
      nc$scientificNameAuthorship += parameters.inf_f_t$scientificNameAuthorship * c$taxonId;
      nc$scientificNameAuthorship += parameters.inf_f_f$scientificNameAuthorship * nc$taxonId;
    }
    if (evidence.isT$e$scientificName()) {
      c$scientificName += parameters.derived_t_tt$scientificName * c$specificEpithet * c$taxonId;
      c$scientificName += parameters.derived_t_tf$scientificName * c$specificEpithet * nc$taxonId;
      c$scientificName += parameters.derived_t_ft$scientificName * nc$specificEpithet * c$taxonId;
      c$scientificName += parameters.derived_t_ff$scientificName * nc$specificEpithet * nc$taxonId;
    }
    if (evidence.isF$e$scientificName()) {
      nc$scientificName += parameters.derived_f_tt$scientificName * c$specificEpithet * c$taxonId;
      nc$scientificName += parameters.derived_f_tf$scientificName * c$specificEpithet * nc$taxonId;
      nc$scientificName += parameters.derived_f_ft$scientificName * nc$specificEpithet * c$taxonId;
      nc$scientificName += parameters.derived_f_ff$scientificName * nc$specificEpithet * nc$taxonId;
    }
    // Ignoring non-base soundexScientificName
    if (evidence.isT$e$genus()) {
      c$genus += parameters.derived_t_tt$genus * c$soundexScientificName * c$scientificName;
      c$genus += parameters.derived_t_tf$genus * c$soundexScientificName * nc$scientificName;
      c$genus += parameters.derived_t_ft$genus * nc$soundexScientificName * c$scientificName;
      c$genus += parameters.derived_t_ff$genus * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isF$e$genus()) {
      nc$genus += parameters.derived_f_tt$genus * c$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_f_tf$genus * c$soundexScientificName * nc$scientificName;
      nc$genus += parameters.derived_f_ft$genus * nc$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_f_ff$genus * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.isT$e$family()) {
      c$family += parameters.inf_t_t$family * c$genus;
      c$family += parameters.inf_t_f$family * nc$genus;
    }
    if (evidence.isF$e$family()) {
      nc$family += parameters.inf_f_t$family * c$genus;
      nc$family += parameters.inf_f_f$family * nc$genus;
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

  public Inference probability(Evidence evidence, SimpleLinnaeanParameters parameters) {
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
  public Inference probability(SimpleLinnaeanClassification classification, Classifier classifier, SimpleLinnaeanParameters parameters) throws StoreException, InferenceException {
    Evidence evidence = classification.match(classifier);
    return this.probability(evidence, parameters);
  }

  public static class Evidence {
    public Boolean e$taxonId;
    public Boolean e$taxonRank;
    public Boolean e$specificEpithet;
    public Boolean e$scientificNameAuthorship;
    public Boolean e$scientificName;
    public Boolean e$soundexScientificName;
    public Boolean e$genus;
    public Boolean e$family;
    public Boolean e$order;
    public Boolean e$class_;
    public Boolean e$phylum;
    public Boolean e$kingdom;

    public boolean isT$e$taxonId() {
      return this.e$taxonId == null || this.e$taxonId;
    }

    public boolean isF$e$taxonId() {
      return this.e$taxonId == null || !this.e$taxonId;
    }

    public boolean isT$e$taxonRank() {
      return this.e$taxonRank == null || this.e$taxonRank;
    }

    public boolean isF$e$taxonRank() {
      return this.e$taxonRank == null || !this.e$taxonRank;
    }

    public boolean isT$e$specificEpithet() {
      return this.e$specificEpithet == null || this.e$specificEpithet;
    }

    public boolean isF$e$specificEpithet() {
      return this.e$specificEpithet == null || !this.e$specificEpithet;
    }

    public boolean isT$e$scientificNameAuthorship() {
      return this.e$scientificNameAuthorship == null || this.e$scientificNameAuthorship;
    }

    public boolean isF$e$scientificNameAuthorship() {
      return this.e$scientificNameAuthorship == null || !this.e$scientificNameAuthorship;
    }

    public boolean isT$e$scientificName() {
      return this.e$scientificName == null || this.e$scientificName;
    }

    public boolean isF$e$scientificName() {
      return this.e$scientificName == null || !this.e$scientificName;
    }

    public boolean isT$e$soundexScientificName() {
      return this.e$soundexScientificName == null || this.e$soundexScientificName;
    }

    public boolean isF$e$soundexScientificName() {
      return this.e$soundexScientificName == null || !this.e$soundexScientificName;
    }

    public boolean isT$e$genus() {
      return this.e$genus == null || this.e$genus;
    }

    public boolean isF$e$genus() {
      return this.e$genus == null || !this.e$genus;
    }

    public boolean isT$e$family() {
      return this.e$family == null || this.e$family;
    }

    public boolean isF$e$family() {
      return this.e$family == null || !this.e$family;
    }

    public boolean isT$e$order() {
      return this.e$order == null || this.e$order;
    }

    public boolean isF$e$order() {
      return this.e$order == null || !this.e$order;
    }

    public boolean isT$e$class_() {
      return this.e$class_ == null || this.e$class_;
    }

    public boolean isF$e$class_() {
      return this.e$class_ == null || !this.e$class_;
    }

    public boolean isT$e$phylum() {
      return this.e$phylum == null || this.e$phylum;
    }

    public boolean isF$e$phylum() {
      return this.e$phylum == null || !this.e$phylum;
    }

    public boolean isT$e$kingdom() {
      return this.e$kingdom == null || this.e$kingdom;
    }

    public boolean isF$e$kingdom() {
      return this.e$kingdom == null || !this.e$kingdom;
    }

  }
}