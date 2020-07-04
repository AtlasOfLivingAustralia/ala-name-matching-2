package au.org.ala.names.generated;

import au.org.ala.bayesian.Normaliser;
import au.org.ala.bayesian.Observable;
import static au.org.ala.names.model.ExternalContext.*;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GrassObservables {

  /** It is raining */
  public static final Observable rain = new Observable(
      "rain",
      null,
      java.lang.String.class,
      Observable.Style.CANONICAL,
      null,
      false
    );
  /** The sprinkler is on */
  public static final Observable sprinkler = new Observable(
      "sprinkler",
      null,
      java.lang.String.class,
      Observable.Style.CANONICAL,
      null,
      false
    );
  /** The grass is wet */
  public static final Observable wet = new Observable(
      "wet",
      null,
      java.lang.String.class,
      Observable.Style.CANONICAL,
      null,
      false
    );

  public static List<Observable> OBSERVABLES = Collections.unmodifiableList(Arrays.asList(
    rain,
    sprinkler,
    wet
  ));

  static {
    rain.setExternal(LUCENE, "rain");
    sprinkler.setExternal(LUCENE, "sprinkler");
    wet.setExternal(LUCENE, "wet");
  }
}
