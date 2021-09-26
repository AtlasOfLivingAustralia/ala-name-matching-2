<#import "derivations.ftl" as derivations>
package ${packageName};

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.Parameters;
import au.org.ala.bayesian.StoreException;
import au.org.ala.names.builder.Builder;

import java.util.Arrays;
import java.util.Deque;
import java.util.Optional;

<#list imports as import>
import ${import};
</#list>

public class ${className} implements Builder {
  public final static String SIGNATURE = "${network.signature}";

  public  ${className}() {
  }

  @Override
  public String getSignature() {
    return SIGNATURE;
  }

  @Override
  public void generate(Classifier classifier) throws InferenceException, StoreException {
    throw new UnsupportedOperationException("Sub-builders do not support this operation");
  }

  @Override
  public void infer(Classifier classifier) throws InferenceException, StoreException {
    throw new UnsupportedOperationException("Sub-builders do not support this operation");
  }

  @Override
  public void expand(Classifier classifier, Deque<Classifier> parents) throws InferenceException, StoreException {
    throw new UnsupportedOperationException("Sub-builders do not support this operation");
  }

  @Override
  public String buildSignature(Classifier classifier) {
    return SIGNATURE;
  }

  @Override
  public Parameters calculate(ParameterAnalyser analyser, Classifier classifier) throws InferenceException, StoreException {
    ${parametersClassName} parameters = new ${parametersClassName}();
<#list inputs as inc>
    parameters.${inc.prior.id} = analyser.computePrior(analyser.getObservation(true, ${factoryClassName}.${inc.observable.javaVariable}, classifier));
</#list>
<#list orderedNodes as node>
  <#list node.inference as inf>
      <#if !inf.derived>
    parameters.${inf.id} = analyser.computeConditional(analyser.getObservation(true, ${factoryClassName}.${inf.outcome.observable.javaVariable}, classifier) <#list inf.contributors as c>, analyser.getObservation(${c.match?c}, ${factoryClassName}.${c.observable.javaVariable}, classifier)</#list>);
      </#if>
  </#list>
</#list>
    return parameters;
  }
}
