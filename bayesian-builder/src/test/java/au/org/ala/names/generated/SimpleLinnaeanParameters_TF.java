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
  public double inf_prefixScientificName_t$t_t; // p(prefixScientificName | taxonID, scientificName) conditional probability
  public double inf_prefixScientificName_f$t_t; // p(¬prefixScientificName | taxonID, scientificName) =  1 - p(prefixScientificName | taxonID, scientificName) conditional probability
  public double inf_prefixScientificName_t$t_f; // p(prefixScientificName | taxonID, ¬scientificName) conditional probability
  public double inf_prefixScientificName_f$t_f; // p(¬prefixScientificName | taxonID, ¬scientificName) =  1 - p(prefixScientificName | taxonID, ¬scientificName) conditional probability
  public double inf_prefixScientificName_t$f_t; // p(prefixScientificName | ¬taxonID, scientificName) conditional probability
  public double inf_prefixScientificName_f$f_t; // p(¬prefixScientificName | ¬taxonID, scientificName) =  1 - p(prefixScientificName | ¬taxonID, scientificName) conditional probability
  public double inf_prefixScientificName_t$f_f; // p(prefixScientificName | ¬taxonID, ¬scientificName) conditional probability
  public double inf_prefixScientificName_f$f_f; // p(¬prefixScientificName | ¬taxonID, ¬scientificName) =  1 - p(prefixScientificName | ¬taxonID, ¬scientificName) conditional probability
  public double inf_soundexScientificName_t$t_t; // p(soundexScientificName | taxonID, scientificName) conditional probability
  public double inf_soundexScientificName_f$t_t; // p(¬soundexScientificName | taxonID, scientificName) =  1 - p(soundexScientificName | taxonID, scientificName) conditional probability
  public double inf_soundexScientificName_t$t_f; // p(soundexScientificName | taxonID, ¬scientificName) conditional probability
  public double inf_soundexScientificName_f$t_f; // p(¬soundexScientificName | taxonID, ¬scientificName) =  1 - p(soundexScientificName | taxonID, ¬scientificName) conditional probability
  public double inf_soundexScientificName_t$f_t; // p(soundexScientificName | ¬taxonID, scientificName) conditional probability
  public double inf_soundexScientificName_f$f_t; // p(¬soundexScientificName | ¬taxonID, scientificName) =  1 - p(soundexScientificName | ¬taxonID, scientificName) conditional probability
  public double inf_soundexScientificName_t$f_f; // p(soundexScientificName | ¬taxonID, ¬scientificName) conditional probability
  public double inf_soundexScientificName_f$f_f; // p(¬soundexScientificName | ¬taxonID, ¬scientificName) =  1 - p(soundexScientificName | ¬taxonID, ¬scientificName) conditional probability
  public double inf_family_t$t_ttt; // p(family | taxonID, scientificName, soundexScientificName, prefixScientificName) conditional probability
  public double inf_family_f$t_ttt; // p(¬family | taxonID, scientificName, soundexScientificName, prefixScientificName) =  1 - p(family | taxonID, scientificName, soundexScientificName, prefixScientificName) conditional probability
  public double inf_family_t$t_ttf; // p(family | taxonID, scientificName, soundexScientificName, ¬prefixScientificName) conditional probability
  public double inf_family_f$t_ttf; // p(¬family | taxonID, scientificName, soundexScientificName, ¬prefixScientificName) =  1 - p(family | taxonID, scientificName, soundexScientificName, ¬prefixScientificName) conditional probability
  public double inf_family_t$t_tft; // p(family | taxonID, scientificName, ¬soundexScientificName, prefixScientificName) conditional probability
  public double inf_family_f$t_tft; // p(¬family | taxonID, scientificName, ¬soundexScientificName, prefixScientificName) =  1 - p(family | taxonID, scientificName, ¬soundexScientificName, prefixScientificName) conditional probability
  public double inf_family_t$t_tff; // p(family | taxonID, scientificName, ¬soundexScientificName, ¬prefixScientificName) conditional probability
  public double inf_family_f$t_tff; // p(¬family | taxonID, scientificName, ¬soundexScientificName, ¬prefixScientificName) =  1 - p(family | taxonID, scientificName, ¬soundexScientificName, ¬prefixScientificName) conditional probability
  public double inf_family_t$t_ftt; // p(family | taxonID, ¬scientificName, soundexScientificName, prefixScientificName) conditional probability
  public double inf_family_f$t_ftt; // p(¬family | taxonID, ¬scientificName, soundexScientificName, prefixScientificName) =  1 - p(family | taxonID, ¬scientificName, soundexScientificName, prefixScientificName) conditional probability
  public double inf_family_t$t_ftf; // p(family | taxonID, ¬scientificName, soundexScientificName, ¬prefixScientificName) conditional probability
  public double inf_family_f$t_ftf; // p(¬family | taxonID, ¬scientificName, soundexScientificName, ¬prefixScientificName) =  1 - p(family | taxonID, ¬scientificName, soundexScientificName, ¬prefixScientificName) conditional probability
  public double inf_family_t$t_fft; // p(family | taxonID, ¬scientificName, ¬soundexScientificName, prefixScientificName) conditional probability
  public double inf_family_f$t_fft; // p(¬family | taxonID, ¬scientificName, ¬soundexScientificName, prefixScientificName) =  1 - p(family | taxonID, ¬scientificName, ¬soundexScientificName, prefixScientificName) conditional probability
  public double inf_family_t$t_fff; // p(family | taxonID, ¬scientificName, ¬soundexScientificName, ¬prefixScientificName) conditional probability
  public double inf_family_f$t_fff; // p(¬family | taxonID, ¬scientificName, ¬soundexScientificName, ¬prefixScientificName) =  1 - p(family | taxonID, ¬scientificName, ¬soundexScientificName, ¬prefixScientificName) conditional probability
  public double inf_family_t$f_ttt; // p(family | ¬taxonID, scientificName, soundexScientificName, prefixScientificName) conditional probability
  public double inf_family_f$f_ttt; // p(¬family | ¬taxonID, scientificName, soundexScientificName, prefixScientificName) =  1 - p(family | ¬taxonID, scientificName, soundexScientificName, prefixScientificName) conditional probability
  public double inf_family_t$f_ttf; // p(family | ¬taxonID, scientificName, soundexScientificName, ¬prefixScientificName) conditional probability
  public double inf_family_f$f_ttf; // p(¬family | ¬taxonID, scientificName, soundexScientificName, ¬prefixScientificName) =  1 - p(family | ¬taxonID, scientificName, soundexScientificName, ¬prefixScientificName) conditional probability
  public double inf_family_t$f_tft; // p(family | ¬taxonID, scientificName, ¬soundexScientificName, prefixScientificName) conditional probability
  public double inf_family_f$f_tft; // p(¬family | ¬taxonID, scientificName, ¬soundexScientificName, prefixScientificName) =  1 - p(family | ¬taxonID, scientificName, ¬soundexScientificName, prefixScientificName) conditional probability
  public double inf_family_t$f_tff; // p(family | ¬taxonID, scientificName, ¬soundexScientificName, ¬prefixScientificName) conditional probability
  public double inf_family_f$f_tff; // p(¬family | ¬taxonID, scientificName, ¬soundexScientificName, ¬prefixScientificName) =  1 - p(family | ¬taxonID, scientificName, ¬soundexScientificName, ¬prefixScientificName) conditional probability
  public double inf_family_t$f_ftt; // p(family | ¬taxonID, ¬scientificName, soundexScientificName, prefixScientificName) conditional probability
  public double inf_family_f$f_ftt; // p(¬family | ¬taxonID, ¬scientificName, soundexScientificName, prefixScientificName) =  1 - p(family | ¬taxonID, ¬scientificName, soundexScientificName, prefixScientificName) conditional probability
  public double inf_family_t$f_ftf; // p(family | ¬taxonID, ¬scientificName, soundexScientificName, ¬prefixScientificName) conditional probability
  public double inf_family_f$f_ftf; // p(¬family | ¬taxonID, ¬scientificName, soundexScientificName, ¬prefixScientificName) =  1 - p(family | ¬taxonID, ¬scientificName, soundexScientificName, ¬prefixScientificName) conditional probability
  public double inf_family_t$f_fft; // p(family | ¬taxonID, ¬scientificName, ¬soundexScientificName, prefixScientificName) conditional probability
  public double inf_family_f$f_fft; // p(¬family | ¬taxonID, ¬scientificName, ¬soundexScientificName, prefixScientificName) =  1 - p(family | ¬taxonID, ¬scientificName, ¬soundexScientificName, prefixScientificName) conditional probability
  public double inf_family_t$f_fff; // p(family | ¬taxonID, ¬scientificName, ¬soundexScientificName, ¬prefixScientificName) conditional probability
  public double inf_family_f$f_fff; // p(¬family | ¬taxonID, ¬scientificName, ¬soundexScientificName, ¬prefixScientificName) =  1 - p(family | ¬taxonID, ¬scientificName, ¬soundexScientificName, ¬prefixScientificName) conditional probability
  public double derived_family_t$t_ttt; // p(family | taxonID, prefixScientificName, soundexScientificName, scientificName) = p(family | taxonID, scientificName, soundexScientificName, prefixScientificName).p(prefixScientificName | taxonID, scientificName).p(soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_f$t_ttt; // p(¬family | taxonID, prefixScientificName, soundexScientificName, scientificName) = p(¬family | taxonID, scientificName, soundexScientificName, prefixScientificName).p(prefixScientificName | taxonID, scientificName).p(soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_t$t_ttf; // p(family | taxonID, prefixScientificName, soundexScientificName, ¬scientificName) = p(family | taxonID, ¬scientificName, soundexScientificName, prefixScientificName).p(prefixScientificName | taxonID, ¬scientificName).p(soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_f$t_ttf; // p(¬family | taxonID, prefixScientificName, soundexScientificName, ¬scientificName) = p(¬family | taxonID, ¬scientificName, soundexScientificName, prefixScientificName).p(prefixScientificName | taxonID, ¬scientificName).p(soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_t$t_tft; // p(family | taxonID, prefixScientificName, ¬soundexScientificName, scientificName) = p(family | taxonID, scientificName, ¬soundexScientificName, prefixScientificName).p(prefixScientificName | taxonID, scientificName).p(¬soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_f$t_tft; // p(¬family | taxonID, prefixScientificName, ¬soundexScientificName, scientificName) = p(¬family | taxonID, scientificName, ¬soundexScientificName, prefixScientificName).p(prefixScientificName | taxonID, scientificName).p(¬soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_t$t_tff; // p(family | taxonID, prefixScientificName, ¬soundexScientificName, ¬scientificName) = p(family | taxonID, ¬scientificName, ¬soundexScientificName, prefixScientificName).p(prefixScientificName | taxonID, ¬scientificName).p(¬soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_f$t_tff; // p(¬family | taxonID, prefixScientificName, ¬soundexScientificName, ¬scientificName) = p(¬family | taxonID, ¬scientificName, ¬soundexScientificName, prefixScientificName).p(prefixScientificName | taxonID, ¬scientificName).p(¬soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_t$t_ftt; // p(family | taxonID, ¬prefixScientificName, soundexScientificName, scientificName) = p(family | taxonID, scientificName, soundexScientificName, ¬prefixScientificName).p(¬prefixScientificName | taxonID, scientificName).p(soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_f$t_ftt; // p(¬family | taxonID, ¬prefixScientificName, soundexScientificName, scientificName) = p(¬family | taxonID, scientificName, soundexScientificName, ¬prefixScientificName).p(¬prefixScientificName | taxonID, scientificName).p(soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_t$t_ftf; // p(family | taxonID, ¬prefixScientificName, soundexScientificName, ¬scientificName) = p(family | taxonID, ¬scientificName, soundexScientificName, ¬prefixScientificName).p(¬prefixScientificName | taxonID, ¬scientificName).p(soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_f$t_ftf; // p(¬family | taxonID, ¬prefixScientificName, soundexScientificName, ¬scientificName) = p(¬family | taxonID, ¬scientificName, soundexScientificName, ¬prefixScientificName).p(¬prefixScientificName | taxonID, ¬scientificName).p(soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_t$t_fft; // p(family | taxonID, ¬prefixScientificName, ¬soundexScientificName, scientificName) = p(family | taxonID, scientificName, ¬soundexScientificName, ¬prefixScientificName).p(¬prefixScientificName | taxonID, scientificName).p(¬soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_f$t_fft; // p(¬family | taxonID, ¬prefixScientificName, ¬soundexScientificName, scientificName) = p(¬family | taxonID, scientificName, ¬soundexScientificName, ¬prefixScientificName).p(¬prefixScientificName | taxonID, scientificName).p(¬soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_t$t_fff; // p(family | taxonID, ¬prefixScientificName, ¬soundexScientificName, ¬scientificName) = p(family | taxonID, ¬scientificName, ¬soundexScientificName, ¬prefixScientificName).p(¬prefixScientificName | taxonID, ¬scientificName).p(¬soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_f$t_fff; // p(¬family | taxonID, ¬prefixScientificName, ¬soundexScientificName, ¬scientificName) = p(¬family | taxonID, ¬scientificName, ¬soundexScientificName, ¬prefixScientificName).p(¬prefixScientificName | taxonID, ¬scientificName).p(¬soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_t$f_ttt; // p(family | ¬taxonID, prefixScientificName, soundexScientificName, scientificName) = p(family | taxonID, scientificName, soundexScientificName, prefixScientificName).p(prefixScientificName | taxonID, scientificName).p(soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_f$f_ttt; // p(¬family | ¬taxonID, prefixScientificName, soundexScientificName, scientificName) = p(¬family | taxonID, scientificName, soundexScientificName, prefixScientificName).p(prefixScientificName | taxonID, scientificName).p(soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_t$f_ttf; // p(family | ¬taxonID, prefixScientificName, soundexScientificName, ¬scientificName) = p(family | taxonID, ¬scientificName, soundexScientificName, prefixScientificName).p(prefixScientificName | taxonID, ¬scientificName).p(soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_f$f_ttf; // p(¬family | ¬taxonID, prefixScientificName, soundexScientificName, ¬scientificName) = p(¬family | taxonID, ¬scientificName, soundexScientificName, prefixScientificName).p(prefixScientificName | taxonID, ¬scientificName).p(soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_t$f_tft; // p(family | ¬taxonID, prefixScientificName, ¬soundexScientificName, scientificName) = p(family | taxonID, scientificName, ¬soundexScientificName, prefixScientificName).p(prefixScientificName | taxonID, scientificName).p(¬soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_f$f_tft; // p(¬family | ¬taxonID, prefixScientificName, ¬soundexScientificName, scientificName) = p(¬family | taxonID, scientificName, ¬soundexScientificName, prefixScientificName).p(prefixScientificName | taxonID, scientificName).p(¬soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_t$f_tff; // p(family | ¬taxonID, prefixScientificName, ¬soundexScientificName, ¬scientificName) = p(family | taxonID, ¬scientificName, ¬soundexScientificName, prefixScientificName).p(prefixScientificName | taxonID, ¬scientificName).p(¬soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_f$f_tff; // p(¬family | ¬taxonID, prefixScientificName, ¬soundexScientificName, ¬scientificName) = p(¬family | taxonID, ¬scientificName, ¬soundexScientificName, prefixScientificName).p(prefixScientificName | taxonID, ¬scientificName).p(¬soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_t$f_ftt; // p(family | ¬taxonID, ¬prefixScientificName, soundexScientificName, scientificName) = p(family | taxonID, scientificName, soundexScientificName, ¬prefixScientificName).p(¬prefixScientificName | taxonID, scientificName).p(soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_f$f_ftt; // p(¬family | ¬taxonID, ¬prefixScientificName, soundexScientificName, scientificName) = p(¬family | taxonID, scientificName, soundexScientificName, ¬prefixScientificName).p(¬prefixScientificName | taxonID, scientificName).p(soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_t$f_ftf; // p(family | ¬taxonID, ¬prefixScientificName, soundexScientificName, ¬scientificName) = p(family | taxonID, ¬scientificName, soundexScientificName, ¬prefixScientificName).p(¬prefixScientificName | taxonID, ¬scientificName).p(soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_f$f_ftf; // p(¬family | ¬taxonID, ¬prefixScientificName, soundexScientificName, ¬scientificName) = p(¬family | taxonID, ¬scientificName, soundexScientificName, ¬prefixScientificName).p(¬prefixScientificName | taxonID, ¬scientificName).p(soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_t$f_fft; // p(family | ¬taxonID, ¬prefixScientificName, ¬soundexScientificName, scientificName) = p(family | taxonID, scientificName, ¬soundexScientificName, ¬prefixScientificName).p(¬prefixScientificName | taxonID, scientificName).p(¬soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_f$f_fft; // p(¬family | ¬taxonID, ¬prefixScientificName, ¬soundexScientificName, scientificName) = p(¬family | taxonID, scientificName, ¬soundexScientificName, ¬prefixScientificName).p(¬prefixScientificName | taxonID, scientificName).p(¬soundexScientificName | taxonID, scientificName)  derived conditional probability
  public double derived_family_t$f_fff; // p(family | ¬taxonID, ¬prefixScientificName, ¬soundexScientificName, ¬scientificName) = p(family | taxonID, ¬scientificName, ¬soundexScientificName, ¬prefixScientificName).p(¬prefixScientificName | taxonID, ¬scientificName).p(¬soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
  public double derived_family_f$f_fff; // p(¬family | ¬taxonID, ¬prefixScientificName, ¬soundexScientificName, ¬scientificName) = p(¬family | taxonID, ¬scientificName, ¬soundexScientificName, ¬prefixScientificName).p(¬prefixScientificName | taxonID, ¬scientificName).p(¬soundexScientificName | taxonID, ¬scientificName)  derived conditional probability
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
    this.inf_prefixScientificName_t$t_t = vector[11];
    this.inf_prefixScientificName_t$t_f = vector[12];
    this.inf_prefixScientificName_t$f_t = vector[13];
    this.inf_prefixScientificName_t$f_f = vector[14];
    this.inf_soundexScientificName_t$t_t = vector[15];
    this.inf_soundexScientificName_t$t_f = vector[16];
    this.inf_soundexScientificName_t$f_t = vector[17];
    this.inf_soundexScientificName_t$f_f = vector[18];
    this.inf_family_t$t_ttt = vector[19];
    this.inf_family_t$t_ttf = vector[20];
    this.inf_family_t$t_tft = vector[21];
    this.inf_family_t$t_tff = vector[22];
    this.inf_family_t$t_ftt = vector[23];
    this.inf_family_t$t_ftf = vector[24];
    this.inf_family_t$t_fft = vector[25];
    this.inf_family_t$t_fff = vector[26];
    this.inf_family_t$f_ttt = vector[27];
    this.inf_family_t$f_ttf = vector[28];
    this.inf_family_t$f_tft = vector[29];
    this.inf_family_t$f_tff = vector[30];
    this.inf_family_t$f_ftt = vector[31];
    this.inf_family_t$f_ftf = vector[32];
    this.inf_family_t$f_fft = vector[33];
    this.inf_family_t$f_fff = vector[34];
    this.inf_order_t$t_t = vector[35];
    this.inf_order_t$t_f = vector[36];
    this.inf_order_t$f_t = vector[37];
    this.inf_order_t$f_f = vector[38];
    this.inf_class__t$t_t = vector[39];
    this.inf_class__t$t_f = vector[40];
    this.inf_class__t$f_t = vector[41];
    this.inf_class__t$f_f = vector[42];
    this.inf_phylum_t$t_t = vector[43];
    this.inf_phylum_t$t_f = vector[44];
    this.inf_phylum_t$f_t = vector[45];
    this.inf_phylum_t$f_f = vector[46];
    this.inf_kingdom_t$t_t = vector[47];
    this.inf_kingdom_t$t_f = vector[48];
    this.inf_kingdom_t$f_t = vector[49];
    this.inf_kingdom_t$f_f = vector[50];
    this.build();
  }

  @Override
  public double[] store() {
    double[] vector = new double[51];

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
    vector[11] = this.inf_prefixScientificName_t$t_t;
    vector[12] = this.inf_prefixScientificName_t$t_f;
    vector[13] = this.inf_prefixScientificName_t$f_t;
    vector[14] = this.inf_prefixScientificName_t$f_f;
    vector[15] = this.inf_soundexScientificName_t$t_t;
    vector[16] = this.inf_soundexScientificName_t$t_f;
    vector[17] = this.inf_soundexScientificName_t$f_t;
    vector[18] = this.inf_soundexScientificName_t$f_f;
    vector[19] = this.inf_family_t$t_ttt;
    vector[20] = this.inf_family_t$t_ttf;
    vector[21] = this.inf_family_t$t_tft;
    vector[22] = this.inf_family_t$t_tff;
    vector[23] = this.inf_family_t$t_ftt;
    vector[24] = this.inf_family_t$t_ftf;
    vector[25] = this.inf_family_t$t_fft;
    vector[26] = this.inf_family_t$t_fff;
    vector[27] = this.inf_family_t$f_ttt;
    vector[28] = this.inf_family_t$f_ttf;
    vector[29] = this.inf_family_t$f_tft;
    vector[30] = this.inf_family_t$f_tff;
    vector[31] = this.inf_family_t$f_ftt;
    vector[32] = this.inf_family_t$f_ftf;
    vector[33] = this.inf_family_t$f_fft;
    vector[34] = this.inf_family_t$f_fff;
    vector[35] = this.inf_order_t$t_t;
    vector[36] = this.inf_order_t$t_f;
    vector[37] = this.inf_order_t$f_t;
    vector[38] = this.inf_order_t$f_f;
    vector[39] = this.inf_class__t$t_t;
    vector[40] = this.inf_class__t$t_f;
    vector[41] = this.inf_class__t$f_t;
    vector[42] = this.inf_class__t$f_f;
    vector[43] = this.inf_phylum_t$t_t;
    vector[44] = this.inf_phylum_t$t_f;
    vector[45] = this.inf_phylum_t$f_t;
    vector[46] = this.inf_phylum_t$f_f;
    vector[47] = this.inf_kingdom_t$t_t;
    vector[48] = this.inf_kingdom_t$t_f;
    vector[49] = this.inf_kingdom_t$f_t;
    vector[50] = this.inf_kingdom_t$f_f;
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
    this.inf_prefixScientificName_f$t_t = 1.0 - this.inf_prefixScientificName_t$t_t;
    this.inf_prefixScientificName_f$t_f = 1.0 - this.inf_prefixScientificName_t$t_f;
    this.inf_prefixScientificName_f$f_t = 1.0 - this.inf_prefixScientificName_t$f_t;
    this.inf_prefixScientificName_f$f_f = 1.0 - this.inf_prefixScientificName_t$f_f;
    this.inf_soundexScientificName_f$t_t = 1.0 - this.inf_soundexScientificName_t$t_t;
    this.inf_soundexScientificName_f$t_f = 1.0 - this.inf_soundexScientificName_t$t_f;
    this.inf_soundexScientificName_f$f_t = 1.0 - this.inf_soundexScientificName_t$f_t;
    this.inf_soundexScientificName_f$f_f = 1.0 - this.inf_soundexScientificName_t$f_f;
    this.inf_family_f$t_ttt = 1.0 - this.inf_family_t$t_ttt;
    this.inf_family_f$t_ttf = 1.0 - this.inf_family_t$t_ttf;
    this.inf_family_f$t_tft = 1.0 - this.inf_family_t$t_tft;
    this.inf_family_f$t_tff = 1.0 - this.inf_family_t$t_tff;
    this.inf_family_f$t_ftt = 1.0 - this.inf_family_t$t_ftt;
    this.inf_family_f$t_ftf = 1.0 - this.inf_family_t$t_ftf;
    this.inf_family_f$t_fft = 1.0 - this.inf_family_t$t_fft;
    this.inf_family_f$t_fff = 1.0 - this.inf_family_t$t_fff;
    this.inf_family_f$f_ttt = 1.0 - this.inf_family_t$f_ttt;
    this.inf_family_f$f_ttf = 1.0 - this.inf_family_t$f_ttf;
    this.inf_family_f$f_tft = 1.0 - this.inf_family_t$f_tft;
    this.inf_family_f$f_tff = 1.0 - this.inf_family_t$f_tff;
    this.inf_family_f$f_ftt = 1.0 - this.inf_family_t$f_ftt;
    this.inf_family_f$f_ftf = 1.0 - this.inf_family_t$f_ftf;
    this.inf_family_f$f_fft = 1.0 - this.inf_family_t$f_fft;
    this.inf_family_f$f_fff = 1.0 - this.inf_family_t$f_fff;
    this.derived_family_t$t_ttt = this.inf_family_t$t_ttt * this.inf_prefixScientificName_t$t_t * this.inf_soundexScientificName_t$t_t;
    this.derived_family_f$t_ttt = this.inf_family_f$t_ttt * this.inf_prefixScientificName_t$t_t * this.inf_soundexScientificName_t$t_t;
    this.derived_family_t$t_ttf = this.inf_family_t$t_ftt * this.inf_prefixScientificName_t$t_f * this.inf_soundexScientificName_t$t_f;
    this.derived_family_f$t_ttf = this.inf_family_f$t_ftt * this.inf_prefixScientificName_t$t_f * this.inf_soundexScientificName_t$t_f;
    this.derived_family_t$t_tft = this.inf_family_t$t_tft * this.inf_prefixScientificName_t$t_t * this.inf_soundexScientificName_f$t_t;
    this.derived_family_f$t_tft = this.inf_family_f$t_tft * this.inf_prefixScientificName_t$t_t * this.inf_soundexScientificName_f$t_t;
    this.derived_family_t$t_tff = this.inf_family_t$t_fft * this.inf_prefixScientificName_t$t_f * this.inf_soundexScientificName_f$t_f;
    this.derived_family_f$t_tff = this.inf_family_f$t_fft * this.inf_prefixScientificName_t$t_f * this.inf_soundexScientificName_f$t_f;
    this.derived_family_t$t_ftt = this.inf_family_t$t_ttf * this.inf_prefixScientificName_f$t_t * this.inf_soundexScientificName_t$t_t;
    this.derived_family_f$t_ftt = this.inf_family_f$t_ttf * this.inf_prefixScientificName_f$t_t * this.inf_soundexScientificName_t$t_t;
    this.derived_family_t$t_ftf = this.inf_family_t$t_ftf * this.inf_prefixScientificName_f$t_f * this.inf_soundexScientificName_t$t_f;
    this.derived_family_f$t_ftf = this.inf_family_f$t_ftf * this.inf_prefixScientificName_f$t_f * this.inf_soundexScientificName_t$t_f;
    this.derived_family_t$t_fft = this.inf_family_t$t_tff * this.inf_prefixScientificName_f$t_t * this.inf_soundexScientificName_f$t_t;
    this.derived_family_f$t_fft = this.inf_family_f$t_tff * this.inf_prefixScientificName_f$t_t * this.inf_soundexScientificName_f$t_t;
    this.derived_family_t$t_fff = this.inf_family_t$t_fff * this.inf_prefixScientificName_f$t_f * this.inf_soundexScientificName_f$t_f;
    this.derived_family_f$t_fff = this.inf_family_f$t_fff * this.inf_prefixScientificName_f$t_f * this.inf_soundexScientificName_f$t_f;
    this.derived_family_t$f_ttt = this.inf_family_t$t_ttt * this.inf_prefixScientificName_t$t_t * this.inf_soundexScientificName_t$t_t;
    this.derived_family_f$f_ttt = this.inf_family_f$t_ttt * this.inf_prefixScientificName_t$t_t * this.inf_soundexScientificName_t$t_t;
    this.derived_family_t$f_ttf = this.inf_family_t$t_ftt * this.inf_prefixScientificName_t$t_f * this.inf_soundexScientificName_t$t_f;
    this.derived_family_f$f_ttf = this.inf_family_f$t_ftt * this.inf_prefixScientificName_t$t_f * this.inf_soundexScientificName_t$t_f;
    this.derived_family_t$f_tft = this.inf_family_t$t_tft * this.inf_prefixScientificName_t$t_t * this.inf_soundexScientificName_f$t_t;
    this.derived_family_f$f_tft = this.inf_family_f$t_tft * this.inf_prefixScientificName_t$t_t * this.inf_soundexScientificName_f$t_t;
    this.derived_family_t$f_tff = this.inf_family_t$t_fft * this.inf_prefixScientificName_t$t_f * this.inf_soundexScientificName_f$t_f;
    this.derived_family_f$f_tff = this.inf_family_f$t_fft * this.inf_prefixScientificName_t$t_f * this.inf_soundexScientificName_f$t_f;
    this.derived_family_t$f_ftt = this.inf_family_t$t_ttf * this.inf_prefixScientificName_f$t_t * this.inf_soundexScientificName_t$t_t;
    this.derived_family_f$f_ftt = this.inf_family_f$t_ttf * this.inf_prefixScientificName_f$t_t * this.inf_soundexScientificName_t$t_t;
    this.derived_family_t$f_ftf = this.inf_family_t$t_ftf * this.inf_prefixScientificName_f$t_f * this.inf_soundexScientificName_t$t_f;
    this.derived_family_f$f_ftf = this.inf_family_f$t_ftf * this.inf_prefixScientificName_f$t_f * this.inf_soundexScientificName_t$t_f;
    this.derived_family_t$f_fft = this.inf_family_t$t_tff * this.inf_prefixScientificName_f$t_t * this.inf_soundexScientificName_f$t_t;
    this.derived_family_f$f_fft = this.inf_family_f$t_tff * this.inf_prefixScientificName_f$t_t * this.inf_soundexScientificName_f$t_t;
    this.derived_family_t$f_fff = this.inf_family_t$t_fff * this.inf_prefixScientificName_f$t_f * this.inf_soundexScientificName_f$t_f;
    this.derived_family_f$f_fff = this.inf_family_f$t_fff * this.inf_prefixScientificName_f$t_f * this.inf_soundexScientificName_f$t_f;
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