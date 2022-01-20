package au.org.ala.names.generated;

import au.org.ala.bayesian.*;
import au.org.ala.names.builder.Builder;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class GrassBuilder implements Builder<GrassClassification> {
  // Assumed to be stateless
  private static final Builder[] BUILDERS = new Builder[] {
    new GrassBuilder_()
  };

  private Map<String, Builder> subBuilders;


  public GrassBuilder() {
    this.subBuilders = new HashMap<>(BUILDERS.length);
    for (Builder b: BUILDERS)
      this.subBuilders.put(b.getSignature(), b);
  }

  @Override
  public String getSignature() {
    return null;
  }

  @Override
  public void generate(Classifier classifier, Analyser<GrassClassification> analyser) throws BayesianException {
  }

  @Override
  public void interpret(Classifier classifier, Analyser<GrassClassification> analyser) throws BayesianException {
  }

  @Override
  public void infer(Classifier classifier, Analyser<GrassClassification> analyser) throws BayesianException {
  }

  @Override
  public void expand(Classifier classifier, Deque<Classifier> parents, Analyser<GrassClassification> analyser) throws BayesianException {
  }

  @Override
  public String buildSignature(Classifier classifier) {
    char[] sig = new char[0];
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
