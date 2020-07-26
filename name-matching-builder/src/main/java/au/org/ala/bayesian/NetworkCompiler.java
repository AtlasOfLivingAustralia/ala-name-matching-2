package au.org.ala.bayesian;

import au.org.ala.util.IdentifierConverter;
import au.org.ala.util.SimpleIdentifierConverter;
import au.org.ala.vocab.ALATerm;
import lombok.Getter;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Compile a network into a sequence of inference steps.
 */
public class NetworkCompiler {

    /** The source network */
    @Getter
    protected Network network;
    /** The reversed graph for dependencies */
    protected DirectedAcyclicGraph<Observable, Dependency> sources;
    /** The horizon computer for vertices */
    protected HorizonAlgorithm<Observable, Dependency> horizonAlgorithm;
    /** The nodes corresponding to the variables */
    @Getter
    protected Map<String, Node> nodes;
    /** The dependency ordered list of nodes */
    @Getter
    protected List<Node> orderedNodes;
    /** The input vertices */
    @Getter
    protected List<Node> inputs;
    /** The output vertices */
    @Getter
    protected List<Node> outputs;
    /** The list of input signatures */
    @Getter
    protected List<boolean[]> inputSignatures;
    /** The identifier converter for variable names */
    protected IdentifierConverter variableConverter;

    /**
     * Construct for a network
     * @param network
     */
    public NetworkCompiler(Network network) {
        this.network = network;
        this.variableConverter = SimpleIdentifierConverter.JAVA_VARIABLE;
        ALATerm.weight.toString(); // Ensure loaded
    }

    /**
     * Get the variables required to create a builder for this network.
     * <p>
     * These represent utility classes that can be used to generate something.
     * </p>
     *
     * @return The variables
     */
    public Set<Derivation.Variable> getBuilderVariables() {
        Set<Derivation.Variable> variables = new HashSet<>();

        for (Observable observable: this.network.getVertices()) {
            if (observable.getDerivation() != null) {
                variables.addAll(observable.getDerivation().getBuilderVariables());
            }
            if (observable.getBase() != null) {
                variables.addAll(observable.getBase().getBuilderVariables());
            }
        }
        return variables;
    }

    /**
     * Get the variables required to create a classification for this network.
     * <p>
     * These represent utility classes that can be used to generate something.
     * </p>
     *
     * @return The variables
     */
    public Set<Derivation.Variable> getClassificationVariables() {
        Set<Derivation.Variable> variables = new HashSet<>();

        for (Observable observable: this.network.getVertices()) {
            if (observable.getDerivation() != null) {
                variables.addAll(observable.getDerivation().getClassificationVariables());
            }
            if (observable.getBase() != null) {
                variables.addAll(observable.getBase().getClassificationVariables());
            }
        }
        return variables;
    }

    public void analyse() throws InferenceException {
        this.sources = new DirectedAcyclicGraph<>(Dependency.class);
        Graphs.addGraphReversed(this.sources, this.network.getGraph());
        this.horizonAlgorithm = new HorizonAlgorithm<>(this.sources);
        this.nodes = this.network.getVertices().stream().collect(Collectors.toMap(v -> v.getId(), v -> new Node(v)));
        this.orderedNodes = this.network.getVertices().stream().map(v -> this.nodes.get(v.getId())).collect(Collectors.toList());
        this.inputs = this.network.getVertices().stream().filter(v -> this.network.getIncoming(v).isEmpty()).map(v -> this.nodes.get(v.getId())).collect(Collectors.toList());
        this.inputSignatures = this.signatures(this.inputs.size());
        this.outputs = this.network.getVertices().stream().filter(v -> this.network.getOutgoing(v).isEmpty()).map(v -> this.nodes.get(v.getId())).collect(Collectors.toList());

        for (Node node: this.inputs) {
            node.input = true;
            node.prior = new InferenceParameter("prior", new Contributor(node.observable, true), Collections.EMPTY_LIST);
            node.invertedPrior = new InferenceParameter("prior", new Contributor(node.observable, false), Collections.EMPTY_LIST, Collections.singletonList(node.prior), true);
        }
        for (Node node: this.outputs) {
            node.output = true;
        }
        for (Node node: this.orderedNodes) {
            this.analyse(node);
        }

        // Computer derived inference variables
        for (Node node: this.orderedNodes) {
            this.buildInterior(node);
        }
    }

    protected void analyse(Node node) throws InferenceException {
        node.evidence = new EvidenceVariable(node.observable);
        node.cE = new ResultVariable("c", node.observable);
        node.cNotE = new ResultVariable("nc", node.observable);
        List<Observable> incoming = this.network.getIncoming(node.observable).stream().map(e -> this.network.getSource(e)).collect(Collectors.toList());
        if (!incoming.isEmpty()) {
            List<boolean[]> signatures = this.signatures(incoming.size());
            node.inference = new ArrayList<>(signatures.size() * 2);
            for (boolean[] sig: signatures) {
                InferenceParameter positive = new InferenceParameter("inf", new Contributor(node.observable, true), incoming, sig);
                InferenceParameter negative = new InferenceParameter("inf", new Contributor(node.observable, false), positive.getContributors(), Collections.singletonList(positive), true);
                node.inference.add(positive);
                node.inference.add(negative);
            }
        }
        node.horizon = this.horizonAlgorithm.computeHorizon(node.observable);
        // Mark any sources
        if (this.network.getOutgoing(node.observable).isEmpty())
            node.source = true;
        for (Observable sv : node.horizon.getHorizon()) {
            Node source = this.nodes.get(sv.getId());
            if (source == null)
                throw new InferenceException("Can't find node for " + sv.getId());
            source.source = true;
        }
        this.buildInterior(node);
    }

