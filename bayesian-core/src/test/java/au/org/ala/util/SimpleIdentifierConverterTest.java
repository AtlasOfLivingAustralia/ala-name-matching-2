package au.org.ala.util;

import au.org.ala.bayesian.Observable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleIdentifierConverterTest {
    @Test
    public void testConvertCamelUpper1() {
        IdentifierConverter converter = new SimpleIdentifierConverter(SimpleIdentifierConverter.Style.CAMEL_CASE, true,  false,true);
        assertEquals("Fred", converter.convert(Observable.string("fred")));
        assertEquals("Fred", converter.convert(Observable.string("Fred")));
        assertEquals("V1", converter.convert(Observable.string("v_1")));
        assertEquals("V1", converter.convert(Observable.string("v1")));
        assertEquals("VA", converter.convert(Observable.string("v_a")));
        assertEquals("OneTwo", converter.convert(Observable.string("oneTwo")));
        assertEquals("OneTwo", converter.convert(Observable.string("one_two")));
        assertEquals("OneTwoThree", converter.convert(Observable.string("one_twoThree")));
        assertEquals("OneTwoThree", converter.convert(Observable.string("one_two_three")));
        assertEquals("OneTwoThree", converter.convert(Observable.string("one_.two.three")));
        assertEquals("F9Red", converter.convert(Observable.string("F9red")));
        assertEquals("XFred", converter.convert(Observable.string("_Fred")));
        assertEquals("Assert_", converter.convert(Observable.string("assert")));
    }

    @Test
    public void testConvertCamelLower1() {
        IdentifierConverter converter = new SimpleIdentifierConverter(SimpleIdentifierConverter.Style.CAMEL_CASE, false, false,true);
        assertEquals("fred", converter.convert(Observable.string("fred")));
        assertEquals("fred", converter.convert(Observable.string("Fred")));
        assertEquals("v1", converter.convert(Observable.string("v_1")));
        assertEquals("v1", converter.convert(Observable.string("v1")));
        assertEquals("vA", converter.convert(Observable.string("v_a")));
        assertEquals("oneTwo", converter.convert(Observable.string("oneTwo")));
        assertEquals("oneTwo", converter.convert(Observable.string("one_two")));
        assertEquals("oneTwoThree", converter.convert(Observable.string("one_twoThree")));
        assertEquals("oneTwoThree", converter.convert(Observable.string("one_two_three")));
        assertEquals("oneTwoThree", converter.convert(Observable.string("one_.two.three")));
        assertEquals("f9Red", converter.convert(Observable.string("F9red")));
        assertEquals("xFred", converter.convert(Observable.string("_Fred")));
    }

    @Test
    public void testConvertUnderscoreUpper1() {
        IdentifierConverter converter = new SimpleIdentifierConverter(SimpleIdentifierConverter.Style.UNDERSCORE, true, false,true);
        assertEquals("Fred", converter.convert(Observable.string("fred")));
        assertEquals("Fred", converter.convert(Observable.string("Fred")));
        assertEquals("V_1", converter.convert(Observable.string("v_1")));
        assertEquals("V_1", converter.convert(Observable.string("v1")));
        assertEquals("V_a", converter.convert(Observable.string("v_a")));
        assertEquals("One_two", converter.convert(Observable.string("oneTwo")));
        assertEquals("One_two", converter.convert(Observable.string("one_two")));
        assertEquals("One_two_three", converter.convert(Observable.string("one_twoThree")));
        assertEquals("One_two_three", converter.convert(Observable.string("one_two_three")));
        assertEquals("One_two_three", converter.convert(Observable.string("one_.two.three")));
        assertEquals("F_9_red", converter.convert(Observable.string("F9red")));
        assertEquals("X_fred", converter.convert(Observable.string("_Fred")));
        assertEquals("Class_", converter.convert(Observable.string("class")));
    }

    @Test
    public void testConvertUnderscoreLower1() {
        IdentifierConverter converter = new SimpleIdentifierConverter(SimpleIdentifierConverter.Style.UNDERSCORE, false, false,false);
        assertEquals("fred", converter.convert(Observable.string("fred")));
        assertEquals("fred", converter.convert(Observable.string("Fred")));
        assertEquals("v_1", converter.convert(Observable.string("v_1")));
        assertEquals("v_1", converter.convert(Observable.string("v1")));
        assertEquals("v_a", converter.convert(Observable.string("v_a")));
        assertEquals("one_two", converter.convert(Observable.string("oneTwo")));
        assertEquals("one_two", converter.convert(Observable.string("one_two")));
        assertEquals("one_two_three", converter.convert(Observable.string("one_twoThree")));
        assertEquals("one_two_three", converter.convert(Observable.string("one_two_three")));
        assertEquals("one_two_three", converter.convert(Observable.string("one_.two.three")));
        assertEquals("f_9_red", converter.convert(Observable.string("F9red")));
        assertEquals("class", converter.convert(Observable.string("class")));
    }


    @Test
    public void testConvertUnderscoreAllUpper1() {
        IdentifierConverter converter = new SimpleIdentifierConverter(SimpleIdentifierConverter.Style.UNDERSCORE, true, true,true);
        assertEquals("FRED", converter.convert(Observable.string("fred")));
        assertEquals("FRED", converter.convert(Observable.string("Fred")));
        assertEquals("V_1", converter.convert(Observable.string("v_1")));
        assertEquals("V_1", converter.convert(Observable.string("v1")));
        assertEquals("V_A", converter.convert(Observable.string("v_a")));
        assertEquals("ONE_TWO", converter.convert(Observable.string("oneTwo")));
        assertEquals("ONE_TWO", converter.convert(Observable.string("one_two")));
        assertEquals("ONE_TWO_THREE", converter.convert(Observable.string("one_twoThree")));
        assertEquals("ONE_TWO_THREE", converter.convert(Observable.string("one_two_three")));
        assertEquals("ONE_TWO_THREE", converter.convert(Observable.string("one_.two.three")));
        assertEquals("F_9_RED", converter.convert(Observable.string("F9red")));
        assertEquals("CLASS_", converter.convert(Observable.string("class")));
    }

}
