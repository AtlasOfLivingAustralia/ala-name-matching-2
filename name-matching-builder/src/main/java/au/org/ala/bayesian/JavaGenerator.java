package au.org.ala.bayesian;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;
import freemarker.template.*;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Compile a network into java code.
 */
public class JavaGenerator extends Generator {
    /** The default package to generate code for */
    private static final String DEFAULT_PACKAGE = "au.org.ala.bayesian.generated";
    /** The encoding to use for code generation */
    private static final String ENCODING = "UTF-8";
    /** The name of the inference class template */
    private static final String INFERENCE_CLASS_TEMPLATE = "bayesian_inference_class.ftl";
    /** The name of the inference class template */
    private static final String PARAMETERS_CLASS_TEMPLATE = "bayesian_parameter_class.ftl";
    /** The name of the builder class template */
    private static final String BUILDER_CLASS_TEMPLATE = "bayesian_builder_class.ftl";

    /** The configuration to use */
    private static Configuration CONFIG =null;

    /** The java code target directory */
    private File javaTargetDir;
    /** The resources target directory */
    private File resourceTargetDir;
    /** The package to use */
    private String packageName;
    /** The artifact name to use */
    private String artifactName;
    /** The object wrapper to use */
    private BeansWrapper wrapper;
    /** The template to use for the inference class */
    private Template inferenceTemplate;
    /** The template to use for the inference class */
    private Template parametersTemplate;
    /** The template to use for the builder class */
    private Template builderTemplate;
    /** Generate the builder? */
    private boolean generateBuilder;
    /** Generate the parameters? */
    private boolean generateParameters;
    /** Generate the inferencer? */
    private boolean generateInferencer;

    /**
     * Construct a generator
     *
     * @javaTargetDir The target directory in which java code is generated
     * @resourceTargetDir The target directory in which resources are generated
     * @packageName The package to generate into
     *
     * @throws IOException if unable to load the templates
     */
    public JavaGenerator(File javaTargetDir, File resourceTargetDir, String packageName) throws IOException {
        this.javaTargetDir = javaTargetDir;
        this.resourceTargetDir = resourceTargetDir;
        this.packageName = packageName;
        this.artifactName = "name-matching-builder";
        Configuration config = getConfig();
        this.inferenceTemplate = config.getTemplate(INFERENCE_CLASS_TEMPLATE);
        this.parametersTemplate = config.getTemplate(PARAMETERS_CLASS_TEMPLATE);
        this.builderTemplate = config.getTemplate(BUILDER_CLASS_TEMPLATE);
        this.wrapper = new DefaultObjectWrapperBuilder(config.getIncompatibleImprovements()).build();
        this.generateBuilder = true;
        this.generateParameters = true;
        this.generateInferencer = true;
    }

    /**
     * Construct a default generator
     *
     * @throws IOException if unable to load the templates
     */
    public JavaGenerator() throws IOException {
        this(new File("."), new File("."), DEFAULT_PACKAGE);
    }

    /**
     * Generate the builder class?
     *
     * @return True if the builder should be generated
     */
    public boolean isGenerateBuilder() {
        return generateBuilder;
    }

    /**
     * Set the generate builder class flag.
     *
     * @param generateBuilder True if the builder is to be generated
     */
    public void setGenerateBuilder(boolean generateBuilder) {
        this.generateBuilder = generateBuilder;
    }

    /**
     * Generate the parameters class?
     *
     * @return True if the builder should be generated
     */
    public boolean isGenerateParameters() {
        return generateParameters;
    }

    /**
     * Set the generate parameters class flag.
     *
     * @param generateParameters True if the parameters class is to be generated
     */
    public void setGenerateParameters(boolean generateParameters) {
        this.generateParameters = generateParameters;
    }

    /**
     * Generate the inferencer class?
     *
     * @return True if the builder should be generated
     */
    public boolean isGenerateInferencer() {
        return generateInferencer;
    }

    /**
     * Set the generate inferencer class flag.
     *
     * @param generateInferencer True if the inferencer is to be generated
     */
    public void setGenerateInferencer(boolean generateInferencer) {
        this.generateInferencer = generateInferencer;
    }

    /**
     * Get the artifact name.
     *
     * @return The name of the artifact (jar, package, etc) that holds this code
     */
    public String getArtifactName() {
        return artifactName;
    }

