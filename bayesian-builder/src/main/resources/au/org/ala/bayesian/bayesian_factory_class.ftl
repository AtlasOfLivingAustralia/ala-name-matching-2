<#import "derivations.ftl" as derivations>
<#assign analyserType><#if analyserImplementationClassName??>${analyserImplementationClassName}<#else>Analyser<${classificationClassName}></#if></#assign>
package ${packageName};

import au.org.ala.bayesian.ClassificationMatcher;
import au.org.ala.bayesian.ClassificationMatcherConfiguration;
import au.org.ala.bayesian.ClassifierSearcher;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.MatchMeasurement;
import au.org.ala.bayesian.NetworkFactory;
import au.org.ala.bayesian.Normaliser;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.Observable.Multiplicity;
import static au.org.ala.bayesian.ExternalContext.*;
import au.org.ala.vocab.BayesianTerm;

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
  public static final Observable<${oType}> ${observable.javaVariable} = new Observable(
      "${observable.id}",
      <#if observable.uri??>URI.create("${observable.uri}")<#else>null</#if>,
      ${oType}.class,
      Observable.Style.${observable.style},
      <#if observable.normaliser??>${observable.normaliser.javaVariable}<#else>null</#if>,
      new ${observable.analysis.class.simpleName}(<#list observable.analysis.constructorParameters as param><#if param?is_boolean>${param?c}<#elseif param?is_number>${param?c}<#else>"${param?j_string}"</#if><#if param?has_next>, </#if></#list>),
      Multiplicity.${observable.matchability},
      Multiplicity.${observable.multiplicity}
    );
  </#list>

  public static List<Observable<?>> OBSERVABLES = Collections.unmodifiableList(Arrays.asList(
  <#list network.observablesById as observable>
    ${observable.javaVariable}<#if observable?has_next>,</#if>
  </#list>
  ));

  public static final TermFactory TERM_FACTORY = TermFactory.instance();

  public static final Term CONCEPT = TERM_FACTORY.findTerm("${network.concept!"http://ala.org.au/bayesian/1.0/Concept"}");

<#list issues as issue>
  /** Issue ${issue.id} <#if issue.description??>
      <p>${issue.description}</p>
  </#if>*/
  public static final Term ${issue.javaConstant} = TERM_FACTORY.findTerm("${issue.uri}");
</#list>

  public static final List<Term> ISSUES = Collections.unmodifiableList(Arrays.asList(
          BayesianTerm.illformedData,
          BayesianTerm.invalidMatch<#if issues?size != 0>,</#if>
<#list issues as issue>
          ${issue.javaConstant}<#if issue?has_next>,</#if>
</#list>
  ));


  static {
    // Force vocabularies to load
<#list allVocabularies as vocab>
    ${vocab.name}.values();
</#list>
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
    <#list observable.getProperties(key, null) as param>
    ${observable.javaVariable}.setProperty(<#if key.getClass().isEnum()>${key.getClass().name}.${key.name()}<#else>TERM_FACTORY.findTerm("${key.qualifiedName()}")</#if>, <#if param?is_boolean>${param?c}<#elseif param?is_number>${param?c}<#else>"${param?j_string}"</#if>);
    </#list>
  </#list>
</#list>
  }

  @Override
  public String getNetworkId() {
    return "${network.id}";
  }

  @Override
  public List<Observable<?>> getObservables() {
    return OBSERVABLES;
  }

  @Override
  public List<Term> getAllIssues() {
    return ISSUES;
  }

  @Override
  public Term getConcept() {
    return CONCEPT;
  }

  @Override
  public Optional<Observable<String>> getIdentifier() {
    return Optional.<#if network.identifierObservable??>of(${network.identifierObservable.javaVariable})<#else>empty()</#if>;
  }

  @Override
  public Optional<Observable<String>> getName() {
    return Optional.<#if network.nameObservable??>of(${network.nameObservable.javaVariable})<#else>empty()</#if>;
  }

  @Override
  public Optional<Observable<String>> getParent() {
    return Optional.<#if network.parentObservable??>of(${network.parentObservable.javaVariable})<#else>empty()</#if>;
  }

  @Override
  public Optional<Observable<String>> getAccepted() {
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
  public ${matcherImplementationClassName} createMatcher(ClassifierSearcher searcher, ClassificationMatcherConfiguration config) {
        return new ${matcherImplementationClassName}(this, searcher, config);
  }
<#else>
  public ClassificationMatcher<${classificationClassName}, ${inferencerClassName}, ${className}, MatchMeasurement> createMatcher(ClassifierSearcher searcher, ClassificationMatcherConfiguration config){
        return new ClassificationMatcher<>(this, searcher, config);
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
