package au.org.ala.names.generated;

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.Inferencer;
import au.org.ala.bayesian.Trace;
import au.org.ala.bayesian.Trace.TraceLevel;

public class SimpleLinnaeanInferencer_FT implements Inferencer<SimpleLinnaeanClassification> {
  public final static String SIGNATURE = "FT";

  public SimpleLinnaeanInferencer_FT() {
  }

  @Override
  public String getSignature() {
    return SIGNATURE;
  }

  public double infer_t(SimpleLinnaeanInferencer.Evidence evidence, SimpleLinnaeanParameters_FT parameters, Trace trace) {
    double c$taxonId = 1.0;
    double nc$taxonId = 0.0;
    double c$taxonRank = 0.0;
    double nc$taxonRank = 0.0;
    double c$scientificNameAuthorship = 0.0;
    double nc$scientificNameAuthorship = 0.0;
    double c$scientificName = 0.0;
    double nc$scientificName = 0.0;
    double c$prefixScientificName = evidence.isT$e$prefixScientificName() ? 1.0 : 0.0;
    double nc$prefixScientificName = evidence.isF$e$prefixScientificName() ? 1.0 : 0.0;
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
      c$taxonRank += parameters.inf_taxonRank_t$t * c$taxonId;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_taxonRank_t$t) + " \u00b7 " + this.formatDouble(c$taxonId);
        trace.add(TraceLevel.TRACE, "p(taxonRank) - taxonRank", "p(taxonRank | taxonID)·taxonID", val_, c$taxonRank);
      }
    }
    if (evidence.isF$e$taxonRank()) {
      nc$taxonRank += parameters.inf_taxonRank_f$t * c$taxonId;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_taxonRank_f$t) + " \u00b7 " + this.formatDouble(c$taxonId);
        trace.add(TraceLevel.TRACE, "p(¬taxonRank) - taxonRank", "p(¬taxonRank | taxonID)·taxonID", val_, nc$taxonRank);
      }
    }
    if (evidence.isT$e$scientificNameAuthorship()) {
      c$scientificNameAuthorship += parameters.inf_scientificNameAuthorship_t$t * c$taxonId;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_scientificNameAuthorship_t$t) + " \u00b7 " + this.formatDouble(c$taxonId);
        trace.add(TraceLevel.TRACE, "p(scientificNameAuthorship) - scientificNameAuthorship", "p(scientificNameAuthorship | taxonID)·taxonID", val_, c$scientificNameAuthorship);
      }
    }
    if (evidence.isF$e$scientificNameAuthorship()) {
      nc$scientificNameAuthorship += parameters.inf_scientificNameAuthorship_f$t * c$taxonId;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_scientificNameAuthorship_f$t) + " \u00b7 " + this.formatDouble(c$taxonId);
        trace.add(TraceLevel.TRACE, "p(¬scientificNameAuthorship) - scientificNameAuthorship", "p(¬scientificNameAuthorship | taxonID)·taxonID", val_, nc$scientificNameAuthorship);
      }
    }
    if (evidence.isT$e$scientificName()) {
      c$scientificName += parameters.inf_scientificName_t$t * c$taxonId;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_scientificName_t$t) + " \u00b7 " + this.formatDouble(c$taxonId);
        trace.add(TraceLevel.TRACE, "p(scientificName) - scientificName", "p(scientificName | taxonID)·taxonID", val_, c$scientificName);
      }
    }
    if (evidence.isF$e$scientificName()) {
      nc$scientificName += parameters.inf_scientificName_f$t * c$taxonId;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_scientificName_f$t) + " \u00b7 " + this.formatDouble(c$taxonId);
        trace.add(TraceLevel.TRACE, "p(¬scientificName) - scientificName", "p(¬scientificName | taxonID)·taxonID", val_, nc$scientificName);
      }
    }
    // Ignoring non-base prefixScientificName
    // Ignoring non-base soundexScientificName
    if (evidence.isT$e$genus()) {
       c$genus += parameters.derived_genus_t$t_ttt * c$taxonId * c$prefixScientificName * c$soundexScientificName * c$scientificName;
       c$genus += parameters.derived_genus_t$t_ttf * c$taxonId * c$prefixScientificName * c$soundexScientificName * nc$scientificName;
       c$genus += parameters.derived_genus_t$t_tft * c$taxonId * c$prefixScientificName * nc$soundexScientificName * c$scientificName;
       c$genus += parameters.derived_genus_t$t_tff * c$taxonId * c$prefixScientificName * nc$soundexScientificName * nc$scientificName;
       c$genus += parameters.derived_genus_t$t_ftt * c$taxonId * nc$prefixScientificName * c$soundexScientificName * c$scientificName;
       c$genus += parameters.derived_genus_t$t_ftf * c$taxonId * nc$prefixScientificName * c$soundexScientificName * nc$scientificName;
       c$genus += parameters.derived_genus_t$t_fft * c$taxonId * nc$prefixScientificName * nc$soundexScientificName * c$scientificName;
       c$genus += parameters.derived_genus_t$t_fff * c$taxonId * nc$prefixScientificName * nc$soundexScientificName * nc$scientificName;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.derived_genus_t$t_ttt) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$prefixScientificName) + " \u00b7 " + this.formatDouble(c$soundexScientificName) + " \u00b7 " + this.formatDouble(c$scientificName) + " + " + this.formatDouble(parameters.derived_genus_t$t_ttf) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$prefixScientificName) + " \u00b7 " + this.formatDouble(c$soundexScientificName) + " \u00b7 " + this.formatDouble(nc$scientificName) + " + " + this.formatDouble(parameters.derived_genus_t$t_tft) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$prefixScientificName) + " \u00b7 " + this.formatDouble(nc$soundexScientificName) + " \u00b7 " + this.formatDouble(c$scientificName) + " + " + this.formatDouble(parameters.derived_genus_t$t_tff) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$prefixScientificName) + " \u00b7 " + this.formatDouble(nc$soundexScientificName) + " \u00b7 " + this.formatDouble(nc$scientificName) + " + " + this.formatDouble(parameters.derived_genus_t$t_ftt) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$prefixScientificName) + " \u00b7 " + this.formatDouble(c$soundexScientificName) + " \u00b7 " + this.formatDouble(c$scientificName) + " + " + this.formatDouble(parameters.derived_genus_t$t_ftf) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$prefixScientificName) + " \u00b7 " + this.formatDouble(c$soundexScientificName) + " \u00b7 " + this.formatDouble(nc$scientificName) + " + " + this.formatDouble(parameters.derived_genus_t$t_fft) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$prefixScientificName) + " \u00b7 " + this.formatDouble(nc$soundexScientificName) + " \u00b7 " + this.formatDouble(c$scientificName) + " + " + this.formatDouble(parameters.derived_genus_t$t_fff) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$prefixScientificName) + " \u00b7 " + this.formatDouble(nc$soundexScientificName) + " \u00b7 " + this.formatDouble(nc$scientificName);
        trace.add(TraceLevel.TRACE, "p(genus) - genus", "p(genus | taxonID, prefixScientificName, soundexScientificName, scientificName)·taxonID·prefixScientificName·soundexScientificName·scientificName + p(genus | taxonID, prefixScientificName, soundexScientificName, ¬scientificName)·taxonID·prefixScientificName·soundexScientificName·¬scientificName + p(genus | taxonID, prefixScientificName, ¬soundexScientificName, scientificName)·taxonID·prefixScientificName·¬soundexScientificName·scientificName + p(genus | taxonID, prefixScientificName, ¬soundexScientificName, ¬scientificName)·taxonID·prefixScientificName·¬soundexScientificName·¬scientificName + p(genus | taxonID, ¬prefixScientificName, soundexScientificName, scientificName)·taxonID·¬prefixScientificName·soundexScientificName·scientificName + p(genus | taxonID, ¬prefixScientificName, soundexScientificName, ¬scientificName)·taxonID·¬prefixScientificName·soundexScientificName·¬scientificName + p(genus | taxonID, ¬prefixScientificName, ¬soundexScientificName, scientificName)·taxonID·¬prefixScientificName·¬soundexScientificName·scientificName + p(genus | taxonID, ¬prefixScientificName, ¬soundexScientificName, ¬scientificName)·taxonID·¬prefixScientificName·¬soundexScientificName·¬scientificName", val_, c$genus);
      }
    }
    if (evidence.isF$e$genus()) {
      nc$genus += parameters.derived_genus_f$t_ttt * c$taxonId * c$prefixScientificName * c$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_genus_f$t_ttf * c$taxonId * c$prefixScientificName * c$soundexScientificName * nc$scientificName;
      nc$genus += parameters.derived_genus_f$t_tft * c$taxonId * c$prefixScientificName * nc$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_genus_f$t_tff * c$taxonId * c$prefixScientificName * nc$soundexScientificName * nc$scientificName;
      nc$genus += parameters.derived_genus_f$t_ftt * c$taxonId * nc$prefixScientificName * c$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_genus_f$t_ftf * c$taxonId * nc$prefixScientificName * c$soundexScientificName * nc$scientificName;
      nc$genus += parameters.derived_genus_f$t_fft * c$taxonId * nc$prefixScientificName * nc$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_genus_f$t_fff * c$taxonId * nc$prefixScientificName * nc$soundexScientificName * nc$scientificName;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.derived_genus_f$t_ttt) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$prefixScientificName) + " \u00b7 " + this.formatDouble(c$soundexScientificName) + " \u00b7 " + this.formatDouble(c$scientificName) + " + " + this.formatDouble(parameters.derived_genus_f$t_ttf) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$prefixScientificName) + " \u00b7 " + this.formatDouble(c$soundexScientificName) + " \u00b7 " + this.formatDouble(nc$scientificName) + " + " + this.formatDouble(parameters.derived_genus_f$t_tft) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$prefixScientificName) + " \u00b7 " + this.formatDouble(nc$soundexScientificName) + " \u00b7 " + this.formatDouble(c$scientificName) + " + " + this.formatDouble(parameters.derived_genus_f$t_tff) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$prefixScientificName) + " \u00b7 " + this.formatDouble(nc$soundexScientificName) + " \u00b7 " + this.formatDouble(nc$scientificName) + " + " + this.formatDouble(parameters.derived_genus_f$t_ftt) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$prefixScientificName) + " \u00b7 " + this.formatDouble(c$soundexScientificName) + " \u00b7 " + this.formatDouble(c$scientificName) + " + " + this.formatDouble(parameters.derived_genus_f$t_ftf) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$prefixScientificName) + " \u00b7 " + this.formatDouble(c$soundexScientificName) + " \u00b7 " + this.formatDouble(nc$scientificName) + " + " + this.formatDouble(parameters.derived_genus_f$t_fft) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$prefixScientificName) + " \u00b7 " + this.formatDouble(nc$soundexScientificName) + " \u00b7 " + this.formatDouble(c$scientificName) + " + " + this.formatDouble(parameters.derived_genus_f$t_fff) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$prefixScientificName) + " \u00b7 " + this.formatDouble(nc$soundexScientificName) + " \u00b7 " + this.formatDouble(nc$scientificName);
        trace.add(TraceLevel.TRACE, "p(¬genus) - !genus", "p(¬genus | taxonID, prefixScientificName, soundexScientificName, scientificName)·taxonID·prefixScientificName·soundexScientificName·scientificName + p(¬genus | taxonID, prefixScientificName, soundexScientificName, ¬scientificName)·taxonID·prefixScientificName·soundexScientificName·¬scientificName + p(¬genus | taxonID, prefixScientificName, ¬soundexScientificName, scientificName)·taxonID·prefixScientificName·¬soundexScientificName·scientificName + p(¬genus | taxonID, prefixScientificName, ¬soundexScientificName, ¬scientificName)·taxonID·prefixScientificName·¬soundexScientificName·¬scientificName + p(¬genus | taxonID, ¬prefixScientificName, soundexScientificName, scientificName)·taxonID·¬prefixScientificName·soundexScientificName·scientificName + p(¬genus | taxonID, ¬prefixScientificName, soundexScientificName, ¬scientificName)·taxonID·¬prefixScientificName·soundexScientificName·¬scientificName + p(¬genus | taxonID, ¬prefixScientificName, ¬soundexScientificName, scientificName)·taxonID·¬prefixScientificName·¬soundexScientificName·scientificName + p(¬genus | taxonID, ¬prefixScientificName, ¬soundexScientificName, ¬scientificName)·taxonID·¬prefixScientificName·¬soundexScientificName·¬scientificName", val_, nc$genus);
      }
    }
    if (evidence.isT$e$family()) {
      c$family += parameters.inf_family_t$t_t * c$taxonId * c$genus;
      c$family += parameters.inf_family_t$t_f * c$taxonId * nc$genus;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_family_t$t_t) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$genus) + " + " + this.formatDouble(parameters.inf_family_t$t_f) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$genus);
        trace.add(TraceLevel.TRACE, "p(family) - family", "p(family | taxonID, genus)·taxonID·genus + p(family | taxonID, ¬genus)·taxonID·¬genus", val_, c$family);
      }
    }
    if (evidence.isF$e$family()) {
      nc$family += parameters.inf_family_f$t_t * c$taxonId * c$genus;
      nc$family += parameters.inf_family_f$t_f * c$taxonId * nc$genus;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_family_f$t_t) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$genus) + " + " + this.formatDouble(parameters.inf_family_f$t_f) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$genus);
        trace.add(TraceLevel.TRACE, "p(¬family) - family", "p(¬family | taxonID, genus)·taxonID·genus + p(¬family | taxonID, ¬genus)·taxonID·¬genus", val_, nc$family);
      }
    }
    if (evidence.isT$e$order()) {
      c$order += parameters.inf_order_t$t_t * c$taxonId * c$family;
      c$order += parameters.inf_order_t$t_f * c$taxonId * nc$family;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_order_t$t_t) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$family) + " + " + this.formatDouble(parameters.inf_order_t$t_f) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$family);
        trace.add(TraceLevel.TRACE, "p(order) - order", "p(order | taxonID, family)·taxonID·family + p(order | taxonID, ¬family)·taxonID·¬family", val_, c$order);
      }
    }
    if (evidence.isF$e$order()) {
      nc$order += parameters.inf_order_f$t_t * c$taxonId * c$family;
      nc$order += parameters.inf_order_f$t_f * c$taxonId * nc$family;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_order_f$t_t) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$family) + " + " + this.formatDouble(parameters.inf_order_f$t_f) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$family);
        trace.add(TraceLevel.TRACE, "p(¬order) - order", "p(¬order | taxonID, family)·taxonID·family + p(¬order | taxonID, ¬family)·taxonID·¬family", val_, nc$order);
      }
    }
    if (evidence.isT$e$class_()) {
      c$class_ += parameters.inf_class__t$t_t * c$taxonId * c$order;
      c$class_ += parameters.inf_class__t$t_f * c$taxonId * nc$order;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_class__t$t_t) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$order) + " + " + this.formatDouble(parameters.inf_class__t$t_f) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$order);
        trace.add(TraceLevel.TRACE, "p(class) - class", "p(class | taxonID, order)·taxonID·order + p(class | taxonID, ¬order)·taxonID·¬order", val_, c$class_);
      }
    }
    if (evidence.isF$e$class_()) {
      nc$class_ += parameters.inf_class__f$t_t * c$taxonId * c$order;
      nc$class_ += parameters.inf_class__f$t_f * c$taxonId * nc$order;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_class__f$t_t) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$order) + " + " + this.formatDouble(parameters.inf_class__f$t_f) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$order);
        trace.add(TraceLevel.TRACE, "p(¬class) - class", "p(¬class | taxonID, order)·taxonID·order + p(¬class | taxonID, ¬order)·taxonID·¬order", val_, nc$class_);
      }
    }
    if (evidence.isT$e$phylum()) {
      c$phylum += parameters.inf_phylum_t$t_t * c$taxonId * c$class_;
      c$phylum += parameters.inf_phylum_t$t_f * c$taxonId * nc$class_;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_phylum_t$t_t) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$class_) + " + " + this.formatDouble(parameters.inf_phylum_t$t_f) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$class_);
        trace.add(TraceLevel.TRACE, "p(phylum) - phylum", "p(phylum | taxonID, class)·taxonID·class + p(phylum | taxonID, ¬class)·taxonID·¬class", val_, c$phylum);
      }
    }
    if (evidence.isF$e$phylum()) {
      nc$phylum += parameters.inf_phylum_f$t_t * c$taxonId * c$class_;
      nc$phylum += parameters.inf_phylum_f$t_f * c$taxonId * nc$class_;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_phylum_f$t_t) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$class_) + " + " + this.formatDouble(parameters.inf_phylum_f$t_f) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$class_);
        trace.add(TraceLevel.TRACE, "p(¬phylum) - phylum", "p(¬phylum | taxonID, class)·taxonID·class + p(¬phylum | taxonID, ¬class)·taxonID·¬class", val_, nc$phylum);
      }
    }
    if (evidence.isT$e$kingdom()) {
      c$kingdom += parameters.inf_kingdom_t$t_t * c$taxonId * c$phylum;
      c$kingdom += parameters.inf_kingdom_t$t_f * c$taxonId * nc$phylum;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_kingdom_t$t_t) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$phylum) + " + " + this.formatDouble(parameters.inf_kingdom_t$t_f) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$phylum);
        trace.add(TraceLevel.TRACE, "p(kingdom) - kingdom", "p(kingdom | taxonID, phylum)·taxonID·phylum + p(kingdom | taxonID, ¬phylum)·taxonID·¬phylum", val_, c$kingdom);
      }
    }
    if (evidence.isF$e$kingdom()) {
      nc$kingdom += parameters.inf_kingdom_f$t_t * c$taxonId * c$phylum;
      nc$kingdom += parameters.inf_kingdom_f$t_f * c$taxonId * nc$phylum;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_kingdom_f$t_t) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(c$phylum) + " + " + this.formatDouble(parameters.inf_kingdom_f$t_f) + " \u00b7 " + this.formatDouble(c$taxonId) + " \u00b7 " + this.formatDouble(nc$phylum);
        trace.add(TraceLevel.TRACE, "p(¬kingdom) - kingdom", "p(¬kingdom | taxonID, phylum)·taxonID·phylum + p(¬kingdom | taxonID, ¬phylum)·taxonID·¬phylum", val_, nc$kingdom);
      }
    }
    double result_ = (c$taxonRank + nc$taxonRank) * (c$scientificNameAuthorship + nc$scientificNameAuthorship) * (c$kingdom + nc$kingdom);
    if (trace != null) {
      String val_ = "(" + this.formatDouble(c$taxonRank) + " + " + this.formatDouble(nc$taxonRank) + ")" + " \u00b7 " + "(" + this.formatDouble(c$scientificNameAuthorship) + " + " + this.formatDouble(nc$scientificNameAuthorship) + ")" + " \u00b7 " + "(" + this.formatDouble(c$kingdom) + " + " + this.formatDouble(nc$kingdom) + ")";
      trace.add(TraceLevel.TRACE, "c(E | taxonID)", "(p(taxonRank) + p(¬taxonRank)) \u00b7 (p(scientificNameAuthorship) + p(¬scientificNameAuthorship)) \u00b7 (p(kingdom) + p(¬kingdom))", val_, result_);
    }
    return result_;
  }

  public double infer_f(SimpleLinnaeanInferencer.Evidence evidence, SimpleLinnaeanParameters_FT parameters, Trace trace) {
    double c$taxonId = 0.0;
    double nc$taxonId = 1.0;
    double c$taxonRank = 0.0;
    double nc$taxonRank = 0.0;
    double c$scientificNameAuthorship = 0.0;
    double nc$scientificNameAuthorship = 0.0;
    double c$scientificName = 0.0;
    double nc$scientificName = 0.0;
    double c$prefixScientificName = evidence.isT$e$prefixScientificName() ? 1.0 : 0.0;
    double nc$prefixScientificName = evidence.isF$e$prefixScientificName() ? 1.0 : 0.0;
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
      c$taxonRank += parameters.inf_taxonRank_t$f * nc$taxonId;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_taxonRank_t$f) + " \u00b7 " + this.formatDouble(nc$taxonId);
        trace.add(TraceLevel.TRACE, "p(taxonRank) - taxonRank", "p(taxonRank | ¬taxonID)·¬taxonID", val_, c$taxonRank);
      }
    }
    if (evidence.isF$e$taxonRank()) {
      nc$taxonRank += parameters.inf_taxonRank_f$f * nc$taxonId;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_taxonRank_f$f) + " \u00b7 " + this.formatDouble(nc$taxonId);
        trace.add(TraceLevel.TRACE, "p(¬taxonRank) - taxonRank", "p(¬taxonRank | ¬taxonID)·¬taxonID", val_, nc$taxonRank);
      }
    }
    if (evidence.isT$e$scientificNameAuthorship()) {
      c$scientificNameAuthorship += parameters.inf_scientificNameAuthorship_t$f * nc$taxonId;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_scientificNameAuthorship_t$f) + " \u00b7 " + this.formatDouble(nc$taxonId);
        trace.add(TraceLevel.TRACE, "p(scientificNameAuthorship) - scientificNameAuthorship", "p(scientificNameAuthorship | ¬taxonID)·¬taxonID", val_, c$scientificNameAuthorship);
      }
    }
    if (evidence.isF$e$scientificNameAuthorship()) {
      nc$scientificNameAuthorship += parameters.inf_scientificNameAuthorship_f$f * nc$taxonId;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_scientificNameAuthorship_f$f) + " \u00b7 " + this.formatDouble(nc$taxonId);
        trace.add(TraceLevel.TRACE, "p(¬scientificNameAuthorship) - scientificNameAuthorship", "p(¬scientificNameAuthorship | ¬taxonID)·¬taxonID", val_, nc$scientificNameAuthorship);
      }
    }
    if (evidence.isT$e$scientificName()) {
      c$scientificName += parameters.inf_scientificName_t$f * nc$taxonId;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_scientificName_t$f) + " \u00b7 " + this.formatDouble(nc$taxonId);
        trace.add(TraceLevel.TRACE, "p(scientificName) - scientificName", "p(scientificName | ¬taxonID)·¬taxonID", val_, c$scientificName);
      }
    }
    if (evidence.isF$e$scientificName()) {
      nc$scientificName += parameters.inf_scientificName_f$f * nc$taxonId;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_scientificName_f$f) + " \u00b7 " + this.formatDouble(nc$taxonId);
        trace.add(TraceLevel.TRACE, "p(¬scientificName) - scientificName", "p(¬scientificName | ¬taxonID)·¬taxonID", val_, nc$scientificName);
      }
    }
    // Ignoring non-base prefixScientificName
    // Ignoring non-base soundexScientificName
    if (evidence.isT$e$genus()) {
       c$genus += parameters.derived_genus_t$f_ttt * nc$taxonId * c$prefixScientificName * c$soundexScientificName * c$scientificName;
       c$genus += parameters.derived_genus_t$f_ttf * nc$taxonId * c$prefixScientificName * c$soundexScientificName * nc$scientificName;
       c$genus += parameters.derived_genus_t$f_tft * nc$taxonId * c$prefixScientificName * nc$soundexScientificName * c$scientificName;
       c$genus += parameters.derived_genus_t$f_tff * nc$taxonId * c$prefixScientificName * nc$soundexScientificName * nc$scientificName;
       c$genus += parameters.derived_genus_t$f_ftt * nc$taxonId * nc$prefixScientificName * c$soundexScientificName * c$scientificName;
       c$genus += parameters.derived_genus_t$f_ftf * nc$taxonId * nc$prefixScientificName * c$soundexScientificName * nc$scientificName;
       c$genus += parameters.derived_genus_t$f_fft * nc$taxonId * nc$prefixScientificName * nc$soundexScientificName * c$scientificName;
       c$genus += parameters.derived_genus_t$f_fff * nc$taxonId * nc$prefixScientificName * nc$soundexScientificName * nc$scientificName;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.derived_genus_t$f_ttt) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$prefixScientificName) + " \u00b7 " + this.formatDouble(c$soundexScientificName) + " \u00b7 " + this.formatDouble(c$scientificName) + " + " + this.formatDouble(parameters.derived_genus_t$f_ttf) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$prefixScientificName) + " \u00b7 " + this.formatDouble(c$soundexScientificName) + " \u00b7 " + this.formatDouble(nc$scientificName) + " + " + this.formatDouble(parameters.derived_genus_t$f_tft) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$prefixScientificName) + " \u00b7 " + this.formatDouble(nc$soundexScientificName) + " \u00b7 " + this.formatDouble(c$scientificName) + " + " + this.formatDouble(parameters.derived_genus_t$f_tff) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$prefixScientificName) + " \u00b7 " + this.formatDouble(nc$soundexScientificName) + " \u00b7 " + this.formatDouble(nc$scientificName) + " + " + this.formatDouble(parameters.derived_genus_t$f_ftt) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$prefixScientificName) + " \u00b7 " + this.formatDouble(c$soundexScientificName) + " \u00b7 " + this.formatDouble(c$scientificName) + " + " + this.formatDouble(parameters.derived_genus_t$f_ftf) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$prefixScientificName) + " \u00b7 " + this.formatDouble(c$soundexScientificName) + " \u00b7 " + this.formatDouble(nc$scientificName) + " + " + this.formatDouble(parameters.derived_genus_t$f_fft) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$prefixScientificName) + " \u00b7 " + this.formatDouble(nc$soundexScientificName) + " \u00b7 " + this.formatDouble(c$scientificName) + " + " + this.formatDouble(parameters.derived_genus_t$f_fff) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$prefixScientificName) + " \u00b7 " + this.formatDouble(nc$soundexScientificName) + " \u00b7 " + this.formatDouble(nc$scientificName);
        trace.add(TraceLevel.TRACE, "p(genus) - genus", "p(genus | ¬taxonID, prefixScientificName, soundexScientificName, scientificName)·¬taxonID·prefixScientificName·soundexScientificName·scientificName + p(genus | ¬taxonID, prefixScientificName, soundexScientificName, ¬scientificName)·¬taxonID·prefixScientificName·soundexScientificName·¬scientificName + p(genus | ¬taxonID, prefixScientificName, ¬soundexScientificName, scientificName)·¬taxonID·prefixScientificName·¬soundexScientificName·scientificName + p(genus | ¬taxonID, prefixScientificName, ¬soundexScientificName, ¬scientificName)·¬taxonID·prefixScientificName·¬soundexScientificName·¬scientificName + p(genus | ¬taxonID, ¬prefixScientificName, soundexScientificName, scientificName)·¬taxonID·¬prefixScientificName·soundexScientificName·scientificName + p(genus | ¬taxonID, ¬prefixScientificName, soundexScientificName, ¬scientificName)·¬taxonID·¬prefixScientificName·soundexScientificName·¬scientificName + p(genus | ¬taxonID, ¬prefixScientificName, ¬soundexScientificName, scientificName)·¬taxonID·¬prefixScientificName·¬soundexScientificName·scientificName + p(genus | ¬taxonID, ¬prefixScientificName, ¬soundexScientificName, ¬scientificName)·¬taxonID·¬prefixScientificName·¬soundexScientificName·¬scientificName", val_, c$genus);
      }
    }
    if (evidence.isF$e$genus()) {
      nc$genus += parameters.derived_genus_f$f_ttt * nc$taxonId * c$prefixScientificName * c$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_genus_f$f_ttf * nc$taxonId * c$prefixScientificName * c$soundexScientificName * nc$scientificName;
      nc$genus += parameters.derived_genus_f$f_tft * nc$taxonId * c$prefixScientificName * nc$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_genus_f$f_tff * nc$taxonId * c$prefixScientificName * nc$soundexScientificName * nc$scientificName;
      nc$genus += parameters.derived_genus_f$f_ftt * nc$taxonId * nc$prefixScientificName * c$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_genus_f$f_ftf * nc$taxonId * nc$prefixScientificName * c$soundexScientificName * nc$scientificName;
      nc$genus += parameters.derived_genus_f$f_fft * nc$taxonId * nc$prefixScientificName * nc$soundexScientificName * c$scientificName;
      nc$genus += parameters.derived_genus_f$f_fff * nc$taxonId * nc$prefixScientificName * nc$soundexScientificName * nc$scientificName;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.derived_genus_f$f_ttt) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$prefixScientificName) + " \u00b7 " + this.formatDouble(c$soundexScientificName) + " \u00b7 " + this.formatDouble(c$scientificName) + " + " + this.formatDouble(parameters.derived_genus_f$f_ttf) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$prefixScientificName) + " \u00b7 " + this.formatDouble(c$soundexScientificName) + " \u00b7 " + this.formatDouble(nc$scientificName) + " + " + this.formatDouble(parameters.derived_genus_f$f_tft) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$prefixScientificName) + " \u00b7 " + this.formatDouble(nc$soundexScientificName) + " \u00b7 " + this.formatDouble(c$scientificName) + " + " + this.formatDouble(parameters.derived_genus_f$f_tff) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$prefixScientificName) + " \u00b7 " + this.formatDouble(nc$soundexScientificName) + " \u00b7 " + this.formatDouble(nc$scientificName) + " + " + this.formatDouble(parameters.derived_genus_f$f_ftt) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$prefixScientificName) + " \u00b7 " + this.formatDouble(c$soundexScientificName) + " \u00b7 " + this.formatDouble(c$scientificName) + " + " + this.formatDouble(parameters.derived_genus_f$f_ftf) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$prefixScientificName) + " \u00b7 " + this.formatDouble(c$soundexScientificName) + " \u00b7 " + this.formatDouble(nc$scientificName) + " + " + this.formatDouble(parameters.derived_genus_f$f_fft) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$prefixScientificName) + " \u00b7 " + this.formatDouble(nc$soundexScientificName) + " \u00b7 " + this.formatDouble(c$scientificName) + " + " + this.formatDouble(parameters.derived_genus_f$f_fff) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$prefixScientificName) + " \u00b7 " + this.formatDouble(nc$soundexScientificName) + " \u00b7 " + this.formatDouble(nc$scientificName);
        trace.add(TraceLevel.TRACE, "p(¬genus) - !genus", "p(¬genus | ¬taxonID, prefixScientificName, soundexScientificName, scientificName)·¬taxonID·prefixScientificName·soundexScientificName·scientificName + p(¬genus | ¬taxonID, prefixScientificName, soundexScientificName, ¬scientificName)·¬taxonID·prefixScientificName·soundexScientificName·¬scientificName + p(¬genus | ¬taxonID, prefixScientificName, ¬soundexScientificName, scientificName)·¬taxonID·prefixScientificName·¬soundexScientificName·scientificName + p(¬genus | ¬taxonID, prefixScientificName, ¬soundexScientificName, ¬scientificName)·¬taxonID·prefixScientificName·¬soundexScientificName·¬scientificName + p(¬genus | ¬taxonID, ¬prefixScientificName, soundexScientificName, scientificName)·¬taxonID·¬prefixScientificName·soundexScientificName·scientificName + p(¬genus | ¬taxonID, ¬prefixScientificName, soundexScientificName, ¬scientificName)·¬taxonID·¬prefixScientificName·soundexScientificName·¬scientificName + p(¬genus | ¬taxonID, ¬prefixScientificName, ¬soundexScientificName, scientificName)·¬taxonID·¬prefixScientificName·¬soundexScientificName·scientificName + p(¬genus | ¬taxonID, ¬prefixScientificName, ¬soundexScientificName, ¬scientificName)·¬taxonID·¬prefixScientificName·¬soundexScientificName·¬scientificName", val_, nc$genus);
      }
    }
    if (evidence.isT$e$family()) {
      c$family += parameters.inf_family_t$f_t * nc$taxonId * c$genus;
      c$family += parameters.inf_family_t$f_f * nc$taxonId * nc$genus;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_family_t$f_t) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$genus) + " + " + this.formatDouble(parameters.inf_family_t$f_f) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$genus);
        trace.add(TraceLevel.TRACE, "p(family) - family", "p(family | ¬taxonID, genus)·¬taxonID·genus + p(family | ¬taxonID, ¬genus)·¬taxonID·¬genus", val_, c$family);
      }
    }
    if (evidence.isF$e$family()) {
      nc$family += parameters.inf_family_f$f_t * nc$taxonId * c$genus;
      nc$family += parameters.inf_family_f$f_f * nc$taxonId * nc$genus;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_family_f$f_t) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$genus) + " + " + this.formatDouble(parameters.inf_family_f$f_f) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$genus);
        trace.add(TraceLevel.TRACE, "p(¬family) - family", "p(¬family | ¬taxonID, genus)·¬taxonID·genus + p(¬family | ¬taxonID, ¬genus)·¬taxonID·¬genus", val_, nc$family);
      }
    }
    if (evidence.isT$e$order()) {
      c$order += parameters.inf_order_t$f_t * nc$taxonId * c$family;
      c$order += parameters.inf_order_t$f_f * nc$taxonId * nc$family;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_order_t$f_t) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$family) + " + " + this.formatDouble(parameters.inf_order_t$f_f) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$family);
        trace.add(TraceLevel.TRACE, "p(order) - order", "p(order | ¬taxonID, family)·¬taxonID·family + p(order | ¬taxonID, ¬family)·¬taxonID·¬family", val_, c$order);
      }
    }
    if (evidence.isF$e$order()) {
      nc$order += parameters.inf_order_f$f_t * nc$taxonId * c$family;
      nc$order += parameters.inf_order_f$f_f * nc$taxonId * nc$family;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_order_f$f_t) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$family) + " + " + this.formatDouble(parameters.inf_order_f$f_f) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$family);
        trace.add(TraceLevel.TRACE, "p(¬order) - order", "p(¬order | ¬taxonID, family)·¬taxonID·family + p(¬order | ¬taxonID, ¬family)·¬taxonID·¬family", val_, nc$order);
      }
    }
    if (evidence.isT$e$class_()) {
      c$class_ += parameters.inf_class__t$f_t * nc$taxonId * c$order;
      c$class_ += parameters.inf_class__t$f_f * nc$taxonId * nc$order;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_class__t$f_t) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$order) + " + " + this.formatDouble(parameters.inf_class__t$f_f) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$order);
        trace.add(TraceLevel.TRACE, "p(class) - class", "p(class | ¬taxonID, order)·¬taxonID·order + p(class | ¬taxonID, ¬order)·¬taxonID·¬order", val_, c$class_);
      }
    }
    if (evidence.isF$e$class_()) {
      nc$class_ += parameters.inf_class__f$f_t * nc$taxonId * c$order;
      nc$class_ += parameters.inf_class__f$f_f * nc$taxonId * nc$order;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_class__f$f_t) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$order) + " + " + this.formatDouble(parameters.inf_class__f$f_f) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$order);
        trace.add(TraceLevel.TRACE, "p(¬class) - class", "p(¬class | ¬taxonID, order)·¬taxonID·order + p(¬class | ¬taxonID, ¬order)·¬taxonID·¬order", val_, nc$class_);
      }
    }
    if (evidence.isT$e$phylum()) {
      c$phylum += parameters.inf_phylum_t$f_t * nc$taxonId * c$class_;
      c$phylum += parameters.inf_phylum_t$f_f * nc$taxonId * nc$class_;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_phylum_t$f_t) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$class_) + " + " + this.formatDouble(parameters.inf_phylum_t$f_f) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$class_);
        trace.add(TraceLevel.TRACE, "p(phylum) - phylum", "p(phylum | ¬taxonID, class)·¬taxonID·class + p(phylum | ¬taxonID, ¬class)·¬taxonID·¬class", val_, c$phylum);
      }
    }
    if (evidence.isF$e$phylum()) {
      nc$phylum += parameters.inf_phylum_f$f_t * nc$taxonId * c$class_;
      nc$phylum += parameters.inf_phylum_f$f_f * nc$taxonId * nc$class_;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_phylum_f$f_t) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$class_) + " + " + this.formatDouble(parameters.inf_phylum_f$f_f) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$class_);
        trace.add(TraceLevel.TRACE, "p(¬phylum) - phylum", "p(¬phylum | ¬taxonID, class)·¬taxonID·class + p(¬phylum | ¬taxonID, ¬class)·¬taxonID·¬class", val_, nc$phylum);
      }
    }
    if (evidence.isT$e$kingdom()) {
      c$kingdom += parameters.inf_kingdom_t$f_t * nc$taxonId * c$phylum;
      c$kingdom += parameters.inf_kingdom_t$f_f * nc$taxonId * nc$phylum;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_kingdom_t$f_t) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$phylum) + " + " + this.formatDouble(parameters.inf_kingdom_t$f_f) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$phylum);
        trace.add(TraceLevel.TRACE, "p(kingdom) - kingdom", "p(kingdom | ¬taxonID, phylum)·¬taxonID·phylum + p(kingdom | ¬taxonID, ¬phylum)·¬taxonID·¬phylum", val_, c$kingdom);
      }
    }
    if (evidence.isF$e$kingdom()) {
      nc$kingdom += parameters.inf_kingdom_f$f_t * nc$taxonId * c$phylum;
      nc$kingdom += parameters.inf_kingdom_f$f_f * nc$taxonId * nc$phylum;
      if (trace != null) {
        String val_ = this.formatDouble(parameters.inf_kingdom_f$f_t) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(c$phylum) + " + " + this.formatDouble(parameters.inf_kingdom_f$f_f) + " \u00b7 " + this.formatDouble(nc$taxonId) + " \u00b7 " + this.formatDouble(nc$phylum);
        trace.add(TraceLevel.TRACE, "p(¬kingdom) - kingdom", "p(¬kingdom | ¬taxonID, phylum)·¬taxonID·phylum + p(¬kingdom | ¬taxonID, ¬phylum)·¬taxonID·¬phylum", val_, nc$kingdom);
      }
    }
    double result_ = (c$taxonRank + nc$taxonRank) * (c$scientificNameAuthorship + nc$scientificNameAuthorship) * (c$kingdom + nc$kingdom);
    if (trace != null) {
      String val_ = "(" + this.formatDouble(c$taxonRank) + " + " + this.formatDouble(nc$taxonRank) + ")" + " \u00b7 " + "(" + this.formatDouble(c$scientificNameAuthorship) + " + " + this.formatDouble(nc$scientificNameAuthorship) + ")" + " \u00b7 " + "(" + this.formatDouble(c$kingdom) + " + " + this.formatDouble(nc$kingdom) + ")";
      trace.add(TraceLevel.TRACE, "c(E | ¬taxonID)", "(p(taxonRank) + p(¬taxonRank)) \u00b7 (p(scientificNameAuthorship) + p(¬scientificNameAuthorship)) \u00b7 (p(kingdom) + p(¬kingdom))", val_, result_);
    }
    return result_;
  }


  public Inference probability(SimpleLinnaeanInferencer.Evidence evidence, SimpleLinnaeanParameters_FT parameters, Trace trace) {
    double c, p;
    double prior = parameters.prior_taxonId_t;
    double ph = 0.0;
    double pe = 0.0;

    if (trace != null)
        trace.push(TraceLevel.TRACE, "p(taxonID)");
    try {
        if (evidence.isT$e$taxonId()) {
          c = this.infer_t(evidence, parameters, trace);
          p = c  * parameters.prior_taxonId_t;
          if (trace != null) {
            trace.value(TraceLevel.TRACE, "c(E | taxonID)\u00b7p(taxonID)", this.formatDouble(c) +  " \u00b7 " + this.formatDouble(parameters.prior_taxonId_t), p);
          }
          ph += p;
          pe += p;
        }
    } finally {
        if (trace != null)
            trace.pop(TraceLevel.TRACE);
    }
    if (trace != null)
        trace.push(TraceLevel.TRACE, "p(¬taxonID)");
    try {
        if (evidence.isF$e$taxonId()) {
          c = this.infer_f(evidence, parameters, trace);
          p = c  * parameters.prior_taxonId_f;
          if (trace != null) {
            trace.value(TraceLevel.TRACE, "c(E | ¬taxonID)\u00b7p(¬taxonID)", this.formatDouble(c) +  " \u00b7 " + this.formatDouble(parameters.prior_taxonId_f), p);
          }
          pe += p;
        }
    } finally {
        if (trace != null)
            trace.pop(TraceLevel.TRACE);
    }
    return Inference.forPEH(prior, pe, ph);
  }

  @Override
  public Inference probability(SimpleLinnaeanClassification classification, Classifier classifier, Trace trace) throws BayesianException {
    if (trace != null)
        trace.push(TraceLevel.TRACE, "inference");
    try {
        SimpleLinnaeanInferencer.Evidence evidence = classification.match(classifier);
        if (trace != null)
            trace.add(TraceLevel.TRACE, "evidence", evidence);
        SimpleLinnaeanParameters_FT params = (SimpleLinnaeanParameters_FT) classifier.getCachedParameters();
        if (params == null) {
          params = new SimpleLinnaeanParameters_FT();
          classifier.loadParameters(params);
        }
        return this.probability(evidence, params, trace);
    } finally {
        if (trace != null)
            trace.pop(TraceLevel.TRACE);
    }
  }
}