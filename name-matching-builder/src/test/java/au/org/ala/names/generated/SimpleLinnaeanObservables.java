package au.org.ala.names.generated;

import au.org.ala.bayesian.Observable;
import static au.org.ala.names.model.ExternalContext.*;

import java.net.URI;

public class SimpleLinnaeanObservables {
  public static final Observable taxonId = new Observable("taxonID", URI.create("http://rs.tdwg.org/dwc/terms/taxonID"));
  public static final Observable taxonRank = new Observable("taxonRank", URI.create("http://rs.tdwg.org/dwc/terms/taxonRank"));
  public static final Observable specificEpithet = new Observable("specificEpithet", URI.create("http://rs.tdwg.org/dwc/terms/specificEpithet"));
  public static final Observable scientificNameAuthorship = new Observable("scientificNameAuthorship", URI.create("http://rs.tdwg.org/dwc/terms/scientificNameAuthorship"));
  public static final Observable scientificName = new Observable("scientificName", URI.create("http://rs.tdwg.org/dwc/terms/scientificName"));
  public static final Observable soundexScientificName = new Observable("soundexScientificName", URI.create("http://id.ala.org.au/terms/1.0/soundexScientificName"));
  public static final Observable genus = new Observable("genus", URI.create("http://rs.tdwg.org/dwc/terms/genus"));
  public static final Observable family = new Observable("family", URI.create("http://rs.tdwg.org/dwc/terms/family"));
  public static final Observable order = new Observable("order", URI.create("http://rs.tdwg.org/dwc/terms/order"));
  public static final Observable class_ = new Observable("class", URI.create("http://rs.tdwg.org/dwc/terms/class"));
  public static final Observable phylum = new Observable("phylum", URI.create("http://rs.tdwg.org/dwc/terms/phylum"));
  public static final Observable kingdom = new Observable("kingdom", URI.create("http://rs.tdwg.org/dwc/terms/kingdom"));

  static {
    taxonId.setExternal(LUCENE, "dwc_taxonID");
    taxonRank.setExternal(LUCENE, "dwc_taxonRank");
    specificEpithet.setExternal(LUCENE, "dwc_specificEpithet");
    scientificNameAuthorship.setExternal(LUCENE, "dwc_scientificNameAuthorship");
    scientificName.setExternal(LUCENE, "dwc_scientificName");
    soundexScientificName.setExternal(LUCENE, "ala_soundexScientificName");
    genus.setExternal(LUCENE, "dwc_genus");
    family.setExternal(LUCENE, "dwc_family");
    order.setExternal(LUCENE, "dwc_order");
    class_.setExternal(LUCENE, "dwc_class");
    phylum.setExternal(LUCENE, "dwc_phylum");
    kingdom.setExternal(LUCENE, "dwc_kingdom");
  }
}
