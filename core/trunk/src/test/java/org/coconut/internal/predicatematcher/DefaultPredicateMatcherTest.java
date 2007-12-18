/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.predicatematcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.coconut.operations.StringPredicates;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DefaultPredicateMatcherTest {

    @Test
    public void testDefaultFilterMatcher() {
        DefaultPredicateMatcher<Integer, String> m = new DefaultPredicateMatcher<Integer, String>();
        assertEquals(0, m.match("foo").size());
        assertNull(m.put(1, StringPredicates.startsWith("bo")));
        assertEquals(StringPredicates.startsWith("bo"), m.put(1, StringPredicates.startsWith("fo")));
        assertNull(m.put(2, StringPredicates.startsWith("f")));
        assertNull(m.put(3, StringPredicates.startsWith("foo")));
        assertNull(m.put(4, StringPredicates.startsWith("foof")));

        assertEquals(3, m.match("foo").size());
        assertTrue(m.match("foo").containsAll(Arrays.asList(1, 2, 3)));
    }

    @Test
    public void testDefaultFilterMatcher2() {
        DefaultPredicateMatcher<Integer, String> m = new DefaultPredicateMatcher<Integer, String>();
        assertNull(m.put(1, StringPredicates.startsWith("f")));
        assertNull(m.put(2, StringPredicates.startsWith("fo")));
        assertNull(m.put(3, StringPredicates.startsWith("foo")));
        assertNull(m.put(4, StringPredicates.startsWith("foof")));
        final List<Integer> l = new ArrayList<Integer>();
        m.matchAndHandle(new PredicateMatcherHandler<Integer, String>() {
            public void handle(Integer key, String object) {
                l.add(key);
                assertEquals(object, "foo");
            }
        }, "foo");
        assertEquals(3, l.size());
    }
}
