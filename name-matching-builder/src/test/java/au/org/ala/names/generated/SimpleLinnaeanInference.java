package au.org.ala.names.generated;

public class SimpleLinnaeanInference {
  public SimpleLinnaeanParameters parameters;

  public double infer(Evidence evidence, double c$taxonId) {
    double nc$taxonId = 1.0 - c$taxonId;
    double c$taxonRank = 0;
    double nc$taxonRank = 0;
    double c$specificEpithet = evidence.isT$specificEpithet();
    double nc$specificEpithet = evidence.isF$specificEpithet();
    double c$scientificNameAuthorship = 0;
    double nc$scientificNameAuthorship = 0;
    double c$scientificName = 0;
    double nc$scientificName = 0;
    double c$soundexScientificName = evidence.isT$soundexScientificName();
    double nc$soundexScientificName = evidence.isF$soundexScientificName();
    double c$genus = 0;
    double nc$genus = 0;
    double c$family = 0;
    double nc$family = 0;
    double c$order = 0;
    double nc$order = 0;
    double c$class_ = 0;
    double nc$class_ = 0;
    double c$phylum = 0;
    double nc$phylum = 0;
    double c$kingdom = 0;
    double nc$kingdom = 0;
    if (evidence.e$taxonRank == null || evidence.e$taxonRank) {
      c$taxonRank += this.parameters.inf_t_t$taxonRank * c$taxonId;
      c$taxonRank += this.parameters.inf_t_f$taxonRank * nc$taxonId;
    }
    if (evidence.e$taxonRank == null || !evidence.e$taxonRank) {
      nc$taxonRank += this.parameters.inf_f_t$taxonRank * c$taxonId;
      nc$taxonRank += this.parameters.inf_f_f$taxonRank * nc$taxonId;
    }
    // Ignoring non-base specificEpithet
    if (evidence.e$scientificNameAuthorship == null || evidence.e$scientificNameAuthorship) {
      c$scientificNameAuthorship += this.parameters.inf_t_t$scientificNameAuthorship * c$taxonId;
      c$scientificNameAuthorship += this.parameters.inf_t_f$scientificNameAuthorship * nc$taxonId;
    }
    if (evidence.e$scientificNameAuthorship == null || !evidence.e$scientificNameAuthorship) {
      nc$scientificNameAuthorship += this.parameters.inf_f_t$scientificNameAuthorship * c$taxonId;
      nc$scientificNameAuthorship += this.parameters.inf_f_f$scientificNameAuthorship * nc$taxonId;
    }
    if (evidence.e$scientificName == null || evidence.e$scientificName) {
      c$scientificName += this.parameters.derived_t_tt$scientificName * c$specificEpithet * c$taxonId;
      c$scientificName += this.parameters.derived_t_tf$scientificName * c$specificEpithet * nc$taxonId;
      c$scientificName += this.parameters.derived_t_ft$scientificName * nc$specificEpithet * c$taxonId;
      c$scientificName += this.parameters.derived_t_ff$scientificName * nc$specificEpithet * nc$taxonId;
    }
    if (evidence.e$scientificName == null || !evidence.e$scientificName) {
      nc$scientificName += this.parameters.derived_f_tt$scientificName * c$specificEpithet * c$taxonId;
      nc$scientificName += this.parameters.derived_f_tf$scientificName * c$specificEpithet * nc$taxonId;
      nc$scientificName += this.parameters.derived_f_ft$scientificName * nc$specificEpithet * c$taxonId;
      nc$scientificName += this.parameters.derived_f_ff$scientificName * nc$specificEpithet * nc$taxonId;
    }
    // Ignoring non-base soundexScientificName
    if (evidence.e$genus == null || evidence.e$genus) {
      c$genus += this.parameters.derived_t_tt$genus * c$soundexScientificName * c$scientificName;
      c$genus += this.parameters.derived_t_tf$genus * c$soundexScientificName * nc$scientificName;
      c$genus += this.parameters.derived_t_ft$genus * nc$soundexScientificName * c$scientificName;
      c$genus += this.parameters.derived_t_ff$genus * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.e$genus == null || !evidence.e$genus) {
      nc$genus += this.parameters.derived_f_tt$genus * c$soundexScientificName * c$scientificName;
      nc$genus += this.parameters.derived_f_tf$genus * c$soundexScientificName * nc$scientificName;
      nc$genus += this.parameters.derived_f_ft$genus * nc$soundexScientificName * c$scientificName;
      nc$genus += this.parameters.derived_f_ff$genus * nc$soundexScientificName * nc$scientificName;
    }
    if (evidence.e$family == null || evidence.e$family) {
      c$family += this.parameters.inf_t_t$family * c$genus;
      c$family += this.parameters.inf_t_f$family * nc$genus;
    }
    if (evidence.e$family == null || !evidence.e$family) {
      nc$family += this.parameters.inf_f_t$family * c$genus;
      nc$family += this.parameters.inf_f_f$family * nc$genus;
    }
    if (evidence.e$order == null || evidence.e$order) {
      c$order += this.parameters.inf_t_t$order * c$family;
      c$order += this.parameters.inf_t_f$order * nc$family;
    }
    if (evidence.e$order == null || !evidence.e$order) {
      nc$order += this.parameters.inf_f_t$order * c$family;
      nc$order += this.parameters.inf_f_f$order * nc$family;
    }
    if (evidence.e$class_ == null || evidence.e$class_) {
      c$class_ += this.parameters.inf_t_t$class_ * c$order;
      c$class_ += this.parameters.inf_t_f$class_ * nc$order;
    }
    if (evidence.e$class_ == null || !evidence.e$class_) {
      nc$class_ += this.parameters.inf_f_t$class_ * c$order;
      nc$class_ += this.parameters.inf_f_f$class_ * nc$order;
    }
    if (evidence.e$phylum == null || evidence.e$phylum) {
      c$phylum += this.parameters.inf_t_t$phylum * c$class_;
      c$phylum += this.parameters.inf_t_f$phylum * nc$class_;
    }
    if (evidence.e$phylum == null || !evidence.e$phylum) {
      nc$phylum += this.parameters.inf_f_t$phylum * c$class_;
      nc$phylum += this.parameters.inf_f_f$phylum * nc$class_;
    }
    if (evidence.e$kingdom == null || evidence.e$kingdom) {
      c$kingdom += this.parameters.inf_t_t$kingdom * c$phylum;
      c$kingdom += this.parameters.inf_t_f$kingdom * nc$phylum;
    }
    if (evidence.e$kingdom == null || !evidence.e$kingdom) {
      nc$kingdom += this.parameters.inf_f_t$kingdom * c$phylum;
      nc$kingdom += this.parameters.inf_f_f$kingdom * nc$phylum;
    }
    return (c$taxonRank + nc$taxonRank) * (c$scientificNameAuthorship + nc$scientificNameAuthorship) * (c$kingdom + nc$kingdom) * (this.parameters.prior_t$taxonId * c$taxonId + this.parameters.prior_f$taxonId * nc$taxonId);
  }

