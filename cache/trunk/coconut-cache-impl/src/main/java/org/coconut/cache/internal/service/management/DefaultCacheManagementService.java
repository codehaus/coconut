/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.management;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.management.JMException;
import javax.management.MBeanServer;

import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.internal.service.service.InternalCacheServiceManager;
import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedGroupVisitor;
import org.coconut.management.Managements;
import org.coconut.management.defaults.DefaultManagedGroup;

/**
 * <p>
 * This service does not implements CacheMXBean because of a name clash between
 * {@link AbstractCacheManagementService#getName()} and {@link CacheMXBean#getName()}
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@ThreadSafe
public class DefaultCacheManagementService extends AbstractCacheManagementService {

    private final CacheMXBean cacheMXBean;

    private final String domain;

    private final ManagedGroup group;

    private final boolean isEnabled;

    private final InternalCacheServiceManager manager;

    private final ManagedGroupVisitor registrant;

    /**
     * Creates a new DefaultCacheManagementService.
     * 
     * @param serviceManager
     *            The cache service manager
     * @param managementConfiguration
     *            the configuration of the Cache Management service
     * @param cache
     *            the cache for which to expose MBeans
     * @param name
     *            the name of the cache
     */
    public DefaultCacheManagementService(InternalCacheServiceManager serviceManager,
            CacheManagementConfiguration managementConfiguration,
            Cache<?, ?> cache, String name) {
        if (serviceManager == null) {
            throw new NullPointerException("serviceManager is null");
        } else if (managementConfiguration == null) {
            throw new NullPointerException("managementConfiguration is null");
        } else if (cache == null) {
            throw new NullPointerException("cache is null");
        } else if (name == null) {
            throw new NullPointerException("name is null");
        }
        this.manager = serviceManager;
        isEnabled = managementConfiguration.isEnabled();
        domain = managementConfiguration.getDomain();
        if (managementConfiguration.getRoot() == null) {
            group = new DefaultManagedGroup(name,
                    "This group contains all managed Cache services", false);
        } else {
            group = managementConfiguration.getRoot();
        }

        MBeanServer server = managementConfiguration.getMBeanServer();
        if (server == null) {
            server = ManagementFactory.getPlatformMBeanServer();
        }
        if (managementConfiguration.getRegistrant() == null) {
            registrant = Managements.register(server, domain, "name", "service", "group");
        } else {
            registrant = managementConfiguration.getRegistrant();
        }

        cacheMXBean = new CacheMXBeanWrapper(cache);
        ManagedGroup g = group.addChild("General",
                "General cache attributes and operations");
        g.add(cacheMXBean);
    }

    /**
     * @see org.coconut.cache.service.management.CacheManagementService#getRoot()
     */
    public ManagedGroup getRoot() {
        if (isEnabled) {
            // TODO im not sure we need to lazy start
            return group;
        } else {
            throw new UnsupportedOperationException(
                    "The service does not support this operation");
        }
    }

    /**
     * @see org.coconut.cache.service.servicemanager.AbstractCacheService#initialize(org.coconut.cache.CacheConfiguration,
     *      java.util.Map)
     */
    @Override
    public void initialize(CacheConfiguration<?, ?> configuration,
            Map<Class<?>, Object> serviceMap) {
        if (isEnabled) {
            serviceMap.put(CacheManagementService.class, new DelegatedManagementService(
                    this));
            serviceMap.put(CacheMXBean.class, cacheMXBean);
        }
    }

    /**
     * @see org.coconut.cache.internal.service.CacheServiceLifecycle#shutdown(org.coconut.cache.internal.service.ShutdownCallback)
     */
    @Override
    public void shutdown(Executor e) {
        if (isEnabled) {
            try {
                group.unregister();
            } catch (JMException jme) {
                throw new CacheException(jme);
            }
        }
    }

    /**
     * @see org.coconut.cache.service.servicemanager.AbstractCacheService#start(org.coconut.cache.Cache)
     */
    @Override
    public void started(Cache<?, ?> cache) {
        if (isEnabled) {
            try {
                registrant.visitManagedGroup(group);
            } catch (JMException e) {
                throw new CacheException(e);
            }
        }
    }
}
