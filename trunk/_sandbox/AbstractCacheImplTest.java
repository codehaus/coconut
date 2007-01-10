/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck;

import java.io.Serializable;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestSuite;

import org.coconut.annotation.ThreadSafe;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.spi.CacheSupport;
import org.coconut.cache.tck.core.BasicCache;
import org.coconut.cache.tck.core.BasicMap;
import org.coconut.cache.tck.core.ClearRemove;
import org.coconut.cache.tck.core.ConcurrentMap;
import org.coconut.cache.tck.core.Constructors;
import org.coconut.cache.tck.core.EntrySet;
import org.coconut.cache.tck.core.EntrySetModifying;
import org.coconut.cache.tck.core.KeySet;
import org.coconut.cache.tck.core.KeySetModifying;
import org.coconut.cache.tck.core.Put;
import org.coconut.cache.tck.core.PutTimeoutable;
import org.coconut.cache.tck.core.Values;
import org.coconut.cache.tck.core.ValuesModifying;
import org.coconut.cache.tck.eventbus.EventBusFeature;
import org.coconut.cache.tck.eventbus.NoEventBusSupport;
import org.coconut.cache.tck.eviction.CacheEntryToPolicy;
import org.coconut.cache.tck.eviction.SerializablePolicyEviction;
import org.coconut.cache.tck.eviction.SimplePolicyEviction;
import org.coconut.cache.tck.expiration.ExpirationCommon;
import org.coconut.cache.tck.expiration.ExpirationConcurrent;
import org.coconut.cache.tck.expiration.ExpirationLazySingleThreaded;
import org.coconut.cache.tck.expiration.ExpirationOnEvict;
import org.coconut.cache.tck.expiration.ExpirationStrict;
import org.coconut.cache.tck.loading.ConcurrentLoading;
import org.coconut.cache.tck.loading.ExtendedCacheLoader;
import org.coconut.cache.tck.loading.FutureLoading;
import org.coconut.cache.tck.loading.Loading;
import org.coconut.cache.tck.loading.NoLoadingSupport;
import org.coconut.cache.tck.other.HitStat;
import org.coconut.cache.tck.other.NoHitStat;
import org.coconut.cache.tck.other.NoSerialization;
import org.coconut.cache.tck.other.Serialization;
import org.coconut.cache.tck.query.CacheQueryBundle;
import org.coconut.cache.tck.query.NoQuerySupport;
import org.junit.runner.RunWith;

@RunWith(TCKRunner.class)
public class AbstractCacheImplTest extends TestSuite {

    private Class<? extends Cache> cacheClazz;

    public AbstractCacheImplTest(Class<? extends Cache> cacheClazz) {
        this.cacheClazz = cacheClazz;
        tt = this;
        setName(cacheClazz.getCanonicalName());
    }

    @SuppressWarnings("unchecked")
    protected Cache<Integer, String> newCache(CacheConfiguration<Integer, String> conf) {
        return conf.newInstance(cacheClazz);
    }

    public void init() {
        System.out.println("adding bundle");

        if (!cacheClazz.isAnnotationPresent(CacheSupport.class)) {
            throw new IllegalStateException(
                    "Cache implementation must have a CacheSupport annotation");
        }
        CacheSupport cs = cacheClazz.getAnnotation(CacheSupport.class);
        boolean isThreadSafe = cacheClazz.isAnnotationPresent(ThreadSafe.class)
                && cacheClazz.getAnnotation(ThreadSafe.class).value();
        addCoreFeatures();
       
    }

    @SuppressWarnings("unchecked")
    public AbstractCacheImplTest addTestBundle(Class<? extends CacheTestBundle> features) {
        addTestBundle((Class<? extends CacheTestBundle>[]) new Class[] { features });
        return this;
    }

    public AbstractCacheImplTest addTestBundle(Class<? extends CacheTestBundle>... features) {
        for (Class<? extends CacheTestBundle> class1 : features) {
        	System.out.println("adding " + class1 );
            addTestSuite(class1);
            //addTest(class1);
            //addTest(new JUnit4TestAdapter(class1));
        }
        return this;
    }




    protected boolean supportsExpiration() {
        return true;
    }

    protected boolean isThreadSafe() {
        return true;
    }
}
