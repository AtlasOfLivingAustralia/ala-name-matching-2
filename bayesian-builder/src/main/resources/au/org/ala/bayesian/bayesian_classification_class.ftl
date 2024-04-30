<#assign analyserType><#if analyserImplementationClassName??>${analyserImplementationClassName}<#else>Analyser<${className}></#if></#assign>
package ${packageName};

import au.org.ala.bayesian.*;
import au.org.ala.bayesian.fidelity.CompositeFidelity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

<#list imports as import>
import ${import};
</#list>

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@TraceDescriptor(identify = true, identifier = "getIdentifier")
public class ${className}<#if superClassName??> extends ${superClassName}</#if> implements Classification<${className}> {
  private static final int MAX_VALID_LENGTH = 4;

  private Issues issues;
  private Hints<${className}> hints;

<#list classificationVariables as variable>
  private ${variable.clazz.simpleName} ${variable.name};
</#list>
<#list modifications as modifier>
  private static Function<${className}, ${className}> ${modifier.javaConstant} =
    c -> {
      ${className} nc;
  <#list modifier.generate(compiler, "c", "nc") as statement>
      ${statement}
  </#list>
  <#if modifier.issues??>
    <#list modifier.issues as issue>
      nc.addIssue(${factoryClassName}.${issue.javaConstant});
    </#list>
  </#if>
      return nc;
    };
</#list>

<#list orderedNodes as node>
  public <#if node.observable.matchability.many>Set<${node.observable.type.name}><#else>${node.observable.type.name}</#if> ${node.observable.javaVariable};
</#list>
<#if additionalNodes?size gt 0>
  // Additional stored classification information not used in inference
</#if>
<#list additionalNodes as node>
  public ${node.observable.type.name} ${node.observable.javaVariable};
