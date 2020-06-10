package au.org.ala.maven;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GraphGeneratorMojoTest {
    @Rule
    public MojoRule rule = new MojoRule();

    @Test
    public void testGenerate1() throws Exception {
        File pom = new File(this.getClass().getResource("graph-generate-pom-1.xml").toURI());
        assertNotNull(pom);

        Mojo mojo = this.rule.lookupMojo("generate-graph", pom);
        mojo.execute();
        File output = new File("target/test-harness/graph-generator-unit-test/network-1.dot");
        assertTrue(output.exists());
    }
}
