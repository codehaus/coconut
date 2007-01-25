/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.internal.util;

import java.util.Properties;

import javax.management.MBeanNotificationInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntryEvent;
import org.coconut.cache.CacheEvent;
import org.coconut.cache.management.CacheMXBean;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.core.EventProcessor;
import org.coconut.core.Transformer;
import org.coconut.event.EventBus;
import org.coconut.filter.LogicFilters;
import org.coconut.internal.jmx.JmxEmitterSupport;

/**
 * TODO this should be abstract, but for now its just a plain class
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class AbstractCacheMXBean<K, V> extends JmxEmitterSupport implements
        CacheMXBean, EventProcessor<CacheEvent<K, V>> {

    private final Cache<K, V> cache;

    private final EventBus<CacheEvent<K, V>> bus;

    /**
     * @param mbeanInterface
     * @throws NotCompliantMBeanException
     * @throws NotCompliantMBeanException
     */
    public AbstractCacheMXBean(Cache<K, V> cache, Properties messages)
            throws NotCompliantMBeanException {
        super(messages, CacheMXBean.class);
        this.cache = cache;
        this.bus = null;
    }

    /**
     * @param mbeanInterface
     * @throws NotCompliantMBeanException
     * @throws NotCompliantMBeanException
     */
    public AbstractCacheMXBean(Cache<K, V> cache, EventBus<CacheEvent<K, V>> bus,
            Properties messages) throws NotCompliantMBeanException {
        super(messages, CacheMXBean.class);
        this.cache = cache;
        this.bus = bus;
    }

    public void initializeSubscriptions() {
        bus.subscribe(this, LogicFilters.TRUE,"internal#CACHE_JMX_SUPPORT");
    }

    @Override
    protected MBeanNotificationInfo[] getNotifInfo() {
        if (bus == null) {
            // if no bus is defined we can't subscribe for events.
            return new MBeanNotificationInfo[] {};
        } else {
            String instanceName = "javax.management.Notification";
            String itemName = "javax.management.Notification";
            String[] cacheInstances = new String[] {
                    CacheEvent.CacheCleared.NAME, CacheEvent.CacheEvicted.NAME,
                    CacheEvent.CacheStatisticsReset.NAME };
            String[] cacheItems = new String[] {
                    CacheEntryEvent.ItemAccessed.NAME,
                    CacheEntryEvent.ItemAdded.NAME,
                    CacheEntryEvent.ItemRemoved.NAME,
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
    public void process(CacheEvent<K,V> event) {
        try {
            Notification n = ((Transformer<Object, Notification>) event)
                    .transform(((AbstractCache) event.getCache()).getName());
            super.sendNotification(n);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#getSize()
     */
    public int getSize() {
        return cache.size();
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#getNumberOfHits()
     */
    public long getNumberOfHits() {
        return cache.getHitStat().getNumberOfHits();
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#getNumberOfMisses()
     */
    public long getNumberOfMisses() {
        return cache.getHitStat().getNumberOfMisses();
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#getHitRatio()
     */
    public double getHitRatio() {
        return cache.getHitStat().getHitRatio();
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#resetHitStat()
     */
    public void resetStatistics() {
        cache.resetStatistics();
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#clear()
     */
    public void clear() {
        cache.clear();
    }

    public void evict() {
        cache.evict();
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#trimToSize(int)
     */
    public void trimToSize(int newSize) {
      throw new UnsupportedOperationException(); 
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#getMemoryUsage()
     */
    public long getMemoryUsage() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#getMaximumSize()
     */
    public int getMaximumSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#setMaximumSize(long)
     */
    public void setMaximumSize(long maximumCapacity) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#getDefaultExpiration()
     */
    public long getDefaultExpiration() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#setDefaultExpiration(long)
     */
    public void setDefaultExpiration(long nanos) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#isMemoryMonitoringSupported()
     */
    public boolean isMemoryMonitoringSupported() {
        return false;
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#getMaximumMemoryUsage()
     */
    public long getMaximumMemoryUsage() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * 
     */
    public void setMaximumMemoryUsage() {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#getName()
     */
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#setMaximumSize(int)
     */
    public void setMaximumSize(int maximumSize) {
        // TODO Auto-generated method stub
        
    }
}
