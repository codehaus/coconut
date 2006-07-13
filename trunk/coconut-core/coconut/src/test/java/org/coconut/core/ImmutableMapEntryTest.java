package org.coconut.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestResult;

import org.coconut.test.MavenDummyTest;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class ImmutableMapEntryTest extends MavenDummyTest {

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ImmutableMapEntryTest.class);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testMapEntryTest() {
        
        Map.Entry<Integer, Integer> me = EventHandlers.newMapEntry(
                0, 1);
        assertEquals(0, me.getKey());
        assertEquals(1, me.getValue());
        me.toString(); // doesn't throw exception
        me.setValue(2); //throws UnsupportedOperationException
    }

    @Test
    public void testHashcode() {
        assertEquals(0, EventHandlers.newMapEntry(null, null)
                .hashCode());
        assertEquals(100 ^ 200, EventHandlers.newMapEntry(100,
                200).hashCode());
    }

    @Test
    public void testEquals() {
        Map.Entry me = EventHandlers.newMapEntry(0, 1);
        assertFalse(me.equals(null));
        assertFalse(me.equals(new Object()));
        assertTrue(me.equals(me));
        assertFalse(me.equals(EventHandlers.newMapEntry(0, 0)));
        assertFalse(me.equals(EventHandlers.newMapEntry(0, null)));
        assertFalse(me.equals(EventHandlers.newMapEntry(1, 1)));
        assertFalse(me.equals(EventHandlers.newMapEntry(null, 1)));
        assertTrue(me.equals(EventHandlers.newMapEntry(0, 1)));
    }

    /**
     * @see junit.framework.Test#countTestCases()
     */
    public int countTestCases() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see junit.framework.Test#run(junit.framework.TestResult)
     */
    public void run(TestResult arg0) {
       throw new UnsupportedOperationException();
    }

}
