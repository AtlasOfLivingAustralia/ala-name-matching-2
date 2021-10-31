<#import "derivations.ftl" as derivations>
<#assign analyserType><#if analyserImplementationClassName??>${analyserImplementationClassName}<#else>Analyser<${classificationClassName}></#if></#assign>
package ${packageName};

import au.org.ala.bayesian.ClassificationMatcher;
import au.org.ala.bayesian.ClassifierSearcher;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.NetworkFactory;
import au.org.ala.bayesian.Normaliser;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.Observable.Multiplicity;
import static au.org.ala.bayesian.ExternalContext.*;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

<#list imports as import>
import ${import};
</#list>

public class ${className}<#if superClassName??> extends ${superClassName}</#if> implements NetworkFactory<${classificationClassName}, ${inferencerClassName}, ${className}> {
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
      Multiplicity.${observable.multiplicity}
    );
  </#list>

  public static List<Observable> OBSERVABLES = Collections.unmodifiableList(Arrays.asList(
  <#list network.observablesById as observable>
    ${observable.javaVariable}<#if observable?has_next>,</#if>
  </#list>
  ));

  public static final TermFactory TERM_FACTORY = TermFactory.instance();

  public static final List<Class> VOCABULARIES = Collections.unmodifiableList(Arrays.asList(
<#list allVocabularies as vocab>
    ${vocab.name}.class<#if vocab?has_next>,</#if>
</#list>
  ));

  public static final Term CONCEPT = TERM_FACTORY.findTerm("${network.concept!"http://id.ala.org.au/bayesian/1.0/Concept"}");

<#list issues as issue>
  /** Issue ${issue.id} <#if issue.description??>
      <p>${issue.description}</p>
  </#if>*/
  public static final Term ${issue.javaConstant} = TERM_FACTORY.findTerm("${issue.uri}");
</#list>

  static {
<#list network.observablesById as observable>
  <#list externalContexts as context>
    ${observable.javaVariable}.setExternal(${context.name()}, "${observable.getExternal(context)}");
  </#list>
  <#if observable.multiplicity.many>
    <#list variantExternalContexts as context>
    ${observable.javaVariable}.setExternal(${context.name()}, "${observable.getExternal(context)}");
    </#list>
  </#if>
  <#list observable.propertyKeys as key>
    <#assign pval = observable.getProperty(key)>
    ${observable.javaVariable}.setProperty(TERM_FACTORY.findTerm("${key.qualifiedName()}"), <#if pval?is_boolean>${pval?c}<#elseif pval?is_number>${pval?c}<#else>"${pval?j_string}"</#if>);
  </#list>
</#list>
  }

  @Override
  public List<Observable> getObservables() {
      return OBSERVABLES;
  }

  @Override
  public Optional<Observable> getIdentifier() {
    return Optional.<#if network.identifierObservable??>of(${network.identifierObservable.javaVariable})<#else>empty()</#if>;
  }

  @Override
  public Optional<Observable> getName() {
    return Optional.<#if network.nameObservable??>of(${network.nameObservable.javaVariable})<#else>empty()</#if>;
  }

  @Override
  public Optional<Observable> getParent() {
    return Optional.<#if network.parentObservable??>of(${network.parentObservable.javaVariable})<#else>empty()</#if>;
  }

  @Override
  public Optional<Observable> getAccepted() {
    return Optional.<#if network.acceptedObservable??>of(${network.acceptedObservable.javaVariable})<#else>empty()</#if>;
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
  public ClassificationMatcher<${classificationClassName}, ${inferencerClassName}, ${className}> createMatcher(ClassifierSearcher searcher){
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
