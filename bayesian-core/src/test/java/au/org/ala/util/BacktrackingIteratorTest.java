package au.org.ala.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;

import static org.junit.Assert.*;

public class BacktrackingIteratorTest {
    @Test
    public void testEnumerate1() throws Exception {
        BacktrackingIterator<Integer> bi = new BacktrackingIterator<>(
            0,
            Arrays.asList(
                Arrays.asList(null, i -> i + 1),
                Arrays.asList(null, i -> i + 1)
            )
        );
        assertTrue(bi.hasNext());
        assertEquals(0, bi.next().intValue());
        assertTrue(bi.hasNext());
        assertEquals(1, bi.next().intValue());
        assertTrue(bi.hasNext());
        assertEquals(1, bi.next().intValue());
        assertTrue(bi.hasNext());
        assertEquals(2, bi.next().intValue());
        assertFalse(bi.hasNext());
    }

    @Test
    public void testEnumerate2() throws Exception {
        BacktrackingIterator<Integer> bi = new BacktrackingIterator<>(
            0,
            Arrays.asList(
                Arrays.asList(i -> i + 1, i -> i + 2),
                Arrays.asList(null, i -> i + 3)
            )
        );
        assertTrue(bi.hasNext());
        assertEquals(1, bi.next().intValue());
        assertTrue(bi.hasNext());
        assertEquals(4, bi.next().intValue());
        assertTrue(bi.hasNext());
        assertEquals(2, bi.next().intValue());
        assertTrue(bi.hasNext());
        assertEquals(5, bi.next().intValue());
        assertFalse(bi.hasNext());
    }

    @Test
    public void testEnumerate3() throws Exception {
        BacktrackingIterator<Integer> bi = new BacktrackingIterator<>(
            0,
            Arrays.asList(
                Collections.singletonList(null)
            )
        );
        assertTrue(bi.hasNext());
        assertEquals(0, bi.next().intValue());
        assertFalse(bi.hasNext());
    }

    @Test
    public void testEnumerate4() throws Exception {
        BacktrackingIterator<String> bi = new BacktrackingIterator<>(
            "",
            Arrays.asList(
                Arrays.asList(null, i -> i + "a"),
                Arrays.asList(null, i -> i + "b")
            )
        );
        assertTrue(bi.hasNext());
        assertEquals("", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("b", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("a", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("ab", bi.next());
        assertFalse(bi.hasNext());
    }


    @Test
    public void testEnumerate5() throws Exception {
        BacktrackingIterator<String> bi = new BacktrackingIterator<>(
            "a",
            Arrays.asList(
                Arrays.asList(null, i -> i + "a"),
                Arrays.asList(null, i -> i + "b", i -> i + "c")
            )
        );
        assertTrue(bi.hasNext());
        assertEquals("a", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("ab", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("ac", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("aa", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("aab", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("aac", bi.next());
        assertFalse(bi.hasNext());
    }


    @Test
    public void testEnumerate6() throws Exception {
        BacktrackingIterator<String> bi = new BacktrackingIterator<>(
                "a",
                Arrays.asList(
                        Arrays.asList(null, i -> i, i -> i + "a"),
                        Arrays.asList(null, i -> i + "b", i -> i + "c")
                )
        );
        assertTrue(bi.hasNext());
        assertEquals("a", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("ab", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("ac", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("a", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("ab", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("ac", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("aa", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("aab", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("aac", bi.next());
        assertFalse(bi.hasNext());
    }

}