    /**
     * Set the artifact name.
     *
     * @param artifactName The new artifact name
     */
    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    /**
     * Generate all results requested.
     * <p>
     * By default, everything is generated.
     * You can choose which files to generate by using {@link #setGenerateBuilder(boolean)}, {@link #setGenerateInferencer(boolean)}
     * and {@link #setGenerateParameters(boolean)}
     * </p>
     *
     * @param compiler The compuler to generate from
     *
     * @throws Exception if unable to generate code
     */
    @Override
    public void generate(NetworkCompiler compiler) throws Exception {
        File javaTarget = this.javaTargetDir;
        File resourceTarget = this.resourceTargetDir;
        String name;
        Writer writer;

        for (String p: this.packageName.split("\\.")) {
            javaTarget = new File(javaTarget, p);
            resourceTarget = new File(resourceTarget, p);
        }
        javaTarget.mkdirs();
        if (!javaTarget.exists())
            throw new IOException("Unable to create " + javaTarget);
        resourceTarget.mkdirs();
        if (!resourceTarget.exists())
            throw new IOException("Unable to create " + resourceTarget);

        // Generate inference class
        if (this.isGenerateInferencer()) {
            name = compiler.getInferenceClassName();
            writer = new FileWriter(new File(javaTarget, name + ".java"));
            this.generateInferenceClass(compiler, name, writer);
            writer.close();
        }
        // Generate parameters class
        if (this.isGenerateParameters()) {
            name = compiler.getParameterClassName();
            writer = new FileWriter(new File(javaTarget, name + ".java"));
            this.generateParameterClass(compiler, name, writer);
            writer.close();
        }

        // Generate builder class
        if (this.isGenerateBuilder()) {
            name = compiler.getBuilderClassName();
            String networkFileName = name + ".json";
            writer = new FileWriter(new File(javaTarget, name + ".java"));
            this.generateBuilderClass(compiler, name, writer);
            writer.close();
            writer = new FileWriter(new File(resourceTarget, name + ".json"));
            this.generateNetworkFile(compiler, name, writer);
        }
    }

    /**
     * Generate code for the inference class
     *
     * @param compiler The compiler/analyser
     */
    protected void generateInferenceClass(NetworkCompiler compiler, String name, Writer writer) throws IOException, TemplateException {
        Environment env = this.inferenceTemplate.createProcessingEnvironment(compiler, writer);
        env.setVariable("packageName", new StringModel(this.packageName, this.wrapper));
        env.setVariable("className", new StringModel(name, this.wrapper));
        env.process();
    }

    /**
     * Generate code for the parameters class
     *
     * @param compiler The compiler/analyser
     */
    protected void generateParameterClass(NetworkCompiler compiler, String name, Writer writer) throws IOException, TemplateException {
        Environment env = this.parametersTemplate.createProcessingEnvironment(compiler, writer);
        env.setVariable("packageName", new StringModel(this.packageName, this.wrapper));
        env.setVariable("className", new StringModel(name, this.wrapper));
        env.process();
    }

    /**
     * Generate code for the builder class
     *
     * @param compiler The compiler/analyser
     */
    protected void generateBuilderClass(NetworkCompiler compiler, String name, Writer writer) throws IOException, TemplateException {
        Environment env = this.builderTemplate.createProcessingEnvironment(compiler, writer);
        env.setVariable("packageName", new StringModel(this.packageName, this.wrapper));
        env.setVariable("className", new StringModel(name, this.wrapper));
        env.setVariable("parameterClassName", new StringModel(compiler.getParameterClassName(), this.wrapper));
        env.setVariable("artifactName", new StringModel(this.artifactName, this.wrapper));
        env.process();
    }

    /**
     * Generate a readable version of the network for the builder class
     *
     * @param compiler The compiler/analyser
     * @param name The file name
     * @param writer The writer to write to
     *
     * @throws IOException if unable to write the network
     */
    protected void generateNetworkFile(NetworkCompiler compiler, String name, Writer writer) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.writeValue(writer, compiler.getNetwork());
    }

    /**
     * Get the freemarker configuration, lazily generated.
     *
     * @return The freemarker configuration
     */
    synchronized public static Configuration getConfig() {
        if (CONFIG == null) {
            CONFIG = new Configuration(Configuration.VERSION_2_3_29);
            CONFIG.setClassForTemplateLoading(JavaGenerator.class, "");
            CONFIG.setDefaultEncoding(ENCODING);
            CONFIG.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        }
        return CONFIG;
    }

    /**
     * Run the java generator
     *
     * @param args Command line arguments
     *
     * @throws Exception if unable to complete
     */
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        Option pkgOption = Option.builder("p").longOpt("package").desc("Output package name").hasArg().argName("PACKAGE").build();
        Option outputOption = Option.builder("o").longOpt("output").desc("Base output directory (not including package)").hasArg().argName("DIR").type(File.class).build();
        options.addOption(pkgOption);
        options.addOption(outputOption);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        String pkg = cmd.getOptionValue("p", "au.org.ala.names.generated");
        File output = new File(cmd.getOptionValue("o", System.getProperty("user.dir")));

        System.out.println("Generating for package " + pkg + " and output " + output.getAbsolutePath());
        if (!pkg.matches("[a-z]+(\\.[a-z]+)*"))
            throw new IllegalArgumentException("Invalid package name " + pkg);
        if (!output.exists())
            throw new IllegalArgumentException("Output directory " + output + " does not exist");
        JavaGenerator generator = new JavaGenerator(output, output, pkg);
        for (String s: cmd.getArgList()) {
            File source = new File(s);
            System.out.println("Generating " + source.getAbsolutePath());
            if (!source.exists())
                throw new IllegalArgumentException("Base network " + source + " does not exist");
            Network network = Network.read(source.toURI().toURL());
            NetworkCompiler compiler = new NetworkCompiler(network);
            compiler.analyse();
            generator.generate(compiler);
        }
    }
}
