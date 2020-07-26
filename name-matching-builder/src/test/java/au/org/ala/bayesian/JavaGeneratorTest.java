package au.org.ala.bayesian;

import au.org.ala.util.TestUtils;
import org.junit.Test;

import java.io.StringWriter;

public class JavaGeneratorTest {
    @Test
    public void testGenerateInference1() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("network-1.json"));
        NetworkCompiler compiler = new NetworkCompiler(network);
        compiler.analyse();
        JavaGenerator generator = new JavaGenerator();
        generator.generateClass(compiler, generator.getInferencerSpec(), writer, null);
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
        generator.generateClass(compiler, generator.getInferencerSpec(), writer, null);
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
        generator.generateClass(compiler, generator.getInferencerSpec(), writer, null);
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
        generator.generateClass(compiler, generator.getParametersSpec(), writer, null);
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
        generator.generateClass(compiler, generator.getParametersSpec(), writer, null);
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
        generator.generateClass(compiler, generator.getParametersSpec(), writer, null);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-3-parameter.java.txt"), writer.toString());
    }


    @Test
    public void testGenerateClassification1() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("network-9.json"));
        NetworkCompiler compiler = new NetworkCompiler(network);
        compiler.analyse();
        JavaGenerator generator = new JavaGenerator();
        generator.generateClass(compiler, generator.getClassificationSpec(), writer, null);
        System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-9-classification.java.txt"), writer.toString());
    }

    @Test
    public void testGenerateFactory1() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("network-9.json"));
        NetworkCompiler compiler = new NetworkCompiler(network);
        compiler.analyse();
        JavaGenerator generator = new JavaGenerator();
        generator.getMatcherSpec().setImplementationClassName("au.org.ala.test.TestMatcher");
        generator.getAnalyserSpec().setImplementationClassName("au.org.ala.test.TestAnalyser");
        generator.generateClass(compiler, generator.getFactorySpec(), writer, null);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-9-factory.java.txt"), writer.toString());
    }

    @Test
    public void testGenerateFactory2() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("/au/org/ala/names/lucene/simple-network.json"));
        NetworkCompiler compiler = new NetworkCompiler(network);
        compiler.analyse();
        JavaGenerator generator = new JavaGenerator();
        generator.generateClass(compiler, generator.getFactorySpec(), writer, null);
        System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "simple-linnaean-factory.java.txt"), writer.toString());
    }

    @Test
    public void testGenerateBuilder1() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("network-9.json"));
        NetworkCompiler compiler = new NetworkCompiler(network);
        compiler.analyse();
        JavaGenerator generator = new JavaGenerator();
        generator.generateClass(compiler, generator.getBuilderSpec(), writer, null);
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
        generator.generateClass(compiler, generator.getCliSpec(), writer, null);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-9-cli.java.txt"), writer.toString());
    }

}
