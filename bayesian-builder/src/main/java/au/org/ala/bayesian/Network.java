package au.org.ala.bayesian;

import au.org.ala.util.JsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import lombok.Setter;
import org.gbif.dwc.terms.Term;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A network for bayesian inference,
 * <p>
 * The network can contain other vertices that have special meanings.
 * These are not included in the graph but can be used to provide additional
 * information to things like an index builder.
 * </p>
 */
@JsonPropertyOrder({"id", "description", "uri", "normalisers", "observables", "vertices", "edges", "modifications", "modifiers" })
public class Network extends Identifiable {
    /** The vertex id map */
    private SortedMap<String, Observable> idMap;
    /** The vertex URI map */
    private Map<URI, Observable> uriMap;
    /** The list of modifiers */
    @JsonProperty
    @Getter
    @Setter
    private List<List<Modifier>> modifiers;
    /** The inference links between vertices */
    @JsonIgnore
    private DirectedAcyclicGraph<Observable, Dependency> graph;

    /**
     * Construct an empty network
     */
    public Network() {
        this.graph = new DirectedAcyclicGraph<Observable, Dependency>(Dependency.class);
        this.idMap = new TreeMap<>();
        this.uriMap = new HashMap<>();
        this.modifiers = new ArrayList<>();
    }

    /**
     * Constuct an empty network with an identifier
     *
     * @param id The identifier
     */
    public Network(String id) {
        super(id);
        this.graph = new DirectedAcyclicGraph<Observable, Dependency>(Dependency.class);
        this.idMap = new TreeMap<>();
        this.uriMap = new HashMap<>();
        this.modifiers = new ArrayList<>();
    }

    /**
     * Get the underlying graph.
     *
     * @return The basic graph
     */
    protected DirectedAcyclicGraph<Observable, Dependency> getGraph() {
        return this.graph;
    }

    /**
     * Get a vertex via Id
     * @param id
     * @return
     */
    public Observable getObservable(String id) {
        return this.idMap.get(id);
    }

    /**
     * Get a vertex via URI.
     *
     * @param uri The URI
     *
     * @return The vertex from a URI or null for not found.
     */
    public Observable getObservable(URI uri) {
        return this.uriMap.get(uri);
     }

    /**
     * Get a vertex via GBIF term.
     *
     * @param term The term
     *
     * @return The vertex from a term or null for not found.
     */
    public Observable getObservable(Term term) {
        return this.getObservable(URI.create(term.qualifiedName()));
    }

    @JsonProperty("normalisers")
    public List<Normaliser> getNormalisers() {
        Set<Normaliser> norms = this.getVertices().stream().map(Observable::getNormaliser).filter(n -> n != null).collect(Collectors.toSet());
        List<Normaliser> normalisers = new ArrayList<>(norms);
        normalisers.sort(Comparator.comparing(Identifiable::getId));
        return normalisers;
    }

    /**
     * Get the network vertices.
     *
     * @return The network vertices.
     */
    @JsonProperty("vertices")
    public List<Observable> getVertices() {
        Iterator<Observable> ordering = new TopologicalOrderIterator<>(this.graph);
        List<Observable> vertices = new ArrayList<>(this.graph.vertexSet().size());
        ordering.forEachRemaining(vertices::add);
        return vertices;
    }


    /**
     * Copy the network vertices.
     * <p>
     *     Any existing vertices are cleared.
     *     The vertices must correspond to the list of observables loaded.
     * </p>
     *
     * @param vertices The new list of vertices
     */
    @JsonProperty("vertices")
    public void setVertices(Collection<Observable> vertices) {
        this.graph = new DirectedAcyclicGraph<Observable, Dependency>(Dependency.class);
        for (Observable v: vertices) {
            Observable used = this.idMap.get(v.getId());
            if (used == null) {
                used = v;
                this.idMap.put(used.getId(), used);
                if (used.getUri() != null)
                    this.uriMap.put(used.getUri(), used);
            }
            this.graph.addVertex(used);
        }
    }

    @JsonProperty("modifications")
    public List<Modifier> getModifications() {
        Set<Modifier> mods = this.modifiers.stream().flatMap(List::stream).collect(Collectors.toSet());
        List<Modifier> modifiers = new ArrayList<>(mods);
        modifiers.sort(Comparator.comparing(Identifiable::getId));
        return modifiers;
    }


    /**
     * Get the network observables.
     *
     * @return The observables in network toplogical order, followed by additional observables.
     */
    @JsonProperty("observables")
    public Collection<Observable> getObservables() {
        return this.idMap.values();
    }

