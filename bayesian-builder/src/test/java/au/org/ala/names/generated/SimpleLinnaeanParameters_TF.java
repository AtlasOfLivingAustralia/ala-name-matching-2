package au.org.ala.names.generated;

import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.Parameters;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(alphabetic = true)
public class SimpleLinnaeanParameters_TF implements Parameters {

  public final static String SIGNATURE = "TF";

  public double prior_taxonId_t; // taxonID prior probability
  public double prior_taxonId_f; // 1 - taxonID prior probability
  public double inf_taxonRank_t$t; // p(taxonRank | taxonID) conditional probability
  public double inf_taxonRank_f$t; // p(¬taxonRank | taxonID) =  1 - p(taxonRank | taxonID) conditional probability
  public double inf_taxonRank_t$f; // p(taxonRank | ¬taxonID) conditional probability
  public double inf_taxonRank_f$f; // p(¬taxonRank | ¬taxonID) =  1 - p(taxonRank | ¬taxonID) conditional probability
  public double inf_scientificNameAuthorship_t$t; // p(scientificNameAuthorship | taxonID) conditional probability
  public double inf_scientificNameAuthorship_f$t; // p(¬scientificNameAuthorship | taxonID) =  1 - p(scientificNameAuthorship | taxonID) conditional probability
  public double inf_scientificNameAuthorship_t$f; // p(scientificNameAuthorship | ¬taxonID) conditional probability
  public double inf_scientificNameAuthorship_f$f; // p(¬scientificNameAuthorship | ¬taxonID) =  1 - p(scientificNameAuthorship | ¬taxonID) conditional probability
  public double inf_specificEpithet_t$t; // p(specificEpithet | taxonID) conditional probability
  public double inf_specificEpithet_f$t; // p(¬specificEpithet | taxonID) =  1 - p(specificEpithet | taxonID) conditional probability
  public double inf_specificEpithet_t$f; // p(specificEpithet | ¬taxonID) conditional probability
  public double inf_specificEpithet_f$f; // p(¬specificEpithet | ¬taxonID) =  1 - p(specificEpithet | ¬taxonID) conditional probability
  public double inf_scientificName_t$t_t; // p(scientificName | taxonID, specificEpithet) conditional probability
  public double inf_scientificName_f$t_t; // p(¬scientificName | taxonID, specificEpithet) =  1 - p(scientificName | taxonID, specificEpithet) conditional probability
  public double inf_scientificName_t$t_f; // p(scientificName | taxonID, ¬specificEpithet) conditional probability
  public double inf_scientificName_f$t_f; // p(¬scientificName | taxonID, ¬specificEpithet) =  1 - p(scientificName | taxonID, ¬specificEpithet) conditional probability
  public double inf_scientificName_t$f_t; // p(scientificName | ¬taxonID, specificEpithet) conditional probability
  public double inf_scientificName_f$f_t; // p(¬scientificName | ¬taxonID, specificEpithet) =  1 - p(scientificName | ¬taxonID, specificEpithet) conditional probability
  public double inf_scientificName_t$f_f; // p(scientificName | ¬taxonID, ¬specificEpithet) conditional probability
  public double inf_scientificName_f$f_f; // p(¬scientificName | ¬taxonID, ¬specificEpithet) =  1 - p(scientificName | ¬taxonID, ¬specificEpithet) conditional probability
  public double derived_scientificName_t$t_t; // p(scientificName | taxonID, specificEpithet) = p(scientificName | taxonID, specificEpithet).p(specificEpithet | taxonID)  derived conditional probability
  public double derived_scientificName_f$t_t; // p(¬scientificName | taxonID, specificEpithet) = p(¬scientificName | taxonID, specificEpithet).p(specificEpithet | taxonID)  derived conditional probability
  public double derived_scientificName_t$t_f; // p(scientificName | taxonID, ¬specificEpithet) = p(scientificName | taxonID, ¬specificEpithet).p(¬specificEpithet | taxonID)  derived conditional probability
  public double derived_scientificName_f$t_f; // p(¬scientificName | taxonID, ¬specificEpithet) = p(¬scientificName | taxonID, ¬specificEpithet).p(¬specificEpithet | taxonID)  derived conditional probability
  public double derived_scientificName_t$f_t; // p(scientificName | ¬taxonID, specificEpithet) = p(scientificName | ¬taxonID, specificEpithet).p(specificEpithet | ¬taxonID)  derived conditional probability
  public double derived_scientificName_f$f_t; // p(¬scientificName | ¬taxonID, specificEpithet) = p(¬scientificName | ¬taxonID, specificEpithet).p(specificEpithet | ¬taxonID)  derived conditional probability
  public double derived_scientificName_t$f_f; // p(scientificName | ¬taxonID, ¬specificEpithet) = p(scientificName | ¬taxonID, ¬specificEpithet).p(¬specificEpithet | ¬taxonID)  derived conditional probability
  public double derived_scientificName_f$f_f; // p(¬scientificName | ¬taxonID, ¬specificEpithet) = p(¬scientificName | ¬taxonID, ¬specificEpithet).p(¬specificEpithet | ¬taxonID)  derived conditional probability
  public double inf_soundexScientificName_t$t_t; // p(soundexScientificName | taxonID, scientificName) conditional probability
  public double inf_soundexScientificName_f$t_t; // p(¬soundexScientificName | taxonID, scientificName) =  1 - p(soundexScientificName | taxonID, scientificName) conditional probability
  public double inf_soundexScientificName_t$t_f; // p(soundexScientificName | taxonID, ¬scientificName) conditional probability
  public double inf_soundexScientificName_f$t_f; // p(¬soundexScientificName | taxonID, ¬scientificName) =  1 - p(soundexScientificName | taxonID, ¬scientificName) conditional probability
  public double inf_soundexScientificName_t$f_t; // p(soundexScientificName | ¬taxonID, scientificName) conditional probability
  public double inf_soundexScientificName_f$f_t; // p(¬soundexScientificName | ¬taxonID, scientificName) =  1 - p(soundexScientificName | ¬taxonID, scientificName) conditional probability
  public double inf_soundexScientificName_t$f_f; // p(soundexScientificName | ¬taxonID, ¬scientificName) conditional probability
  public double inf_soundexScientificName_f$f_f; // p(¬soundexScientificName | ¬taxonID, ¬scientificName) =  1 - p(soundexScientificName | ¬taxonID, ¬scientificName) conditional probability
  public double inf_family_t$t_tt; // p(family | taxonID, scientificName, soundexScientificName) conditional probability
  public double inf_family_f$t_tt; // p(¬family | taxonID, scientificName, soundexScientificName) =  1 - p(family | taxonID, scientificName, soundexScientificName) conditional probability
  public double inf_family_t$t_tf; // p(family | taxonID, scientificName, ¬soundexScientificName) conditional probability
  public double inf_family_f$t_tf; // p(¬family | taxonID, scientificName, ¬soundexScientificName) =  1 - p(family | taxonID, scientificName, ¬soundexScientificName) conditional probability
  public double inf_family_t$t_ft; // p(family | taxonID, ¬scientificName, soundexScientificName) conditional probability
  public double inf_family_f$t_ft; // p(¬family | taxonID, ¬scientificName, soundexScientificName) =  1 - p(family | taxonID, ¬scientificName, soundexScientificName) conditional probability
  public double inf_family_t$t_ff; // p(family | taxonID, ¬scientificName, ¬soundexScientificName) conditional probability
  public double inf_family_f$t_ff; // p(¬family | taxonID, ¬scientificName, ¬soundexScientificName) =  1 - p(family | taxonID, ¬scientificName, ¬soundexScientificName) conditional probability
  public double inf_family_t$f_tt; // p(family | ¬taxonID, scientificName, soundexScientificName) conditional probability
  public double inf_family_f$f_tt; // p(¬family | ¬taxonID, scientificName, soundexScientificName) =  1 - p(family | ¬taxonID, scientificName, soundexScientificName) conditional probability
  public double inf_family_t$f_tf; // p(family | ¬taxonID, scientificName, ¬soundexScientificName) conditional probability
  public double inf_family_f$f_tf; // p(¬family | ¬taxonID, scientificName, ¬soundexScientificName) =  1 - p(family | ¬taxonID, scientificName, ¬soundexScientificName) conditional probability
  public double inf_family_t$f_ft; // p(family | ¬taxonID, ¬scientificName, soundexScientificName) conditional probability
  public double inf_family_f$f_ft; // p(¬family | ¬taxonID, ¬scientificName, soundexScientificName) =  1 - p(family | ¬taxonID, ¬scientificName, soundexScientificName) conditional probability
  public double inf_family_t$f_ff; // p(family | ¬taxonID, ¬scientificName, ¬soundexScientificName) conditional probability
  public double inf_family_f$f_ff; // p(¬family | ¬taxonID, ¬scientificName, ¬soundexScientificName) =  1 - p(family | ¬taxonID, ¬scientificName, ¬soundexScientificName) conditional probability
  public double derived_family_t$t_tt; // p(family | taxonID, soundexScientificName, scientificName) = p(family | taxonID, scientificName, soundexScientificName).p(soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_f$t_tt; // p(¬family | taxonID, soundexScientificName, scientificName) = p(¬family | taxonID, scientificName, soundexScientificName).p(soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_t$t_tf; // p(family | taxonID, soundexScientificName, ¬scientificName) = p(family | taxonID, ¬scientificName, soundexScientificName).p(soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_f$t_tf; // p(¬family | taxonID, soundexScientificName, ¬scientificName) = p(¬family | taxonID, ¬scientificName, soundexScientificName).p(soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_t$t_ft; // p(family | taxonID, ¬soundexScientificName, scientificName) = p(family | taxonID, scientificName, ¬soundexScientificName).p(¬soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_f$t_ft; // p(¬family | taxonID, ¬soundexScientificName, scientificName) = p(¬family | taxonID, scientificName, ¬soundexScientificName).p(¬soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_t$t_ff; // p(family | taxonID, ¬soundexScientificName, ¬scientificName) = p(family | taxonID, ¬scientificName, ¬soundexScientificName).p(¬soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_f$t_ff; // p(¬family | taxonID, ¬soundexScientificName, ¬scientificName) = p(¬family | taxonID, ¬scientificName, ¬soundexScientificName).p(¬soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_t$f_tt; // p(family | ¬taxonID, soundexScientificName, scientificName) = p(family | taxonID, scientificName, soundexScientificName).p(soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_f$f_tt; // p(¬family | ¬taxonID, soundexScientificName, scientificName) = p(¬family | taxonID, scientificName, soundexScientificName).p(soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_t$f_tf; // p(family | ¬taxonID, soundexScientificName, ¬scientificName) = p(family | taxonID, ¬scientificName, soundexScientificName).p(soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_f$f_tf; // p(¬family | ¬taxonID, soundexScientificName, ¬scientificName) = p(¬family | taxonID, ¬scientificName, soundexScientificName).p(soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_t$f_ft; // p(family | ¬taxonID, ¬soundexScientificName, scientificName) = p(family | taxonID, scientificName, ¬soundexScientificName).p(¬soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_f$f_ft; // p(¬family | ¬taxonID, ¬soundexScientificName, scientificName) = p(¬family | taxonID, scientificName, ¬soundexScientificName).p(¬soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_t$f_ff; // p(family | ¬taxonID, ¬soundexScientificName, ¬scientificName) = p(family | taxonID, ¬scientificName, ¬soundexScientificName).p(¬soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_f$f_ff; // p(¬family | ¬taxonID, ¬soundexScientificName, ¬scientificName) = p(¬family | taxonID, ¬scientificName, ¬soundexScientificName).p(¬soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double inf_order_t$t_t; // p(order | taxonID, family) conditional probability
  public double inf_order_f$t_t; // p(¬order | taxonID, family) =  1 - p(order | taxonID, family) conditional probability
  public double inf_order_t$t_f; // p(order | taxonID, ¬family) conditional probability
  public double inf_order_f$t_f; // p(¬order | taxonID, ¬family) =  1 - p(order | taxonID, ¬family) conditional probability
  public double inf_order_t$f_t; // p(order | ¬taxonID, family) conditional probability
  public double inf_order_f$f_t; // p(¬order | ¬taxonID, family) =  1 - p(order | ¬taxonID, family) conditional probability
  public double inf_order_t$f_f; // p(order | ¬taxonID, ¬family) conditional probability
  public double inf_order_f$f_f; // p(¬order | ¬taxonID, ¬family) =  1 - p(order | ¬taxonID, ¬family) conditional probability
  public double inf_class__t$t_t; // p(class | taxonID, order) conditional probability
  public double inf_class__f$t_t; // p(¬class | taxonID, order) =  1 - p(class | taxonID, order) conditional probability
  public double inf_class__t$t_f; // p(class | taxonID, ¬order) conditional probability
  public double inf_class__f$t_f; // p(¬class | taxonID, ¬order) =  1 - p(class | taxonID, ¬order) conditional probability
  public double inf_class__t$f_t; // p(class | ¬taxonID, order) conditional probability
  public double inf_class__f$f_t; // p(¬class | ¬taxonID, order) =  1 - p(class | ¬taxonID, order) conditional probability
  public double inf_class__t$f_f; // p(class | ¬taxonID, ¬order) conditional probability
  public double inf_class__f$f_f; // p(¬class | ¬taxonID, ¬order) =  1 - p(class | ¬taxonID, ¬order) conditional probability
  public double inf_phylum_t$t_t; // p(phylum | taxonID, class) conditional probability
  public double inf_phylum_f$t_t; // p(¬phylum | taxonID, class) =  1 - p(phylum | taxonID, class) conditional probability
  public double inf_phylum_t$t_f; // p(phylum | taxonID, ¬class) conditional probability
  public double inf_phylum_f$t_f; // p(¬phylum | taxonID, ¬class) =  1 - p(phylum | taxonID, ¬class) conditional probability
  public double inf_phylum_t$f_t; // p(phylum | ¬taxonID, class) conditional probability
  public double inf_phylum_f$f_t; // p(¬phylum | ¬taxonID, class) =  1 - p(phylum | ¬taxonID, class) conditional probability
  public double inf_phylum_t$f_f; // p(phylum | ¬taxonID, ¬class) conditional probability
  public double inf_phylum_f$f_f; // p(¬phylum | ¬taxonID, ¬class) =  1 - p(phylum | ¬taxonID, ¬class) conditional probability
  public double inf_kingdom_t$t_t; // p(kingdom | taxonID, phylum) conditional probability
  public double inf_kingdom_f$t_t; // p(¬kingdom | taxonID, phylum) =  1 - p(kingdom | taxonID, phylum) conditional probability
  public double inf_kingdom_t$t_f; // p(kingdom | taxonID, ¬phylum) conditional probability
  public double inf_kingdom_f$t_f; // p(¬kingdom | taxonID, ¬phylum) =  1 - p(kingdom | taxonID, ¬phylum) conditional probability
  public double inf_kingdom_t$f_t; // p(kingdom | ¬taxonID, phylum) conditional probability
  public double inf_kingdom_f$f_t; // p(¬kingdom | ¬taxonID, phylum) =  1 - p(kingdom | ¬taxonID, phylum) conditional probability
  public double inf_kingdom_t$f_f; // p(kingdom | ¬taxonID, ¬phylum) conditional probability
  public double inf_kingdom_f$f_f; // p(¬kingdom | ¬taxonID, ¬phylum) =  1 - p(kingdom | ¬taxonID, ¬phylum) conditional probability

