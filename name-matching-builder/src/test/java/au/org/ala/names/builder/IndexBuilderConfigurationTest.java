package au.org.ala.names.builder;

import au.org.ala.names.generated.GrassBuilder;
import au.org.ala.names.lucene.LuceneLoadStore;
import au.org.ala.util.TestUtils;
import au.org.ala.vocab.ALATerm;
import org.gbif.dwc.terms.Term;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.StringWriter;

import static org.junit.Assert.*;

/**
 * Test cases for the {@link IndexBuilderConfiguration} class
 */
public class IndexBuilderConfigurationTest {
    @Before
    public void setUp() {
        Term test = ALATerm.altScientificName; // Ensure class loaded
    }
    @Test
    public void testCreate1() throws Exception {
        IndexBuilderConfiguration config = new IndexBuilderConfiguration();
        assertNull(config.getWork());
        assertEquals(LuceneLoadStore.class, config.getLoadStoreClass());
    }

    @Test
    public void testWrite1() throws Exception {
        IndexBuilderConfiguration config = new IndexBuilderConfiguration();
        config.setWork(new File("/tmp"));
        config.setLoadStoreClass(TestLoadStore.class);
        StringWriter writer = new StringWriter();
        config.write(writer);
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "config-1.json"), writer.toString());
    }

    @Test
    public void testWrite3() throws Exception {
        IndexBuilderConfiguration config = new IndexBuilderConfiguration();
        config.setBuilderClass(GrassBuilder.class);
        config.setLogInterval(200);
        StringWriter writer = new StringWriter();
        config.write(writer);
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "config-3.json"), writer.toString());
    }

    @Test
    public void testRead1() throws Exception {
        IndexBuilderConfiguration config = IndexBuilderConfiguration.read(this.getClass().getResource("config-1.json"));
        assertNotNull(config.getWork());
        assertEquals("/tmp", config.getWork().getAbsolutePath());
        assertEquals(TestLoadStore.class, config.getLoadStoreClass());
    }

    @Test
    public void testRead2() throws Exception {
        IndexBuilderConfiguration config = IndexBuilderConfiguration.read(this.getClass().getResource("config-3.json"));
        assertNull(config.getWork());
        assertEquals(LuceneLoadStore.class, config.getLoadStoreClass());
        assertEquals(GrassBuilder.class, config.getBuilderClass());
        assertEquals(200, config.getLogInterval());
    }

    @Test
    public void testCreateLoadStore1() throws Exception {
        Annotator annotator = new TestAnnotator();
        IndexBuilderConfiguration config = new IndexBuilderConfiguration();
        config.setWork(new File("/tmp"));
        config.setLoadStoreClass(TestLoadStore.class);
        LoadStore store = config.createLoadStore(annotator);
        assertEquals(TestLoadStore.class, store.getClass());
        store.close();
    }

    @Test
    public void testCreateBuilder1() throws Exception {
        Annotator annotator = new TestAnnotator();
        IndexBuilderConfiguration config = new IndexBuilderConfiguration();
        config.setBuilderClass(EmptyBuilder.class);
        Builder builder  = config.createBuilder(annotator, new TestFactory());
        assertEquals(EmptyBuilder.class, builder.getClass());
    }

}
