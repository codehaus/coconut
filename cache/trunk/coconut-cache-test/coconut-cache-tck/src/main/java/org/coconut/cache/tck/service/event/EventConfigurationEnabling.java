package org.coconut.cache.tck.service.event;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

public class EventConfigurationEnabling extends CacheTestBundle  {

    @Test
    public void testNotEnabled() {
        CacheConfiguration<?, ?> conf = CacheConfiguration.create();
        c = newCache(conf);
        assertFalse(c.hasService(CacheEventService.class));
        assertFalse(c.getAllServices().containsKey(CacheEventService.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotEnabledCE() {
        CacheConfiguration<?, ?> conf = CacheConfiguration.create();
        c = newCache(conf);
        c.getService(CacheEventService.class);
    }

    @Test
    public void testEnabled() {
        CacheConfiguration<?, ?> conf = CacheConfiguration.create();
        conf.event().setEnabled(true);
        c = newCache(conf);
        assertTrue(c.hasService(CacheEventService.class));
        assertTrue(c.getAllServices().containsKey(CacheEventService.class));
        Object cs = c.getAllServices().get(CacheEventService.class);
        assertSame(cs, c.getService(CacheEventService.class));
    }
}