    protected void buildInterior(Node node) throws InferenceException {
        if (node.horizon.getInterior().isEmpty()) // No interior nodes, so easy to calculate
            return;
        List<Observable> horizon = node.horizon.getVertices();
        node.interior = new ArrayList<>(2 << horizon.size());
        for (boolean[] sig: this.signatures(horizon.size())) {
            List<Contributor> cs = IntStream.range(0, horizon.size()).mapToObj(i -> new Contributor(horizon.get(i), sig[i])).collect(Collectors.toList());
            List<InferenceParameter> derivedFrom = new ArrayList<>(node.horizon.getInterior().size() + 1);
            for (Observable in: node.horizon.getInterior()) {
                Node inn = this.nodes.get(in.getId());
                if (inn.inference.isEmpty())
                    throw new InferenceException("Expecting inference nodes on horizon");
                Set<Observable> inos = inn.inference.get(0).getContributors().stream().map(Contributor::getObservable).collect(Collectors.toSet());
                List<Contributor> scs = cs.stream().filter(c -> inos.contains(c.getObservable())).collect(Collectors.toList());
                boolean positive = cs.stream().filter(c -> c.getObservable().equals(in)).map(c -> c.isMatch()).findFirst().get();
                derivedFrom.add(this.findMatchingParameter(scs, inn.inference, positive));
            }
            List<InferenceParameter> pi = new ArrayList<>(derivedFrom.size() + 1);
            List<Contributor> local = cs.stream().filter(c -> node.inference.get(0).hasObservable(c.getObservable())).collect(Collectors.toList());
            pi.add(this.findMatchingParameter(local, node.inference, true));
            pi.addAll(derivedFrom);
            InferenceParameter positive = new InferenceParameter("derived", new Contributor(node.observable, true), cs, pi, false);
            node.interior.add(positive);
            List<InferenceParameter> ni = new ArrayList<>(derivedFrom.size() + 1);
            ni.add(this.findMatchingParameter(local, node.inference, false));
            ni.addAll(derivedFrom);
            InferenceParameter negative = new InferenceParameter("derived", new Contributor(node.observable, false), cs, ni, false);
            node.interior.add(negative);
        }
    }

    protected InferenceParameter findMatchingParameter(List<Contributor> pattern, List<InferenceParameter> candidates, boolean positive) throws InferenceException {
        for (InferenceParameter candidate: candidates) {
            if (candidate.getOutcome().isMatch() != positive)
                continue;
            boolean match = true;
            Iterator<Contributor> c = pattern.iterator();
            while (match && c.hasNext())
                match = candidate.hasContributor(c.next());
            if (match)
                return candidate;
        }
        throw new InferenceException("Unable to match contributor pattern " + pattern + " candidates " + candidates);
    }


    protected List<boolean[]> signatures(int size) {
        List<boolean[]> accumulator = new ArrayList<>();
        boolean[] trace = new boolean[size];
        this. signature(trace, 0, accumulator);
        return accumulator;
    }

    protected void signature(boolean[] trace, int p, List<boolean[]> accumulator) {
        if (p >= trace.length)
            accumulator.add(Arrays.copyOf(trace, trace.length));
        else {
            trace[p] = true;
            signature(trace, p + 1, accumulator);
            trace[p] = false;
            signature(trace, p + 1, accumulator);
        }
    }

    public class Node {
        /** The associated observable */
        @Getter
        private Observable observable;
        /** The variable that holds the matchiung evidence for this variable (if any) */
        @Getter
        private Variable evidence;
        /** The variable that holds the conditional probability of seeing positive evidence **/
        @Getter
        private Variable cE;
        /** The variable that conditional probability of seeing negative evidence **/
        @Getter
        private Variable cNotE;
        /** The variable tha holds the prior probability for an incoming node */
        @Getter
        private InferenceParameter prior;
        /** The variable that holds the inverted prior probability for an incoming node */
        @Getter
        private InferenceParameter invertedPrior;
        /** The list of inference variables */
        @Getter
        private List<InferenceParameter> inference;
        /** The predecessor vertices; every vertex that feeds directly or indirectly into the node */
        @Getter
        private HorizonAlgorithm.Horizon<Observable> horizon;
        /** The interior variables for this node */
        @Getter
        private List<InferenceParameter> interior;
        /** Is this a source node; a node that acts as a source to other node calculations */
        @Getter
        private boolean source;
        /** Is this an input node; a node with no incoming dependencies */
        @Getter
        private boolean input;
        /** Is this an output node; a node with no outgoing dependencies */
        @Getter
        private boolean output;

        public List<InferenceParameter> getFactors() {
            return this.interior != null && !this.interior.isEmpty() ? this.interior : this.inference;
        }

        /**
         * Construct an empty node with a observable
         *
         * @param observable The observable
         */
        public Node(Observable observable) {
            this.observable = observable;
            this.inference = new ArrayList<>();
            this.interior = new ArrayList<>();
        }

    }
}
