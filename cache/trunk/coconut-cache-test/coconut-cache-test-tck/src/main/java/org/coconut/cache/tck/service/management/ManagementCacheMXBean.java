/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.management;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Before;
import org.junit.Test;

public class ManagementCacheMXBean extends AbstractCacheTCKTestBundle {

    CacheMXBean mxBean;

    MBeanServer mbs;

    //TODO
    //We should test for a default objectname
    //a.la. for cache named foo 
    //ObjName=org.coconut.cache:name=343434, service=General
    
    @Before
    public void setup()  {
        mbs = MBeanServerFactory.createMBeanServer();
        c = newCache(newConf().management().setEnabled(true).setMBeanServer(mbs).c());
        mxBean = findMXBean(mbs, CacheMXBean.class);
    }

    
    @Test
    public void getName() {
        c = newCache(newConf().setName("foo").management().setEnabled(true)
                .setMBeanServer(mbs));
        mxBean = findMXBean(mbs, CacheMXBean.class);
        assertEquals("foo", mxBean.getName());
        assertEquals(c.getName(), mxBean.getName());

    }

    @Test
    public void getNameUninitialized() {
        assertEquals(c.getName(), mxBean.getName());
    }

    @Test
    public void getCapacity() {
        assertEquals(c.getVolume(), mxBean.getVolume());
        put(M1);
        put(M2);
        assertEquals(2, mxBean.getVolume());
        assertEquals(c.getVolume(), mxBean.getVolume());
    }

    @Test
    public void getSize() {
        assertEquals(c.size(), mxBean.getSize());
        put(M1);
        put(M2);
        assertEquals(2, mxBean.getSize());
        assertEquals(c.size(), mxBean.getSize());
    }

    @Test
    public void clear() {
        assertEquals(0, c.size());
        put(M1);
        put(M2);
        assertEquals(2, c.size());
        mxBean.clear();
        assertEquals(0, c.size());
    }

    @Test
    public void evictTest() {
        mxBean.evict();
    }

}
