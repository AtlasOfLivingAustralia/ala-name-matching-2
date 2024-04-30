package au.org.ala.bayesian;

import org.apache.commons.lang3.StringUtils;
import org.gbif.dwc.terms.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * A handy tool that will allow you to build queries and report on the results for analysis purposes.
 * <p>
 * Subclasses of the tool can
 * </p>
 *
 * @param <C> The classification class
 * @param <I> Yhe inferencer class
 * @param <F> The factory class
 * @param <M> The measurements class
 */
abstract public class MatchTool<C extends Classification<C>, I extends Inferencer<C>, F extends NetworkFactory<C, I, F>, M extends MatchMeasurement> implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(MatchTool.class);

    protected F factory;
    protected ClassificationMatcher<C, I, F, M> matcher;
    protected List<BiConsumer<String[], Check>> inputs;
    protected List<BiFunction<Check, Match<C, M>, Object>> outputs;
    protected List<Object> headers;
    protected BiFunction<Check, Match<C, M>, Object> matchValid;
    protected BiFunction<Check, Match<C, M>, Object> searchName;
    protected BiFunction<Check, Match<C, M>, Object> matchName;
    protected BiFunction<Check, Match<C, M>, Object> matchProbability;
    protected BiFunction<Check, Match<C, M>, Object> matchFidelity;
    protected boolean instrument;

    /**
     * Construct for a searchable store.
     *
     * @param factory    The factory
     * @param searcher   The classifier searcher
     * @param config     The configuration to use
     * @param analyserConfig The analyser config to use
     * @param instrument Record instrumentation
     */
    public MatchTool(F factory, ClassifierSearcher<?> searcher, ClassificationMatcherConfiguration config, AnalyserConfig analyserConfig, boolean instrument) {
        this.factory = factory;
        this.matcher = this.factory.createMatcher(searcher, config, analyserConfig);
        this.instrument = instrument;
    }

    /**
     * Close this tool
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        if (this.matcher != null)
            this.matcher.close();
    }

    /**
     * Run the match.
     *
     * @throws Exception on error
     */
    public void run() throws Exception {
        String[] inputHeaders = this.readInputs();
        this.buildTranslator(inputHeaders);
        this.writeHeader();
        Check check;
        while ((check = this.nextCheck()) != null) {
            Match<C, M> match = this.matcher.findMatch(check.classification.clone(), MatchOptions.ALL);
            this.writeResult(check, match);
        }
        this.writeFooter();
        this.close();
    }

    /**
     * Build the functions needed to read/write the appropriate data for this
     *
     * @param inputHeader The input data
     */
    public void buildTranslator(String[] inputHeader) {
        C example = this.factory.createClassification();
        M exampleMeasurement = this.matcher.createMeasurement();
        Map<String, Observable<?>> mappings = new HashMap<>();
        for (Observable<?> observble : this.factory.getObservables()) {
            if (observble.getUri() != null) {
                mappings.put(observble.getUri().toString(), observble);
                String path = observble.getUri().getPath();
                if (path != null) {
                    int p = Math.max(path.lastIndexOf('/'), path.lastIndexOf('#'));
                    path = p >= 0 ? path.substring(p + 1) : path;
                    if (!path.isEmpty())
                        mappings.put(path, observble);
                }
            }
            mappings.put(observble.getId(), observble);
        }

        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.headers = new ArrayList<>();

        // Process inputs
        for (int i = 0; i < inputHeader.length; i++) {
            final String name = inputHeader[i];
            final int index = i;
            BiConsumer<String[], Check> input;
            BiFunction<Check, Match<C, M>, Object> output;
            String header = name;
            final Observable<Object> observable = (Observable<Object>) mappings.get(name);
            if (observable == null) {
                input = (s, c) -> c.additional.put(name, StringUtils.trimToNull(s[index]));
                output = (c, m) -> c.additional.get(name);
            } else {
                try {
                    final Field field = example.getClass().getField(observable.getJavaVariable());
                    input = (s, c) -> {
                        try {
                            field.set(c.classification, observable.getAnalysis().fromString(StringUtils.trimToNull(s[index]), null));
                        } catch (Exception ex) {
                            logger.error("Unable to access " + name, ex);
                        }
                    };
                    output = (c, m) -> {
                        try {
                            return observable.getAnalysis().toStore(field.get(c.classification));
                        } catch (Exception ex) {
                            logger.error("Unable to acceess " + name, ex);
                            return null;
                        }
                    };
                } catch (NoSuchFieldException ex) {
                    logger.error("Unable to access field for " + name, ex);
                    input = (s, c) -> {
                    };
                    output = (c, m) -> null;
                }
                header = "Supplied " + header;
            }
            this.inputs.add(input);
            this.outputs.add(output);
            this.headers.add(header);
        }
        // Add the match valid
        this.matchValid = (c, m) -> m.isValid();
        this.outputs.add(this.matchValid);
        this.headers.add("matchValid");
        // Add the match probability
        this.matchProbability = (c, m) -> m.isValid() ? m.getProbability().getPosterior() : null;
        this.outputs.add(this.matchProbability);
        this.headers.add("matchProbability");
        // Add the match fidelity
        this.matchFidelity = (c, m) -> m.getFidelity() != null ? m.getFidelity().getFidelity() : null;
        this.outputs.add(this.matchFidelity);
        this.headers.add("matchFidelity");
        // Add the search name (the name which was actually looked for)
        this.searchName = (c, m) -> m.getActual() != null ? m.getActual().getName() : null;
        this.outputs.add(this.searchName);
        this.headers.add("searchName");
        // Add the match name
        this.matchName = (c, m) -> m.isValid() ? m.getMatch().getName() : null;
        this.outputs.add(this.matchName);
        this.headers.add("matchedName");

        // Process outputs
        for (Observable<?> observable : this.factory.getObservables()) {
            final String name = observable.getId();
            try {
                final Observable<Object> obs = (Observable<Object>) observable;
                BiFunction<Check, Match<C, M>, Object> output;
                String header = name;
                final Field field = example.getClass().getField(obs.getJavaVariable());
                output = (c, m) -> {
                    try {
                        Object v = m.isValid() ? obs.getAnalysis().toStore(field.get(m.getAccepted())) : null;
                        return v == null ? null : v.toString();
                    } catch (Exception ex) {
                        logger.error("Unable to acceess " + name, ex);
                        return null;
                    }
                };
                this.outputs.add(output);
                this.headers.add(header);
            } catch (NoSuchFieldException ex) {
                logger.info("Unable to access field for " + name);
            }
        }

        // Process issues
        for (Term issue : this.factory.getAllIssues()) {
            this.outputs.add((c, m) -> m.getIssues().contains(issue) ? true : null);
            this.headers.add(issue.prefix() != null ? issue.prefixedName() : issue.simpleName());
        }

        // Process measurements
        if (this.instrument) {
            try {
                BeanInfo info = Introspector.getBeanInfo(exampleMeasurement.getClass());
                for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
                    // Skip object-level properties
                    if (!MatchMeasurement.class.isAssignableFrom(descriptor.getReadMethod().getDeclaringClass()))
                        continue;
                    String header = descriptor.getDisplayName();
                    BiFunction<Check, Match<C, M>, Object> output;

                    output = (c, m) -> {
                        try {
                            return m.getMeasurement() != null ? descriptor.getReadMethod().invoke(m.getMeasurement()) : null;
                        } catch (Exception ex) {
                            logger.error("Unable to acceess " + descriptor.getName(), ex);
                            return null;
                        }
                    };
                    this.outputs.add(output);
                    this.headers.add(header);
                }
            } catch (Exception ex) {
                logger.error("Unable to access measurement info", ex);
            }
        }
    }

    /**
     * Write the headers.
     * <p>
     * By default, this writes to the logger.
     * Do something sensible with it for a proper tool.
     * </p>
     */
    public void writeHeader() {
        logger.info(this.headers.toString());
    }

    /**
     * Write the footer.
     * <p>
     * By default, this writes statisticl information to the logger.
     * Do something sensible with it for a proper tool.
     * </p>
     */
    public void writeFooter() {
        logger.info(this.matcher.getTimeStatistics());
        logger.info(this.matcher.getSearchStatistics());
        logger.info(this.matcher.getSearchModificationStatistics());
        logger.info(this.matcher.getCandidateStatistics());
        logger.info(this.matcher.getHintModificationStatistics());
        logger.info(this.matcher.getMatchStatistics());
        logger.info(this.matcher.getMaxCandidateStatistics());
        logger.info(this.matcher.getMatchableStatistics());
    }

    protected void writeResult(Check check, Match<C, M> match) {
        List<Object> values = this.outputs.stream().map(o -> o.apply(check, match)).collect(Collectors.toList());
        this.writeValues(values);
    }

    /**
     * Write the list of values for the result.
     * <p>
     * By default, this writes to the logger.
     * Subclasses should so something sensible with this.
     * </p>
     *
     * @param values The log values
     */
    protected void writeValues(List<Object> values) {
        logger.info(values.toString());
    }


    /**
     * Get the next check to match.
     *
     * @return The next check, or null for no more data
     * @throws Exception when untable to aquire a check value
     */
    protected Check nextCheck() throws Exception {
        final String[] row = nextRow();
        if (row == null)
            return null;
        final Check check = new Check(this.factory.createClassification());
        this.inputs.forEach(i -> i.accept(row, check));
        return check;
    }

    /**
     * Read the inputs.
     * <p>
     * By default, this is the first row in a file.
     * Clever implementations can alter this.
     * </p>
     *
     * @return The input headers
     * @throws Exception if unable to read the headers
     */
    protected String[] readInputs() throws Exception {
        return this.nextRow();
    }

    /**
     * Read the next row of input values.
     *
     * @return The next row or null for none
     * @throws Exception if unable to read the row
     */
    abstract protected String[] nextRow() throws Exception;

    /**
     * The description of the thing to check.
     * <p>
     * This includes
     * </p>
     */
    class Check {
        /**
         * The requested classification
         */
        public C classification;
        /**
         * Additional information
         */
        public Map<String, String> additional = new HashMap<>();

        public Check(C classification) {
            this.classification = classification;
        }
    }
}
