package au.org.ala.maven;

import au.org.ala.bayesian.DotGenerator;
import au.org.ala.bayesian.Network;
import au.org.ala.bayesian.NetworkCompiler;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;

/**
 * Goal that generates a graph in DOT language for a bayesian network.
 */
@Mojo(name = "generate-graph", defaultPhase = LifecyclePhase.SITE)
public class GraphGeneratorMojo extends AbstractMojo {
    /** Project reference */
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    /** The location of the network file */
    @Parameter(property="source", required=true)
    private File source;

    /** The file to write to */
    @Parameter(property="output", defaultValue = "${project.build.directory}/network.dot")
    private File output;

    /** The file to write to */
    @Parameter(property="full", defaultValue = "false")
    private boolean full;

    /**
     * Run the goal.
     * <p>
     *     Read and analyse the network. Then create a graph  in the output file.
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
            Network network = Network.read(this.source.toURI().toURL());
            NetworkCompiler compiler = new NetworkCompiler(network);
            compiler.analyse();
            File parentDir = this.output.getParentFile();
            if (!parentDir.exists() && !parentDir.mkdirs())
                throw new MojoExecutionException("Unable to create parent directory " + parentDir);
            FileWriter writer = new FileWriter(this.output);
            DotGenerator generator = new DotGenerator(writer, this.full);
            generator.generate(compiler);
            writer.close();
        } catch (MojoExecutionException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new MojoExecutionException("Unable to create network graph" , ex);
        }
    }
}
