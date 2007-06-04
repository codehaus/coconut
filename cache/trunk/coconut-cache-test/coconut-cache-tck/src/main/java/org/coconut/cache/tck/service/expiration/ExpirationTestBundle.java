/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.expiration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.tck.CacheTestBundle;
import org.coconut.cache.tck.util.IntegerToStringLoader;
import org.coconut.test.CollectionUtils;
import org.junit.Before;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ExpirationTestBundle extends CacheTestBundle {

    public static final IntegerToStringLoader DEFAULT_LOADER = new IntegerToStringLoader();

    protected Cache<Integer, String> noExpiration;

    @Before
    public void setupLoading() {
        noExpiration = c;
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        c = newCache(cc.expiration().c());
    }

    public CacheExpirationService<Integer, String> s() {
        return c.getService(CacheExpirationService.class);
    }

    public void assertNoLoadingService() {
        assertNotNull(c.getService(CacheExpirationService.class));
        assertNull(noExpiration.getService(CacheExpirationService.class));
    }

    protected void putAll(long timeout, TimeUnit unit,
            Map.Entry<Integer, String>... entries) {
        s().putAll(CollectionUtils.asMap(entries), timeout, unit);
    }

    protected void putAll(long timeout, Map.Entry<Integer, String>... entries) {
        putAll(timeout, TimeUnit.NANOSECONDS, entries);
    }

    protected String put(Map.Entry<Integer, String> e, long timeout, TimeUnit unit) {
        return s().put(e.getKey(), e.getValue(), timeout, unit);
    }

    protected String put(Map.Entry<Integer, String> e, long timeout) {
        return s().put(e.getKey(), e.getValue(), timeout, TimeUnit.MILLISECONDS);
    }

}
