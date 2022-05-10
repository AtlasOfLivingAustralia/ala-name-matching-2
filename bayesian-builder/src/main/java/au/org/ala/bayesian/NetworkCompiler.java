package au.org.ala.bayesian;

import au.org.ala.bayesian.derivation.CompiledDerivation;
import au.org.ala.names.builder.BuilderException;
import au.org.ala.util.IdentifierConverter;
import au.org.ala.util.SimpleIdentifierConverter;
import au.org.ala.vocab.BayesianTerm;
import lombok.Getter;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Compile a network into a sequence of inference steps.
 */
public class NetworkCompiler {
    /** The source network */
    @Getter
    protected Network network;
    /** The parent compiler */
    @Getter
    protected NetworkCompiler parent;
    /** The child compiler */
    @Getter
    protected List<NetworkCompiler> children;
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
    /** Any additional nodes */
    @Getter
    protected List<Node> additionalNodes;
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
     *
     * @param network The network to compile
     * @param parent The parent compiler for sub-networks. Null for the top-level compiler
     */
    public NetworkCompiler(Network network, NetworkCompiler parent) {
        this.network = network;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.variableConverter = SimpleIdentifierConverter.JAVA_VARIABLE;
        BayesianTerm.weight.toString(); // Ensure loaded
    }

    /**
     * Get a list of the things that can modify something.
     *
     * @return The list of modifiers
     */
    public List<Modifier> getModifications() {
        return this.network.getModifications();
    }

