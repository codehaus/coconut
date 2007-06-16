/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.test.util;

import java.util.concurrent.atomic.AtomicLong;

import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.core.AttributeMap;

/**
 * A simple cache loader used for testing. Will return 1->A, 2->B, 3->C, 4->D, 5->E and
 * <code>null</code> for any other key.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: IntegerToStringLoader.java 341 2007-06-10 09:54:42Z kasper $
 */
public class IntegerToStringLoader implements CacheLoader<Integer, String> {

    private final AtomicLong totalLoads = new AtomicLong();

    public long getNumberOfLoads() {
        return totalLoads.get();
    }
    /**
     * @see org.coconut.cache.service.loading.CacheLoader#load(java.lang.Object,
     *      org.coconut.core.AttributeMap)
     */
    public String load(Integer key, AttributeMap ignore) throws Exception {
        totalLoads.incrementAndGet();
        if (1 <= key && key <= 5) {
            return "" + (char) (key + 64);
        } else {
            return null;
        }
    }
}
