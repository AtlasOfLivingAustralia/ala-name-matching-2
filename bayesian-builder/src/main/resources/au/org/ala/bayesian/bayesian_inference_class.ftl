package ${packageName};

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.Inferencer;

import java.util.HashMap;
import java.util.Map;


public class ${className}<#if superClassName??> extends ${superClassName}</#if> implements Inferencer<${classificationClassName}> {
  private Map<String, Inferencer<${classificationClassName}>> subInferencers;

  // Assumed to be stateless
  private static final Inferencer<${classificationClassName}>[] INFERENCERS = new Inferencer[] {
<#list children as child>
    new ${className}_${child.network.signature}()<#if child_has_next>,</#if>
</#list>
  };

  public ${className}() {
    this.subInferencers = new HashMap<>(INFERENCERS.length);
    for (Inferencer<${classificationClassName}> i: INFERENCERS)
      this.subInferencers.put(i.getSignature(), i);
  }

  @Override
  public String getSignature() {
    return null;
  }

  @Override
  public Inference probability(${classificationClassName} classification, Classifier classifier) throws BayesianException {
    Inferencer<${classificationClassName}> sub = this.subInferencers.get(classifier.getSignature());
    if (sub == null)
      throw new IllegalArgumentException("Signature '" + classifier.getSignature() + "' is not recognised");
    return sub.probability(classification, classifier);
  }

  public static class Evidence {
<#list orderedNodes as node>
    public Boolean ${node.evidence.id};
</#list>

<#list orderedNodes as node>
    public boolean isT$${node.evidence.id}() {
      return this.${node.evidence.id} == null || this.${node.evidence.id};
    }

    public boolean isF$${node.evidence.id}() {
      return this.${node.evidence.id} == null || !this.${node.evidence.id};
    }

</#list>
  }
}