    /**
     * Get the collections of erased observables
     */
    public List<List<Observable<?>>> getErasureStructure() {
        List<String> erasureGroups = this.network.getGroups();
        return erasureGroups.stream()
                .map(g -> this.network.getObservables().stream()
                    .filter(o -> g.equals(o.getGroup()))
                    .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }


    /**
     * Get the list of issue defintitions.
     *
     * @return The list of issues
     */
    public List<Issue> getIssues() {
        return this.network.getIssues();
    }

    /**
     * Get all the vocabularies in use by the network.
     * <p>
     * This includes the {@link BayesianTerm} vocabulary by default.
     * </p>
     *
     * @return The vocabularies in use.
     */
    public Set<Class> getAllVocabularies() {
        Set<Class> all = new LinkedHashSet<>(this.network.getVocabularies());
        all.add(BayesianTerm.class);
        return all;
    }

    public void analyse() throws InferenceException {
        this.sources = new DirectedAcyclicGraph<>(Dependency.class);
        Graphs.addGraphReversed(this.sources, this.network.getGraph());
        this.horizonAlgorithm = new HorizonAlgorithm<>(this.sources);
        this.nodes = this.network.getVertices().stream().collect(Collectors.toMap(v -> v.getId(), v -> new Node(v)));
        this.orderedNodes = this.network.getVertices().stream().map(v -> this.nodes.get(v.getId())).collect(Collectors.toList());
        this.additionalNodes = this.network.getObservablesById().stream().filter(o -> o.hasProperty(BayesianTerm.additional, true) && !nodes.containsKey(o.getId())).map(o -> new Node(o)).collect(Collectors.toList());
        this.inputs = this.network.getInputs().stream().map(v -> this.nodes.get(v.getId())).collect(Collectors.toList());
        this.inputSignatures = this.signatures(this.inputs.size());
        this.outputs = this.network.getOutputs().stream().map(v -> this.nodes.get(v.getId())).collect(Collectors.toList());

        for (Node node: this.inputs) {
            node.input = true;
            node.prior = new InferenceParameter("prior", new Contributor(node.observable, true), Collections.emptyList(), Collections.emptyList());
            node.invertedPrior = new InferenceParameter("prior", new Contributor(node.observable, false), Collections.emptyList(), Collections.emptyList(), Collections.singletonList(node.prior), true);
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

        // Generate children
        if (this.parent == null) {
            for (Network sub: this.network.createSubNetworks()) {
                NetworkCompiler child = new NetworkCompiler(sub, this);
                child.analyse();
                this.children.add(child);
            }
        }
    }

    protected void analyse(Node node) throws InferenceException {
        node.evidence = new EvidenceVariable(node.observable);
        node.cE = new ResultVariable("c", node.observable);
        node.cNotE = new ResultVariable("nc", node.observable);
        List<Observable> incoming = this.network.getIncoming(node.observable).stream().map(e -> this.network.getSource(e)).collect(Collectors.toList());
        if (!incoming.isEmpty()) {
            node.inference = new ArrayList<>(incoming.size() * this.inputSignatures.size() * 2);
            for (boolean[] psig: this.inputSignatures) {
                List<Contributor> postulates = IntStream.range(0, psig.length).mapToObj(i -> new Contributor(this.inputs.get(i).observable, psig[i])).collect(Collectors.toList());
                List<boolean[]> signatures = this.signatures(incoming.size());
                 for (boolean[] sig : signatures) {
                    InferenceParameter positive = new InferenceParameter("inf", new Contributor(node.observable, true), postulates, incoming, sig);
                    InferenceParameter negative = new InferenceParameter("inf", new Contributor(node.observable, false), postulates, positive.getContributors(), Collections.singletonList(positive), true);
                    if (!positive.isContradiction())
                        node.inference.add(positive);
                    if (!negative.isContradiction())
                        node.inference.add(negative);
                }
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
        for (boolean[] psig: this.inputSignatures) {
            List<Contributor> postulates = IntStream.range(0, psig.length).mapToObj(i -> new Contributor(this.inputs.get(i).observable, psig[i])).collect(Collectors.toList());
            for (boolean[] sig : this.signatures(horizon.size())) {
                List<Contributor> cs = IntStream.range(0, horizon.size()).mapToObj(i -> new Contributor(horizon.get(i), sig[i])).collect(Collectors.toList());
                List<InferenceParameter> derivedFrom = new ArrayList<>(node.horizon.getInterior().size() + 1);
                for (Observable in : node.horizon.getInterior()) {
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
                InferenceParameter mp = this.findMatchingParameter(local, node.inference, true);
                pi.add(mp);
                pi.addAll(derivedFrom);
                InferenceParameter positive = new InferenceParameter("derived", new Contributor(node.observable, true), postulates, cs, pi, false);
                if (!positive.isContradiction())
                    node.interior.add(positive);
                List<InferenceParameter> ni = new ArrayList<>(derivedFrom.size() + 1);
                InferenceParameter mn = this.findMatchingParameter(local, node.inference, false);
                ni.add(mn);
                ni.addAll(derivedFrom);
                InferenceParameter negative = new InferenceParameter("derived", new Contributor(node.observable, false), postulates, cs, ni, false);
                if (!negative.isContradiction())
                    node.interior.add(negative);
            }
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
        throw new InferenceException("Unable to find matching parameter for " + pattern + " from " + candidates);
    }

    /**
     * Get a list of observables in order of derivation, based on the partial order of inputs and outputs
     *
     * @return The observables in derivation order
     */
    public List<Observable> getDerivationOrder() throws BuilderException {
        Predicate<Observable> isDerived = o -> o.getDerivation() != null;
        final List<Observable> derived = new ArrayList<>(this.orderedNodes.size() + this.additionalNodes.size());
        derived.addAll(this.orderedNodes.stream().map(Node::getObservable).filter(isDerived).collect(Collectors.toList()));
        derived.addAll(this.additionalNodes.stream().map(Node::getObservable).filter(isDerived).collect(Collectors.toList()));
        final Set<Observable> seen = new HashSet<>(derived.size());
        seen.addAll(this.orderedNodes.stream().map(Node::getObservable).filter(isDerived.negate()).collect(Collectors.toSet()));
        seen.addAll(this.additionalNodes.stream().map(Node::getObservable).filter(isDerived.negate()).collect(Collectors.toSet()));
        final List<Observable> ordered = new ArrayList<>(derived.size());
        while (!derived.isEmpty()) {
           Observable active = derived.stream().filter(o -> o.getDerivation().getInputs().stream().allMatch(i -> seen.contains(i))).findFirst().orElse(null);
            if (active == null)
                throw new BuilderException("Unable to find next derivation in " + derived);
            ordered.add(active);
            seen.add(active);
            derived.remove(active);
        }
        return ordered;
    }

    /**
     * Get a list of observables in order of base generation
     *
     * @return The observables in base generation order
     */
    public List<Observable> getBaseOrder() throws BuilderException {
        Predicate<Observable> isBase = o -> o.getBase() != null;
        final List<Observable> ordered = new ArrayList<>(this.orderedNodes.size() + this.additionalNodes.size());
        ordered.addAll(this.orderedNodes.stream().map(Node::getObservable).filter(isBase).collect(Collectors.toList()));
        ordered.addAll(this.additionalNodes.stream().map(Node::getObservable).filter(isBase).collect(Collectors.toList()));
         return ordered;
    }

    /**
     * Get the variables required to create a builder for this network.
     * <p>
     * These represent utility classes that can be used to generate something.
     * </p>
     *
     * @return The variables
     */
    public Set<CompiledDerivation.Variable> getBuilderVariables() {
        return this.getCompiledDerivationVariables(CompiledDerivation::getBuilderVariables);
    }

    /**
     * Get the variables required to create a classification for this network.
     * <p>
     * These represent utility classes that can be used to generate something.
     * </p>
     *
     * @return The variables
     */
    public Set<CompiledDerivation.Variable> getClassificationVariables() {
        return this.getCompiledDerivationVariables(CompiledDerivation::getClassificationVariables);
    }

    /**
     * Collect a set of variables from a compiler derivation.
     *
     * @param getter The accessor for the variables.
     *
     * @return The set of variables to use.
     */
    public Set<CompiledDerivation.Variable> getCompiledDerivationVariables(Function<CompiledDerivation, Collection<CompiledDerivation.Variable>> getter) {
        Set<CompiledDerivation.Variable> variables = new HashSet<>();

        for (Observable observable: this.network.getVertices()) {
            if (observable.getDerivation() != null && observable.getDerivation().isCompiled()) {
                variables.addAll(getter.apply((CompiledDerivation) observable.getDerivation()));
            }
            if (observable.getBase() != null && observable.getBase().isCompiled()) {
                variables.addAll(getter.apply((CompiledDerivation) observable.getBase()));
            }
        }
        return variables;
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
        private final Observable observable;
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

        public String getFormula() {
            return "p(" + this.observable.getLabel() + ")";
        }

        public String getNotFormula() {
            return "p(\u00ac" + this.observable.getLabel() + ")";
        }

        public List<InferenceParameter> matchingInference(final String signature) {
            return this.inference.stream().filter(p -> signature.equals(p.getPostulateSignature())).collect(Collectors.toList());
        }

        public List<InferenceParameter> matchingInterior(final String signature) {
            return this.interior.stream().filter(p -> signature.equals(p.getPostulateSignature())).collect(Collectors.toList());
        }

        /**
         * Construct an empty node with an observable and a list of postulates
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
