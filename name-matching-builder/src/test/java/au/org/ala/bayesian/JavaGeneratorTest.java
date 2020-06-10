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
        generator.generateInferenceClass(compiler, compiler.getInferenceClassName(), writer);
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
        generator.generateInferenceClass(compiler, compiler.getInferenceClassName(), writer);
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
        generator.generateInferenceClass(compiler, compiler.getInferenceClassName(), writer);
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
        generator.generateParameterClass(compiler, compiler.getParameterClassName(), writer);
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
        generator.generateParameterClass(compiler, compiler.getParameterClassName(), writer);
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
        generator.generateParameterClass(compiler, compiler.getParameterClassName(), writer);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-3-parameter.java.txt"), writer.toString());
    }

    @Test
    public void testGenerateBuilder1() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("network-9.json"));
        NetworkCompiler compiler = new NetworkCompiler(network);
        compiler.analyse();
        JavaGenerator generator = new JavaGenerator();
        generator.generateBuilderClass(compiler, compiler.getBuilderClassName(), writer);
        System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-9-builder.java.txt"), writer.toString());
    }

}