  public SimpleLinnaeanParameters_TF() {
  }

  @Override
  public void load(double[] vector) {
    this.prior_taxonId_t = vector[0];
    this.inf_taxonRank_t$t = vector[1];
    this.inf_taxonRank_t$f = vector[2];
    this.inf_scientificNameAuthorship_t$t = vector[3];
    this.inf_scientificNameAuthorship_t$f = vector[4];
    this.inf_specificEpithet_t$t = vector[5];
    this.inf_specificEpithet_t$f = vector[6];
    this.inf_scientificName_t$t_t = vector[7];
    this.inf_scientificName_t$t_f = vector[8];
    this.inf_scientificName_t$f_t = vector[9];
    this.inf_scientificName_t$f_f = vector[10];
    this.inf_soundexScientificName_t$t_t = vector[11];
    this.inf_soundexScientificName_t$t_f = vector[12];
    this.inf_soundexScientificName_t$f_t = vector[13];
    this.inf_soundexScientificName_t$f_f = vector[14];
    this.inf_family_t$t_tt = vector[15];
    this.inf_family_t$t_tf = vector[16];
    this.inf_family_t$t_ft = vector[17];
    this.inf_family_t$t_ff = vector[18];
    this.inf_family_t$f_tt = vector[19];
    this.inf_family_t$f_tf = vector[20];
    this.inf_family_t$f_ft = vector[21];
    this.inf_family_t$f_ff = vector[22];
    this.inf_order_t$t_t = vector[23];
    this.inf_order_t$t_f = vector[24];
    this.inf_order_t$f_t = vector[25];
    this.inf_order_t$f_f = vector[26];
    this.inf_class__t$t_t = vector[27];
    this.inf_class__t$t_f = vector[28];
    this.inf_class__t$f_t = vector[29];
    this.inf_class__t$f_f = vector[30];
    this.inf_phylum_t$t_t = vector[31];
    this.inf_phylum_t$t_f = vector[32];
    this.inf_phylum_t$f_t = vector[33];
    this.inf_phylum_t$f_f = vector[34];
    this.inf_kingdom_t$t_t = vector[35];
    this.inf_kingdom_t$t_f = vector[36];
    this.inf_kingdom_t$f_t = vector[37];
    this.inf_kingdom_t$f_f = vector[38];
    this.build();
  }

