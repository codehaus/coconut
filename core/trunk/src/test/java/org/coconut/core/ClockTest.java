/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.core;

import static org.coconut.test.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.coconut.core.Clock.DeterministicClock;
import org.junit.Test;
/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ClockTest {

    @Test
    public void testDefaultClock() {
        Clock c = Clock.DEFAULT_CLOCK;
        assertTrue(System.nanoTime() <= c.relativeTime());
        assertTrue(c.relativeTime() <= System.nanoTime());

        assertTrue(System.currentTimeMillis() <= c.timestamp());
        assertTrue(c.timestamp() <= System.currentTimeMillis());

        long now = System.currentTimeMillis();
        long deadline = c.getDeadlineFromNow(1, TimeUnit.SECONDS);
        assertTrue(now + 1000 <= deadline);
        assertTrue(deadline <= System.currentTimeMillis() + 1000);

        assertTrue(c.isPassed(c.timestamp()));
        
        assertIsSerializable(c);
    }

    @Test
    public void testDeterministiskClock() {
        DeterministicClock c = new Clock.DeterministicClock();
        assertEquals(0l, c.relativeTime());
        assertEquals(0l, c.timestamp());
        c.setRelativeTime(100);
        c.setTimestamp(200);

        assertEquals(100l, c.relativeTime());
        assertEquals(200l, c.timestamp());
        
        c.incrementRelativeTime();
        c.incrementTimestamp();

        assertEquals(101l, c.relativeTime());
        assertEquals(201l, c.timestamp());
        
        c.incrementRelativeTime(10);
        c.incrementTimestamp(10);
        
        assertEquals(111l, c.relativeTime());
        assertEquals(211l, c.timestamp());

        assertIsSerializable(c);
    }
}
