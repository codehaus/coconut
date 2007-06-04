/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.management;

import java.util.Map;

import javax.management.JMException;
import javax.management.MBeanServer;

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
 * {@link AbstractCacheManagementService#getName()} and
 * {@link CacheMXBean#getName()}
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheManagementService extends AbstractCacheManagementService {

	private final String domain;

	private final ManagedGroup group;

	private final InternalCacheServiceManager manager;

	private final ManagedGroupVisitor registrant;

	private final boolean isEnabled;

	private final Cache cache;

	public DefaultCacheManagementService(InternalCacheServiceManager manager,
			CacheManagementConfiguration cmc, Cache cache, String name) {
		this.manager = manager;
		isEnabled = cmc.isEnabled();
		domain = cmc.getDomain();
		if (cmc.getRoot() == null) {
			group = new DefaultManagedGroup(name,
					"This group contains all managed Cache services", false);
		} else {
			group = cmc.getRoot();
		}

		MBeanServer server = cmc.getMBeanServer();

		if (cmc.getRegistrant() == null) {
			registrant = Managements.register(server, domain, "name", "service", "group");
		} else {
			registrant = cmc.getRegistrant();
		}

		if (cache instanceof CacheMXBean) {
			this.cache = cache;
			ManagedGroup g = group.addChild("General",
					"General cache attributes and operations");
			g.add(cache);
		} else {
			this.cache = null;
		}
	}

	/**
     * @see org.coconut.cache.service.management.CacheManagementService#getRoot()
     */
	public ManagedGroup getRoot() {
		if (isEnabled) {
			manager.lazyStart(false);// todo im not sure we need this one
			return group;
		} else {
			// throw exception??
			return null;
		}
	}

	/**
     * @see org.coconut.cache.internal.service.CacheServiceLifecycle#shutdown(org.coconut.cache.internal.service.ShutdownCallback)
     */
	public void shutdown() throws JMException {
		if (isEnabled) {
			group.unregister();
		}
	}

	@Override
    public void start(CacheConfiguration<?, ?> configuration,
            Map<Class<?>, Object> serviceMap) {
        if (isEnabled) {
            serviceMap.put(CacheManagementService.class, new DelegatedManagementService(
                    this));
            if (cache != null) {
                serviceMap.put(CacheMXBean.class, cache);
            }
        }
    }

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