  @Override
  public double[] store() {
    double[] vector = new double[39];

    vector[0] = this.prior_taxonId_t;
    vector[1] = this.inf_taxonRank_t$t;
    vector[2] = this.inf_taxonRank_t$f;
    vector[3] = this.inf_scientificNameAuthorship_t$t;
    vector[4] = this.inf_scientificNameAuthorship_t$f;
    vector[5] = this.inf_specificEpithet_t$t;
    vector[6] = this.inf_specificEpithet_t$f;
    vector[7] = this.inf_scientificName_t$t_t;
    vector[8] = this.inf_scientificName_t$t_f;
    vector[9] = this.inf_scientificName_t$f_t;
    vector[10] = this.inf_scientificName_t$f_f;
    vector[11] = this.inf_soundexScientificName_t$t_t;
    vector[12] = this.inf_soundexScientificName_t$t_f;
    vector[13] = this.inf_soundexScientificName_t$f_t;
    vector[14] = this.inf_soundexScientificName_t$f_f;
    vector[15] = this.inf_family_t$t_tt;
    vector[16] = this.inf_family_t$t_tf;
    vector[17] = this.inf_family_t$t_ft;
    vector[18] = this.inf_family_t$t_ff;
    vector[19] = this.inf_family_t$f_tt;
    vector[20] = this.inf_family_t$f_tf;
    vector[21] = this.inf_family_t$f_ft;
    vector[22] = this.inf_family_t$f_ff;
    vector[23] = this.inf_order_t$t_t;
    vector[24] = this.inf_order_t$t_f;
    vector[25] = this.inf_order_t$f_t;
    vector[26] = this.inf_order_t$f_f;
    vector[27] = this.inf_class__t$t_t;
    vector[28] = this.inf_class__t$t_f;
    vector[29] = this.inf_class__t$f_t;
    vector[30] = this.inf_class__t$f_f;
    vector[31] = this.inf_phylum_t$t_t;
    vector[32] = this.inf_phylum_t$t_f;
    vector[33] = this.inf_phylum_t$f_t;
    vector[34] = this.inf_phylum_t$f_f;
    vector[35] = this.inf_kingdom_t$t_t;
    vector[36] = this.inf_kingdom_t$t_f;
    vector[37] = this.inf_kingdom_t$f_t;
    vector[38] = this.inf_kingdom_t$f_f;
    return vector;
  }

