/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.service.eviction;

import static org.coconut.test.CollectionUtils.M1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.policy.paging.LRUPolicy;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Tests that an instance of a CacheEntry is passed to the specified cache policy.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheEntryToPolicy extends AbstractCacheTCKTest {

    private BlockingQueue<Object> q = new LinkedBlockingQueue<Object>();

    @SuppressWarnings("unchecked")
    @Test
    public void isCacheEntry() throws InterruptedException {
        c = newCache(newConf().eviction().setPolicy(new PolicyMock()).setMaximumSize(5)
                .c());

        c.put(M1.getKey(), M1.getValue());

        Object o = q.poll(1, TimeUnit.SECONDS);
        assertNotNull("No object was handed off to the queue from PolicyMock", o);
        assertTrue(o instanceof CacheEntry);
        CacheEntry<Integer, String> ce = (CacheEntry<Integer, String>) o;
        assertSame(M1.getKey(), ce.getKey());
        assertSame(M1.getValue(), ce.getValue());
    }

    @SuppressWarnings( { "serial", "unchecked" })
    class PolicyMock extends LRUPolicy {
        @Override
        public int add(Object data) {
            q.add(data);
            return super.add(data);
        }
    }
}
