package ${packageName};

import au.org.ala.bayesian.Classification;

public class ${className} extends Classification {
<#list orderedNodes as node>
  public ${node.observable.type.name} ${node.classifier.id};
</#list>

  public ${className}() {
  }
}