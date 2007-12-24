/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import static org.coconut.test.TestUtil.dummy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.coconut.attribute.Attributes;
import org.coconut.cache.internal.service.loading.LoadableFutureTask.LoadValueCallable;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests {@link LoadingUtilsTest} class.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheConfigurationTest.java 434 2007-11-13 15:36:17Z kasper $
 */
@SuppressWarnings("unchecked")
@RunWith(JMock.class)
public class LoadingUtilsTest {

    Mockery context = new JUnit4Mockery();

    @Test(expected = NullPointerException.class)
    public void createLoadCallableNPE() {
        LoadableFutureTask.createLoadCallable(null, "A", Attributes.EMPTY_MAP);
    }

    @Test(expected = NullPointerException.class)
    public void createLoadCallableNPE1() {
        LoadableFutureTask.createLoadCallable(dummy(InternalCacheLoadingService.class), null,
                Attributes.EMPTY_MAP);
    }

    @Test(expected = NullPointerException.class)
    public void createLoadCallableNPE2() {
        LoadableFutureTask.createLoadCallable(dummy(InternalCacheLoadingService.class), "A", null);
    }

    @Test
    public void loadValueCallable() {
        final InternalCacheLoadingService service = context.mock(InternalCacheLoadingService.class);
        LoadValueCallable callable = new LoadValueCallable(service, 1, Attributes.EMPTY_MAP);
        assertEquals(1, callable.getKey());
        assertSame(Attributes.EMPTY_MAP, callable.getAttributes());
        context.checking(new Expectations() {
            {
                one(service).loadAndAddToCache(1, Attributes.EMPTY_MAP, false);
            }
        });
        callable.call();
    }

    /**
     * Tests that
     * {@link LoadingUtils#wrapMXBean(org.coconut.cache.service.loading.CacheLoadingService)}
     * throws a {@link NullPointerException} when invoked with a null argument.
     */
    @Test(expected = NullPointerException.class)
    public void wrapMXBeanNPE() {
        LoadingUtils.wrapMXBean(null);
    }

    /**
     * Tests that
     * {@link LoadingUtils#wrapService(org.coconut.cache.service.loading.CacheLoadingService)}
     * throws a {@link NullPointerException} when invoked with a null argument.
     */
    @Test(expected = NullPointerException.class)
    public void wrapServiceNPE() {
        LoadingUtils.wrapService(null);
    }
}
