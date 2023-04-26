package au.org.ala.bayesian;

import au.org.ala.util.JsonUtils;
import au.org.ala.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class TraceTest {
    private Trace trace;
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        this.trace = new Trace(new TestFactory(), Trace.TraceLevel.DEBUG, "top");
        this.mapper = JsonUtils.createMapper();
    }

    protected void compare(String resource) throws Exception {
        String expected = TestUtils.getResource(this.getClass(), resource);
        String actual = this.mapper.writeValueAsString(this.trace);
        TestUtils.compareNoSpaces(expected, actual);
    }

    @Test
    public void testSimple1() throws Exception {
        this.trace.value(Trace.TraceLevel.INFO, 100);
        this.compare("trace-1.json");
    }


    @Test
    public void testSimple2() throws Exception {
        this.trace.value(Trace.TraceLevel.SUMMARY, Arrays.asList("Hello", 21));
        this.compare("trace-2.json");
    }


    @Test
    public void testSimple3() throws Exception {
        this.trace.value(Trace.TraceLevel.TRACE, Arrays.asList("Hello", 21));
        this.compare("trace-10.json");
    }

    @Test
    public void testDescriptor1() throws Exception {
        TestTracable tracable = TestTracable.builder().id("1").value1("Hello").value2("There").build();
        this.trace.value(Trace.TraceLevel.INFO, tracable);
        this.compare("trace-3.json");
    }

    @Test
    public void testDescriptor2() throws Exception {
        TestTracable tracable = TestTracable.builder().id("XX").value1("Hello").value2("There").build();
        this.trace.value(Trace.TraceLevel.INFO, tracable);
        this.trace.add(Trace.TraceLevel.DEBUG, "repeated", tracable);
        this.compare("trace-4.json");
    }

    @Test
    public void testDescriptor3() throws Exception {
        TestTracable2 tracable = TestTracable2.builder().id("1").value1("Hello").build();
        this.trace.value(Trace.TraceLevel.SUMMARY, tracable);
        this.compare("trace-5.json");
    }

    @Test
    public void testDescriptor4() throws Exception {
        TestTracable2 tracable = TestTracable2.builder().id("XX").value1("Hello").value2("There").build();
        this.trace.value(Trace.TraceLevel.INFO, tracable);
        this.trace.add(Trace.TraceLevel.TRACE.DEBUG, "repeated", tracable);
        this.compare("trace-6.json");
    }

    @Test
    public void testDescriptor5() throws Exception {
        TestTracable2 tracable = TestTracable2.builder().id("XX").value1("Hello").value2("There").build();
        this.trace.addSummary(Trace.TraceLevel.INFO, "repeated", tracable);
        this.compare("trace-7.json");
    }

    @Test
    public void testClassification1() throws Exception {
        TestClassification classification = new TestClassification();
        classification.scientificName = "Acacia dealbata";
        classification.taxonID = "ABC:123";
        classification.testEnum = TestEnum.FOO;
        this.trace.value(Trace.TraceLevel.DEBUG, classification);
        this.compare("trace-8.json");
    }


    @Test
    public void testClassification2() throws Exception {
        TestClassification classification = new TestClassification();
        classification.scientificName = "Acacia dealbata";
        classification.taxonID = "ABC:123";
        classification.testEnum = TestEnum.FOO;
        this.trace.value(Trace.TraceLevel.INFO, classification);
        this.trace.add(Trace.TraceLevel.DEBUG, "repeat", classification);
        this.compare("trace-9.json");
    }

}
