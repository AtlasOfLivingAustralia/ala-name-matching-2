package au.org.ala.names.generated;

import au.org.ala.bayesian.*;
import au.org.ala.bayesian.derivation.SoundexGenerator;
import au.org.ala.names.builder.Builder;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class SimpleLinnaeanBuilder implements Builder<SimpleLinnaeanClassification> {
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
  public void generate(Classifier classifier, Analyser<SimpleLinnaeanClassification> analyser) throws BayesianException {
  }

  @Override
  public void interpret(Classifier classifier, Analyser<SimpleLinnaeanClassification> analyser) throws BayesianException {
  }

  @Override
  public void infer(Classifier classifier, Analyser<SimpleLinnaeanClassification> analyser) throws BayesianException {
     if (!classifier.has(SimpleLinnaeanFactory.soundexScientificName)){
       java.lang.String i_0 = classifier.get(SimpleLinnaeanFactory.scientificName);
       java.lang.String v_0 = this.soundex.soundex((String) i_0);
       classifier.add(SimpleLinnaeanFactory.soundexScientificName, v_0, false);
    }
    for (java.lang.String i_0: classifier.getAll(SimpleLinnaeanFactory.scientificName)){
      java.lang.String v_0 = this.soundex.soundex((String) i_0);
      classifier.add(SimpleLinnaeanFactory.soundexScientificName, v_0, true);
    }
  }

  @Override
  public void expand(Classifier classifier, Deque<Classifier> parents, Analyser<SimpleLinnaeanClassification> analyser) throws BayesianException {
      Classifier d_0;
      if (classifier.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "genus".equals(x)))
          d_0 = classifier;
      else
          d_0 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "genus".equals(x))).findFirst().orElse(null);
      if (d_0 != null) {
        if (!classifier.has(SimpleLinnaeanFactory.genus)) {
          java.lang.String i_0 = d_0.get(SimpleLinnaeanFactory.scientificName);
          classifier.add(SimpleLinnaeanFactory.genus, i_0, false);
        }
      }
      Classifier d_1;
      if (classifier.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "family".equals(x)))
          d_1 = classifier;
      else
          d_1 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "family".equals(x))).findFirst().orElse(null);
      if (d_1 != null) {
        if (!classifier.has(SimpleLinnaeanFactory.family)) {
          java.lang.String i_1 = d_1.get(SimpleLinnaeanFactory.scientificName);
          classifier.add(SimpleLinnaeanFactory.family, i_1, false);
        }
        for(java.lang.String i_1: d_1.getAll(SimpleLinnaeanFactory.scientificName, SimpleLinnaeanFactory.synonymScientificName)) {
          classifier.add(SimpleLinnaeanFactory.family, i_1,  true);
        }
      }
      Classifier d_2;
      if (classifier.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "order".equals(x)))
          d_2 = classifier;
      else
          d_2 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "order".equals(x))).findFirst().orElse(null);
      if (d_2 != null) {
        if (!classifier.has(SimpleLinnaeanFactory.order)) {
          java.lang.String i_2 = d_2.get(SimpleLinnaeanFactory.scientificName);
          classifier.add(SimpleLinnaeanFactory.order, i_2, false);
        }
        for(java.lang.String i_2: d_2.getAll(SimpleLinnaeanFactory.scientificName, SimpleLinnaeanFactory.synonymScientificName)) {
          classifier.add(SimpleLinnaeanFactory.order, i_2,  true);
        }
      }
      Classifier d_3;
      if (classifier.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "class".equals(x)))
          d_3 = classifier;
      else
          d_3 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "class".equals(x))).findFirst().orElse(null);
      if (d_3 != null) {
        if (!classifier.has(SimpleLinnaeanFactory.class_)) {
          java.lang.String i_3 = d_3.get(SimpleLinnaeanFactory.scientificName);
          classifier.add(SimpleLinnaeanFactory.class_, i_3, false);
        }
        for(java.lang.String i_3: d_3.getAll(SimpleLinnaeanFactory.scientificName, SimpleLinnaeanFactory.synonymScientificName)) {
          classifier.add(SimpleLinnaeanFactory.class_, i_3,  true);
        }
      }
      Classifier d_4;
      if (classifier.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "phylum".equals(x)))
          d_4 = classifier;
      else
          d_4 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "phylum".equals(x))).findFirst().orElse(null);
      if (d_4 != null) {
        if (!classifier.has(SimpleLinnaeanFactory.phylum)) {
          java.lang.String i_4 = d_4.get(SimpleLinnaeanFactory.scientificName);
          classifier.add(SimpleLinnaeanFactory.phylum, i_4, false);
        }
        for(java.lang.String i_4: d_4.getAll(SimpleLinnaeanFactory.scientificName, SimpleLinnaeanFactory.synonymScientificName)) {
          classifier.add(SimpleLinnaeanFactory.phylum, i_4,  true);
        }
      }
      Classifier d_5;
      if (classifier.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "kingdom".equals(x)))
          d_5 = classifier;
      else
          d_5 = parents.stream().filter(c -> c.getAll(SimpleLinnaeanFactory.taxonRank).stream().anyMatch(x -> "kingdom".equals(x))).findFirst().orElse(null);
      if (d_5 != null) {
        if (!classifier.has(SimpleLinnaeanFactory.kingdom)) {
          java.lang.String i_5 = d_5.get(SimpleLinnaeanFactory.scientificName);
          classifier.add(SimpleLinnaeanFactory.kingdom, i_5, false);
        }
        for(java.lang.String i_5: d_5.getAll(SimpleLinnaeanFactory.scientificName, SimpleLinnaeanFactory.synonymScientificName)) {
          classifier.add(SimpleLinnaeanFactory.kingdom, i_5,  true);
        }
      }
  }

  @Override
  public String buildSignature(Classifier classifier) {
    char[] sig = new char[2];
    sig[0] = (classifier.hasAny(SimpleLinnaeanFactory.specificEpithet)) ? 'T' : 'F';
    sig[1] = (classifier.hasAny(SimpleLinnaeanFactory.genus)) ? 'T' : 'F';
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
