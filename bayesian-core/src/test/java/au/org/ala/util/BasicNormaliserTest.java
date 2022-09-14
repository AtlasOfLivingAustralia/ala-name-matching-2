

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
    private BasicNormaliser basic = new BasicNormaliser("basic", true, false, false, false, false, false);
    private BasicNormaliser punctuation = new BasicNormaliser("punctuation", true, false, true, false, false, false);
    private BasicNormaliser scientific = new BasicNormaliser("scientific", true, false, true, true, true, false);
    private BasicNormaliser clean = new BasicNormaliser("scientific", true, true, false, true, true, false);

    @Test
    public void testNormaliseSpaces1() {
        BasicNormaliser normaliser = new BasicNormaliser("s", true, false, false, false, false, false);
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
    public void testRemovePunctuation1() {
        BasicNormaliser normaliser = new BasicNormaliser("p", false, true, false, false, false, false);
        assertEquals("Something good", normaliser.normalise("Something good"));
        assertEquals("Something    good", normaliser.normalise("   Something    good    ")); // Trims
        assertEquals("Something good", normaliser.normalise("‘Something good’"));
        assertEquals("Something good", normaliser.normalise("“Something good”"));
        assertEquals("Somethinggood", normaliser.normalise("Something—good")); // Normal dash replaces em-dash
        assertEquals("Something good α", normaliser.normalise("Something good α"));
        assertEquals("ẞomething good α", normaliser.normalise("ẞomething good α"));
        assertEquals("Garçon désolé", normaliser.normalise("Garçon désolé"));
    }

    @Test
    public void testNormalisePunctuation1() {
        BasicNormaliser normaliser = new BasicNormaliser("p", false, false, true, false, false, false);
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
        BasicNormaliser normaliser = new BasicNormaliser("s", false, false, false, true, false, false);
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
        BasicNormaliser normaliser = new BasicNormaliser("a", false, false, false, false, true, false);
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
        BasicNormaliser normaliser = new BasicNormaliser("c", false, false, false, false, false, true);
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
    public void testNormaliseClean1() {
        BasicNormaliser normaliser = new BasicNormaliser("c", false, true, false, false, false, false);
        assertEquals("Something good", normaliser.normalise("Something good"));
        assertEquals("Something    good", normaliser.normalise("   Something    good    "));
        assertEquals("Something good", normaliser.normalise("‘Something good’"));
        assertEquals("Something good", normaliser.normalise("“Something good”"));
        assertEquals("Somethinggood", normaliser.normalise("Something—good"));
        assertEquals("Something good α", normaliser.normalise("Something good α"));
        assertEquals("ßomething good α", normaliser.normalise("ßomething good α"));
        assertEquals("Garçon désolé", normaliser.normalise("Garçon désolé"));
    }

    @Test
    public void testToJson1() throws Exception {
        BasicNormaliser normaliser = new BasicNormaliser("normaliser_1", true, false, true, true, true, false);
        ObjectMapper mapper = JsonUtils.createMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, normaliser);
        // System.out.println(writer.toString());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "normaliser-1.json"), writer.toString());
    }

    @Test
    public void testFromJson1() throws Exception {
        ObjectMapper mapper = JsonUtils.createMapper();
        Normaliser normaliser = mapper.readValue(TestUtils.getResource(this.getClass(), "normaliser-1.json"), Normaliser.class);
        assertEquals("normaliser_1", normaliser.getId());
        assertEquals("SSomething GOOD alpha", normaliser.normalise("ẞomething  GOOD  α"));
    }

    @Test
    public void testFromJson2() throws Exception {
        ObjectMapper mapper = JsonUtils.createMapper();
        Normaliser normaliser = mapper.readValue(TestUtils.getResource(this.getClass(), "normaliser-2.json"), Normaliser.class);
        assertEquals("normaliser_2", normaliser.getId());
        assertEquals("SSomething  GOOD   alpha", normaliser.normalise("ẞomething  GOOD  α"));
    }


    @Test
    public void testBasicnormaliser1() {
        assertEquals("Ozothamnus diosmifolius", this.basic.normalise("Ozothamnus diosmifolius"));
        assertEquals("Ozothamnus diosmifolius", this.punctuation.normalise("Ozothamnus diosmifolius"));
        assertEquals("Ozothamnus diosmifolius", this.scientific.normalise("Ozothamnus diosmifolius"));
    }

    @Test
    public void testBasicnormaliser2() {
        assertEquals("Ozothamnus diosmifolius", this.basic.normalise(" Ozothamnus     diosmifolius   "));
        assertEquals("Ozothamnus diosmifolius", this.punctuation.normalise(" Ozothamnus     diosmifolius   "));
        assertEquals("Ozothamnus diosmifolius", this.scientific.normalise(" Ozothamnus     diosmifolius   "));
    }

    @Test
    public void testBasicnormaliser3() {
        assertEquals("Ozothamnus diosmifolius 'Adelaide White'", this.basic.normalise(" Ozothamnus     diosmifolius  'Adelaide White' "));
        assertEquals("Ozothamnus diosmifolius 'Adelaide White'", this.punctuation.normalise(" Ozothamnus     diosmifolius  'Adelaide White' "));
        assertEquals("Ozothamnus diosmifolius 'Adelaide White'", this.scientific.normalise(" Ozothamnus     diosmifolius  'Adelaide White' "));
    }

    @Test
    public void testBasicnormaliser4() {
        String name = "Ozothamnus\u00a0diosmifolius";
        assertEquals("Ozothamnus diosmifolius", this.basic.normalise(name));
        assertEquals("Ozothamnus diosmifolius", this.punctuation.normalise(name));
        assertEquals("Ozothamnus diosmifolius", this.scientific.normalise(name));
    }

    @Test
    public void testPunctnormaliser1() {
        String name = "Ozothamnus diosmifolius \u2018Adelaide White\u2019";
        assertEquals("Ozothamnus diosmifolius \u2018Adelaide White\u2019", this.basic.normalise(name));
        assertEquals("Ozothamnus diosmifolius 'Adelaide White'", this.punctuation.normalise(name));
        assertEquals("Ozothamnus diosmifolius 'Adelaide White'", this.scientific.normalise(name));
    }

    @Test
    public void testPunctnormaliser2() {
        String name = "Ozothamnus diosmifolius \u201cAdelaide White\u201d";
        assertEquals("Ozothamnus diosmifolius \u201cAdelaide White\u201d", this.basic.normalise(name));
        assertEquals("Ozothamnus diosmifolius \"Adelaide White\"", this.punctuation.normalise(name));
        assertEquals("Ozothamnus diosmifolius \"Adelaide White\"", this.scientific.normalise(name));
    }

    @Test
    public void testPunctnormaliser3() {
        String name = "Bernhardia novae\u2010hollandiae";
        assertEquals("Bernhardia novae\u2010hollandiae", this.basic.normalise(name));
        assertEquals("Bernhardia novae\u002dhollandiae", this.punctuation.normalise(name));
        assertEquals("Bernhardia novae\u002dhollandiae", this.scientific.normalise(name));
    }

    @Test
    public void testPunctnormaliser4() {
        String name = "Oribatida \u2013 astigmata";
        assertEquals("Oribatida \u2013 astigmata", this.basic.normalise(name));
        assertEquals("Oribatida - astigmata", this.punctuation.normalise(name));
        assertEquals("Oribatida - astigmata", this.scientific.normalise(name));
    }

    @Test
    public void testPunctnormaliser5() {
        String name = "Olearia\u00a0\u00d7matthewsii";
        assertEquals("Olearia \u00d7matthewsii", this.basic.normalise(name));
        assertEquals("Olearia \u00d7matthewsii", this.punctuation.normalise(name));
        assertEquals("Olearia x matthewsii", this.scientific.normalise(name));
    }

    @Test
    public void testAccentednormaliser1() {
        String name = "Aleochara haemorrho\u00efdalis";
        assertEquals("Aleochara haemorrho\u00efdalis", this.basic.normalise(name));
        assertEquals("Aleochara haemorrho\u00efdalis", this.punctuation.normalise(name));
        assertEquals("Aleochara haemorrhoidalis", this.scientific.normalise(name));
     }

    @Test
    public void testAccentednormaliser2() {
        String name = "Staurastrum subbr\u00e9bissonii";
        assertEquals("Staurastrum subbr\u00e9bissonii", this.basic.normalise(name));
        assertEquals("Staurastrum subbr\u00e9bissonii", this.punctuation.normalise(name));
        assertEquals("Staurastrum subbrebissonii", this.scientific.normalise(name));
    }

    @Test
    public void testGreeknormaliser1() {
        String name = "Senecio banksii var. \u03b2 velleia";
        assertEquals("Senecio banksii var. \u03b2 velleia", this.basic.normalise(name));
        assertEquals("Senecio banksii var. \u03b2 velleia", this.punctuation.normalise(name));
        assertEquals("Senecio banksii var. beta velleia", this.scientific.normalise(name));
    }

}
