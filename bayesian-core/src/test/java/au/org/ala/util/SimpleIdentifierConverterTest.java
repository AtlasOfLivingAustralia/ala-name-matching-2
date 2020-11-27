package au.org.ala.util;

import au.org.ala.bayesian.Observable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleIdentifierConverterTest {
    @Test
    public void testConvertCamelUpper1() {
        IdentifierConverter converter = new SimpleIdentifierConverter(SimpleIdentifierConverter.Style.CAMEL_CASE, true,  false,true);
        assertEquals("Fred", converter.convert(new Observable("fred")));
        assertEquals("Fred", converter.convert(new Observable("Fred")));
        assertEquals("V1", converter.convert(new Observable("v_1")));
        assertEquals("V1", converter.convert(new Observable("v1")));
        assertEquals("VA", converter.convert(new Observable("v_a")));
        assertEquals("OneTwo", converter.convert(new Observable("oneTwo")));
        assertEquals("OneTwo", converter.convert(new Observable("one_two")));
        assertEquals("OneTwoThree", converter.convert(new Observable("one_twoThree")));
        assertEquals("OneTwoThree", converter.convert(new Observable("one_two_three")));
        assertEquals("OneTwoThree", converter.convert(new Observable("one_.two.three")));
        assertEquals("F9Red", converter.convert(new Observable("F9red")));
        assertEquals("XFred", converter.convert(new Observable("_Fred")));
        assertEquals("Assert_", converter.convert(new Observable("assert")));
    }

    @Test
    public void testConvertCamelLower1() {
        IdentifierConverter converter = new SimpleIdentifierConverter(SimpleIdentifierConverter.Style.CAMEL_CASE, false, false,true);
        assertEquals("fred", converter.convert(new Observable("fred")));
        assertEquals("fred", converter.convert(new Observable("Fred")));
        assertEquals("v1", converter.convert(new Observable("v_1")));
        assertEquals("v1", converter.convert(new Observable("v1")));
        assertEquals("vA", converter.convert(new Observable("v_a")));
        assertEquals("oneTwo", converter.convert(new Observable("oneTwo")));
        assertEquals("oneTwo", converter.convert(new Observable("one_two")));
        assertEquals("oneTwoThree", converter.convert(new Observable("one_twoThree")));
        assertEquals("oneTwoThree", converter.convert(new Observable("one_two_three")));
        assertEquals("oneTwoThree", converter.convert(new Observable("one_.two.three")));
        assertEquals("f9Red", converter.convert(new Observable("F9red")));
        assertEquals("xFred", converter.convert(new Observable("_Fred")));
    }

    @Test
    public void testConvertUnderscoreUpper1() {
        IdentifierConverter converter = new SimpleIdentifierConverter(SimpleIdentifierConverter.Style.UNDERSCORE, true, false,true);
        assertEquals("Fred", converter.convert(new Observable("fred")));
        assertEquals("Fred", converter.convert(new Observable("Fred")));
        assertEquals("V_1", converter.convert(new Observable("v_1")));
        assertEquals("V_1", converter.convert(new Observable("v1")));
        assertEquals("V_a", converter.convert(new Observable("v_a")));
        assertEquals("One_two", converter.convert(new Observable("oneTwo")));
        assertEquals("One_two", converter.convert(new Observable("one_two")));
        assertEquals("One_two_three", converter.convert(new Observable("one_twoThree")));
        assertEquals("One_two_three", converter.convert(new Observable("one_two_three")));
        assertEquals("One_two_three", converter.convert(new Observable("one_.two.three")));
        assertEquals("F_9_red", converter.convert(new Observable("F9red")));
        assertEquals("X_fred", converter.convert(new Observable("_Fred")));
        assertEquals("Class_", converter.convert(new Observable("class")));
    }

    @Test
    public void testConvertUnderscoreLower1() {
        IdentifierConverter converter = new SimpleIdentifierConverter(SimpleIdentifierConverter.Style.UNDERSCORE, false, false,false);
        assertEquals("fred", converter.convert(new Observable("fred")));
        assertEquals("fred", converter.convert(new Observable("Fred")));
        assertEquals("v_1", converter.convert(new Observable("v_1")));
        assertEquals("v_1", converter.convert(new Observable("v1")));
        assertEquals("v_a", converter.convert(new Observable("v_a")));
        assertEquals("one_two", converter.convert(new Observable("oneTwo")));
        assertEquals("one_two", converter.convert(new Observable("one_two")));
        assertEquals("one_two_three", converter.convert(new Observable("one_twoThree")));
        assertEquals("one_two_three", converter.convert(new Observable("one_two_three")));
        assertEquals("one_two_three", converter.convert(new Observable("one_.two.three")));
        assertEquals("f_9_red", converter.convert(new Observable("F9red")));
        assertEquals("class", converter.convert(new Observable("class")));
    }


    @Test
    public void testConvertUnderscoreAllUpper1() {
        IdentifierConverter converter = new SimpleIdentifierConverter(SimpleIdentifierConverter.Style.UNDERSCORE, true, true,true);
        assertEquals("FRED", converter.convert(new Observable("fred")));
        assertEquals("FRED", converter.convert(new Observable("Fred")));
        assertEquals("V_1", converter.convert(new Observable("v_1")));
        assertEquals("V_1", converter.convert(new Observable("v1")));
        assertEquals("V_A", converter.convert(new Observable("v_a")));
        assertEquals("ONE_TWO", converter.convert(new Observable("oneTwo")));
        assertEquals("ONE_TWO", converter.convert(new Observable("one_two")));
        assertEquals("ONE_TWO_THREE", converter.convert(new Observable("one_twoThree")));
        assertEquals("ONE_TWO_THREE", converter.convert(new Observable("one_two_three")));
        assertEquals("ONE_TWO_THREE", converter.convert(new Observable("one_.two.three")));
        assertEquals("F_9_RED", converter.convert(new Observable("F9red")));
        assertEquals("CLASS_", converter.convert(new Observable("class")));
    }

}
