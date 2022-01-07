package au.org.ala.util;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class CounterTest {
    private static final Logger logger = LoggerFactory.getLogger(CounterTest.class);
    private Counter counter;

    @Before
    public void setUp() throws Exception {
        this.counter = new Counter("Test {0} elapsed {1,number,0}, {2,number,0}/s, {3}%, last {4}", logger, 10, 10);
    }

    protected void run(int steps) throws Exception {
        for (int i = 0; i < steps; i++) {
            Thread.sleep(100);
            this.counter.increment(i);
        }
    }

    @Test
    public void testCount1() throws Exception {
        this.counter.start();
        this.run(10);
        this.counter.stop();
        assertEquals(10, this.counter.getCount());
    }


    @Test
    public void testCurrentRate1() throws Exception {
        this.counter.start();
        this.run(10);
        this.counter.stop();
        assertEquals(10.0, this.counter.getCurrentRate(), 1.0);
    }

    @Test
    public void testCurrentRate2() throws Exception {
        this.counter.start();
        this.run(9);
        assertEquals(10.0, this.counter.getCurrentRate(), 1.0);
        this.counter.stop();
    }

    @Test
    public void testBuildMessage1() throws Exception {
        this.counter.start();
        this.run(9);
        String message = this.counter.buildMessgae(System.currentTimeMillis(), null);
        this.counter.stop();
        assertEquals("Test 9 elapsed 1, 10/s, 90%, last -", message);
    }

    @Test
    public void testBuildMessage2() throws Exception {
        this.counter.start();
        this.run(35);
        String message = this.counter.buildMessgae(System.currentTimeMillis(), "Waffle");
        this.counter.stop();
        assertEquals("Test 35 elapsed 4, 10/s, 350%, last Waffle", message);
    }

    @Test
    public void testStartTime1() throws Exception {
        long start = System.currentTimeMillis();
        this.counter.start();
        this.run(10);
        this.counter.stop();
        assertTrue(this.counter.getStartTime().getTime() - start < 5);
    }

    @Test
    public void testStopTime1() throws Exception {
        this.counter.start();
        this.run(10);
        long stop = System.currentTimeMillis();
        this.counter.stop();
        assertTrue(this.counter.getStopTime().getTime() - stop < 5);
    }

}