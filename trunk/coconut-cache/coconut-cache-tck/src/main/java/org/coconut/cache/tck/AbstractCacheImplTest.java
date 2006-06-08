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

public class AbstractCacheImplTest extends TestSuite {

    private Class<? extends Cache> cacheClazz;

    public AbstractCacheImplTest(Class<? extends Cache> cacheClazz) {
        this.cacheClazz = cacheClazz;
        tt = this;
    }

    @SuppressWarnings("unchecked")
    protected Cache<Integer, String> newCache(CacheConfiguration<Integer, String> conf) {
        return conf.newInstance(cacheClazz);
    }

    public void init() {
        if (!cacheClazz.isAnnotationPresent(CacheSupport.class)) {
            throw new IllegalStateException(
                    "Cache implementation must have a CacheSupport annotation");
        }
        CacheSupport cs = cacheClazz.getAnnotation(CacheSupport.class);
        boolean isThreadSafe = cacheClazz.isAnnotationPresent(ThreadSafe.class)
                && cacheClazz.getAnnotation(ThreadSafe.class).value();
        addCoreFeatures();
        if (cs.CacheLoadingSuppurt()) {
            addTestBundle(Loading.class);
            addTestBundle(FutureLoading.class);
            if (cs.CacheEntrySupport()) {
                addTestBundle(ExtendedCacheLoader.class);
            }
            if (isThreadSafe) {
                addTestBundle(ConcurrentLoading.class);
            }
        } else {
            addTestBundle(NoLoadingSupport.class);
        }
        if (cs.ExpirationSupport()) {
            addTestBundle(ExpirationCommon.class);
            addTestBundle(ExpirationOnEvict.class);
            addTestBundle(ExpirationStrict.class);
            if (isThreadSafe) {
                addTestBundle(ExpirationConcurrent.class);
            } else {
                addTestBundle(ExpirationLazySingleThreaded.class);
            }
        }
        if (cs.statisticsSupport()) {
            addTestBundle(HitStat.class);
        } else {
            addTestBundle(NoHitStat.class);
        }
        if (cs.eventSupport()) {
            addTestBundle(EventBusFeature.class);
        } else {
            addTestBundle(NoEventBusSupport.class);
        }
        if (Serializable.class.isAssignableFrom(cacheClazz)) {
            addTestBundle(Serialization.class);
            addTestBundle(SerializablePolicyEviction.class);
        } else {
            addTestBundle(NoSerialization.class);
        }
        if (cs.querySupport()) {
            addTestBundle(CacheQueryBundle.class);
        } else {
            addTestBundle(NoQuerySupport.class);
        }

        addTestBundle(CacheEntryToPolicy.class);
        addTestBundle(SimplePolicyEviction.class);
        // all cache implementations supports eviction
    }

    @SuppressWarnings("unchecked")
    public AbstractCacheImplTest addTestBundle(Class<? extends CacheTestBundle> features) {
        addTestBundle((Class<? extends CacheTestBundle>[]) new Class[] { features });
        return this;
    }

    public AbstractCacheImplTest addTestBundle(Class<? extends CacheTestBundle>... features) {
        for (Class<? extends CacheTestBundle> class1 : features) {
            addTest(new JUnit4TestAdapter(class1));
        }
        return this;
    }

    static AbstractCacheImplTest tt;

    public class MFSuite extends JUnit4TestAdapter {

        public MFSuite(Class<? extends Object> newTestClass) {
            super(newTestClass);
        }

        // @Override
        // public void run(TestResult result) {
        // tt = AbstractCacheImplTest.this;
        // super.run(result);
        // Enumeration<TestFailure> e=result.errors();
        // while (e.hasMoreElements()) {
        // e.nextElement().thrownException().printStackTrace();
        // }
        // }
    }

    private void addCoreFeatures() {
        addTestBundle(BasicCache.class);
        addTestBundle(BasicMap.class);
        addTestBundle(ClearRemove.class);
        addTestBundle(ConcurrentMap.class);
        addTestBundle(Constructors.class);
        addTestBundle(PutTimeoutable.class);
        addTestBundle(Put.class);
        addCollectionViews();
    }

    private void addCollectionViews() {
        addTestBundle(EntrySet.class);
        addTestBundle(EntrySetModifying.class);
        addTestBundle(KeySet.class);
        addTestBundle(KeySetModifying.class);
        addTestBundle(Values.class);
        addTestBundle(ValuesModifying.class);
    }

    protected boolean supportsExpiration() {
        return true;
    }

    protected boolean isThreadSafe() {
        return true;
    }
}
