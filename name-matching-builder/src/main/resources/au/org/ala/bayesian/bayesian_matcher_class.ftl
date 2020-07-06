<#import "derivations.ftl" as derivations>
package ${packageName};

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.ClassifierSearcher;
import au.org.ala.bayesian.ClassificationMatcher;

public class ${className} extends ClassificationMatcher<${classificationClassName}, ${parametersClassName}, ${inferenceClassName}> {
  public ${className}(ClassifierSearcher searcher) {
    super(searcher, new ${inferenceClassName}());
  }

  @Override
  public ${classificationClassName} createClassification() {
    return new ${classificationClassName}();
  }

  @Override
  public ${parametersClassName} createParameters() {
    return new ${parametersClassName}();
  }
}
