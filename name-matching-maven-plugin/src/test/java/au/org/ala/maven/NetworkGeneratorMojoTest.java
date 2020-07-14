package au.org.ala.maven;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class NetworkGeneratorMojoTest {
    @Rule
    public MojoRule rule = new MojoRule();

    @Test
    public void testGenerate1() throws Exception {
        File pom = new File(this.getClass().getResource("network-generate-pom-1.xml").toURI());
        MavenProjectStub project = new MavenProjectStub();
        project.setArtifactId("test-artifact");
        assertNotNull(pom);
        NetworkGeneratorMojo mojo = (NetworkGeneratorMojo) this.rule.lookupMojo("generate-network", pom);
        mojo.project = project;
        mojo.execute();
        File base = new File("target/test-harness/network-generator-unit-test/generated-sources/java/au/org/ala/maven/test");
        assertTrue(base.exists());
        File inference = new File(base, "Network1Inferencer.java");
        assertTrue(inference.exists());
        File parameters = new File(base, "Network1Parameters.java");
        assertTrue(parameters.exists());
        File builder = new File(base, "Network1Builder.java");
        assertTrue(builder.exists());
        File classification = new File(base, "Network1Classification.java");
        assertTrue(classification.exists());
        File factory = new File(base, "Network1Factory.java");
        assertTrue(factory.exists());
        File cli = new File(base, "Network1Cli.java");
        assertTrue(cli.exists());
        base = new File("target/test-harness/network-generator-unit-test/generated-resources/au/org/ala/maven/test");
        assertTrue(base.exists());
        File network = new File(base, "Network1.json");
        assertTrue(network.exists());
    }

    @Test
    public void testGenerate2() throws Exception {
        File pom = new File(this.getClass().getResource("network-generate-pom-2.xml").toURI());
        MavenProjectStub project = new MavenProjectStub();
        project.setArtifactId("test-artifact");
        assertNotNull(pom);
        NetworkGeneratorMojo mojo = (NetworkGeneratorMojo) this.rule.lookupMojo("generate-network", pom);
        mojo.project = project;
        mojo.execute();
        File base = new File("target/test-harness/network-generator-unit-test-2/generated-sources/java/au/org/ala/maven/test");
        assertTrue(base.exists());
        File inference = new File(base, "Network1Inference.java");
        assertFalse(inference.exists());
        File parameters = new File(base, "Network1Parameters.java");
        assertFalse(parameters.exists());
        File builder = new File(base, "Network1Builder.java");
        assertTrue(builder.exists());
        File classification = new File(base, "Network1Classification.java");
        assertFalse(classification.exists());
        File factory = new File(base, "Network1Factory.java");
        assertFalse(factory.exists());
        File cli = new File(base, "Network1Cli.java");
        assertTrue(cli.exists());
        base = new File("target/test-harness/network-generator-unit-test-2/generated-resources/au/org/ala/maven/test");
        assertTrue(base.exists());
        File network = new File(base, "Network1.json");
        assertTrue(network.exists());
    }
}