    /**
     * Get the network observables sorted by identifier.
     *
     * @return The vertices in breadth-first order, followed by additional vertices.
     */
    @JsonIgnore
    public List<Observable> getObservablesById() {
        List<Observable> observables = new ArrayList<>(this.idMap.values());
        observables.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));
        return observables;
    }

    /**
     * Copy the network vertices.
     * <p>
     *     Any existing vertices are cleared.
     * </p>
     *
     * @param vertices The new list of vertices
     */
    @JsonProperty("observables")
    public void setObservables(Collection<Observable> vertices) {
        this.idMap.clear();
        this.uriMap.clear();
        for (Observable v: vertices) {
            if (this.idMap.containsKey(v.getId()))
                throw new IllegalStateException("Observable " + v.getId() + " duplicated");
            this.idMap.put(v.getId(), v);
            if (v.getUri() != null)
                this.uriMap.put(v.getUri(), v);
        }
    }

    /**
     * Find an observable with a particular property.
     *
     * @param property The property
     * @param value The property value
     *
     * @return An optional observable
     */
    public Optional<Observable> findObservable(Term property, Object value) {
        return this.idMap.values().stream().filter(o -> o.hasProperty(property, value)).findFirst();
    }

    /**
     * Get a list of all the observables with a particular property.
     *
     * @param property The property
     * @param value The value
     *
     * @return A, possibly empty, list of matching observables.
     */
    public List<Observable> findObservables(Term property, Object value) {
        return this.idMap.values().stream().filter(o -> o.hasProperty(property, value)).collect(Collectors.toList());
    }

    /**
     * Get the source of an dependency
     *
     * @param dependency The dependency
     *
     * @return The vertex that is the dependency source
     */
    public Observable getSource(Dependency dependency) {
        return this.graph.getEdgeSource(dependency);
    }

    /**
     * Get the target of an dependency
     *
     * @param dependency The dependency
     *
     * @return The vertex that is the dependency target
     */
    public Observable getTarget(Dependency dependency) {
        return this.graph.getEdgeTarget(dependency);
    }

    /**
     * Get the dependency list
     *
     * @return The list odf edges in vertex breadth first order.
     */
    @JsonProperty("edges")
    public List<FullEdge> getEdges() {
        Iterator<Observable> vi = new BreadthFirstIterator<Observable, Dependency>(this.graph);
        List<FullEdge> edges = new ArrayList<>(this.graph.edgeSet().size());
        while (vi.hasNext()) {
            Observable v = vi.next();
            for (Dependency e: this.graph.outgoingEdgesOf(v)) {
                Observable t = this.graph.getEdgeTarget(e);
                edges.add(new FullEdge(v, t, e));
            }
        }
        return edges;
    }

    /**
     * Copy the network edges.
     * <p>
     *     Any existing edges are cleared.
     * </p>
     *
     * @param edges The dependency list, including the source and target vertices
     *
     * @see FullEdge
     */
    @JsonProperty("edges")
    public void setEdges(Collection<FullEdge> edges) {
        this.graph.removeAllEdges(this.graph.edgeSet());
        for (FullEdge e: edges) {
            this.graph.addVertex(e.source);
            this.graph.addVertex(e.target);
            this.graph.addEdge(e.source, e.target, e.edge);
        }
    }

    /**
     * Get the incoming edges for a observable.
     *
     * @param observable The observable
     *
     * @return The incoming edges
     */
    public Set<Dependency> getIncoming(Observable observable) {
        return this.graph.incomingEdgesOf(observable);
    }

    /**
     * Get the incoming edges for a observable.
     *
     * @param observable The observable
     *
     * @return The incoming edges
     */
    public Set<Dependency> getOutgoing(Observable observable) {
        return this.graph.outgoingEdgesOf(observable);
    }

    /**
     * Read a network description from a URL.
     *
     * @param source The source URL
     *
     * @return The resulting network
     *
     * @throws IOException If unable to read the network
     */
    public static Network read(URL source) throws IOException {
        return JsonUtils.createMapper().readValue(source, Network.class);
    }

    /**
     * A helper class for getting/setting edges.
     * <p>
     * The graph representation does not include the source/target
     * </p>
     */
    public static class FullEdge {
        @JsonProperty
        public Observable source;
        @JsonProperty
        public Observable target;
        @JsonProperty
        public Dependency edge;

        public FullEdge(Observable source, Observable target, Dependency edge) {
            this.source = source;
            this.target = target;
            this.edge = edge;
        }

        public FullEdge() {
        }

        public Observable getSource() {
            return source;
        }

        public Observable getTarget() {
            return target;
        }

        public Dependency getEdge() {
            return edge;
        }
    }
}
