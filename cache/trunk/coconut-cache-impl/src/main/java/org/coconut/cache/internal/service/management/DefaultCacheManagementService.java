/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.management;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.management.JMException;
import javax.management.MBeanServer;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.internal.service.service.AbstractInternalCacheService;
import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedGroupVisitor;
import org.coconut.management.Managements;
import org.coconut.management.defaults.DefaultManagedGroup;

/**
 * The default implementation of {@link CacheManagementService}. All methods exposed
 * through the CacheManagementService interface can be invoked in a thread safe manner.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheManagementService extends AbstractInternalCacheService implements
        CacheManagementService {

    /** The Managed root group. */
    private final ManagedGroup root;

    /** Whether or not this service is enabled. */
    private final boolean isEnabled;

    /** Used to register all services. */
    private final ManagedGroupVisitor registrant;

    /**
     * Creates a new DefaultCacheManagementService.
     * 
     * @param configuration
     *            the configuration of the Cache Management service
     * @param cacheName
     *            the name of the cache
     */
    public DefaultCacheManagementService(CacheManagementConfiguration configuration,
            String cacheName) {
        super(CacheManagementConfiguration.SERVICE_NAME);
        if (configuration == null) {
            throw new NullPointerException("configuration is null");
        } else if (cacheName == null) {
            throw new NullPointerException("cache is null");
        }

        /* Set IsEnabled */
        isEnabled = configuration.isEnabled();

        /* Set Management Root */
        ManagedGroup tmpRoot = configuration.getRoot();
        root = tmpRoot == null ? new DefaultManagedGroup(cacheName,
                "This group contains all managed Cache services", false) : tmpRoot;

        /* Set Registrant */
        if (configuration.getRegistrant() == null) {
            MBeanServer server = configuration.getMBeanServer();
            if (server == null) {
                server = ManagementFactory.getPlatformMBeanServer();
            }
            String domain = configuration.getDomain();
            if (domain == null) {
                domain = CacheMXBean.DEFAULT_JMX_DOMAIN;
            }
            registrant = Managements.register(server, domain, "name", "service", "group");
        } else {
            registrant = configuration.getRegistrant();
        }
    }

    /**
     * @see org.coconut.cache.service.management.CacheManagementService#getRoot()
     */
    public ManagedGroup getRoot() {
        if (isEnabled) {
            return root;
        } else {
            throw new UnsupportedOperationException(
                    "This service does not support this operation");
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
            serviceMap.put(CacheManagementService.class, ManagementUtils
                    .wrapService(this));
        }
    }

    /**
     * @see org.coconut.cache.internal.service.CacheServiceLifecycle#shutdown(org.coconut.cache.internal.service.ShutdownCallback)
     */
    @Override
    public void shutdown(Executor e) {
        if (isEnabled) {
            try {
                root.unregister();
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
            ManagedGroup g = root.addChild(CacheMXBean.MANAGED_SERVICE_NAME,
                    "General cache attributes and operations");
            g.add(ManagementUtils.wrapMXBean(cache));

            try {
                registrant.visitManagedGroup(root);
            } catch (JMException e) {
                throw new CacheException(e);
            }
        }
    }
}
