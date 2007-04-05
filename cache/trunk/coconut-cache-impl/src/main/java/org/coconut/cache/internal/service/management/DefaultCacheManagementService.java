/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.management;

import java.util.concurrent.Executor;

import javax.management.JMException;
import javax.management.MBeanServer;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.CacheServiceManager;
import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.management.ManagedGroup;
import org.coconut.management.Managements;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheManagementService extends AbstractCacheManagementService {

    private final Cache cache;

    private final String domain;

    private final ManagedGroup group;

    private final CacheServiceManager manager;

    public DefaultCacheManagementService(CacheServiceManager manager,
            CacheConfiguration conf, CacheManagementConfiguration cmc, Cache cache) {
        this.manager = manager;
        this.cache = cache;
        domain = cmc.getDomain();
        MBeanServer server = cmc.getMBeanServer();
        group = Managements.newGroup(conf.getName(), "Base bean", server);
    }

    /**
     * @see org.coconut.cache.internal.service.CacheServiceLifecycle#doStart()
     */
    public void doStart() throws JMException {
        ManagedGroup g = group.addNewGroup("General",
                "General cache attributes and settings");
        if (cache instanceof CacheMXBean) {
            g.add(cache);
        }
        group.registerAll(Managements.newRegistrant(domain, "name", "service", "group"));
    }

    public ManagedGroup getGroup() {
        manager.checkStarted();
        return group;
    }

    /**
     * @see org.coconut.cache.internal.service.InternalCacheService#isDummy()
     */
    public boolean isDummy() {
        return false;
    }

    /**
     * @see org.coconut.cache.internal.service.CacheServiceLifecycle#shutdown(org.coconut.cache.internal.service.ShutdownCallback)
     */
    public void shutdown(Executor callback) throws JMException {
        group.unregister();
    }
}
