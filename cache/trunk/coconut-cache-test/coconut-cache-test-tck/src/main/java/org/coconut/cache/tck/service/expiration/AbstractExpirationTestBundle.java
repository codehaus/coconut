package org.coconut.cache.tck.service.expiration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.coconut.test.CollectionUtils;

class AbstractExpirationTestBundle extends AbstractCacheTCKTestBundle {

    protected void putAll(long timeout, TimeUnit unit,
            Map.Entry<Integer, String>... entries) {
        expiration().putAll(CollectionUtils.asMap(entries), timeout, unit);
    }

    protected void putAll(long timeout, Map.Entry<Integer, String>... entries) {
        putAll(timeout, TimeUnit.NANOSECONDS, entries);
    }

    protected String put(Map.Entry<Integer, String> e, long timeout, TimeUnit unit) {
        return expiration().put(e.getKey(), e.getValue(), timeout, unit);
    }

    protected String put(Map.Entry<Integer, String> e, long timeout) {
        return expiration().put(e.getKey(), e.getValue(), timeout, TimeUnit.MILLISECONDS);
    }
}