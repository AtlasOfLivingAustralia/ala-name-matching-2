package au.org.ala.bayesian;

import au.org.ala.bayesian.modifier.RemoveModifier;
import au.org.ala.util.JsonUtils;
import au.org.ala.util.TestUtils;
import au.org.ala.vocab.TestTerms;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.StringWriter;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class NetworkTest {
    @Test
    public void testToJson1() throws Exception {
        Observable v1 = new Observable("v_1");
        Network network = new Network("network_1");
        network.setVertices(Arrays.asList(v1));
        ObjectMapper mapper = JsonUtils.createMapper();
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
        ObjectMapper mapper = JsonUtils.createMapper();
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
        ObjectMapper mapper = JsonUtils.createMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, network);
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-3.json"), writer.toString());
    }


    @Test
    public void testToJson10() throws Exception {
        Observable v1 = new Observable("v_1");
        Observable v2 = new Observable("v_2");
        Network.FullEdge e1 = new Network.FullEdge(v1, v2, new Dependency());
        Network network = new Network("network_10");
        network.setVertices(Arrays.asList(v1, v2));
        network.setEdges(Arrays.asList(e1));
        Modifier modifier = new RemoveModifier("mod_1", Arrays.asList(v1), false);
        network.setMatchModifiers(Arrays.asList(Arrays.asList(modifier)));
        Issue issue = new Issue(URI.create("http://localhost/issue_1"));
        network.getIssues().add(issue);
        modifier.setIssues(Collections.singleton(issue));
        network.setMatchModifiers(Arrays.asList(Arrays.asList(modifier)));
        ObjectMapper mapper = JsonUtils.createMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, network);
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-10.json"), writer.toString());
    }

    @Test
    public void testToJson12() throws Exception {
        Observable v1 = new Observable(TestTerms.test1);
        Network network = new Network("network_12");
        network.setVertices(Arrays.asList(v1));
        network.getVocabularies().add(TestTerms.class);
        ObjectMapper mapper = JsonUtils.createMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, network);
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "network-12.json"), writer.toString());
    }

    @Test
    public void testFromJson1() throws Exception {
        ObjectMapper mapper = JsonUtils.createMapper();
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
        ObjectMapper mapper = JsonUtils.createMapper();
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
        ObjectMapper mapper = JsonUtils.createMapper();
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

    @Test
    public void testFromJson10() throws Exception {
        ObjectMapper mapper = JsonUtils.createMapper();
        Network network = mapper.readValue(TestUtils.getResource(this.getClass(), "network-10.json"), Network.class);
        assertEquals("network_10", network.getId());
        assertNull(network.getDescription());
        List<Observable> vertices = network.getVertices();
        assertEquals(2, vertices.size());
        assertEquals("v_1", vertices.get(0).getId());
        assertEquals("v_2", vertices.get(1).getId());
        List<Network.FullEdge> edges = network.getEdges();
        assertEquals(1, edges.size());
        assertSame(vertices.get(0), edges.get(0).source);
        assertSame(vertices.get(1), edges.get(0).target);
        List<Modifier> modifications = network.getModifications();
        assertNotNull(modifications);
        assertEquals(1, modifications.size());
        Modifier modifier = modifications.get(0);
        assertEquals("mod_1", modifier.getId());
        assertEquals(URI.create("http://localhost/issue_1"), modifier.getIssues().iterator().next().getUri());
        assertEquals(RemoveModifier.class, modifier.getClass());
        assertEquals(Collections.singleton(vertices.get(0)), ((RemoveModifier) modifier).getObservables());
    }


    @Test
    public void testFromJson12() throws Exception {
        ObjectMapper mapper = JsonUtils.createMapper();
        Network network = mapper.readValue(TestUtils.getResource(this.getClass(), "network-12.json"), Network.class);
        assertEquals("network_12", network.getId());
        assertNotNull(network.getVocabularies());
        assertEquals(1, network.getVocabularies().size());
        assertTrue(network.getVocabularies().contains(TestTerms.class));
        List<Observable> vertices = network.getVertices();
        assertEquals(1, vertices.size());
        Observable test1 = vertices.get(0);
        assertEquals("test1", test1.getId());
        assertEquals(TestTerms.test1, test1.getTerm());
        assertEquals("test_test1", test1.getExternal(ExternalContext.LUCENE));
    }


    @Test
    public void testCreateSubNetwork1() throws Exception {
        ObjectMapper mapper = JsonUtils.createMapper();
        Network network = mapper.readValue(TestUtils.getResource(this.getClass(), "network-6.json"), Network.class);
        Observable a = network.getObservable("A");
        Observable b = network.getObservable("B");
        Observable f = network.getObservable("F");
        Network subNetwork = network.createSubNetwork(Arrays.asList(a, b, f), "sub");
        assertEquals(6, subNetwork.getObservables().size());
        assertEquals(3, subNetwork.getVertices().size());
        assertTrue(subNetwork.getVertices().contains(a));
        assertTrue(subNetwork.getVertices().contains(b));
        assertTrue(subNetwork.getVertices().contains(f));
        List<Observable> outgoing = subNetwork.getOutgoing(a).stream().map(d -> subNetwork.getTarget(d)).collect(Collectors.toList());
        assertEquals(2, outgoing.size());
        assertTrue(outgoing.contains(b));
        assertTrue(outgoing.contains(f));
        outgoing = subNetwork.getOutgoing(b).stream().map(d -> subNetwork.getTarget(d)).collect(Collectors.toList());
        assertEquals(1, outgoing.size());
        assertSame(f, outgoing.get(0));
        outgoing = subNetwork.getOutgoing(f).stream().map(d -> subNetwork.getTarget(d)).collect(Collectors.toList());
        assertEquals(0, outgoing.size());
   }


    @Test
    public void testCreateSubNetwork2() throws Exception {
        ObjectMapper mapper = JsonUtils.createMapper();
        Network network = mapper.readValue(TestUtils.getResource(this.getClass(), "network-8.json"), Network.class);
        Observable a = network.getObservable("A");
        Observable b = network.getObservable("B");
        Observable c = network.getObservable("C");
        Observable g = network.getObservable("G");
        Observable h = network.getObservable("H");
        Network subNetwork = network.createSubNetwork(Arrays.asList(a, b, c, g, h), "sub");
        assertEquals(8, subNetwork.getObservables().size());
        assertEquals(5, subNetwork.getVertices().size());
        assertTrue(subNetwork.getVertices().contains(a));
        assertTrue(subNetwork.getVertices().contains(b));
        assertTrue(subNetwork.getVertices().contains(c));
        assertTrue(subNetwork.getVertices().contains(g));
        assertTrue(subNetwork.getVertices().contains(h));
        List<Observable> outgoing = subNetwork.getOutgoing(a).stream().map(d -> subNetwork.getTarget(d)).collect(Collectors.toList());
        assertEquals(2, outgoing.size());
        assertTrue(outgoing.contains(b));
        assertTrue(outgoing.contains(c));
        outgoing = subNetwork.getOutgoing(b).stream().map(d -> subNetwork.getTarget(d)).collect(Collectors.toList());
        assertEquals(1, outgoing.size());
        assertSame(g, outgoing.get(0));
        outgoing = subNetwork.getOutgoing(c).stream().map(d -> subNetwork.getTarget(d)).collect(Collectors.toList());
        assertEquals(1, outgoing.size());
        assertSame(g, outgoing.get(0));
        outgoing = subNetwork.getOutgoing(g).stream().map(d -> subNetwork.getTarget(d)).collect(Collectors.toList());
        assertEquals(1, outgoing.size());
        assertSame(h, outgoing.get(0));
        outgoing = subNetwork.getOutgoing(h).stream().map(d -> subNetwork.getTarget(d)).collect(Collectors.toList());
        assertEquals(0, outgoing.size());
    }

    @Test
    public void testGetGroups1() throws Exception {
        ObjectMapper mapper = JsonUtils.createMapper();
        Network network = mapper.readValue(TestUtils.getResource(this.getClass(), "network-6.json"), Network.class);
        List<String> groups = network.getGroups();
        assertTrue(groups.isEmpty());
    }

    @Test
    public void testGetGroups2() throws Exception {
        ObjectMapper mapper = JsonUtils.createMapper();
        Network network = mapper.readValue(TestUtils.getResource(this.getClass(), "network-11.json"), Network.class);
        List<String> groups = network.getGroups();
        assertEquals(Arrays.asList("B", "C"), groups);
    }

    @Test
    public void testCreateSubNetworks1() throws Exception {
        ObjectMapper mapper = JsonUtils.createMapper();
        Network network = mapper.readValue(TestUtils.getResource(this.getClass(), "network-6.json"), Network.class);
        List<Network> networks = network.createSubNetworks();
        assertEquals(1, networks.size());
        Network sub = networks.get(0);
        assertEquals(network.getGraph(), sub.getGraph());
        assertEquals("", sub.getSignature());
    }

    @Test
    public void testCreateSubNetworks2() throws Exception {
        ObjectMapper mapper = JsonUtils.createMapper();
        Network network = mapper.readValue(TestUtils.getResource(this.getClass(), "network-11.json"), Network.class);
        Observable a = network.getObservable("A");
        Observable b1 = network.getObservable("B1");
        Observable b2 = network.getObservable("B2");
        Observable c1 = network.getObservable("C1");
        Observable d = network.getObservable("D");
        List<Network> networks = network.createSubNetworks();
        assertEquals(4, networks.size());
        Network sub = networks.get(0);
        assertEquals(network.getGraph(), sub.getGraph());
        assertEquals("TT", sub.getSignature());
        sub = networks.get(1);
        assertEquals(Arrays.asList("C"), sub.getErasures());
        assertTrue(sub.getVertices().contains(b1));
        assertFalse(sub.getVertices().contains(c1));
        assertTrue(sub.getGraph().containsEdge(b2, d));
        assertEquals("TF", sub.getSignature());
        sub = networks.get(2);
        assertEquals(Arrays.asList("B"), sub.getErasures());
        assertFalse(sub.getVertices().contains(b1));
        assertTrue(sub.getVertices().contains(c1));
        assertTrue(sub.getGraph().containsEdge(a, c1));
        assertEquals("FT", sub.getSignature());
        sub = networks.get(3);
        assertEquals(Arrays.asList("B", "C"), sub.getErasures());
        assertFalse(sub.getVertices().contains(b1));
        assertFalse(sub.getVertices().contains(c1));
        assertTrue(sub.getGraph().containsEdge(a, d));
        assertEquals("FF", sub.getSignature());
    }


}
