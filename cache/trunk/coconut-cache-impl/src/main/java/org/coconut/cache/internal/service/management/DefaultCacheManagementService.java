/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.management;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Collection;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheException;
import org.coconut.cache.internal.service.servicemanager.CompositeService;
import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedVisitor;
import org.coconut.management.Managements;
import org.coconut.management.defaults.DefaultManagedGroup;

/**
 * The default implementation of the {@link CacheManagementService} interface. All methods
 * exposed through the CacheManagementService interface can be invoked in a thread safe
 * manner.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases of Coconut Cache.
 * <p>
 * This is class is thread-safe.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DefaultCacheManagementService extends AbstractCacheLifecycle implements
        CacheManagementService, CompositeService {

    /** The Managed root group. */
    private final ManagedGroup root;

    /** Used to register all services. */
    private final ManagedVisitor registrant;

    /** Whether or not this service has been shutdown. */
    private volatile boolean isShutdown;

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
        if (configuration == null) {
            throw new NullPointerException("configuration is null");
        } else if (cacheName == null) {
            throw new NullPointerException("cacheName is null");
        }

        /* Set Management Root */
        // We probably want to lock the group, when shutting down the cache.
        root = new DefaultManagedGroup(cacheName, "This group contains all managed Cache services")
        {
            @Override
            protected void beforeMutableOperation() {
                if (isShutdown) {
                    throw new IllegalStateException("Cache has been shutdown");
                }
            }
        };

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
            registrant = Managements.hierarchicalRegistrant(server, domain, "name", "service",
                    "group");
        } else {
            registrant = configuration.getRegistrant();
        }
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void initialize(CacheLifecycle.Initializer cli) {
        cli.registerService(CacheManagementService.class, ManagementUtils.wrapService(this));
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void shutdown(Shutdown shutdown) throws JMException {
        try {
            Managements.unregister().traverse(root);
        } finally {
            isShutdown = true;
        }
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void started(Cache<?, ?> cache) {
        ManagedGroup g = root.addChild(CacheMXBean.MANAGED_SERVICE_NAME,
                "General cache attributes and operations");
        g.add(ManagementUtils.wrapMXBean(cache));
        try {
            registrant.traverse(root);
        } catch (JMException e) {
            throw new CacheException(e);
        }
    }

    /** {@inheritDoc} */
    public Collection<?> getChildServices() {
        return Arrays.asList(registrant);
    }

    /** {@inheritDoc} */
    public ManagedGroup add(Object o) {
        return root.add(o);
    }

    /** {@inheritDoc} */
    public ManagedGroup addChild(String name, String description) {
        return root.addChild(name, description);
    }

    /** {@inheritDoc} */
    public Collection<ManagedGroup> getChildren() {
        return root.getChildren();
    }

    /** {@inheritDoc} */
    public String getDescription() {
        return root.getDescription();
    }

    /** {@inheritDoc} */
    public ObjectName getObjectName() {
        return root.getObjectName();
    }

    /** {@inheritDoc} */
    public Collection<?> getObjects() {
        return root.getObjects();
    }

    /** {@inheritDoc} */
    public ManagedGroup getParent() {
        return root.getParent();
    }

    /** {@inheritDoc} */
    public MBeanServer getServer() {
        return root.getServer();
    }

    /** {@inheritDoc} */
    public boolean isRegistered() {
        return root.isRegistered();
    }

    /** {@inheritDoc} */
    public void register(MBeanServer server, ObjectName objectName) throws JMException {
        root.register(server, objectName);
    }

    /** {@inheritDoc} */
    public void remove() {
        root.remove();
    }

    /** {@inheritDoc} */
    public void unregister() throws JMException {
        root.unregister();
    }

    @Override
    public String toString() {
        return "Management Service";
    }

    public String getName() {
        return root.getName();
    }
}
