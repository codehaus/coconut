/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.expiration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.test.CollectionUtils;

public class AbstractExpirationTestBundle extends AbstractCacheTCKTest {

    protected void putAll(long timeout, TimeUnit unit,
            Map.Entry<Integer, String>... entries) {
        expiration().putAll(CollectionUtils.asMap(entries), timeout, unit);
    }

    protected void putAll(long timeout, Map.Entry<Integer, String>... entries) {
        putAll(timeout, TimeUnit.MILLISECONDS, entries);
    }

    protected String put(Map.Entry<Integer, String> e, long timeout, TimeUnit unit) {
        return expiration().put(e.getKey(), e.getValue(), timeout, unit);
    }

    protected String put(Map.Entry<Integer, String> e, long timeout) {
        return expiration().put(e.getKey(), e.getValue(), timeout, TimeUnit.MILLISECONDS);
    }
    
    protected void purge() {
        expiration().purgeExpired();
    }
}
