/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.management;

import java.lang.management.ManagementFactory;
import java.util.Map;

import javax.management.JMException;
import javax.management.MBeanServer;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.AbstractCacheService;
import org.coconut.cache.internal.util.WrapperCacheMXBean;
import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.management.ManagedGroup;
import org.coconut.management.Managements;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheManagementService<K, V> extends AbstractCacheService<K, V> {

    private final ManagedGroup group;

    private final String domain;

    public DefaultCacheManagementService(CacheConfiguration<K, V> conf) {
        super(conf);
        CacheManagementConfiguration cmc = conf
                .getServiceConfiguration(CacheManagementConfiguration.class);
        if (cmc != null) {
            domain = cmc.getDomain();
            MBeanServer server = cmc.getMBeanServer();
            group = Managements.newGroup(conf.getName(), "Base bean", server);
        } else {
            domain = null;
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            group = Managements.newGroup(conf.getName(), "Base bean", server);
        }
    }

    public ManagedGroup getGroup() {
        return group;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheService#doStart(org.coconut.cache.spi.AbstractCache,
     *      java.util.Map)
     */
    @Override
    protected void doStart(AbstractCache<K, V> cache, Map<String, Object> properties)
            throws JMException {
        ManagedGroup g = group.addGroup("General",
                "General cache attributes and settings");
        g.add(new WrapperCacheMXBean(cache));
        group.registerAll(Managements.newRegistrant(domain, "name", "service", "group"));
    }
}
