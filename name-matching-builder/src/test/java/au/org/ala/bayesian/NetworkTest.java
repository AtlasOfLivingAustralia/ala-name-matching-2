package au.org.ala.bayesian;

import au.org.ala.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class NetworkTest {
    @Test
    public void testToJson1() throws Exception {
        Observable v1 = new Observable("v_1");
        Network network = new Network("network_1");
        network.setVertices(Arrays.asList(v1));
        ObjectMapper mapper = TestUtils.createMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, network);
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-1.json"), writer.toString());
    }

    @Test
    public void testToJson2() throws Exception {
        Observable v1 = new Observable("v_1");
        Observable v2 = new Observable("v_2");
        Network.FullEdge e1 = new Network.FullEdge(v1, v2, new Dependency());
        Network network = new Network("network_2");
        network.setVertices(Arrays.asList(v1, v2));
        network.setEdges(Arrays.asList(e1));
        ObjectMapper mapper = TestUtils.createMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, network);
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-2.json"), writer.toString());
    }

    @Test
    public void testToJson3() throws Exception {
        Observable v1 = new Observable("v_1");
        Observable v2 = new Observable("v_2");
        Observable v3 = new Observable("v_3");
        Network.FullEdge e1 = new Network.FullEdge(v1, v2, new Dependency());
        Network.FullEdge e2 = new Network.FullEdge(v2, v3, new Dependency());
        Network.FullEdge e3 = new Network.FullEdge(v1, v3, new Dependency());
        Network network = new Network("network_3");
        network.setVertices(Arrays.asList(v1, v2, v3));
        network.setEdges(Arrays.asList(e1, e2, e3));
        ObjectMapper mapper = TestUtils.createMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, network);
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-3.json"), writer.toString());
    }

    @Test
    public void testFromJson1() throws Exception {
        ObjectMapper mapper = TestUtils.createMapper();
        Network network = mapper.readValue(TestUtils.getResource(this.getClass(), "network-1.json"), Network.class);
        assertEquals("network_1", network.getId());
        assertNull(network.getDescription());
        List<Observable> vertices = network.getVertices();
        assertEquals(1, vertices.size());
        assertEquals("v_1", vertices.get(0).getId());
        List<Network.FullEdge> edges = network.getEdges();
        assertEquals(0, edges.size());
    }

    @Test
    public void testFromJson2() throws Exception {
        ObjectMapper mapper = TestUtils.createMapper();
        Network network = mapper.readValue(TestUtils.getResource(this.getClass(), "network-2.json"), Network.class);
        assertEquals("network_2", network.getId());
        assertNull(network.getDescription());
        List<Observable> vertices = network.getVertices();
        assertEquals(2, vertices.size());
        assertEquals("v_1", vertices.get(0).getId());
        assertEquals("v_2", vertices.get(1).getId());
        List<Network.FullEdge> edges = network.getEdges();
        assertEquals(1, edges.size());
        assertSame(vertices.get(0), edges.get(0).source);
        assertSame(vertices.get(1), edges.get(0).target);
    }

    @Test
    public void testFromJson3() throws Exception {
        ObjectMapper mapper = TestUtils.createMapper();
        Network network = mapper.readValue(TestUtils.getResource(this.getClass(), "network-3.json"), Network.class);
        assertEquals("network_3", network.getId());
        assertNull(network.getDescription());
        List<Observable> vertices = network.getVertices();
        assertEquals(3, vertices.size());
        assertEquals("v_1", vertices.get(0).getId());
        assertEquals("v_2", vertices.get(1).getId());
        assertEquals("v_3", vertices.get(2).getId());
        List<Network.FullEdge> edges = network.getEdges();
        assertEquals(3, edges.size());
        assertSame(vertices.get(0), edges.get(0).source);
        assertSame(vertices.get(1), edges.get(0).target);
        assertSame(vertices.get(0), edges.get(1).source);
        assertSame(vertices.get(2), edges.get(1).target);
        assertSame(vertices.get(1), edges.get(2).source);
        assertSame(vertices.get(2), edges.get(2).target);
    }

}
