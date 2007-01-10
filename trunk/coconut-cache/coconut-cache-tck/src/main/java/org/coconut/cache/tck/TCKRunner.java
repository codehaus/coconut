/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck;

import java.io.Serializable;

import org.coconut.annotation.ThreadSafe;
import org.coconut.cache.Cache;
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
import org.coconut.cache.tck.expiration.ExpirationEvict;
import org.coconut.cache.tck.expiration.ExpirationFilterBased;
import org.coconut.cache.tck.expiration.ExpirationTimeBased;
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
import org.junit.Test;
import org.junit.internal.runners.CompositeRunner;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;
import org.junit.internal.runners.TestIntrospector;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class TCKRunner extends Runner {

    private final Class<? extends Cache> klass;

    private CompositeRunner composite;

    static Class<? extends Cache> tt;

    @SuppressWarnings("unchecked")
    public TCKRunner(Class<? extends Cache> klass) throws Throwable {
        this.klass = klass;
        tt = klass.getAnnotation(TCKClassTester.class).value();
        composite = new CompositeRunner(klass.getName());
        addTests(composite);
        // only add the test class itself if it contains tests
        if (new TestIntrospector(klass).getTestMethods(Test.class).size() > 0) {
            composite.add(new TestClassRunner(klass));
        }
    }

    private void addTests(CompositeRunner runner) throws InitializationError {
        if (!tt.isAnnotationPresent(CacheSupport.class)) {
            throw new IllegalStateException(
                    "Cache implementation must have a CacheSupport annotation");
        }
        CacheSupport cs = (CacheSupport) tt.getAnnotation(CacheSupport.class);
        boolean isThreadSafe = klass.isAnnotationPresent(ThreadSafe.class)
                && ((ThreadSafe) klass.getAnnotation(ThreadSafe.class)).value();
        addCoreFeatures(runner);

        addExpiration(runner, cs, isThreadSafe);
        addLoading(runner, cs, isThreadSafe);
        if (cs.statisticsSupport()) {
            composite.add(new TestClassRunner(HitStat.class));
        } else {
            composite.add(new TestClassRunner(NoHitStat.class));
        }
        if (cs.eventSupport()) {
            composite.add(new TestClassRunner(EventBusFeature.class));
        } else {
            composite.add(new TestClassRunner(NoEventBusSupport.class));
        }
        if (Serializable.class.isAssignableFrom(tt)) {
            composite.add(new TestClassRunner(Serialization.class));
            composite.add(new TestClassRunner(SerializablePolicyEviction.class));
        } else {
            composite.add(new TestClassRunner(NoSerialization.class));
        }
        if (cs.querySupport()) {
            composite.add(new TestClassRunner(CacheQueryBundle.class));
        } else {
            composite.add(new TestClassRunner(NoQuerySupport.class));
        }

        composite.add(new TestClassRunner(CacheEntryToPolicy.class));
        composite.add(new TestClassRunner(SimplePolicyEviction.class));
        // all cache implementations supports eviction

    }

    private void addExpiration(CompositeRunner runner, CacheSupport cs,
            boolean isThreadSafe) throws InitializationError {
        if (cs.ExpirationSupport()) {
            composite.add(new TestClassRunner(ExpirationEvict.class));
            composite.add(new TestClassRunner(ExpirationFilterBased.class));
            composite.add(new TestClassRunner(ExpirationTimeBased.class));
//            composite.add(new TestClassRunner(ExpirationCommon.class));
//            composite.add(new TestClassRunner(ExpirationOnEvict.class));
//            composite.add(new TestClassRunner(ExpirationStrict.class));
//            if (isThreadSafe) {
//                composite.add(new TestClassRunner(ExpirationConcurrent.class));
//            } else {
//                //TODO decide what todo, I think we should throw an exception.
//                //when we have a non asynchronous loader and the lazy strategy
//                //at construction time that is...
//                
//                //composite.add(new TestClassRunner(ExpirationLazySingleThreaded.class));
//            }
        }
    }

    private void addCoreFeatures(CompositeRunner runner) throws InitializationError {
        composite.add(new TestClassRunner(BasicCache.class));
        composite.add(new TestClassRunner(BasicMap.class));
        composite.add(new TestClassRunner(ClearRemove.class));
        composite.add(new TestClassRunner(ConcurrentMap.class));
        composite.add(new TestClassRunner(Constructors.class));
        composite.add(new TestClassRunner(PutTimeoutable.class));
        composite.add(new TestClassRunner(Put.class));
        addCollectionViews(runner);
    }

    private void addCollectionViews(CompositeRunner runner) throws InitializationError {
        composite.add(new TestClassRunner(EntrySet.class));
        composite.add(new TestClassRunner(EntrySetModifying.class));
        composite.add(new TestClassRunner(KeySet.class));
        composite.add(new TestClassRunner(KeySetModifying.class));
        composite.add(new TestClassRunner(Values.class));
        composite.add(new TestClassRunner(ValuesModifying.class));
    }

    private void addLoading(CompositeRunner runner, CacheSupport cs, boolean isThreadSafe)
            throws InitializationError {
        if (cs.CacheLoadingSupport()) {
            composite.add(new TestClassRunner(Loading.class));
            composite.add(new TestClassRunner(FutureLoading.class));
            if (cs.CacheEntrySupport()) {
                composite.add(new TestClassRunner(ExtendedCacheLoader.class));
            }
            if (isThreadSafe) {
                composite.add(new TestClassRunner(ConcurrentLoading.class));
            }
        } else {
            composite.add(new TestClassRunner(NoLoadingSupport.class));
        }
    }

    /**
     * @see org.junit.runner.Runner#getDescription()
     */
    @Override
    public Description getDescription() {
        return composite.getDescription();
    }

    /**
     * @see org.junit.runner.Runner#run(org.junit.runner.notification.RunNotifier)
     */
    @Override
    public void run(RunNotifier not) {
        composite.run(not);
    }

}
