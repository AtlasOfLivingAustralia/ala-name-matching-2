<#import "derivations.ftl" as derivations>
package ${packageName};

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.ClassifierSearcher;
import au.org.ala.bayesian.ClassificationMatcher;
<#if analyserClass??>
import ${analyserClass};
</#if>

public class ${className} extends ClassificationMatcher<${classificationClassName}, ${parametersClassName}, ${inferenceClassName}> {
  public ${className}(ClassifierSearcher searcher) {
    super(searcher, new ${inferenceClassName}());
  }

<#if analyserClassName??>
  @Override
  public ${analyserClassName} createAnalyser() {
    return new ${analyserClassName}();
  }
</#if>

  @Override
  public ${classificationClassName} createClassification() {
    return new ${classificationClassName}();
  }
}