</#list>

  public ${className}() {
    this.issues = new Issues();
    this.hints = new Hints<>();
<#list classificationVariables as variable>
    this.${variable.name} = new ${variable.clazz.simpleName}();
</#list>
  }

  public ${className}(Classifier classifier) throws BayesianException {
    this();
    this.read(classifier, true);
  }

  @Override
  @SneakyThrows
  public @NonNull ${className} clone() {
      ${className} clone = (${className}) super.clone();
      clone.issues = new Issues(this.issues);
      return clone;
  }

  @Override
  public void addIssue(Term issue) {
    this.issues = this.issues.with(issue);
  }

  @Override
  public void addIssues(Issues issues) {
        this.issues = this.issues.merge(issues);
  }

  @Override
  public Hints<${className}> getHints() {
    return this.hints;
  }

  @Override
  public <T> void addHint(Observable<T> observable, T value) {
        this.hints.addHint(observable, value);
  }

  @Override
  @JsonIgnore
  public @NonNull Term getType() {
    return ${factoryClassName}.CONCEPT;
  }

  @Override
  @JsonIgnore
  public Issues getIssues() {
    return this.issues;
  }

  @JsonProperty("issues")
  public List<String> getIssueStrings() {
    return this.issues.asStrings();
  }

  @JsonProperty("issues")
  public void setIssueStrings(List<String> issues) {
    this.issues = Issues.fromStrings(issues);
  }

  @Override
  @JsonIgnore
  public String getIdentifier() {
    return <#if network.identifierObservable??>this.${network.identifierObservable.javaVariable}<#else>null</#if>;
  }

  @Override
  @JsonIgnore
  public String getName() {
    return <#if network.nameObservable??>this.${network.nameObservable.javaVariable}<#else>null</#if>;
  }

  @Override
  @JsonIgnore
  public String getParent() {
    return <#if network.parentObservable??>this.${network.parentObservable.javaVariable}<#else>null</#if>;
  }

  @Override
  @JsonIgnore
  public String getAccepted() {
    return <#if network.acceptedObservable??>this.${network.acceptedObservable.javaVariable}<#else>null</#if>;
  }

  @Override
  public Collection<Observation<?>> toObservations() {
    Collection<Observation<?>> obs = new ArrayList(${orderedNodes?size});

<#list orderedNodes as node>
    if (this.${node.observable.javaVariable} != null)
      obs.add(new Observation(true, ${factoryClassName}.${node.observable.javaVariable}, this.${node.observable.javaVariable}));
</#list>
    return obs;
  }

  @Override
  public void inferForSearch(@NonNull Analyser<${className}> analyser, @NonNull MatchOptions options) throws BayesianException {
<#list orderedNodes + additionalNodes as node>
  <#assign observable = node.observable >
  <#if observable?? && observable.analysis??>
    this.${observable.javaVariable} = ${factoryClassName}.${observable.javaVariable}.analyse(this.${observable.javaVariable});
  </#if>
</#list>
<#list derivationOrder as observable>
  <#assign derivation = observable.derivation>
  <#if derivation.preAnalysis>
    <#if derivation.hasTransform()>
    if (this.${observable.javaVariable} == null<#if derivation.optional> && ${derivation.buildOptionCondition("options")}</#if>) {
      this.${observable.javaVariable} = ${derivation.generateClassificationTransform()};
    }
    </#if>
  </#if>
</#list>
    analyser.analyseForSearch(this, options);
<#list derivationOrder as observable>
  <#assign derivation = observable.derivation>
  <#if derivation.postAnalysis>
    <#if derivation.hasTransform()>
    if (this.${observable.javaVariable} == null<#if derivation.optional> && ${derivation.buildOptionCondition("options")}</#if>) {
      this.${observable.javaVariable} = ${derivation.generateClassificationTransform()};
    }
    </#if>
  </#if>
</#list>
  }

  @Override
  public boolean isValidCandidate(Classifier candidate) throws BayesianException {
<#list checkedErasureStructure as erasure>
  <#if erasure?is_first>
    // Check signature includes groups present in the classifier
    String signature = candidate.getSignature();
    String name = this.getName();
  </#if>
    if (<#list erasure as observable>this.${observable.javaVariable} != null && !(this.${observable.javaVariable}).equalsIgnoreCase(name)<#if observable?has_next> || </#if></#list>) {
      if (signature.charAt(${erasure?index}) == 'F')
        return false;
    }
</#list>
<#list approximateNameNodes as node>
    <#assign oType><#if node.observable.type??>${node.observable.type.simpleName}<#else>String</#if></#assign>
    if (this.${node.observable.javaVariable} != null) {
        final int maxLength = Math.min(this.${node.observable.javaVariable}.length(), MAX_VALID_LENGTH);
        final Set<${oType}> matches = candidate.getAll(${factoryClassName}.${node.observable.javaVariable});
        if (matches != null && !matches.isEmpty() && !matches.stream().anyMatch(v -> this.${node.observable.javaVariable}.regionMatches(0, v.toString(), 0, maxLength)))
          return false;
    }
</#list>
    return true;
  }

  @Override
  public Fidelity<${className}> buildFidelity(${className} actual) throws InferenceException {
    CompositeFidelity<${className}> fidelity = new CompositeFidelity<>(this, actual);
<#list orderedNodes + additionalNodes as node>
  <#assign observable = node.observable >
  <#if observable?? && observable.analysis??>
    if (this.${observable.javaVariable} != null)
      fidelity.add(${factoryClassName}.${observable.javaVariable}.getAnalysis().buildFidelity(this.${observable.javaVariable}, actual.${observable.javaVariable}));
  </#if>
</#list>
    return fidelity;
  }

  @Override
  public List<List<Function<${className}, ${className}>>> searchModificationOrder() {
        List<List<Function<${className}, ${className}>>> modifications = new ArrayList();
<#if network.sourceModifiers?? && network.sourceModifiers?size gt 0>
    List<Function<${className}, ${className}>> ml;
  <#list network.sourceModifiers as ml>
    ml = new ArrayList();
    ml.add(null);
    <#list ml as modifier>
      <#assign check = modifier.buildCheck(compiler, "this", true)>
      <#if modifier.buildCheck(compiler, "this", true)??>
    if (${modifier.buildCheck(compiler, "this", true)})
      ml.add(${modifier.javaConstant});
      <#else>
    ml.add(${modifier.javaConstant});
      </#if>
    </#list>
    if (ml.size() > 1)
      modifications.add(ml);
  </#list>
</#if>
    return modifications;
  }

  @Override
  public List<List<Function<${className}, ${className}>>> matchModificationOrder() {
    List<List<Function<${className}, ${className}>>> modifications = new ArrayList();
<#if network.matchModifiers?? && network.matchModifiers?size gt 0>
    List<Function<${className}, ${className}>> ml;
<#list network.matchModifiers as ml>
    ml = new ArrayList();
    ml.add(null);
    <#list ml as modifier>
      <#if modifier.buildCheck(compiler, "this", true)??>
    if (${modifier.buildCheck(compiler, "this", true)})
      ml.add(${modifier.javaConstant});
      <#else>
    ml.add(${modifier.javaConstant});
      </#if>
    </#list>
    if (ml.size() > 1)
      modifications.add(ml);
</#list>
</#if>
    return modifications;
  }


  @Override
  public List<List<Function<${className}, ${className}>>> hintModificationOrder() {
    List<List<Function<${className}, ${className}>>> modifications = new ArrayList();
<#list orderedNodes as node>
    this.hints.buildModifications(${factoryClassName}.${node.observable.javaVariable}, ${node.observable.type.name}.class, (c, v) -> { c.${node.observable.javaVariable} = <#if node.observable.matchability.many>Collections.singleton(v)<#else>v</#if>; }, modifications);
</#list>
    return modifications;
  }

  @Override
  public void read(Classifier classifier, boolean overwrite) throws BayesianException {
<#list orderedNodes + additionalNodes as node>
  <#if node.observable.matchability.many>
    if (overwrite || this.${node.observable.javaVariable} == null || this.${node.observable.javaVariable}.isEmpty()) {
      this.${node.observable.javaVariable} = classifier.getAll(${factoryClassName}.${node.observable.javaVariable});
    }
  <#else>
    if (overwrite || this.${node.observable.javaVariable} == null) {
      this.${node.observable.javaVariable} = classifier.get(${factoryClassName}.${node.observable.javaVariable});
    }
  </#if>
</#list>
  }

  @Override
  public void write(Classifier classifier, boolean overwrite) throws BayesianException {
    if(overwrite){
<#list orderedNodes + additionalNodes as node>
      classifier.clear(${factoryClassName}.${node.observable.javaVariable});
</#list>
    }
<#list orderedNodes + additionalNodes as node>
  <#if node.observable.matchability.many>
    classifier.addAll(${factoryClassName}.${node.observable.javaVariable}, this.${node.observable.javaVariable}, false, false);
  <#else>
    classifier.add(${factoryClassName}.${node.observable.javaVariable}, this.${node.observable.javaVariable}, false, false);
  </#if>
</#list>
  }

  public ${inferencerClassName}.Evidence match(Classifier classifier) throws BayesianException {
    ${inferencerClassName}.Evidence evidence = new ${inferencerClassName}.Evidence();
<#list orderedNodes as node>
  <#assign observable = node.observable >
    evidence.${node.evidence.id} = classifier.match(this.${observable.javaVariable}<#list observable.matchers as matcher>, ${factoryClassName}.${matcher.javaVariable}</#list>);
</#list>
    return evidence;
  }

}