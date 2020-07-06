package au.org.ala.names.generated;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.ClassifierSearcher;
import au.org.ala.bayesian.ClassificationMatcher;

public class SimpleLinnaeanMatcher extends ClassificationMatcher<SimpleLinnaeanClassification, SimpleLinnaeanParameters, SimpleLinnaeanInference> {
  public SimpleLinnaeanMatcher(ClassifierSearcher searcher) {
    super(searcher, new SimpleLinnaeanInference());
  }

  @Override
  public SimpleLinnaeanClassification createClassification() {
    return new SimpleLinnaeanClassification();
  }

  @Override
  public SimpleLinnaeanParameters createParameters() {
    return new SimpleLinnaeanParameters();
  }
}
