/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.util;

import java.util.Properties;

import javax.management.MBeanNotificationInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;

import org.coconut.cache.service.event.CacheEntryEvent;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.core.EventProcessor;
import org.coconut.core.Transformer;
import org.coconut.event.EventBus;
import org.coconut.filter.Filters;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AbstractCacheMXBean2<K,V> implements EventProcessor<CacheEvent<K, V>> {

    private final EventBus<CacheEvent<K, V>> bus;

    /**
     * @param cache
     * @param bus
     * @param messages
     * @throws NotCompliantMBeanException
     */
    public AbstractCacheMXBean2(AbstractCache cache, EventBus bus, Properties messages)
            throws NotCompliantMBeanException {
        this.bus = bus;
    }

    public void initializeSubscriptions() {
        bus.subscribe(this, Filters.TRUE, "internal#CACHE_JMX_SUPPORT");
    }

    protected MBeanNotificationInfo[] getNotifInfo() {
        if (bus == null) {
            // if no bus is defined we can't subscribe for events.
            return new MBeanNotificationInfo[] {};
        } else {
            String instanceName = "javax.management.Notification";
            String itemName = "javax.management.Notification";
            String[] cacheInstances = new String[] { CacheEvent.CacheCleared.NAME,
                    CacheEvent.CacheEvicted.NAME, CacheEvent.CacheStatisticsReset.NAME };
            String[] cacheItems = new String[] { CacheEntryEvent.ItemAccessed.NAME,
                    CacheEntryEvent.ItemAdded.NAME, CacheEntryEvent.ItemRemoved.NAME,
                    CacheEntryEvent.ItemUpdated.NAME, };
            return new MBeanNotificationInfo[] {
                    new MBeanNotificationInfo(cacheInstances, instanceName,
                            "Coconut Cache Instance Notifications"),
                    new MBeanNotificationInfo(cacheItems, itemName,
                            "Coconut Cache Item Notifications") };
        }
    }

    /**
     * @see org.coconut.cache.CacheListener#handle(org.coconut.cache.CacheEvent)
     */
    public void process(CacheEvent<K, V> event) {
        try {
            Notification n = ((Transformer<Object, Notification>) event)
                    .transform(((AbstractCache) event.getCache()).getName());
            //super.sendNotification(n);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
