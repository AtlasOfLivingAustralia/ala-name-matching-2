package au.org.ala.bayesian.derivation;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SoundexGeneratorTest {
    private SoundexGenerator generator;

    @Before
    public void setUp() throws Exception {
        this.generator = new SoundexGenerator();
    }

    @Test
    public void textSoundex1() {
        assertEquals("H400", this.generator.soundex("Hello"));
        assertEquals("H400", this.generator.soundex("Hella"));
    }

    @Test
    public void textSoundex2() {
        assertEquals("R300K526", this.generator.soundex("Red Kangaroo"));
        assertEquals("R300K526", this.generator.soundex("Red Kangaru"));
    }

    @Test
    public void textSoundex3() {
        assertEquals("W450F422C455", this.generator.soundex("William Ffolkes-Cholmondeley"));
        assertEquals("W450F422C455", this.generator.soundex("Willam Folkes-Cholmondely"));
    }


    @Test
    public void textSoundex4() {
        assertEquals("S400", this.generator.soundex("Seelowe"));
        assertEquals("S400", this.generator.soundex("Seelöwe"));
    }

}