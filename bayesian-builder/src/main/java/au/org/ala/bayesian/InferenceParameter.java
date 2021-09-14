package au.org.ala.bayesian;

import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Value
public class InferenceParameter extends Variable {
    /** The observable this inference is associated with */
    private Contributor outcome;
    /** The list of postulates */
    private List<Contributor> postulates;
    /** The list of incoming contributions */
    private List<Contributor> contributors;
    /** The variables that this parameter is derived from */
    private List<InferenceParameter> derivedFrom;
    /** Is this an inverted derivation */
    private boolean inverted;
    /** Is this contradictory */
    private boolean contradiction;
    /** The postulate signature */
    private String postulateSignature;

    public InferenceParameter(String prefix, Contributor outcome, List<Contributor> postulates, List<Observable> sources, boolean[] signature) {
        this(prefix, outcome, postulates, makeContributors(sources, signature));
    }

    public InferenceParameter(String prefix, Contributor outcome, List<Contributor> postulates, List<Contributor> contributors) {
        this(prefix, outcome, postulates, contributors, null, false);
    }


    public InferenceParameter(String prefix, Contributor outcome, List<Contributor> postulates, List<Contributor> contributors, List<InferenceParameter> derivedFrom, boolean inverted) {
        this(prefix, outcome, postulates, reduceContributors(postulates, contributors), derivedFrom, inverted, contradiction(postulates, contributors));
    }

    protected InferenceParameter(String prefix, Contributor outcome, List<Contributor> postulates, List<Contributor> contributors, List<InferenceParameter> derivedFrom, boolean inverted, boolean contradiction) {
        super(
                prefix +
                        "_" +
                        outcome.getObservable().getJavaVariable() +
                        "_" +
                        (outcome.isMatch() ? "t" : "f") +
                        (postulates.isEmpty() && contributors.isEmpty() ? "" : "$") +
                        signatureTrace(postulates) +
                        (contributors.isEmpty() ? "" : "_")  +
                        signatureTrace(contributors)
        );
        this.outcome = outcome;
        this.postulates = postulates;
        this.contributors = new ArrayList<>(postulates);
        this.contributors.addAll(contributors);
        this.derivedFrom = derivedFrom;
        this.inverted = inverted;
        this.contradiction = contradiction;
        this.postulateSignature = this.postulates.stream().map(p -> p.isMatch() ? "t" : "f").collect(Collectors.joining());
    }

    /**
     * Does this inference parameter use this observable?
     *
     * @param observable The observable
     *
     * @return True if there is a contributor for this observable
     */
    public boolean hasObservable(Observable observable) {
        return this.contributors.stream().anyMatch(c -> c.getObservable().equals(observable));
    }

    /**
     * Is this a derived inference parameter, meaning that it can be build from other parameters.
     *
     * @return True if there is something to dervied this parameter from
     */
    public boolean isDerived() {
        return this.derivedFrom != null && !this.derivedFrom.isEmpty();
    }

    /**
     * Does this inference variable have this contributor?
     *
     * @param contributor The contributor to find
     *
     * @return True if present, false otherwise
     */
    public boolean hasContributor(Contributor contributor) {
        return this.contributors.stream().anyMatch(c -> c.equals(contributor));
    }

    /**
     * Write the formula for this intference parameter as a conditional probability.
     * <p>
     * For example <code>p(v_3 | v_1, ¬v_2)</code>
     * </p>
     *
     * @return The formula element
     */
    public String getFormula() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("p(");
        buffer.append(outcome.getFormula());
        if (!this.contributors.isEmpty() || !this.postulates.isEmpty()) {
           buffer.append(" | ");
           buffer.append(this.contributors.stream().map(Contributor::getFormula).collect(Collectors.joining(", ")));
         }
        buffer.append(")");
        return buffer.toString();
    }

    /**
     * Produce a trace of a contributor signature
     *
     * @param contributors The contributors
     *
     * @return A trace with the signature divided into null:u, true:t, false:f
     */
    protected static String signatureTrace(List<Contributor> contributors) {
        StringBuffer s = new StringBuffer(contributors.size());
        for (Contributor c: contributors)
            s.append(c.isMatch() ? 't' : 'f');
        return s.toString();
    }

    /**
     * Zip a list of sources and a signture together to build a list of contributors.
     *
     * @param sources The source obsaervables
     * @param signature The signature
     *
     * @return A list of contributors
     */
    public static List<Contributor> makeContributors(List<Observable> sources, boolean[] signature) {
        return IntStream.rangeClosed(0, sources.size() - 1).mapToObj(i -> new Contributor(sources.get(i), signature[i])).collect(Collectors.toList());
    }

    /**
     * Reduce the contributors to those that haven't already been assumed.
     *
     * @param postulates The list of postulates
     * @param contributors The list of contributors
     *
     * @return The reduced list of contributors
     */
    public static List<Contributor> reduceContributors(final List<Contributor> postulates, final List<Contributor> contributors) {
        return contributors.stream().filter(c -> !postulates.stream().anyMatch(p -> p.equals(c))).collect(Collectors.toList());
    }

    /**
     * Reduce the contributors to those that haven't already been assumed.
     *
     * @param postulates The list of postulates
     * @param contributors The list of contributors
     *
     * @return True if the two are condtradictory
     */
    public static boolean contradiction(final List<Contributor> postulates, final List<Contributor> contributors) {
        return contributors.stream().anyMatch(c -> postulates.stream().anyMatch(p -> p.contradicts(c)));
    }

}
