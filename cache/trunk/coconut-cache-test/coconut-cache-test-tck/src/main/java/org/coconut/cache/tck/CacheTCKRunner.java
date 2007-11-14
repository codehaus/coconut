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
import org.coconut.cache.tck.service.exceptionhandling.ExceptionHandlingSuite;
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
 * This class is responsible for running TCK cache tests. Use <code>@RunWith</code> to indicate a test should run with CacheTCKRunner.
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheTCKRunner extends Runner {

    static Class<? extends Cache> tt;

    /**
     * If the file test-tck/src/main/resources/defaulttestclass exists. We will try to
     * open it and read which cache implementation should be tested by default.
     * <p>
     * This is very usefull if you just want to run a subset of the tests in an IDE.
     */
    static {
        try {
            InputStream is = CacheTCKRunner.class.getClassLoader().getResourceAsStream(
                    "defaulttestclass");
            Properties p = new Properties();
            p.load(is);
            tt = (Class<? extends Cache>) Class.forName(p.getProperty("default"));
        } catch (ClassNotFoundException e) {
            // ignore, user has not defined a class
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CompositeRunner composite;

    Class<? extends Cache> tt2;

    /**
     * Creates a new CacheTCKRunner testing the specified type of cache.
     * 
     * @param klass
     *            the class to test
     * @throws Exception
     *             could not create this class
     */
    @SuppressWarnings("unchecked")
    public CacheTCKRunner(Class<? extends Cache> klass) throws Exception {
        tt = klass.getAnnotation(CacheTCKImplementationSpecifier.class).value();
        tt2 = tt;
        composite = new CompositeRunner(klass.getName());
        addTests(tt, composite);
        // only add the test class itself if it contains tests
        if (new TestIntrospector(klass).getTestMethods(Test.class).size() > 0) {
            composite.add(new TestClassRunner(klass));
        }
    }

    /** {@inheritDoc} */
    @Override
    public Description getDescription() {
        return composite.getDescription();
    }

    /** {@inheritDoc} */
    @Override
    public void run(RunNotifier not) {
        tt = tt2;
        composite.run(not);
    }

    /**
     * Decides which tests to run for the configured cache test class
     * 
     * @param runner
     * @throws InitializationError
     */
    protected void addTests(Class<? extends Cache> cacheClass, CompositeRunner runner)
            throws Exception {
        if (!cacheClass.isAnnotationPresent(CacheServiceSupport.class)) {
            throw new IllegalStateException(
                    "Cache implementation must have a CacheServiceSupport annotation");
        }
        CacheServiceSupport ss = tt.getAnnotation(CacheServiceSupport.class);
        List<Class> services = new ArrayList<Class>();
        if (ss != null) {
            services = Arrays.asList(ss.value());
        }
        runner.add(new Suite(CacheEntrySuite.class));
        runner.add(new Suite(CoreSuite.class));
        runner.add(new Suite(ExceptionHandlingSuite.class));
        if (services.contains(CacheEventService.class)) {
            runner.add(new Suite(EventSuite.class));
        }
        if (services.contains(CacheEvictionService.class)) {
            runner.add(new Suite(EvictionSuite.class));
        }
        if (services.contains(CacheExpirationService.class)) {
            runner.add(new Suite(ExpirationSuite.class));
        }
        if (services.contains(CacheLoadingService.class)) {
            runner.add(new Suite(LoadingSuite.class));
        }
        if (services.contains(CacheManagementService.class)) {
            runner.add(new Suite(ManagementSuite.class));
        }
        if (services.contains(CacheServiceManagerService.class)) {
            runner.add(new Suite(ServiceManagerSuite.class));
        }
        if (services.contains(CacheStatisticsService.class)) {
            runner.add(new Suite(StatisticsSuite.class));
        }
    }
}
