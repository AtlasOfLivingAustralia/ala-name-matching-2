package au.org.ala.bayesian;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class NetworkCompilerTest {

    @Test
    public void testCompile1() throws Exception {
        Network network = Network.read(this.getClass().getResource("network-1.json"));
        NetworkCompiler compiler = new NetworkCompiler(network, null);
        compiler.analyse();
        assertEquals(1, compiler.getInputs().size());
        assertEquals("v_1", compiler.getInputs().get(0).getObservable().getId());
        assertEquals(2, compiler.getInputSignatures().size());
        assertEquals(1, compiler.getInputSignatures().get(0).length);
        assertEquals(true, compiler.getInputSignatures().get(0)[0]);
        assertEquals(1, compiler.getInputSignatures().get(1).length);
        assertEquals(false, compiler.getInputSignatures().get(1)[0]);
        assertEquals(1, compiler.getOutputs().size());
        assertEquals("v_1", compiler.getOutputs().get(0).getObservable().getId());
        assertEquals(1, compiler.getOrderedNodes().size());
        NetworkCompiler.Node node = compiler.getOrderedNodes().get(0);
        assertEquals("v_1", node.getObservable().getId());
        assertEquals("e$v1", node.getEvidence().getId());
        assertEquals("prior_t$v1", node.getPrior().getId());
        assertEquals("c$v1", node.getCE().getId());
        assertEquals("nc$v1", node.getCNotE().getId());
        assertEquals(0, node.getInference().size());
        JavaGenerator generator = new JavaGenerator();
        // StringWriter writer = new StringWriter();
        // JavaGenerator generator = new JavaGenerator();
        // generator.generateParameterClass(compiler, compiler.getParameterClassName(), writer);
        // generator.generateInferenceClass(compiler, compiler.getInferenceClassName(), writer);
        // System.out.println(writer.toString());
        // TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-1-inference.java.txt"), writer.toString());
    }

    @Test
    public void testCompile2() throws Exception {
        Network network = Network.read(this.getClass().getResource("network-2.json"));
        NetworkCompiler compiler = new NetworkCompiler(network, null);
        compiler.analyse();
        assertEquals(1, compiler.getInputs().size());
        assertEquals("v_1", compiler.getInputs().get(0).getObservable().getId());
        assertEquals(2, compiler.getInputSignatures().size());
        assertEquals(1, compiler.getInputSignatures().get(0).length);
        assertEquals(true, compiler.getInputSignatures().get(0)[0]);
        assertEquals(1, compiler.getInputSignatures().get(1).length);
        assertEquals(false, compiler.getInputSignatures().get(1)[0]);
        assertEquals(1, compiler.getOutputs().size());
        assertEquals("v_2", compiler.getOutputs().get(0).getObservable().getId());
        assertEquals(2, compiler.getOrderedNodes().size());
        NetworkCompiler.Node node = compiler.getOrderedNodes().get(0);
        assertEquals("v_1", node.getObservable().getId());
        assertEquals("e$v1", node.getEvidence().getId());
        assertEquals("prior_t$v1", node.getPrior().getId());
        assertEquals("c$v1", node.getCE().getId());
        assertEquals("nc$v1", node.getCNotE().getId());
        assertEquals(0, node.getInference().size());
        node = compiler.getOrderedNodes().get(1);
        assertEquals("v_2", node.getObservable().getId());
        assertEquals("e$v2", node.getEvidence().getId());
        assertNull(node.getPrior());
        assertEquals("c$v2", node.getCE().getId());
        assertEquals("nc$v2", node.getCNotE().getId());
        assertEquals(4, node.getInference().size());
        InferenceParameter parameter = node.getInference().get(0);
        assertEquals(1, parameter.getContributors().size());
        assertEquals(true, parameter.getContributors().get(0).isMatch());
        assertEquals(1, parameter.getContributors().size());
        assertEquals("v_1", parameter.getContributors().get(0).getObservable().getId());
        parameter = node.getInference().get(1);
        assertEquals(1, parameter.getContributors().size());
        assertEquals(true, parameter.getContributors().get(0).isMatch());
        assertEquals("v_1", parameter.getContributors().get(0).getObservable().getId());
        // StringWriter writer = new StringWriter();
        // JavaGenerator generator = new JavaGenerator();
        // generator.generateParameterClass(compiler, compiler.getParameterClassName(), writer);
        // generator.generateInferenceClass(compiler, compiler.getInferenceClassName(), writer);
        // System.out.println(writer.toString());
        // TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-1-inference.java.txt"), writer.toString());
    }
}
