# Network Compiler Maven Plugin

This is a maven plugin designed to allow you to include network code generation
in your maven build process.

To use this plugin, you will need a Bayesian network defintion.
This is a JSON file that will be used to generate the code that implements the network.
See [here](../doc/network.md) for how to define a network.

You will need to include the GBIF and ALA repositories in your `pom.xml`

```xml
<repositories>
  <repository>
    <id>gbif-all</id>
    <url>https://repository.gbif.org/content/groups/gbif</url>
  </repository>
  <repository>
    <id>ala-nexus</id>
    <url>https://nexus.ala.org.au/content/groups/public/</url>
  </repository>
</repositories>
```

You will need to include and configure the generator in the `build` element

```xml
<plugin>
    <groupId>au.org.ala.maven</groupId>
    <artifactId>bayesian-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <executions>
        <execution>
            <id>ala-network</id>
            <goals>
                <goal>generate-network</goal>
            </goals>
            <configuration>
                <source>${project.basedir}/src/main/resources/ala-linnaean.json</source>
                <outputPackage>au.org.ala.names</outputPackage>
                <generateBuilder>false</generateBuilder>
                <generateCli>false</generateCli>
                <generateInferencer>true</generateInferencer>
                <generateParameters>true</generateParameters>
                <generateClassification>true</generateClassification>
                <generateFactory>true</generateFactory>
                <analyserClass>au.org.ala.names.AlaNameAnalyser</analyserClass>
                <weightAnalyserClass>au.org.ala.names.AlaWeightAnalyser</weightAnalyserClass>
            </configuration>
        </execution>
        <execution>
            <id>ala-network-graph</id>
            <goals>
                <goal>generate-graph</goal>
            </goals>
            <configuration>
                <source>${project.basedir}/src/main/resources/ala-linnaean.json</source>
                <output>${project.basedir}/target/diagrams/ala-linnaean.dot</output>
                <full>false</full>
            </configuration>
        </execution>
     </executions>
    <dependencies>
        <dependency>
            <groupId>au.org.ala.names</groupId>
            <artifactId>taxonomic-tools</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
</plugin>
```

Note that you may need to include other domain-specific dependencies in the plugin.
This will allow the plugin to use domain-specific derivations and analysis while
building the network.

## Generate Network

This goal generates the java classes that implement the network.
Generated code is placed in the `target/generated-sources` directory, ready for  compilation.
It uses the following properties:

* **source** The source Bayesian network definition
* **outputPackage** The java package that holds the implementation.
* **generateBuilder** Generate a builder class that analyses a classifier against all other
  classifiers and builds a set of conditional probabilties for that specific classifier.
* **generateCli** Generate a command-line inferface to the index builder for this
  network, with sensible default values.
* **generateInferencer** Generate a suite of classes that computer the probability
  that a classifier matches the evidence.
* **generateParameters** Generate a suite of classes that contain the conditional
  probabilties that an inferencer uses to calculate the probabilty of a match.
  **generateClassification** Generate a classification object that serves both as
  a template to match classifiers against and the result of a fully-filled-out
  classification from a matching classifier.
* **generateFactory** Generate a factory class that contains observables, issues
  and constructor methods things like inferences and classifiers.
* **analyserClass** The class to use for classification analysis.
* **weightAnalyserClass** The class to use for probability weighting analysis.

There are a number of other parameters that can be set to link the generated
class to superclasses, implementation classes and the like.
For more information, use

`mvn bayesian:help -Dgoal=generate-network -Ddetail=true`

Generally, you want to split network generation into two parts (modules).

* The _model_ part contains the classes that are needed to test classifiers for a
  probability of matching.
  The model usually contains the inferencer, parameters, classification and factory.
* The _builder_ part depends on the model but contains the classes that are needed
  to build an index that can be searched for candidate matches and contains the
  conditional probabilities that determine whether a match is successful.
  The builder usually contains the builder and CLI.

## Generate Graph

This goal generates a graph of the source network in [graphviz](https://graphviz.org/) dot format. 
A dot graph can be compiled into a pleasing-looking graphic buy using the graphviz tools.
For example, to generate an SVG representation of the network, use

`dot -Tsvg -o ala-linnaean.svg ala-linnaean.dot`

It uses the following properties:

* **source** The source Bayesian network definition
* **output** The output dot graph
* **full** Produce a detailed graph showing the flow of inference.
  This is currenty ignored, since it generates a gigantic mess.

For more information, use

`mvn bayesian:help -Dgoal=generate-graph -Ddetail=true`
