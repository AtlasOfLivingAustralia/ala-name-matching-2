<#import "derivations.ftl" as derivations>
package ${packageName};

import au.org.ala.bayesian.ClassificationMatcher;
import au.org.ala.bayesian.ClassifierSearcher;
import au.org.ala.bayesian.EvidenceAnalyser;
import au.org.ala.bayesian.NetworkFactory;
import au.org.ala.bayesian.Normaliser;
import au.org.ala.bayesian.Observable;
import static au.org.ala.names.model.ExternalContext.*;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ${className} implements NetworkFactory<${classificationClassName}, ${parametersClassName}, ${inferencerClassName}> {
    private static ${className} instance = null;

<#list network.normalisers as normaliser>
  <#if normaliser.description??>
    /** ${normaliser.description} */
  </#if>
  public static final Normaliser ${normaliser.javaVariable} = ${normaliser.creator};
</#list>

  <#list network.observablesById as observable>
    <#if observable.description??>
  /** ${observable.description} */
    </#if>
  public static final Observable ${observable.javaVariable} = new Observable(
      "${observable.id}",
      <#if observable.uri??>URI.create("${observable.uri}")<#else>null</#if>,
      ${observable.type.name}.class,
      Observable.Style.${observable.style},
      <#if observable.normaliser??>${observable.normaliser.javaVariable}<#else>null</#if>,
      ${observable.required?c}
    );
  </#list>

  public static List<Observable> OBSERVABLES = Collections.unmodifiableList(Arrays.asList(
  <#list network.observablesById as observable>
    ${observable.javaVariable}<#if observable?has_next>,</#if>
  </#list>
  ));

  static {
<#list network.observablesById as observable>
  <#list externalContexts as context>
    ${observable.javaVariable}.setExternal(${context.name()}, "${observable.getExternal(context)}");
  </#list>
</#list>
  }

  @Override
  public List<Observable> getObservables() {
      return OBSERVABLES;
  }

  @Override
  public ${classificationClassName} createClassification() {
      return new ${classificationClassName}();
  }

  @Override
  public ${inferencerClassName} createInferencer() {
      return new ${inferencerClassName}();
  }

  @Override
  public ${parametersClassName} createParameters() {
        return new ${parametersClassName}();
  }

  @Override
<#if analyserClassName??>
  public ${analyserClassName} createAnalyser() {
        return new ${analyserClassName}();
  }
<#else>
  public EvidenceAnalyser<${classificationClassName}> createAnalyser() {
        return null;
  }
</#if>

  @Override
<#if matcherClassName??>
  public ${matcherClassName} createMatcher(ClassifierSearcher searcher) {
        return new ${matcherClassName}(this,searcher);
  }
<#else>
  public ClassificationMatcher<${classificationClassName}, ${parametersClassName}, ${inferencerClassName}> createMatcher(ClassifierSearcher searcher){
        return new ClassificationMatcher<>(this, searcher);
  }
</#if>

  public static ${className} instance() {
      if (instance == null) {
          synchronized (${className}.class) {
              if (instance == null) {
                  instance = new ${className}();
              }
          }
      }
      return instance;
  }
}
