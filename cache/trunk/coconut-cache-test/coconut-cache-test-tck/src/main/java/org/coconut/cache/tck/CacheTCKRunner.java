/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.coconut.cache.Cache;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.spi.CacheServiceSupport;
import org.coconut.cache.tck.cacheentry.CacheEntrySuite;
import org.coconut.cache.tck.core.CoreSuite;
import org.coconut.cache.tck.service.event.EventSuite;
import org.coconut.cache.tck.service.eviction.EvictionSuite;
import org.coconut.cache.tck.service.expiration.ExpirationSuite;
import org.coconut.cache.tck.service.loading.LoadingSuite;
import org.coconut.cache.tck.service.management.ManagementSuite;
import org.coconut.cache.tck.service.servicemanager.ServiceManagerSuite;
import org.coconut.cache.tck.service.statistics.StatisticsSuite;
import org.junit.Test;
import org.junit.internal.runners.CompositeRunner;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;
import org.junit.internal.runners.TestIntrospector;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheTCKRunner extends Runner {

    static Class<? extends Cache> tt;

    private final Class<? extends Cache> klass;

    private CompositeRunner composite;

    static {
        try {
            InputStream is = CacheTCKRunner.class.getClassLoader().getResourceAsStream(
                    "defaulttest.txt");
            Properties p = new Properties();
            p.load(is);
            tt = (Class<? extends Cache>) Class.forName(p.getProperty("default"));
        } catch (ClassNotFoundException e) {
            // ignore
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public CacheTCKRunner(Class<? extends Cache> klass) throws Throwable {
        this.klass = klass;
        tt = klass.getAnnotation(CacheTCKImplementationSpecifier.class).value();
        composite = new CompositeRunner(klass.getName());
        addTests(composite);
        // only add the test class itself if it contains tests
        if (new TestIntrospector(klass).getTestMethods(Test.class).size() > 0) {
            composite.add(new TestClassRunner(klass));
        }
    }

    private void addTests(CompositeRunner runner) throws InitializationError {
        if (!tt.isAnnotationPresent(CacheServiceSupport.class)) {
            throw new IllegalStateException(
                    "Cache implementation must have a CacheServiceSupport annotation");
        }
        CacheServiceSupport ss = tt.getAnnotation(CacheServiceSupport.class);
        List<Class> services = new ArrayList<Class>();
        if (ss != null) {
            services = Arrays.asList(ss.value());
        }
        composite.add(new Suite(CacheEntrySuite.class));
        composite.add(new Suite(CoreSuite.class));
        if (services.contains(CacheEventService.class)) {
            composite.add(new Suite(EventSuite.class));
        }
        if (services.contains(CacheEvictionService.class)) {
            composite.add(new Suite(EvictionSuite.class));
        }
        if (services.contains(CacheExpirationService.class)) {
            composite.add(new Suite(ExpirationSuite.class));
        }
        if (services.contains(CacheLoadingService.class)) {
            composite.add(new Suite(LoadingSuite.class));
        }
        if (services.contains(CacheManagementService.class)) {
            composite.add(new Suite(ManagementSuite.class));
        }
        if (services.contains(CacheServiceManagerService.class)) {
            composite.add(new Suite(ServiceManagerSuite.class));
        }
        if (services.contains(CacheStatisticsService.class)) {
            composite.add(new Suite(StatisticsSuite.class));
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
