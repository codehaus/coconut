/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.expiration;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.tck.CacheTestBundle;
import org.coconut.cache.tck.util.AsyncIntegerToStringLoader;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EvictRefresh extends CacheTestBundle {
    /**
     * Test refresh window
     */
    @SuppressWarnings("unchecked")
    @Test
    public void refreshWindowSingleElement() throws Exception {
        AsyncIntegerToStringLoader loader = new AsyncIntegerToStringLoader();
        c = newCache(newConf().setClock(clock).expiration().setRefreshInterval(2,
                TimeUnit.NANOSECONDS).c().backend().setBackend(loader).c());
        c.put(M1.getKey(), "AB1", 2, TimeUnit.NANOSECONDS);
        c.put(M2.getKey(), "AB2", 3, TimeUnit.NANOSECONDS);
        c.put(M3.getKey(), "AB3", 4, TimeUnit.NANOSECONDS);
        c.put(M4.getKey(), "AB4", 7, TimeUnit.NANOSECONDS);

        incTime(); // time is one
        getAll(M1, M2, M3, M4);
        // test no refresh on get
        assertEquals(0, loader.getLoadedKeys().size());
        c.evict();
        assertEquals(2, loader.getLoadedKeys().size());
        waitAndAssertGet(M1, M2);
        assertEquals("AB3", get(M3));
        assertEquals("AB4", get(M4));
    }
    
    /**
     * Test refresh window
     */
    @SuppressWarnings("unchecked")
    @Test
    public void refreshWindowSingleElementEvict() throws Exception {
        AsyncIntegerToStringLoader loader = new AsyncIntegerToStringLoader();
        c = newCache(newConf().setClock(clock).expiration().setRefreshInterval(2,
                TimeUnit.NANOSECONDS).c().backend().setBackend(loader).c());
        c.put(M1.getKey(), "AB1", 2, TimeUnit.NANOSECONDS);
        c.put(M2.getKey(), "AB2", 3, TimeUnit.NANOSECONDS);
        c.put(M3.getKey(), "AB3", 4, TimeUnit.NANOSECONDS);
        c.put(M4.getKey(), "AB4", 7, TimeUnit.NANOSECONDS);

        incTime(); // time is one
        // test no refresh on get
        c.evict();
        assertEquals(2, loader.getLoadedKeys().size());
        waitAndAssertGet(M1, M2);

        assertEquals("AB3", get(M3));
        assertEquals("AB4", get(M4));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void refreshWindowSingleElementGet() throws Exception {
        AsyncIntegerToStringLoader loader = new AsyncIntegerToStringLoader();
        c = newCache(newConf().setClock(clock).expiration().setRefreshInterval(2,
                TimeUnit.NANOSECONDS).c().backend().setBackend(loader).c());
        c.put(M1.getKey(), "AB1", 2, TimeUnit.NANOSECONDS);
        c.put(M2.getKey(), "AB2", 3, TimeUnit.NANOSECONDS);
        c.put(M3.getKey(), "AB3", 4, TimeUnit.NANOSECONDS);
        c.put(M4.getKey(), "AB4", 7, TimeUnit.NANOSECONDS);

        incTime(); // time is one
        // test no refresh on get
        getAll(M1, M2, M3, M4);
        assertEquals(2, loader.getLoadedKeys().size());
        waitAndAssertGet(M1, M2);
        assertEquals("AB3", get(M3));
        assertEquals("AB4", get(M4));
    }
    
}