  public void build() {
    this.prior_taxonId_f = 1.0 - this.prior_taxonId_t;
    this.inf_taxonRank_f$t = 1.0 - this.inf_taxonRank_t$t;
    this.inf_taxonRank_f$f = 1.0 - this.inf_taxonRank_t$f;
    this.inf_scientificNameAuthorship_f$t = 1.0 - this.inf_scientificNameAuthorship_t$t;
    this.inf_scientificNameAuthorship_f$f = 1.0 - this.inf_scientificNameAuthorship_t$f;
    this.inf_specificEpithet_f$t = 1.0 - this.inf_specificEpithet_t$t;
    this.inf_specificEpithet_f$f = 1.0 - this.inf_specificEpithet_t$f;
    this.inf_scientificName_f$t_t = 1.0 - this.inf_scientificName_t$t_t;
    this.inf_scientificName_f$t_f = 1.0 - this.inf_scientificName_t$t_f;
    this.inf_scientificName_f$f_t = 1.0 - this.inf_scientificName_t$f_t;
    this.inf_scientificName_f$f_f = 1.0 - this.inf_scientificName_t$f_f;
    this.derived_scientificName_t$t_t = this.inf_scientificName_t$t_t * this.inf_specificEpithet_t$t;
    this.derived_scientificName_f$t_t = this.inf_scientificName_f$t_t * this.inf_specificEpithet_t$t;
    this.derived_scientificName_t$t_f = this.inf_scientificName_t$t_f * this.inf_specificEpithet_f$t;
    this.derived_scientificName_f$t_f = this.inf_scientificName_f$t_f * this.inf_specificEpithet_f$t;
    this.derived_scientificName_t$f_t = this.inf_scientificName_t$f_t * this.inf_specificEpithet_t$f;
    this.derived_scientificName_f$f_t = this.inf_scientificName_f$f_t * this.inf_specificEpithet_t$f;
    this.derived_scientificName_t$f_f = this.inf_scientificName_t$f_f * this.inf_specificEpithet_f$f;
    this.derived_scientificName_f$f_f = this.inf_scientificName_f$f_f * this.inf_specificEpithet_f$f;
    this.inf_soundexScientificName_f$t_t = 1.0 - this.inf_soundexScientificName_t$t_t;
    this.inf_soundexScientificName_f$t_f = 1.0 - this.inf_soundexScientificName_t$t_f;
    this.inf_soundexScientificName_f$f_t = 1.0 - this.inf_soundexScientificName_t$f_t;
    this.inf_soundexScientificName_f$f_f = 1.0 - this.inf_soundexScientificName_t$f_f;
    this.inf_family_f$t_tt = 1.0 - this.inf_family_t$t_tt;
    this.inf_family_f$t_tf = 1.0 - this.inf_family_t$t_tf;
    this.inf_family_f$t_ft = 1.0 - this.inf_family_t$t_ft;
    this.inf_family_f$t_ff = 1.0 - this.inf_family_t$t_ff;
    this.inf_family_f$f_tt = 1.0 - this.inf_family_t$f_tt;
    this.inf_family_f$f_tf = 1.0 - this.inf_family_t$f_tf;
    this.inf_family_f$f_ft = 1.0 - this.inf_family_t$f_ft;
    this.inf_family_f$f_ff = 1.0 - this.inf_family_t$f_ff;
    this.derived_family_t$t_tt = this.inf_family_t$t_tt * this.inf_soundexScientificName_t$t_t;
    this.derived_family_f$t_tt = this.inf_family_f$t_tt * this.inf_soundexScientificName_t$t_t;
    this.derived_family_t$t_tf = this.inf_family_t$t_ft * this.inf_soundexScientificName_t$t_f;
    this.derived_family_f$t_tf = this.inf_family_f$t_ft * this.inf_soundexScientificName_t$t_f;
    this.derived_family_t$t_ft = this.inf_family_t$t_tf * this.inf_soundexScientificName_f$t_t;
    this.derived_family_f$t_ft = this.inf_family_f$t_tf * this.inf_soundexScientificName_f$t_t;
    this.derived_family_t$t_ff = this.inf_family_t$t_ff * this.inf_soundexScientificName_f$t_f;
    this.derived_family_f$t_ff = this.inf_family_f$t_ff * this.inf_soundexScientificName_f$t_f;
    this.derived_family_t$f_tt = this.inf_family_t$t_tt * this.inf_soundexScientificName_t$t_t;
    this.derived_family_f$f_tt = this.inf_family_f$t_tt * this.inf_soundexScientificName_t$t_t;
    this.derived_family_t$f_tf = this.inf_family_t$t_ft * this.inf_soundexScientificName_t$t_f;
    this.derived_family_f$f_tf = this.inf_family_f$t_ft * this.inf_soundexScientificName_t$t_f;
    this.derived_family_t$f_ft = this.inf_family_t$t_tf * this.inf_soundexScientificName_f$t_t;
    this.derived_family_f$f_ft = this.inf_family_f$t_tf * this.inf_soundexScientificName_f$t_t;
    this.derived_family_t$f_ff = this.inf_family_t$t_ff * this.inf_soundexScientificName_f$t_f;
    this.derived_family_f$f_ff = this.inf_family_f$t_ff * this.inf_soundexScientificName_f$t_f;
    this.inf_order_f$t_t = 1.0 - this.inf_order_t$t_t;
    this.inf_order_f$t_f = 1.0 - this.inf_order_t$t_f;
    this.inf_order_f$f_t = 1.0 - this.inf_order_t$f_t;
    this.inf_order_f$f_f = 1.0 - this.inf_order_t$f_f;
    this.inf_class__f$t_t = 1.0 - this.inf_class__t$t_t;
    this.inf_class__f$t_f = 1.0 - this.inf_class__t$t_f;
    this.inf_class__f$f_t = 1.0 - this.inf_class__t$f_t;
    this.inf_class__f$f_f = 1.0 - this.inf_class__t$f_f;
    this.inf_phylum_f$t_t = 1.0 - this.inf_phylum_t$t_t;
    this.inf_phylum_f$t_f = 1.0 - this.inf_phylum_t$t_f;
    this.inf_phylum_f$f_t = 1.0 - this.inf_phylum_t$f_t;
    this.inf_phylum_f$f_f = 1.0 - this.inf_phylum_t$f_f;
    this.inf_kingdom_f$t_t = 1.0 - this.inf_kingdom_t$t_t;
    this.inf_kingdom_f$t_f = 1.0 - this.inf_kingdom_t$t_f;
    this.inf_kingdom_f$f_t = 1.0 - this.inf_kingdom_t$f_t;
    this.inf_kingdom_f$f_f = 1.0 - this.inf_kingdom_t$f_f;
  }

}