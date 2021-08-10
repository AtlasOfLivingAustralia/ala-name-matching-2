package au.org.ala.names.generated;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.Parameters;
import au.org.ala.bayesian.StoreException;
import au.org.ala.names.builder.Builder;

import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import au.org.ala.bayesian.analysis.StringAnalysis;
import org.apache.commons.codec.language.Soundex;
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

  private Soundex soundex;

  public SimpleLinnaeanBuilder() {
    this.soundex = new Soundex();
    this.subBuilders = new HashMap<>(BUILDERS.length);
    for (Builder b: BUILDERS)
      this.subBuilders.put(b.getSignature(), b);
  }

  @Override
  public String getSignature() {
    return null;
  }

  @Override
  public void infer(Classifier classifier) throws InferenceException, StoreException {
    for (Object v: classifier.getAll(SimpleLinnaeanFactory.scientificName)) {
      Object d = this.soundex.soundex((String) v);
      classifier.add(SimpleLinnaeanFactory.soundexScientificName, d);
    }
  }

    @Override
    public void expand(Classifier classifier, Deque<Classifier> parents) throws InferenceException, StoreException {
    Optional<Classifier> d_6 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "genus".equals(x))).findFirst();
    if (d_6.isPresent()){
      for(Object v: d_6.get().getAll(SimpleLinnaeanFactory.scientificName)){
        Object d = v;
        classifier.add(SimpleLinnaeanFactory.genus, d);
      }
    }
    Optional<Classifier> d_7 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "family".equals(x))).findFirst();
    if (d_7.isPresent()){
      for(Object v: d_7.get().getAll(SimpleLinnaeanFactory.scientificName)){
        Object d = v;
        classifier.add(SimpleLinnaeanFactory.family, d);
      }
    }
    Optional<Classifier> d_8 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "order".equals(x))).findFirst();
    if (d_8.isPresent()){
      for(Object v: d_8.get().getAll(SimpleLinnaeanFactory.scientificName)){
        Object d = v;
        classifier.add(SimpleLinnaeanFactory.order, d);
      }
    }
    Optional<Classifier> d_9 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "class".equals(x))).findFirst();
    if (d_9.isPresent()){
      for(Object v: d_9.get().getAll(SimpleLinnaeanFactory.scientificName)){
        Object d = v;
        classifier.add(SimpleLinnaeanFactory.class_, d);
      }
    }
    Optional<Classifier> d_10 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "phylum".equals(x))).findFirst();
    if (d_10.isPresent()){
      for(Object v: d_10.get().getAll(SimpleLinnaeanFactory.scientificName)){
        Object d = v;
        classifier.add(SimpleLinnaeanFactory.phylum, d);
      }
    }
    Optional<Classifier> d_11 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "kingdom".equals(x))).findFirst();
    if (d_11.isPresent()){
      for(Object v: d_11.get().getAll(SimpleLinnaeanFactory.scientificName)){
        Object d = v;
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
  public Parameters calculate(ParameterAnalyser analyser, Classifier classifier) throws InferenceException, StoreException {
    Builder sub = this.subBuilders.get(classifier.getSignature());
    if (sub == null)
        throw new IllegalArgumentException("Signature " + classifier.getSignature() + " not found");
    return sub.calculate(analyser, classifier);
  }
}
