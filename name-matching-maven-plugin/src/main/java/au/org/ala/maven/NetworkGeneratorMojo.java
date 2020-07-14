package au.org.ala.maven;

import au.org.ala.bayesian.EvidenceAnalyser;
import au.org.ala.bayesian.JavaGenerator;
import au.org.ala.bayesian.Network;
import au.org.ala.bayesian.NetworkCompiler;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * Goal that generates java code for bayesian networks.
 */
@Mojo(name = "generate-network", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class NetworkGeneratorMojo extends AbstractMojo {
    /** Project reference */
    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;

    /** The location of the network file (this is expected to be in a resources folder somewhere, so that it can be included in the final package) */
    @Parameter(property="source", required=true)
    private File source;

    /** The package to use */
    @Parameter(property="outputPackage", defaultValue = "${project.groupId}.generated")
    private String outputPackage;

    /** The directory to write to (generated java and resources subdirectories are added as required) */
    @Parameter(property="outputDirectory", defaultValue = "${project.build.directory}")
    private File outputDirectory;


    /** Any matcher to use */
    @Parameter(property="matcher")
    private String matcherClass;

    /** Any analyser to use */
    @Parameter(property="analyser")
    private String analyserClass;

    /** Generate the builder? */
    @Parameter(property="generateBuilder", defaultValue = "true")
    private boolean generateBuilder = true;

    /** Generate the parameters? */
    @Parameter(property="generateParameters", defaultValue = "true")
    private boolean generateParameters = true;

    /** Generate the inferencer? */
    @Parameter(property="generateInferencer", defaultValue = "true")
    private boolean generateInferencer = true;

    /** Generate the classification? */
    @Parameter(property="generateClassification", defaultValue = "true")
    private boolean generateClassification = true;

    /** Generate the factory? */
    @Parameter(property="generateMatcher", defaultValue = "true")
    private boolean generateFactory = true;

    /** Generate the command line interface? */
    @Parameter(property="generateCli", defaultValue = "true")
    private boolean generateCli = true;

    /**
     * Run the goal.
     * <p>
     *     Read and analyse the network. Then create appropriate files in the output directory.
     * </p>
     *
     * @see AbstractMojo#execute()
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (this.project == null)
                throw new MojoExecutionException("Network code generator requires a project");
            File javaOutput = new File(outputDirectory, "generated-sources/java");
            File resourcesOutput = new File(outputDirectory, "generated-resources");
            this.getLog().info("Compiling " + this.source + " to " + this.outputDirectory + " under " + this.outputPackage);
            this.project.addCompileSourceRoot(javaOutput.getCanonicalPath());
            Resource resource = new Resource();
            resource.setDirectory(resourcesOutput.getCanonicalPath());
            resource.addInclude("**/*");
            resource.setFiltering(false);
            this.project.addResource(resource);
            Network network = Network.read(this.source.toURI().toURL());
            NetworkCompiler compiler = new NetworkCompiler(network);
            compiler.analyse();
            JavaGenerator generator = new JavaGenerator(javaOutput, resourcesOutput, this.outputPackage);
            generator.setGenerateBuilder(this.generateBuilder);
            generator.setGenerateInferencer(this.generateInferencer);
            generator.setGenerateParameters(this.generateParameters);
            generator.setGenerateClassification(this.generateClassification);
            generator.setGenerateFactory(this.generateFactory);
            generator.setGenerateCli(this.generateCli);
            generator.setArtifactName(this.project.getArtifactId());
            generator.setMatcherClass(this.matcherClass);
            generator.setAnalyserClass(this.analyserClass);
            generator.generate(compiler);
        } catch (MojoExecutionException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new MojoExecutionException("Unable to create network code for " + this.source + " to " + this.outputDirectory + " under " + this.outputPackage + ": " + ex.getMessage() , ex);
        }
    }
}
