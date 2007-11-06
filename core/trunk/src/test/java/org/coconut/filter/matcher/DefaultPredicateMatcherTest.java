/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.filter.matcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.coconut.filter.StringPredicates;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultPredicateMatcherTest {

    @Test
    public void testDefaultFilterMatcher() {
        DefaultPredicateMatcher<Integer, String> m = new DefaultPredicateMatcher<Integer, String>();
        assertEquals(0, m.match("foo").size());
        assertNull(m.put(1, StringPredicates.startsWith("bo")));
        assertEquals(StringPredicates.startsWith("bo"), m.put(1, StringPredicates
                .startsWith("fo")));
        assertNull(m.put(2, StringPredicates.startsWith("f")));
        assertNull(m.put(3, StringPredicates.startsWith("foo")));
        assertNull(m.put(4, StringPredicates.startsWith("foof")));

        assertEquals(3, m.match("foo").size());
        assertTrue(m.match("foo").containsAll(Arrays.asList(1, 2, 3)));
    }

}
