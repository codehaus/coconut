/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.management;

import static org.junit.Assert.assertEquals;

import org.coconut.cache.Cache;
import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.management.ManagedGroup;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests {@link ManagementUtils}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@RunWith(JMock.class)
public class ManagementUtilsTest {
    Mockery context = new JUnit4Mockery();

    @Test
    public void testWrapService() {
        final CacheManagementService mock = context.mock(CacheManagementService.class);
        final ManagedGroup dummy = context.mock(ManagedGroup.class);
        context.checking(new Expectations() {
            {
                one(mock).add("foo");
                will(returnValue(dummy));
            }
        });
        CacheManagementService service = ManagementUtils.wrapService(mock);
        assertEquals(dummy, service.add("foo"));
    }

    @Test(expected = NullPointerException.class)
    public void wrapServiceNPE() {
        ManagementUtils.wrapService(null);
    }

    @Test(expected = NullPointerException.class)
    public void testWrapServiceNPE() {
        ManagementUtils.synchronizedGroup(null, null);
    }
    
    @Test
    public void testWrapMXBean() {
        final Cache mock = context.mock(Cache.class);
        context.checking(new Expectations() {
            {
                one(mock).clear();
                one(mock).getVolume();
                will(returnValue(1l));
                one(mock).getName();
                will(returnValue("fooName"));
                one(mock).size();
                will(returnValue(2));
            }
        });
        CacheMXBean mxBean = ManagementUtils.wrapMXBean(mock);
        mxBean.clear();
        assertEquals(1l, mxBean.getVolume());
        assertEquals("fooName", mxBean.getName());
        assertEquals(2, mxBean.getSize());
    }

    @Test(expected = NullPointerException.class)
    public void testWrapMXBeanNPE() {
        ManagementUtils.wrapMXBean(null);
    }
}
