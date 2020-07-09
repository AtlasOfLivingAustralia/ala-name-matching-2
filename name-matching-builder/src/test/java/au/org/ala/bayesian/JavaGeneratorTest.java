package au.org.ala.bayesian;

import au.org.ala.util.TestUtils;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Properties;

public class JavaGeneratorTest {
    protected Properties createProperties(String base) {
        Properties context = new Properties();
        context.put("packageName", "au.org.ala.bayesian.generated");
        context.put("classificationClassName", base + "Classification");
        context.put("observablesClassName", base + "Observables");
        context.put("builderClassName", base + "Builder");
        context.put("parametersClassName", base + "Parameters");
        context.put("inferenceClassName", base + "Inference");
        context.put("networkFileName", "network.json");
        context.put("artifactName", "name-matching-builder");
        return context;
    }

    @Test
    public void testGenerateInference1() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("network-1.json"));
        NetworkCompiler compiler = new NetworkCompiler(network);
        compiler.analyse();
        JavaGenerator generator = new JavaGenerator();
        generator.generateInferenceClass(compiler, "Network1Inference", this.createProperties("Network1"), writer);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-1-inference.java.txt"), writer.toString());
    }

    @Test
    public void testGenerateInference2() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("network-2.json"));
        NetworkCompiler compiler = new NetworkCompiler(network);
        compiler.analyse();
        JavaGenerator generator = new JavaGenerator();
        generator.generateInferenceClass(compiler, "Network2Inference",  this.createProperties("Network2"), writer);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-2-inference.java.txt"), writer.toString());
    }

    @Test
    public void testGenerateInference3() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("network-3.json"));
        NetworkCompiler compiler = new NetworkCompiler(network);
        compiler.analyse();
        JavaGenerator generator = new JavaGenerator();
        generator.generateInferenceClass(compiler, "Network3Inference",  this.createProperties("Network3"), writer);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-3-inference.java.txt"), writer.toString());
    }

    @Test
    public void testGenerateParameters1() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("network-1.json"));
        NetworkCompiler compiler = new NetworkCompiler(network);
        compiler.analyse();
        JavaGenerator generator = new JavaGenerator();
        generator.generateParameterClass(compiler, "Network1Parameters",  this.createProperties("Network1"), writer);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-1-parameter.java.txt"), writer.toString());
    }

    @Test
    public void testGenerateParameters2() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("network-2.json"));
        NetworkCompiler compiler = new NetworkCompiler(network);
        compiler.analyse();
        JavaGenerator generator = new JavaGenerator();
        generator.generateParameterClass(compiler, "Network3Parameters",  this.createProperties("Network3"), writer);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-2-parameter.java.txt"), writer.toString());
    }

    @Test
    public void testGenerateParameters3() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("network-3.json"));
        NetworkCompiler compiler = new NetworkCompiler(network);
        compiler.analyse();
        JavaGenerator generator = new JavaGenerator();
        generator.generateParameterClass(compiler, "Network3Parameters",  this.createProperties("Network3"), writer);
        System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-3-parameter.java.txt"), writer.toString());
    }

    @Test
    public void testGenerateBuilder1() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("network-9.json"));
        NetworkCompiler compiler = new NetworkCompiler(network);
        compiler.analyse();
        JavaGenerator generator = new JavaGenerator();
        generator.generateBuilderClass(compiler, "Network9Builder",  this.createProperties("Network9"), writer);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-9-builder.java.txt"), writer.toString());
    }

    @Test
    public void testGenerateCli1() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("network-9.json"));
        NetworkCompiler compiler = new NetworkCompiler(network);
        compiler.analyse();
        JavaGenerator generator = new JavaGenerator();
        generator.generateCliClass(compiler, "Network9Cli",  this.createProperties("Network9"), writer);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-9-cli.java.txt"), writer.toString());
    }

}
