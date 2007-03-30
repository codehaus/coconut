/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.management;

import javax.management.JMException;
import javax.management.MBeanServer;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.CacheServiceLifecycle;
import org.coconut.cache.internal.service.CacheServiceManager;
import org.coconut.cache.internal.service.ShutdownCallback;
import org.coconut.cache.internal.util.WrapperCacheMXBean;
import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.management.ManagedGroup;
import org.coconut.management.Managements;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheManagementService<K, V> implements CacheServiceLifecycle {

    private final ManagedGroup group;

    private final String domain;

    private final CacheServiceManager manager;

    private final AbstractCache cache;

    public DefaultCacheManagementService(CacheServiceManager manager,
            CacheConfiguration<K, V> conf, CacheManagementConfiguration<K, V> cmc,
            AbstractCache cache) {
        this.manager = manager;
        this.cache = cache;
        domain = cmc.getDomain();
        MBeanServer server = cmc.getMBeanServer();
        group = Managements.newGroup(conf.getName(), "Base bean", server);
    }

    public ManagedGroup getGroup() {
        manager.checkStarted();
        return group;
    }

    /**
     * @see org.coconut.cache.internal.service.CacheServiceLifecycle#doStart()
     */
    public void doStart() throws JMException {
        ManagedGroup g = group.addNewGroup("General",
                "General cache attributes and settings");
        g.add(new WrapperCacheMXBean(cache));
        group.registerAll(Managements.newRegistrant(domain, "name", "service", "group"));
    }

    /**
     * @see org.coconut.cache.internal.service.CacheServiceLifecycle#shutdown(org.coconut.cache.internal.service.ShutdownCallback)
     */
    public void shutdown(ShutdownCallback callback) throws JMException {
        group.unregister();
    }
}
