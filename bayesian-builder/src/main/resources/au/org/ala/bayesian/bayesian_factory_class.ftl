<#import "derivations.ftl" as derivations>
<#assign analyserType><#if analyserImplementationClassName??>${analyserImplementationClassName}<#else>Analyser<${classificationClassName}></#if></#assign>
package ${packageName};

import au.org.ala.bayesian.ClassificationMatcher;
import au.org.ala.bayesian.ClassifierSearcher;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.NetworkFactory;
import au.org.ala.bayesian.Normaliser;
import au.org.ala.bayesian.Observable;
import static au.org.ala.bayesian.ExternalContext.*;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

<#list imports as import>
import ${import};
</#list>

public class ${className}<#if superClassName??> extends ${superClassName}</#if> implements NetworkFactory<${classificationClassName}, ${parametersClassName}, ${inferencerClassName}, ${className}> {
    private static ${className} instance = null;

<#list network.normalisers as normaliser>
  <#if normaliser.description??>
    /** ${normaliser.description} */
  </#if>
  public static final Normaliser ${normaliser.javaVariable} = ${normaliser.creator};
</#list>

  <#list network.observablesById as observable>
    <#assign oType><#if observable.type??>${observable.type.simpleName}<#else>String</#if></#assign>
    <#if observable.description??>
  /** ${observable.description} */
    </#if>
  public static final Observable ${observable.javaVariable} = new Observable(
      "${observable.id}",
      <#if observable.uri??>URI.create("${observable.uri}")<#else>null</#if>,
      ${oType}.class,
      Observable.Style.${observable.style},
      <#if observable.normaliser??>${observable.normaliser.javaVariable}<#else>null</#if>,
      new ${observable.analysis.class.simpleName}(),
      ${observable.required?c}
    );
  </#list>

  public static List<Observable> OBSERVABLES = Collections.unmodifiableList(Arrays.asList(
  <#list network.observablesById as observable>
    ${observable.javaVariable}<#if observable?has_next>,</#if>
  </#list>
  ));

  public static final TermFactory TERM_FACTORY = TermFactory.instance();

<#list modifications as modifier>
    <#if modifier.issue??>
  /** Issue if ${modifier.id} is used using matching */
  public static final Term ${"ISSUE_" + modifier.javaConstant} = TERM_FACTORY.findTerm("${modifier.issue}");
    </#if>
</#list>

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
      return new ${classificationImplementationClassName}();
  }

  @Override
  public ${inferencerClassName} createInferencer() {
      return new ${inferencerImplementationClassName}();
  }

  @Override
  public ${parametersClassName} createParameters() {
        return new ${parametersImplementationClassName}();
  }

  @Override
  public ${analyserType} createAnalyser() {
<#if analyserImplementationClassName??>
        return new ${analyserImplementationClassName}();
<#else>
        return new au.org.ala.bayesian.NullAnalyser<>();
</#if>
  }

  @Override
<#if matcherImplementationClassName??>
  public ${matcherImplementationClassName} createMatcher(ClassifierSearcher searcher) {
        return new ${matcherImplementationClassName}(this,searcher);
  }
<#else>
  public ClassificationMatcher<${classificationClassName}, ${parametersClassName}, ${inferencerClassName}, ${className}> createMatcher(ClassifierSearcher searcher){
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
