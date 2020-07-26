

package au.org.ala.util;

import au.org.ala.bayesian.Normaliser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

/**
 * Test cases for cleaned scientific names
 */
public class BasicNormaliserTest {

    @Test
    public void testNormaliseSpaces1() {
        BasicNormaliser normaliser = new BasicNormaliser("s", true, false, false, false, false);
        assertEquals("Something good", normaliser.normalise("Something good"));
        assertEquals("Something good", normaliser.normalise("   Something    good    "));
        assertEquals("‘Something good’", normaliser.normalise("‘Something good’"));
        assertEquals("“Something good”", normaliser.normalise("“Something good”"));
        assertEquals("Something—good", normaliser.normalise("Something—good"));
        assertEquals("Something good α", normaliser.normalise("Something good α"));
        assertEquals("ẞomething good α", normaliser.normalise("ẞomething good α"));
        assertEquals("Garçon désolé", normaliser.normalise("Garçon désolé"));
    }

    @Test
    public void testNormalisePunctuation1() {
        BasicNormaliser normaliser = new BasicNormaliser("p", false, true, false, false, false);
        assertEquals("Something good", normaliser.normalise("Something good"));
        assertEquals("Something    good", normaliser.normalise("   Something    good    ")); // Trims
        assertEquals("'Something good'", normaliser.normalise("‘Something good’"));
        assertEquals("\"Something good\"", normaliser.normalise("“Something good”"));
        assertEquals("Something-good", normaliser.normalise("Something—good")); // Normal dash replaces em-dash
        assertEquals("Something good α", normaliser.normalise("Something good α"));
        assertEquals("ẞomething good α", normaliser.normalise("ẞomething good α"));
        assertEquals("Garçon désolé", normaliser.normalise("Garçon désolé"));
   }

    @Test
    public void testNormaliseSymbols1() {
        BasicNormaliser normaliser = new BasicNormaliser("s", false, false, true, false, false);
        assertEquals("Something good", normaliser.normalise("Something good"));
        assertEquals("Something    good", normaliser.normalise("   Something    good    "));
        assertEquals("‘Something good’", normaliser.normalise("‘Something good’"));
        assertEquals("“Something good”", normaliser.normalise("“Something good”"));
        assertEquals("Something—good", normaliser.normalise("Something—good"));
        assertEquals("Something good  alpha", normaliser.normalise("Something good α"));
        assertEquals("SSomething good  alpha", normaliser.normalise("ẞomething good α"));
        assertEquals("Garçon désolé", normaliser.normalise("Garçon désolé"));
    }

    @Test
    public void testNormaliseAccents1() {
        BasicNormaliser normaliser = new BasicNormaliser("a", false, false, false, true, false);
        assertEquals("Something good", normaliser.normalise("Something good"));
        assertEquals("Something    good", normaliser.normalise("   Something    good    "));
        assertEquals("‘Something good’", normaliser.normalise("‘Something good’"));
        assertEquals("“Something good”", normaliser.normalise("“Something good”"));
        assertEquals("Something—good", normaliser.normalise("Something—good"));
        assertEquals("Something good α", normaliser.normalise("Something good α"));
        assertEquals("ẞomething good α", normaliser.normalise("ẞomething good α"));
        assertEquals("Garcon desole", normaliser.normalise("Garçon désolé"));
    }

    @Test
    public void testNormaliseCase1() {
        BasicNormaliser normaliser = new BasicNormaliser("c", false, false, false, false, true);
        assertEquals("something good", normaliser.normalise("Something good"));
        assertEquals("something    good", normaliser.normalise("   Something    good    "));
        assertEquals("‘something good’", normaliser.normalise("‘Something good’"));
        assertEquals("“something good”", normaliser.normalise("“Something good”"));
        assertEquals("something—good", normaliser.normalise("Something—good"));
        assertEquals("something good α", normaliser.normalise("Something good α"));
        assertEquals("ßomething good α", normaliser.normalise("ẞomething good α"));
        assertEquals("garçon désolé", normaliser.normalise("Garçon désolé"));
    }

    @Test
    public void testNormaliseAll1() {
        BasicNormaliser normaliser = new BasicNormaliser();
        assertEquals("something good", normaliser.normalise("Something good"));
        assertEquals("something good", normaliser.normalise("   Something    good    "));
        assertEquals("'something good'", normaliser.normalise("‘Something good’"));
        assertEquals("\"something good\"", normaliser.normalise("“Something good”"));
        assertEquals("something-good", normaliser.normalise("Something—good"));
        assertEquals("something good alpha", normaliser.normalise("Something good α"));
        assertEquals("ssomething good alpha", normaliser.normalise("ßomething good α"));
        assertEquals("garcon desole", normaliser.normalise("Garçon désolé"));
    }

    @Test
    public void testToJson1() throws Exception {
        BasicNormaliser normaliser = new BasicNormaliser("normaliser_1", true, true, true, true, false);
        ObjectMapper mapper = TestUtils.createMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, normaliser);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "normaliser-1.json"), writer.toString());
    }

    @Test
    public void testFromJson1() throws Exception {
        ObjectMapper mapper = TestUtils.createMapper();
        Normaliser normaliser = mapper.readValue(TestUtils.getResource(this.getClass(), "normaliser-1.json"), Normaliser.class);
        assertEquals("normaliser_1", normaliser.getId());
        assertEquals("SSomething GOOD alpha", normaliser.normalise("ẞomething  GOOD  α"));
    }

    @Test
    public void testFromJson2() throws Exception {
        ObjectMapper mapper = TestUtils.createMapper();
        Normaliser normaliser = mapper.readValue(TestUtils.getResource(this.getClass(), "normaliser-2.json"), Normaliser.class);
        assertEquals("normaliser_2", normaliser.getId());
        assertEquals("SSomething  GOOD   alpha", normaliser.normalise("ẞomething  GOOD  α"));
    }

}
