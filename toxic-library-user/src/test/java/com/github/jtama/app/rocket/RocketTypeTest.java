package com.github.jtama.app.rocket;

import org.junit.Test;
import static org.junit.Assert.*;

public class RocketTypeTest {
    @Test
    public void testRocketTypeEnum() {
        assertEquals("explosive", RocketType.EXPLOSIVE.getLuxury());
        assertEquals("first class", RocketType.FIRST_CLASS.getLuxury());
        assertEquals("luxury", RocketType.LUXURY.getLuxury());
    }

    @Test
    public void testRocketTypeIs() {
        assertTrue(RocketType.LUXURY.is(RocketType.EXPLOSIVE) > 0);
        assertTrue(RocketType.EXPLOSIVE.is(RocketType.LUXURY) < 0);
        assertEquals(0, RocketType.LUXURY.is(RocketType.LUXURY));
    }
}
