package au.org.ala.bayesian;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import static org.junit.Assert.*;

public class AnalyserConfigTest {
    private static final File BASE = new File("/data/test");

    @Test
    public void testRelative1() throws Exception {
        AnalyserConfig config = AnalyserConfig.builder().build();
        AnalyserConfig relative = config.relative(BASE);
        assertNull(relative.getSpecialCases());
    }

    @Test
    public void testRelative2() throws Exception {
        URL special = new URL("https://somewhere.com/x.txt");
        AnalyserConfig config = AnalyserConfig.builder().specialCases(special).build();
        AnalyserConfig relative = config.relative(BASE);
        assertNotNull(relative.getSpecialCases());
        assertEquals(special.toString(), relative.getSpecialCases().toString());
    }

    @Test
    public void testRelative3() throws Exception {
        URL special = new URL("file:///notdata/test/x.txt");
        AnalyserConfig config = AnalyserConfig.builder().specialCases(special).build();
        AnalyserConfig relative = config.relative(BASE);
        assertNotNull(relative.getSpecialCases());
        assertEquals(special.toString(), relative.getSpecialCases().toString());
    }

    @Test
    public void testRelative4() throws Exception {
        URL special = new URL("file:///data/test/x.txt");
        AnalyserConfig config = AnalyserConfig.builder().specialCases(special).build();
        AnalyserConfig relative = config.relative(BASE);
        assertNotNull(relative.getSpecialCases());
        assertEquals("file:x.txt", relative.getSpecialCases().toString());
    }

    public void testAbsolute1() throws Exception {
        AnalyserConfig config = AnalyserConfig.builder().build();
        AnalyserConfig relative = config.absolute(BASE);
        assertNull(relative.getSpecialCases());
    }

    public void testAbsolute2() throws Exception {
        URL special = new URL("https://somewhere.com/x.txt");
        AnalyserConfig config = AnalyserConfig.builder().specialCases(special).build();
        AnalyserConfig relative = config.absolute(BASE);
        assertEquals(special, relative.getSpecialCases());
    }

    public void testAbsolute3() throws Exception {
        URL special = new URL("file:///somewhere/x.txt");
        AnalyserConfig config = AnalyserConfig.builder().specialCases(special).build();
        AnalyserConfig relative = config.absolute(BASE);
        assertEquals(special, relative.getSpecialCases());
    }

    public void testAbsolute4() throws Exception {
        URL special = new URL("file:x.txt");
        AnalyserConfig config = AnalyserConfig.builder().specialCases(special).build();
        AnalyserConfig relative = config.absolute(BASE);
        assertEquals("file:///data/test/x.txt", relative.getSpecialCases().toString());
    }

}
