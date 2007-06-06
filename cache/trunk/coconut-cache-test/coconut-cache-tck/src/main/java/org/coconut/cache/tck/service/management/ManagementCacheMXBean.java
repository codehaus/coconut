package org.coconut.cache.tck.service.management;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;

import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Before;
import org.junit.Test;

public class ManagementCacheMXBean extends AbstractCacheTCKTestBundle {

    @Before
    public void setup() {
        c = newCache(newConf().management().setEnabled(true).c());
    }

    @Test
    public void getName() {
        assertEquals(c.getName(), managementMXBean().getName());

        c = newCache(newConf().setName("foo").management().setEnabled(true));
        assertEquals("foo", managementMXBean().getName());
        assertEquals(c.getName(), managementMXBean().getName());

    }

    @Test
    public void getCapacity() {
        assertEquals(c.getCapacity(), managementMXBean().getCapacity());
        put(M1);
        put(M2);
        assertEquals(2, managementMXBean().getCapacity());
        assertEquals(c.getCapacity(), managementMXBean().getCapacity());
    }

    @Test
    public void getSize() {
        assertEquals(c.size(), managementMXBean().getSize());
        put(M1);
        put(M2);
        assertEquals(2, managementMXBean().getSize());
        assertEquals(c.size(), managementMXBean().getSize());
    }

    @Test
    public void clear() {
        assertEquals(0, c.size());
        put(M1);
        put(M2);
        assertEquals(2, c.size());
        managementMXBean().clear();
        assertEquals(0, c.size());
    }

    @Test
    public void evict() {
    // TODO test evict
    }

}
