package au.org.ala.bayesian;

import au.org.ala.util.TestUtils;
import org.junit.Test;

import java.io.StringWriter;

public class DotGeneratorTest {
    @Test
    public void testGenerateDot1() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("network-1.json"));
        NetworkCompiler compiler = new NetworkCompiler(network, null);
        compiler.analyse();
        DotGenerator generator = new DotGenerator(writer, false);
        generator.generate(compiler, null);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-1-compiled.dot"), writer.toString());
    }

    @Test
    public void testGenerateDot2() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("network-2.json"));
        NetworkCompiler compiler = new NetworkCompiler(network, null);
        compiler.analyse();
        DotGenerator generator = new DotGenerator(writer, false);
        generator.generate(compiler, null);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-2-compiled.dot"), writer.toString());
    }

    @Test
    public void testGenerateDot3() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("network-3.json"));
        NetworkCompiler compiler = new NetworkCompiler(network, null);
        compiler.analyse();
        DotGenerator generator = new DotGenerator(writer, false);
        generator.generate(compiler, null);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-3-compiled.dot"), writer.toString());
    }

    @Test
    public void testGenerateDotGrass() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("grass-network.json"));
        NetworkCompiler compiler = new NetworkCompiler(network, null);
        compiler.analyse();
        DotGenerator generator = new DotGenerator(writer, false);
        generator.generate(compiler, null);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "grass-compiled.dot"), writer.toString());
    }

    @Test
    public void testGenerateDotSimpleLinnaean() throws Exception {
        StringWriter writer = new StringWriter();
        Network network = Network.read(this.getClass().getResource("../names/lucene/simple-network.json"));
        NetworkCompiler compiler = new NetworkCompiler(network, null);
        compiler.analyse();
        DotGenerator generator = new DotGenerator(writer, false);
        generator.generate(compiler, null);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "simple-linnaean-compiled.dot"), writer.toString());
    }

}
