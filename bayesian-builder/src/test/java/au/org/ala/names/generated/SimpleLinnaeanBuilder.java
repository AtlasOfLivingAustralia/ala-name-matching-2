package au.org.ala.names.generated;

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.Parameters;
import au.org.ala.names.builder.Builder;

import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import au.org.ala.bayesian.analysis.StringAnalysis;
import au.org.ala.bayesian.derivation.SoundexGenerator;
import au.org.ala.bayesian.analysis.IntegerAnalysis;
import au.org.ala.bayesian.analysis.DoubleAnalysis;

public class SimpleLinnaeanBuilder implements Builder {
  // Assumed to be stateless
  private static final Builder[] BUILDERS = new Builder[] {
    new SimpleLinnaeanBuilder_TT(),
    new SimpleLinnaeanBuilder_TF(),
    new SimpleLinnaeanBuilder_FT(),
    new SimpleLinnaeanBuilder_FF()
  };

  private Map<String, Builder> subBuilders;

  private SoundexGenerator soundex;

  public SimpleLinnaeanBuilder() {
    this.soundex = new SoundexGenerator();
    this.subBuilders = new HashMap<>(BUILDERS.length);
    for (Builder b: BUILDERS)
      this.subBuilders.put(b.getSignature(), b);
  }

  @Override
  public String getSignature() {
    return null;
  }


  @Override
  public void generate(Classifier classifier) throws BayesianException {
        Object d;
  }

  @Override
  public void infer(Classifier classifier) throws BayesianException {
    Object d;
    for(Object v: classifier.getAll(SimpleLinnaeanFactory.scientificName)){
      v = this.soundex.soundex((String) v);
      classifier.add(SimpleLinnaeanFactory.soundexScientificName, v);
    }
  }

  @Override
    public void expand(Classifier classifier, Deque<Classifier> parents) throws BayesianException {
      Object d;
      Optional<Classifier> d_6 = classifier.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "genus".equals(x)) ? Optional.of(classifier) : parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "genus".equals(x))).findFirst();
      if (d_6.isPresent()) {
        if (!classifier.has(SimpleLinnaeanFactory.genus)) {
          d = d_6.get().get(SimpleLinnaeanFactory.scientificName);
          classifier.add(SimpleLinnaeanFactory.genus, d);
        }
     }
      Optional<Classifier> d_7 = classifier.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "family".equals(x)) ? Optional.of(classifier) : parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "family".equals(x))).findFirst();
      if (d_7.isPresent()) {
        if (!classifier.has(SimpleLinnaeanFactory.family)) {
          d = d_7.get().get(SimpleLinnaeanFactory.scientificName);
          classifier.add(SimpleLinnaeanFactory.family, d);
        }
     }
      Optional<Classifier> d_8 = classifier.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "order".equals(x)) ? Optional.of(classifier) : parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "order".equals(x))).findFirst();
      if (d_8.isPresent()) {
        if (!classifier.has(SimpleLinnaeanFactory.order)) {
          d = d_8.get().get(SimpleLinnaeanFactory.scientificName);
          classifier.add(SimpleLinnaeanFactory.order, d);
        }
     }
      Optional<Classifier> d_9 = classifier.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "class".equals(x)) ? Optional.of(classifier) : parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "class".equals(x))).findFirst();
      if (d_9.isPresent()) {
        if (!classifier.has(SimpleLinnaeanFactory.class_)) {
          d = d_9.get().get(SimpleLinnaeanFactory.scientificName);
          classifier.add(SimpleLinnaeanFactory.class_, d);
        }
     }
      Optional<Classifier> d_10 = classifier.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "phylum".equals(x)) ? Optional.of(classifier) : parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "phylum".equals(x))).findFirst();
      if (d_10.isPresent()) {
        if (!classifier.has(SimpleLinnaeanFactory.phylum)) {
          d = d_10.get().get(SimpleLinnaeanFactory.scientificName);
          classifier.add(SimpleLinnaeanFactory.phylum, d);
        }
     }
      Optional<Classifier> d_11 = classifier.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "kingdom".equals(x)) ? Optional.of(classifier) : parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "kingdom".equals(x))).findFirst();
      if (d_11.isPresent()) {
        if (!classifier.has(SimpleLinnaeanFactory.kingdom)) {
          d = d_11.get().get(SimpleLinnaeanFactory.scientificName);
          classifier.add(SimpleLinnaeanFactory.kingdom, d);
        }
     }
  }

  @Override
  public String buildSignature(Classifier classifier) {
    char[] sig = new char[2];
    sig[0] = (classifier.has(SimpleLinnaeanFactory.specificEpithet)) ? 'T' : 'F';
    sig[1] = (classifier.has(SimpleLinnaeanFactory.genus)) ? 'T' : 'F';
    return new String(sig);
  }

  @Override
  public Parameters calculate(ParameterAnalyser analyser, Classifier classifier) throws BayesianException {
    Builder sub = this.subBuilders.get(classifier.getSignature());
    if (sub == null)
        throw new IllegalArgumentException("Signature " + classifier.getSignature() + " not found");
    return sub.calculate(analyser, classifier);
  }
}
