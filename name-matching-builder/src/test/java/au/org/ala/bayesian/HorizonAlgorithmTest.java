package au.org.ala.bayesian;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for the {@link HorizonAlgorithm} class.
 * <p>
 * This uses networks read from the {@link Network} JSON to provide example graphs.
 * </p>
 */
public class HorizonAlgorithmTest {
    @Test
    public void test1() throws Exception {
        DirectedAcyclicGraph<String, DefaultEdge> graph = new DirectedAcyclicGraph<>(DefaultEdge.class);
        graph.addVertex("v_1");
        graph.addVertex("v_2");
        graph.addVertex("v_3");
        graph.addEdge("v_1", "v_2");
        graph.addEdge("v_2", "v_3");
        graph.addEdge("v_1", "v_3");
        HorizonAlgorithm<String, DefaultEdge> algorithm = new HorizonAlgorithm<>(graph);
        HorizonAlgorithm.Horizon<String> horizon;
        horizon = algorithm.computeHorizon("v_3");
        assertEquals(0, horizon.getHorizon().size());
        assertEquals(0, horizon.getInterior().size());
        horizon = algorithm.computeHorizon("v_2");
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains("v_3"));
        assertEquals(0, horizon.getInterior().size());
        horizon = algorithm.computeHorizon("v_1");
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains("v_3"));
        assertEquals(1, horizon.getInterior().size());
        assertTrue(horizon.getInterior().contains("v_2"));
    }

    @Test
    public void test2() throws Exception {
        Network network = Network.read(this.getClass().getResource("network-4.json"));
        HorizonAlgorithm<Observable, Dependency> algorithm = new HorizonAlgorithm<>(network.getGraph());
        HorizonAlgorithm.Horizon<Observable> horizon = algorithm.computeHorizon(network.getObservable("A"));
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("D")));
        assertEquals(2, horizon.getInterior().size());
        assertTrue(horizon.getInterior().contains(network.getObservable("B")));
        assertTrue(horizon.getInterior().contains(network.getObservable("C")));
        horizon = algorithm.computeHorizon(network.getObservable("B"));
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("D")));
        assertEquals(0, horizon.getInterior().size());
        horizon = algorithm.computeHorizon(network.getObservable("C"));
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("D")));
         assertEquals(0, horizon.getInterior().size());
        horizon = algorithm.computeHorizon(network.getObservable("D"));
        assertEquals(0, horizon.getHorizon().size());
        assertEquals(0, horizon.getInterior().size());
    }

    @Test
    public void test3() throws Exception {
        Network network = Network.read(this.getClass().getResource("network-5.json"));
        HorizonAlgorithm<Observable, Dependency> algorithm = new HorizonAlgorithm<>(network.getGraph());
        HorizonAlgorithm.Horizon<Observable> horizon = algorithm.computeHorizon(network.getObservable("A"));
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("D")));
        assertEquals(2, horizon.getInterior().size());
        assertTrue(horizon.getInterior().contains(network.getObservable("B")));
        assertTrue(horizon.getInterior().contains(network.getObservable("C")));
        horizon = algorithm.computeHorizon(network.getObservable("B"));
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("D")));
        assertEquals(0, horizon.getInterior().size());
        horizon = algorithm.computeHorizon(network.getObservable("C"));
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("D")));
        assertEquals(0, horizon.getInterior().size());
        horizon = algorithm.computeHorizon(network.getObservable("D"));
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("E")));
        assertEquals(0, horizon.getInterior().size());
        horizon = algorithm.computeHorizon(network.getObservable("E"));
        assertEquals(0, horizon.getHorizon().size());
        assertEquals(0, horizon.getInterior().size());
    }

    @Test
    public void test4() throws Exception {
        Network network = Network.read(this.getClass().getResource("network-6.json"));
        HorizonAlgorithm<Observable, Dependency> algorithm = new HorizonAlgorithm<>(network.getGraph());
        HorizonAlgorithm.Horizon<Observable> horizon = algorithm.computeHorizon(network.getObservable("A"));
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("E")));
        assertEquals(3, horizon.getInterior().size());
        assertTrue(horizon.getInterior().contains(network.getObservable("B")));
        assertTrue(horizon.getInterior().contains(network.getObservable("C")));
        assertTrue(horizon.getInterior().contains(network.getObservable("D")));
        horizon = algorithm.computeHorizon(network.getObservable("B"));
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("C")));
        assertEquals(0, horizon.getInterior().size());
        horizon = algorithm.computeHorizon(network.getObservable("C"));
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("D")));
        assertEquals(0, horizon.getInterior().size());
        horizon = algorithm.computeHorizon(network.getObservable("D"));
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("E")));
        assertEquals(0, horizon.getInterior().size());
        horizon = algorithm.computeHorizon(network.getObservable("E"));
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("F")));
        assertEquals(0, horizon.getInterior().size());
        horizon = algorithm.computeHorizon(network.getObservable("F"));
        assertEquals(0, horizon.getHorizon().size());
        assertEquals(0, horizon.getInterior().size());
    }

    @Test
    public void test5() throws Exception {
        Network network = Network.read(this.getClass().getResource("network-7.json"));
        HorizonAlgorithm<Observable, Dependency> algorithm = new HorizonAlgorithm<>(network.getGraph());
        HorizonAlgorithm.Horizon<Observable> horizon = algorithm.computeHorizon(network.getObservable("A"));
        assertEquals(2, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("F")));
        assertTrue(horizon.getHorizon().contains(network.getObservable("H")));
        assertEquals(4, horizon.getInterior().size());
        assertTrue(horizon.getInterior().contains(network.getObservable("B")));
        assertTrue(horizon.getInterior().contains(network.getObservable("C")));
        assertTrue(horizon.getInterior().contains(network.getObservable("D")));
        assertTrue(horizon.getInterior().contains(network.getObservable("E")));
        horizon = algorithm.computeHorizon(network.getObservable("B"));
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("C")));
        assertEquals(0, horizon.getInterior().size());
    }

    @Test
    public void test6() throws Exception {
        Network network = Network.read(this.getClass().getResource("network-8.json"));
        HorizonAlgorithm<Observable, Dependency> algorithm = new HorizonAlgorithm<>(network.getGraph());
        HorizonAlgorithm.Horizon<Observable> horizon = algorithm.computeHorizon(network.getObservable("A"));
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("D")));
        assertEquals(2, horizon.getInterior().size());
        assertTrue(horizon.getInterior().contains(network.getObservable("B")));
        assertTrue(horizon.getInterior().contains(network.getObservable("C")));
        horizon = algorithm.computeHorizon(network.getObservable("B"));
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("D")));
        assertEquals(0, horizon.getInterior().size());
        horizon = algorithm.computeHorizon(network.getObservable("D"));
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("G")));
        assertEquals(2, horizon.getInterior().size());
        assertTrue(horizon.getInterior().contains(network.getObservable("E")));
        assertTrue(horizon.getInterior().contains(network.getObservable("F")));
        horizon = algorithm.computeHorizon(network.getObservable("G"));
        assertEquals(1, horizon.getHorizon().size());
        assertTrue(horizon.getHorizon().contains(network.getObservable("H")));
        assertEquals(0, horizon.getInterior().size());
    }

}
