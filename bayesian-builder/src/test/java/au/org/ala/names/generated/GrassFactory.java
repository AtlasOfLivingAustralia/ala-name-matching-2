package au.org.ala.names.generated;

import au.org.ala.bayesian.ClassificationMatcher;
import au.org.ala.bayesian.ClassificationMatcherConfiguration;
import au.org.ala.bayesian.ClassifierSearcher;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.AnalyserConfig;
import au.org.ala.bayesian.MatchMeasurement;
import au.org.ala.bayesian.NetworkFactory;
import au.org.ala.bayesian.Normaliser;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.Observable.Multiplicity;
import static au.org.ala.bayesian.ExternalContext.*;
import au.org.ala.vocab.BayesianTerm;

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
  public static final Observable<String> rain = new Observable(
      "rain",
      null,
      String.class,
      Observable.Style.CANONICAL,
      null,
      new StringAnalysis(),
      Multiplicity.OPTIONAL,
      Multiplicity.OPTIONAL
    );
  /** The sprinkler is on */
  public static final Observable<String> sprinkler = new Observable(
      "sprinkler",
      null,
      String.class,
      Observable.Style.CANONICAL,
      null,
      new StringAnalysis(),
      Multiplicity.OPTIONAL,
      Multiplicity.OPTIONAL
    );
  /** The grass is wet */
  public static final Observable<String> wet = new Observable(
      "wet",
      null,
      String.class,
      Observable.Style.CANONICAL,
      null,
      new StringAnalysis(),
      Multiplicity.OPTIONAL,
      Multiplicity.OPTIONAL
    );

  public static List<Observable<?>> OBSERVABLES = Collections.unmodifiableList(Arrays.asList(
    rain,
    sprinkler,
    wet
  ));

  public static final TermFactory TERM_FACTORY = TermFactory.instance();

  public static final Term CONCEPT = TERM_FACTORY.findTerm("http://ala.org.au/bayesian/1.0/Concept");

  public static final List<Observable<?>> KEY = null;


  public static final List<Term> ISSUES = Collections.unmodifiableList(Arrays.asList(
          BayesianTerm.illformedData,
          BayesianTerm.invalidMatch
  ));


  static {
    // Force vocabularies to load
    au.org.ala.vocab.BayesianTerm.values();
    au.org.ala.vocab.OptimisationTerm.values();
    rain.setExternal(LUCENE, "rain");
    sprinkler.setExternal(LUCENE, "sprinkler");
    wet.setExternal(LUCENE, "wet");
  }

  @Override
  public String getNetworkId() {
    return "grass";
  }

  @Override
  public List<Observable<?>> getObservables() {
    return OBSERVABLES;
  }

  @Override
  public List<Term> getAllIssues() {
    return ISSUES;
  }

  @Override
  public Term getConcept() {
    return CONCEPT;
  }

  @Override
  public Optional<Observable<String>> getIdentifier() {
    return Optional.empty();
  }

  @Override
  public Optional<Observable<String>> getName() {
    return Optional.empty();
  }

  @Override
  public Optional<Observable<String>> getParent() {
    return Optional.empty();
  }

  @Override
  public Optional<Observable<String>> getAccepted() {
    return Optional.empty();
  }

  @Override
  public List<Observable<?>> getKey() {
    return KEY;
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
  public Analyser<GrassClassification> createAnalyser(AnalyserConfig config) {
        return new au.org.ala.bayesian.NullAnalyser<>();
  }

  @Override
  public ClassificationMatcher<GrassClassification, GrassInferencer, GrassFactory, MatchMeasurement> createMatcher(ClassifierSearcher searcher, ClassificationMatcherConfiguration config, AnalyserConfig analyserConfig){
        return new ClassificationMatcher<>(this, searcher, config, analyserConfig);
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
