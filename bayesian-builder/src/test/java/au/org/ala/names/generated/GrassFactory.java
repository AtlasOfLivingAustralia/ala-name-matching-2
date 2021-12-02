package au.org.ala.names.generated;

import au.org.ala.bayesian.ClassificationMatcher;
import au.org.ala.bayesian.ClassifierSearcher;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.NetworkFactory;
import au.org.ala.bayesian.Normaliser;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.Observable.Multiplicity;
import static au.org.ala.bayesian.ExternalContext.*;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import au.org.ala.bayesian.analysis.StringAnalysis;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.ClassificationMatcher;

public class GrassFactory implements NetworkFactory<GrassClassification, GrassInferencer, GrassFactory> {
    private static GrassFactory instance = null;


  /** It is raining */
  public static final Observable rain = new Observable(
      "rain",
      null,
      String.class,
      Observable.Style.CANONICAL,
      null,
      new StringAnalysis(),
      Multiplicity.OPTIONAL
    );
  /** The sprinkler is on */
  public static final Observable sprinkler = new Observable(
      "sprinkler",
      null,
      String.class,
      Observable.Style.CANONICAL,
      null,
      new StringAnalysis(),
      Multiplicity.OPTIONAL
    );
  /** The grass is wet */
  public static final Observable wet = new Observable(
      "wet",
      null,
      String.class,
      Observable.Style.CANONICAL,
      null,
      new StringAnalysis(),
      Multiplicity.OPTIONAL
    );

  public static List<Observable> OBSERVABLES = Collections.unmodifiableList(Arrays.asList(
    rain,
    sprinkler,
    wet
  ));

  public static final TermFactory TERM_FACTORY = TermFactory.instance();

  public static final List<Class> VOCABULARIES = Collections.unmodifiableList(Arrays.asList(
    au.org.ala.vocab.BayesianTerm.class
  ));

  public static final Term CONCEPT = TERM_FACTORY.findTerm("http://ala.org.au/bayesian/1.0/Concept");


  static {
    rain.setExternal(LUCENE, "rain");
    sprinkler.setExternal(LUCENE, "sprinkler");
    wet.setExternal(LUCENE, "wet");
  }

  @Override
  public List<Observable> getObservables() {
      return OBSERVABLES;
  }

  @Override
  public Optional<Observable> getIdentifier() {
    return Optional.empty();
  }

  @Override
  public Optional<Observable> getName() {
    return Optional.empty();
  }

  @Override
  public Optional<Observable> getParent() {
    return Optional.empty();
  }

  @Override
  public Optional<Observable> getAccepted() {
    return Optional.empty();
  }

  @Override
  public GrassClassification createClassification() {
      return new GrassClassification();
  }

  @Override
  public GrassInferencer createInferencer() {
      return new GrassInferencer();
  }

  @Override
  public Analyser<GrassClassification> createAnalyser() {
        return new au.org.ala.bayesian.NullAnalyser<>();
  }

  @Override
  public ClassificationMatcher<GrassClassification, GrassInferencer, GrassFactory> createMatcher(ClassifierSearcher searcher){
        return new ClassificationMatcher<>(this, searcher);
  }

  public static GrassFactory instance() {
      if (instance == null) {
          synchronized (GrassFactory.class) {
              if (instance == null) {
                  instance = new GrassFactory();
              }
          }
      }
      return instance;
  }
}
