/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.util;

import static org.coconut.test.CollectionUtils.M3;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.coconut.cache.CacheEntry;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: ExpirationFilter.java 142 2006-10-18 19:02:07Z kasper $
 */
public class CacheEntryFilter implements Filter<CacheEntry<Integer, String>> {

    private volatile boolean accept = false;

    private volatile CacheEntry<Integer, String> lastEntry;

    /**
     * @see org.coconut.filter.Filter#accept(E)
     */
    public boolean accept(CacheEntry<Integer, String> element) {
        lastEntry = element;
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    public CacheEntry<Integer, String> getLastEntry() {
        return lastEntry;
    }
    
    public void assertLastEquals(Map.Entry<Integer, String> e) {
        assertEquals(e.getKey(), lastEntry.getKey());
        assertEquals(e.getValue(), lastEntry.getValue());
    }
}
