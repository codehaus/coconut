/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.coconut.test.MockTestCase;
import org.jmock.Mock;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@SuppressWarnings("unchecked")
public class Caches_UnmodifiableCacheTest extends MockTestCase {
    
    @Test
    public void testUnmodifiableCache() {
        Mock m = mock(Cache.class);
        Cache c = Caches.unmodifiableCache((Cache) m.proxy());

        try {
            c.clear();
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
        try {
            c.evict();
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
        try {
            c.getEventBus();
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
        try {
            c.load("");
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
        try {
            c.loadAll(new LinkedList());
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
        try {
            c.put("", "");
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
        try {
            c.put("", "", 1, TimeUnit.MICROSECONDS);
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
        try {
            c.putAll(new HashMap());
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
        try {
            c.putAll(new HashMap(), 1, TimeUnit.MICROSECONDS);
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
        try {
            c.putIfAbsent("", "");
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
        try {
            c.remove("");
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
        try {
            c.remove("", "");
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
        try {
            c.replace("", "");
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
        try {
            c.replace("", "", "");
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
        try {
            c.resetStatistics();
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }


    }

    @Test
    public void testCollectionViews() {
        Mock m = mock(Cache.class);
        Cache c = Caches.unmodifiableCache((Cache) m.proxy());
        
        /* Collection Views */
        m.expects(once()).method("entrySet").will(returnValue(new HashSet()));
        try {
            c.entrySet().remove("");
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
        m.expects(once()).method("keySet").will(returnValue(new HashSet()));
        try {
            c.keySet().remove("");
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
        m.expects(once()).method("values").will(returnValue(new ArrayList()));
        try {
            c.values().remove("");
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
    }
    
    @Test
    public void testGet() {
        Mock m = mock(Cache.class);
        Cache c = Caches.unmodifiableCache((Cache) m.proxy());
        
        m.expects(once()).method("peek").with(eq(0)).will(returnValue(1));
        assertEquals(1, c.get(0));
        CacheEntry result = (CacheEntry) mock(CacheEntry.class).proxy();
        m.expects(once()).method("peekEntry").with(eq(0)).will(returnValue(result));
        assertEquals(result, c.getEntry(0));
        try {
            c.getAll(null);
            shouldThrow();
        } catch (NullPointerException e) { /* okay */
        }

        m.expects(once()).method("peek").with(eq(3)).will(returnValue(4));
        m.expects(once()).method("peek").with(eq(6)).will(returnValue(9));
        HashMap hm = new HashMap();
        hm.put(3, 4);
        hm.put(6, 9);
        assertEquals(hm, c.getAll(Arrays.asList(3, 6)));
    }
}
