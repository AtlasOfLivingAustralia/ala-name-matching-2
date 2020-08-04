package au.org.ala.bayesian;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Calculate the horizon of a vertex in a DAG.
 * <p>
 * A horizon consists of two sets, the horizon, which is the set of
 * ancestors of the vertex that contain no common ancestors and the
 * set of interior vertices which are intermediate points between the
 * horizon and the vertex.
 * </p>
 * <p>
 * For example, if there is the graph <code>G = ({A, B, C, D}, {(A, B), (A, C), (B, D), (C, D)})</code>
 * then the horizon for <code>A</code> is <code>{D}</code> with interior vertices <code>{B, C}</code>.
 * </p>
 * <p>
 * As another example, if there is the graph <code>H = ({A, B, C, D}, {(A, B), (B, C), (B, D), (C, D)})</code>
 * then the horizon for <code>A</code> is <code>{B}</code> with interior vertices <code>{ }</code>
 * and the horizon for <code>B</code> is <code>{C, D}</code> with interior vertices <code>{ }</code>.
 * </p>
 * <p>
 * This algorithm can be used in a Bayesian network to determine the minimum
 * set of direct dependencies you need to calculate a conditional probability.
 * For example, using graph <code>G</code>,
 * pr(A, B, C, D) = pr(A | B).pr(B | D).pr(D) + pr(A | C).pr(C | D).pr(D)
 * </p>
 * <p>
 * There <em>has</em> to be a proper terminology and algorithm for this, somewhere ...
 * </p>
 *
 * @param <V> The vertex type
 * @param <E> The edge type
 */
public class HorizonAlgorithm<V extends Comparable<V>, E> implements Comparator<V> {
    /** The source graph */
    private DirectedAcyclicGraph<V, E> graph;
    /** The level number for each vertex */
    private Map<V, Integer> levels;
    /** The maximum level */
    int maximumLevel;

    /**
     * Construct for a graph.
     *
     * @param graph The graph
     */
    public HorizonAlgorithm(DirectedAcyclicGraph<V, E> graph) {
        this.graph = graph;
        this.levels = new HashMap<V, Integer>();
        TopologicalOrderIterator<V, E> i = new TopologicalOrderIterator<>(this.graph);
        while (i.hasNext()) {
            V v = i.next();
            int level = this.getSources(v).stream().map(s -> this.levels.getOrDefault(s, 0)).max(Integer::compareTo).orElse(-1) + 1;
            this.levels.put(v, level);
        }
        this.maximumLevel = this.levels.entrySet().stream().mapToInt(Map.Entry::getValue).max().orElse(0) + 1;
    }

    private List<V> getTargets(V v) {
        return this.graph.outgoingEdgesOf(v).stream().map(e -> this.graph.getEdgeTarget(e)).sorted().collect(Collectors.toList());
    }

    private List<V> getSources(V v) {
        return this.graph.incomingEdgesOf(v).stream().map(e -> this.graph.getEdgeSource(e)).sorted().collect(Collectors.toList());
    }

    public Horizon<V> computeHorizon(V vertex) {
        TreeSet<V> horizon = new TreeSet<>(this);
        TreeSet<V> interior = new TreeSet<>(this);
        TreeSet<V> queue = new TreeSet<>(this);
        queue.addAll(this.getTargets(vertex));
        while (!queue.isEmpty()) {
            V v = queue.pollFirst();
            if (isInterior(v, queue)) {
                interior.add(v);
                queue.addAll(this.getTargets(v));
            } else
                horizon.add(v);
        }
        return new Horizon(interior, horizon);
    }

    /**
     * Is this vertex an interior vertex?
     *
     * @param vertex The vertex
     * @param others Other vertices that have yet to be processed
     *
     * @return True if the vertex and the others share descendants (including the other vertices themselves)
     */
    protected boolean isInterior(V vertex, Collection<V> others) {
        if (others.isEmpty())
            return false;
       Set<V> descendants = this.graph.getDescendants(vertex);
       for (V o: others) {
           if (descendants.contains(o))
               return true;
           Set<V> od = this.graph.getDescendants(o);
           for (V d: descendants) {
               if (od.contains(d))
                   return true;
           }
       }
       return false;
    }

    @Override
    public int compare(V o1, V o2) {
        int l1 = HorizonAlgorithm.this.levels.getOrDefault(o1, this.maximumLevel);
        int l2 = HorizonAlgorithm.this.levels.getOrDefault(o2, this.maximumLevel);
        return l1 != l2 ? l1 - l2 : o1.compareTo(o2);
    }

    /**
     * A horizon result.
     *
     * @param <V> The vertex type
     */
    public static class Horizon<V> {
        /** All vertices */
        private List<V> vertices;
        /** The start point of the horizon */
        private int horizon;

        /**
         * COnstruct for an interior list and a horizon list
         */
        public Horizon(Collection<V> interior, Collection<V> horizon) {
            this.vertices = new ArrayList<>(interior.size() + horizon.size());
            this.vertices.addAll(interior);
            this.vertices.addAll(horizon);
            this.horizon = interior.size();
        }

        /**
         * Get the complete list of vertices
         *
         * @return The vertex list, interior vertices first
         */
        public List<V> getVertices() {
            return this.vertices;
        }

        /**
         * Get the horizon vertices.
         *
         * @return The list of vertices that do not share dependencies.
         */
        public List<V> getHorizon() {
            return this.vertices.subList(this.horizon, this.vertices.size());
        }

        /**
         * Get the interior vertices.
         *
         * @return The list of vertices that do not share dependencies.
         */
        public List<V> getInterior() {
            return this.vertices.subList(0, this.horizon);
        }

        /**
         * Get the index of a vertex in an associated t/f signature.
         *
         * @param v The vertex
         *
         * @return The signature position, or -1 for not found
         */
        public int indexOf(V v) {
            return this.vertices.indexOf(v);
        }
    }
}
