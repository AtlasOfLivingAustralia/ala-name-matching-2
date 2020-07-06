package au.org.ala.names.generated;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.ClassifierSearcher;
import au.org.ala.bayesian.ClassificationMatcher;

public class GrassMatcher extends ClassificationMatcher<GrassClassification, GrassParameters, GrassInference> {
  public GrassMatcher(ClassifierSearcher searcher) {
    super(searcher, new GrassInference());
  }

  @Override
  public GrassClassification createClassification() {
    return new GrassClassification();
  }

  @Override
  public GrassParameters createParameters() {
    return new GrassParameters();
  }
}
