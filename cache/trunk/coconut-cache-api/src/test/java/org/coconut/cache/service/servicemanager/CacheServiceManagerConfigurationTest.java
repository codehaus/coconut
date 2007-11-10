/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.coconut.cache.spi.XmlConfiguratorTest.reloadService;

import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedObject;
import org.coconut.test.MockTestCase;
import org.junit.Before;
import org.junit.Test;

public class CacheServiceManagerConfigurationTest {

    CacheServiceManagerConfiguration c;

    @Before
    public void setUp() {
        c = new CacheServiceManagerConfiguration();
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerIAE() {
        c.add(new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerIAE1() {
        ManagedObject mo = MockTestCase.mockDummy(ManagedObject.class);
        c.add(mo);
        c.add(mo);
    }

    @Test(expected = NullPointerException.class)
    public void registerNPE() {
        c.add(null);
    }

    @Test
    public void xmlSave() throws Exception {
        c = reloadService(c);
        assertEquals(0, c.getObjects().size());

        c.add(new LoadableLifecycle());
        c = reloadService(c, new CacheServiceManagerConfiguration());
        assertEquals(1, c.getObjects().size());
        assertTrue(c.getObjects().iterator().next().getClass().equals(
                LoadableLifecycle.class));

        c.add(new LoadableManagedObject());
        c = reloadService(c, new CacheServiceManagerConfiguration());
        assertEquals(2, c.getObjects().size());

    }

// @Test(expected = NullPointerException.class)
// public void addServiceNPE() {
// c.addService(null);
// }
//
// @Test(expected = IllegalArgumentException.class)
// public void addServiceIAE() {
// Object o = new Object();
// c.addService(o);
// c.addService(o);
// }

    @Test(expected = IllegalArgumentException.class)
    public void registerIAE2() {
        CacheLifecycle cl = MockTestCase.mockDummy(CacheLifecycle.class);
        c.add(cl);
        c.add(cl);
    }

// @Test(expected = IllegalArgumentException.class)
// public void addDoubleIAE1() {
// ManagedObject mo = MockTestCase.mockDummy(ManagedObject.class);
// c.add(mo);
// c.addService(mo);
// }
//
// @Test(expected = IllegalArgumentException.class)
// public void addDoubleIAE2() {
// ManagedObject mo = MockTestCase.mockDummy(ManagedObject.class);
// c.addService(mo);
// c.add(mo);
// }

    public void register() {
        assertEquals(0, c.getObjects().size());
        ManagedObject mo = MockTestCase.mockDummy(ManagedObject.class);
        ManagedObject mo1 = MockTestCase.mockDummy(ManagedObject.class);
        CacheLifecycle cl = MockTestCase.mockDummy(CacheLifecycle.class);
        CacheLifecycle cl1 = MockTestCase.mockDummy(CacheLifecycle.class);
        c.add(mo);
        c.add(mo1);
        c.add(cl);
        c.add(cl1);
        assertEquals(4, c.getObjects());
        assertTrue(c.getObjects().contains(mo));
        assertTrue(c.getObjects().contains(mo1));
        assertTrue(c.getObjects().contains(cl));
        assertTrue(c.getObjects().contains(cl1));
    }

    public static class LoadableManagedObject implements ManagedObject {
        public void manage(ManagedGroup parent) {}
    }

    public class NonLoadableManagedObject implements ManagedObject {
        public void manage(ManagedGroup parent) {}
    }

    public static class LoadableLifecycle extends AbstractCacheLifecycle {}

    public class NonLoadableLifecycle extends AbstractCacheLifecycle {}

}
