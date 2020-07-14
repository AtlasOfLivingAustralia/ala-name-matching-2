package au.org.ala.bayesian;

import au.org.ala.names.model.ExternalContext;
import au.org.ala.util.IdentifierConverter;
import au.org.ala.util.SimpleIdentifierConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.CollectionModel;
import freemarker.ext.beans.StringModel;
import freemarker.template.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Properties;

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
    /** The name of the classification class template */
    private static final String CLASSIFICATION_CLASS_TEMPLATE = "bayesian_classification_class.ftl";
    /** The name of the builder class template */
    private static final String BUILDER_CLASS_TEMPLATE = "bayesian_builder_class.ftl";
    /** The name of the observables class template */
    private static final String FACTORY_CLASS_TEMPLATE = "bayesian_factory_class.ftl";
    /** The name of the CLI class template */
    private static final String CLI_CLASS_TEMPLATE = "builder_cli_class.ftl";

    /** The configuration to use */
    private static Configuration CONFIG =null;

    /** The java code target directory */
    private final File javaTargetDir;
    /** The resources target directory */
    private final File resourceTargetDir;
    /** The package to use */
    @Getter
    private final String packageName;
    /** The artifact name to use */
    @Getter
    @Setter
    private String artifactName;
    /** The object wrapper to use */
    private final BeansWrapper wrapper;
    /** The template to use for the inference class */
    private final Template inferencerTemplate;
    /** The template to use for the inference class */
    private final Template parametersTemplate;
    /** The template to use for the classification class */
    private final Template classificationTemplate;
    /** The template to use for the builder class */
    private final Template builderTemplate;
    /** The template to use for the factory class */
    private final Template factoryTemplate;
    /** The template to use for the CLI class */
    private final Template cliTemplate;
    /** The identifier converter for creating class names */
    private final IdentifierConverter classConverter;
    /** The analyser class to use, if needed */
    @Getter
    @Setter
    private String analyserClass;
    /** The matcher class to use, if needed */
    @Getter
    @Setter
    private String matcherClass;
    /** Generate the builder? */
    @Getter
    @Setter
    private boolean generateBuilder;
    /** Generate the parameters? */
    @Getter
    @Setter
    private boolean generateParameters;
    /** Generate the classification */
    @Getter
    @Setter
    private boolean generateClassification;
    /** Generate the inferencer? */
    @Getter
    @Setter
    private boolean generateInferencer;
    /** Generate the factory? */
    @Getter
    @Setter
    private boolean generateFactory;
    /** Generate the CLI? */
    @Getter
    @Setter
    private boolean generateCli;

    /**
     * Construct a generator
     *
     * @param javaTargetDir The target directory in which java code is generated
     * @param resourceTargetDir The target directory in which resources are generated
     * @param packageName The package to generate into
     *
     * @throws IOException if unable to load the templates
     */
    public JavaGenerator(File javaTargetDir, File resourceTargetDir, String packageName) throws IOException {
        this.javaTargetDir = javaTargetDir;
        this.resourceTargetDir = resourceTargetDir;
        this.packageName = packageName;
        this.artifactName = "name-matching-builder";
        Configuration config = getConfig();
        this.inferencerTemplate = config.getTemplate(INFERENCE_CLASS_TEMPLATE);
        this.parametersTemplate = config.getTemplate(PARAMETERS_CLASS_TEMPLATE);
        this.classificationTemplate = config.getTemplate(CLASSIFICATION_CLASS_TEMPLATE);
        this.builderTemplate = config.getTemplate(BUILDER_CLASS_TEMPLATE);
        this.factoryTemplate = config.getTemplate(FACTORY_CLASS_TEMPLATE);
        this.cliTemplate = config.getTemplate(CLI_CLASS_TEMPLATE);
        this.classConverter = SimpleIdentifierConverter.JAVA_CLASS;
        this.wrapper = new DefaultObjectWrapperBuilder(config.getIncompatibleImprovements()).build();
        this.analyserClass = null;
        this.matcherClass = null;
        this.generateBuilder = true;
        this.generateParameters = true;
        this.generateClassification = true;
        this.generateInferencer = true;
        this.generateFactory = true;
        this.generateCli = true;
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
     * Remove the package name from a class to make a simple name
     *
     * @param className The class name
     *
     * @return The class name without package
     */
    private String stripPackage(String className) {
        return className.replaceFirst("^([a-z$_][a-zA-Z0-9$_]*\\.)+", "");
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
        String classBase = this.classConverter.convert(compiler.getNetwork());
        File javaTarget = this.javaTargetDir;
        File resourceTarget = this.resourceTargetDir;
        String inferencerName = classBase + "Inferencer";
        String parametersName = classBase + "Parameters";
        String classificationName = classBase + "Classification";
        String builderName = classBase + "Builder";
        String factoryName = classBase + "Factory";
        String cliName = classBase + "Cli";
        String networkFileName = classBase + ".json";
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

        // Common context
        Properties context = new Properties();
        context.put("inferencerClassName", inferencerName);
        context.put("parametersClassName", parametersName);
        context.put("classificationClassName", classificationName);
        context.put("builderClassName", builderName);
        context.put("factoryClassName", factoryName);
        context.put("cliClassName", cliName);
        context.put("artifactName", this.artifactName);
        context.put("packageName", this.packageName);
        context.put("networkFile", networkFileName);
        if (this.matcherClass != null) {
            context.put("matcherClass", this.matcherClass);
            context.put("matcherClassName", this.stripPackage(this.matcherClass));
        }
        if (this.analyserClass != null) {
            context.put("analyserClass", this.analyserClass);
            context.put("analyserClassName", this.stripPackage(this.analyserClass));
        }

        // Generate inference class
        if (this.isGenerateInferencer()) {
            writer = new FileWriter(new File(javaTarget, inferencerName + ".java"));
            this.generateInferenceClass(compiler, inferencerName, context, writer);
            writer.close();
        }
        // Generate parameters class
        if (this.isGenerateParameters()) {
            writer = new FileWriter(new File(javaTarget, parametersName + ".java"));
            this.generateParameterClass(compiler, parametersName, context, writer);
            writer.close();
        }
        // Generate classification class
        if (this.isGenerateClassification()) {
            writer = new FileWriter(new File(javaTarget, classificationName + ".java"));
            this.generateClassificationClass(compiler, classificationName, context, writer);
            writer.close();
        }
        // Generate factory class
        if (this.isGenerateFactory()) {
            writer = new FileWriter(new File(javaTarget, factoryName + ".java"));
            this.generateFactoryClass(compiler, factoryName, context, writer);
            writer.close();
        }

        // Generate builder class
        if (this.isGenerateBuilder()) {
            writer = new FileWriter(new File(javaTarget, builderName + ".java"));
            this.generateBuilderClass(compiler, builderName, context, writer);
            writer.close();
        }
        if (this.isGenerateCli()) {
            writer = new FileWriter(new File(javaTarget, cliName + ".java"));
            this.generateCliClass(compiler, cliName, context, writer);
            writer.close();
            writer = new FileWriter(new File(resourceTarget, networkFileName));
            this.generateNetworkFile(compiler, context, writer);
        }
    }

    /**
     * Populate the template environnment with commobn values.
     *
     * @param environment The environmwent to populate
     * @param context The properties map
     */
    protected void populate(Environment environment, Properties context) {
        for (String name: context.stringPropertyNames()) {
            environment.setVariable(name, new StringModel(context.getProperty(name), this.wrapper));
        }
    }

    /**
     * Generate code for the inference class
     *
     * @param compiler The compiler/analyser
     * @param name The class name
     * @param context Context variables
     * @param writer The output desitination for the code
     */
    protected void generateInferenceClass(@NonNull NetworkCompiler compiler, String name, Properties context, Writer writer) throws IOException, TemplateException {
        Environment env = this.inferencerTemplate.createProcessingEnvironment(compiler, writer);
        env.setVariable("className", new StringModel(name, this.wrapper));
        this.populate(env, context);
        env.process();
    }

    /**
     * Generate code for the parameters class
     *
     * @param compiler The compiler/analyser
     * @param name The class name
     * @param context Context variables
     * @param writer The output desitination for the code
     */
    protected void generateParameterClass(@NonNull NetworkCompiler compiler, String name, Properties context, Writer writer) throws IOException, TemplateException {
        Environment env = this.parametersTemplate.createProcessingEnvironment(compiler, writer);
        env.setVariable("className", new StringModel(name, this.wrapper));
        this.populate(env, context);
        env.process();
    }

    /**
     * Generate code for the classification class
     *
     * @param compiler The compiler/analyser
     * @param name The class name
     * @param context Context variables
     * @param writer The output desitination for the code
     */
    protected void generateClassificationClass(@NonNull NetworkCompiler compiler, String name, Properties context, Writer writer) throws IOException, TemplateException {
        Environment env = this.classificationTemplate.createProcessingEnvironment(compiler, writer);
        env.setVariable("className", new StringModel(name, this.wrapper));
        this.populate(env, context);
        env.process();
    }

    /**
     * Generate code for the observables class
     *
     * @param compiler The compiler/analyser
     * @param name The class name
     * @param context Context variables
     * @param writer The output desitination for the code
    */
    protected void generateFactoryClass(@NonNull NetworkCompiler compiler, String name, Properties context, Writer writer) throws IOException, TemplateException {
        Environment env = this.factoryTemplate.createProcessingEnvironment(compiler, writer);
        env.setVariable("className", new StringModel(name, this.wrapper));
        this.populate(env, context);
        env.setVariable("externalContexts", new CollectionModel(Collections.singletonList(ExternalContext.LUCENE), this.wrapper));
        env.process();
    }


    /**
     * Generate code for the builder class
     *
     * @param compiler The compiler/analyser
     * @param name The class name
     * @param context Context variables
     * @param writer The output desitination for the code
     */
    protected void generateBuilderClass(@NonNull NetworkCompiler compiler, String name, Properties context, Writer writer) throws IOException, TemplateException {
        Environment env = this.builderTemplate.createProcessingEnvironment(compiler, writer);
        env.setVariable("className", new StringModel(name, this.wrapper));
        this.populate(env, context);
        env.process();
    }

    /**
     * Generate code for the builder class
     *
     * @param compiler The compiler/analyser
     * @param name The class name
     * @param context Context variables
     * @param writer The output desitination for the code
     */
    protected void generateCliClass(@NonNull NetworkCompiler compiler, String name, Properties context, Writer writer) throws IOException, TemplateException {
        Environment env = this.cliTemplate.createProcessingEnvironment(compiler, writer);
        env.setVariable("className", new StringModel(name, this.wrapper));
        this.populate(env, context);
        env.process();
    }

    /**
     * Generate a readable version of the network for the builder class
     *
     * @param compiler The compiler/analyser
     * @param context The context to use
     * @param writer The writer to write to
     *
     * @throws IOException if unable to write the network
     */
    protected void generateNetworkFile(@NonNull NetworkCompiler compiler, @SuppressWarnings("unused") Properties context, Writer writer) throws IOException {
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
