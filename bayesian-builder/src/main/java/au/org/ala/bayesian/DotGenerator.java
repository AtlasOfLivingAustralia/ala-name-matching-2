package au.org.ala.bayesian;

import com.beust.jcommander.Parameter;
import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generate a DOT description of the compiled graph.
 * <p>
 * Optionally, include parameter values and evidence values.
 * </p>
 */
public class DotGenerator extends Generator {
    private static final Logger logger = LoggerFactory.getLogger(DotGenerator.class);

    /** The encoding to use for code generation */
    private static final String ENCODING = "UTF-8";
    /** The dot template name */
    private static final String DOT_TEMPLATE = "dot_network.ftl";
    /** The dot template name */
    private static final String DOT_TEMPLATE_FULL = "dot_network_full.ftl";
    /** The configuration to use */
    private static Configuration CONFIG = null;


    /** The object wrapper to use */
    private final BeansWrapper wrapper;
    /** The template to use for generation */
    private final Template dotTemplate;
    /** The writer to output to */
    private final Writer writer;
    /** Include the full network */
    private final boolean full;

    public DotGenerator(Writer writer, boolean full) throws IOException {
        this.writer = writer;
        this.full = full;
        Configuration config = getConfig();
        this.dotTemplate = config.getTemplate(full ? DOT_TEMPLATE_FULL : DOT_TEMPLATE);
        this.wrapper = new DefaultObjectWrapperBuilder(config.getIncompatibleImprovements()).build();
    }



    /**
     * Get the freemarker configuration, lazily generated.
     *
     * @return The freemarker configuration
     */
    synchronized public static Configuration getConfig() {
        if (CONFIG == null) {
            CONFIG = new Configuration(Configuration.VERSION_2_3_29);
            CONFIG.setClassForTemplateLoading(DotGenerator.class, "");
            CONFIG.setDefaultEncoding(ENCODING);
            CONFIG.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        }
        return CONFIG;
    }

    /**
     * Generate a dot graph from the compiled network.
     *
     * @param compiler The compiler that contains the analysed network
     *
     * @throws Exception if unable to generate for some reason.
     */
    @Override
    public void generate(NetworkCompiler compiler) throws Exception {
        Environment env = this.dotTemplate.createProcessingEnvironment(compiler, this.writer);
        env.setVariable("parameters", new BeanModel(new HashMap(), this.wrapper));
        env.process();
    }

    /**
     * Generate a dot network graph from the compiled network.
     *
     * @param compiler The compiler that contains the analysed network
     * @param parameters The parameters that populate this network
     *
     * @throws Exception if unable to generate for some reason.
     */
    protected void generate(NetworkCompiler compiler, Parameters parameters) throws Exception {
        Environment env = this.dotTemplate.createProcessingEnvironment(compiler, this.writer);
        env.setVariable("parameters", new BeanModel(this.buildParameterMap(compiler, parameters), this.wrapper));
        env.process();
    }

    /**
     * Generate a freemarker-friendly map of parameter values
     *
     * @param compiler The compiler
     * @param parameters The parameter model. If null then a map onto nulls is generated.
     *
     * @return The parameter map
     */
    protected Map<String, Double> buildParameterMap(NetworkCompiler compiler, Parameters parameters) {
        Set<String> names = new HashSet<>(256);
        for (NetworkCompiler.Node node: compiler.nodes.values()) {
            if (node.getPrior() != null)
                names.add(node.getPrior().getId());
            if (node.getInvertedPrior() != null)
                names.add(node.getInvertedPrior().getId());
            for (InferenceParameter param: node.getInference())
                names.add(param.getId());
            for (InferenceParameter param: node.getInterior())
                names.add(param.getId());
        }
        return names.stream().filter(n -> this.getParameter(n, parameters) != null).collect(Collectors.toMap(n -> n, n -> this.getParameter(n, parameters)));
    }

    /**
     * Get a parameter from a generic parameter holder using reflection.
     *
     * @param name The parameter name
     * @param parameters The parameter list
     *
     * @return Either a double value or null for not found.
     */
    protected Double getParameter(String name, Parameters parameters) {
        if (parameters == null)
            return null;
        try {
            Field field = parameters.getClass().getField(name);
            return field.getDouble(parameters);
        } catch (NoSuchFieldException e) {
            return null;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("But I thought we could do this?", e);
        }
    }

    /**
     * Run the dot generator
     *
     * @param args Command line arguments
     *
     * @throws Exception if unable to complete
     */
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        Writer output = new OutputStreamWriter(System.out);
        if (options.output != null) {
            output = new FileWriter(options.output);
        }
        DotGenerator generator = new DotGenerator(output, options.full);
        for (File source: options.sources) {
            logger.info("Generating " + source.getAbsolutePath());
            if (!source.exists())
                throw new IllegalArgumentException("Base network " + source + " does not exist");
            Network network = Network.read(source.toURI().toURL());
            NetworkCompiler compiler = new NetworkCompiler(network, null);
            compiler.analyse();
            generator.generate(compiler);
        }
        output.close();
    }

    public static class Options {
        @Parameter(names = "-o")
        public File output = null;
        @Parameter(names = "-f")
        public boolean full = false;
        @Parameter(required = true)
        public List<File> sources;
    }

}
