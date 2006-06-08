package org.coconut.cache;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * Test cache constants.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheTest {

    @Test
    public void testConstanst() {
        assertEquals(0l, Cache.DEFAULT_EXPIRATION);
        assertEquals(Long.MAX_VALUE, Cache.NEVER_EXPIRE);
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CacheTest.class);
    }

}
