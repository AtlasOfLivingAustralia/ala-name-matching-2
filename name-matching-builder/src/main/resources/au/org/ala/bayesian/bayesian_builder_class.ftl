<#import "derivations.ftl" as derivations>
package ${packageName};

import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.names.builder.Builder;
import au.org.ala.names.builder.IndexBuilder;
import au.org.ala.names.builder.IndexBuilderConfiguration;
import au.org.ala.names.builder.Source;
import org.apache.commons.cli.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Deque;

<#list builderVariables as variable>
import ${variable.clazz.name};
</#list>

public class ${className} extends Builder<${parameterClassName}> {
  <#list builderVariables as variable>
  private ${variable.clazz.simpleName} ${variable.name};
  </#list>

  public ${className}() {
  <#list builderVariables as variable>
    this.${variable.name} = new ${variable.clazz.simpleName}();
  </#list>
  }

  @Override
  public void infer(Document document) {
<#list orderedNodes as node>
  <#assign observable = node.observable >
  <#if observable?? && observable.derivation??>
    <#assign derivation = observable.derivation>
    for (String v: ${derivation.getValues("document")}) {
      String d = ${derivation.getTransform("v", "document")};
      document.add(new StringField("${observable.field}", d, Store.YES));
    }
  </#if>
</#list>
  }

    @Override
    public void expand(Document document, Deque<Document> parents) {
<#list orderedNodes as node>
  <#assign observable = node.observable >
  <#if observable?? && observable.base??>
    <#assign derivation = observable.base>
    <#assign docVar = "document">
    <#assign condition = derivation.getCondition("d", "document", "parents")>
    <#if condition??>
      <#assign docVar = "d_${node?index}">
    Document ${docVar} = null;
    for (Document d: parents) {
      if (${condition}) {
        ${docVar} = d;
        break;
      }
    }
    </#if>
    if (${docVar} != null){
      for(String v: ${derivation.getValues("${docVar}")}){
        String d= ${derivation.getTransform("v", "document")};
        document.add(new StringField("${observable.field}" ,d,Store.YES));
      }
    }
  </#if>
</#list>
  }

  @Override
  public ${parameterClassName} createParameters() {
      return new ${parameterClassName}();
  }

  @Override
  public void calculate(${parameterClassName} parameters, ParameterAnalyser<Document> analyser, Document document) throws InferenceException {
    <#list inputs as inc>
    parameters.${inc.prior.id} = analyser.computePrior(analyser.getObservation(true, "${inc.observable.id}", document));
    </#list>
    <#list orderedNodes as node>
        <#list node.inference as inf>
            <#if !inf.derived>
    parameters.${inf.id} = analyser.computeConditional(analyser.getObservation(true, "${inf.outcome.observable.id}", document) <#list inf.contributors as c>, analyser.getObservation(${c.match?c}, "${c.observable.id}", document)</#list>);
            </#if>
        </#list>
    </#list>
  }

   public static void main(String[] args) throws Exception {
     Options options = new Options();
     Option configOption = Option.builder("c").longOpt("config").desc("Specify a configuration file").hasArg().argName("URL").type(URL.class).build();
     Option workOption = Option.builder("w").longOpt("work").desc("Working directory").hasArg().argName("DIR").type(File.class).build();
     Option outputOption = Option.builder("o").longOpt("output").desc("Output index directory").hasArg().argName("DIR").type(File.class).build();
     Option helpOption = Option.builder("h").longOpt("help").desc("Print help").build();
     options.addOption(configOption);
     options.addOption(workOption);
     options.addOption(outputOption);
     options.addOption(helpOption);
     IndexBuilderConfiguration config;
     File output;

     CommandLineParser parser = new DefaultParser();
     CommandLine cmd = parser.parse(options, args);

     if (cmd.hasOption(helpOption.getOpt())) {
       HelpFormatter help = new HelpFormatter();
       help.printHelp("java -jar ${artifactName}.jar [OPTIONS] [SOURCES]", options);
       System.exit(0);
     }
     if (cmd.hasOption(configOption.getOpt())) {
       config = IndexBuilderConfiguration.read(((URL) cmd.getParsedOptionValue(configOption.getOpt())));
     } else {
       config = new IndexBuilderConfiguration();
       config.setBuilderClass(${className}.class);
       config.setNetwork(${className}.class.getResource("${className}.json"));
     }
     if (cmd.hasOption(workOption.getOpt())) {
       config.setWork((File) cmd.getParsedOptionValue(workOption.getOpt()));
     }
     if (cmd.hasOption(outputOption.getOpt())) {
       output = (File) cmd.getParsedOptionValue(outputOption.getOpt());
     } else {
       output = new File(config.getWork(), "output");
     }
     IndexBuilder builder = new IndexBuilder(config);
     for (String input: cmd.getArgs()) {
       URL in = new URL(input);
       Source source = null;
       source = Source.create(in);
       builder.load(source);
       source.close();
     }
     builder.build();
     builder.buildIndex(output);
     builder.close();
   }
}