  public double probability(Evidence evidence) {
    double p;
    double ph = 0.0;
    double pe = 0.0;

    p = evidence.isT$taxonID() * this.infer(evidence, 1.0);
    ph += p;
    pe += p;
    p = evidence.isF$taxonID() * this.infer(evidence, 0.0);
    pe += p;
    return pe == 0.0 ? 0.0 : ph / pe;
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

    public double isT$taxonID() {
      return this.e$taxonId == null || this.e$taxonId ? 1.0 : 0.0;
    }

    public double isF$taxonID() {
      return this.e$taxonId == null || !this.e$taxonId ? 1.0 : 0.0;
    }

    public double isT$taxonRank() {
      return this.e$taxonRank == null || this.e$taxonRank ? 1.0 : 0.0;
    }

    public double isF$taxonRank() {
      return this.e$taxonRank == null || !this.e$taxonRank ? 1.0 : 0.0;
    }

    public double isT$specificEpithet() {
      return this.e$specificEpithet == null || this.e$specificEpithet ? 1.0 : 0.0;
    }

    public double isF$specificEpithet() {
      return this.e$specificEpithet == null || !this.e$specificEpithet ? 1.0 : 0.0;
    }

    public double isT$scientificNameAuthorship() {
      return this.e$scientificNameAuthorship == null || this.e$scientificNameAuthorship ? 1.0 : 0.0;
    }

    public double isF$scientificNameAuthorship() {
      return this.e$scientificNameAuthorship == null || !this.e$scientificNameAuthorship ? 1.0 : 0.0;
    }

    public double isT$scientificName() {
      return this.e$scientificName == null || this.e$scientificName ? 1.0 : 0.0;
    }

    public double isF$scientificName() {
      return this.e$scientificName == null || !this.e$scientificName ? 1.0 : 0.0;
    }

    public double isT$soundexScientificName() {
      return this.e$soundexScientificName == null || this.e$soundexScientificName ? 1.0 : 0.0;
    }

    public double isF$soundexScientificName() {
      return this.e$soundexScientificName == null || !this.e$soundexScientificName ? 1.0 : 0.0;
    }

    public double isT$genus() {
      return this.e$genus == null || this.e$genus ? 1.0 : 0.0;
    }

    public double isF$genus() {
      return this.e$genus == null || !this.e$genus ? 1.0 : 0.0;
    }

    public double isT$family() {
      return this.e$family == null || this.e$family ? 1.0 : 0.0;
    }

    public double isF$family() {
      return this.e$family == null || !this.e$family ? 1.0 : 0.0;
    }

    public double isT$order() {
      return this.e$order == null || this.e$order ? 1.0 : 0.0;
    }

    public double isF$order() {
      return this.e$order == null || !this.e$order ? 1.0 : 0.0;
    }

    public double isT$class() {
      return this.e$class_ == null || this.e$class_ ? 1.0 : 0.0;
    }

    public double isF$class() {
      return this.e$class_ == null || !this.e$class_ ? 1.0 : 0.0;
    }

    public double isT$phylum() {
      return this.e$phylum == null || this.e$phylum ? 1.0 : 0.0;
    }

    public double isF$phylum() {
      return this.e$phylum == null || !this.e$phylum ? 1.0 : 0.0;
    }

    public double isT$kingdom() {
      return this.e$kingdom == null || this.e$kingdom ? 1.0 : 0.0;
    }

    public double isF$kingdom() {
      return this.e$kingdom == null || !this.e$kingdom ? 1.0 : 0.0;
    }

  }
}