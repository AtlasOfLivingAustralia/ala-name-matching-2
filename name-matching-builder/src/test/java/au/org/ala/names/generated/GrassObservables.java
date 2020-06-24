package au.org.ala.names.generated;

import au.org.ala.bayesian.Observable;
import static au.org.ala.names.model.ExternalContext.*;

import java.net.URI;

public class GrassObservables {
  /** It is raining */
  public static final Observable rain = new Observable("rain");
  /** The sprinkler is on */
  public static final Observable sprinkler = new Observable("sprinkler");
  /** The grass is wet */
  public static final Observable wet = new Observable("wet");

  static {
    rain.setExternal(LUCENE, "rain");
    sprinkler.setExternal(LUCENE, "sprinkler");
    wet.setExternal(LUCENE, "wet");
  }
}
