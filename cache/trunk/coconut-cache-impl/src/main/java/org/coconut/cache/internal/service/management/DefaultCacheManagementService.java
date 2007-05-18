/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.management;

import java.util.concurrent.Executor;

import javax.management.JMException;
import javax.management.MBeanServer;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.internal.service.CacheServiceManager;
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
public class DefaultCacheManagementService extends AbstractCacheManagementService
		implements CacheManagementService {

	private final Cache cache;

	private final String domain;

	private final ManagedGroup group;

	private final CacheServiceManager manager;

	private final ManagedGroupVisitor registrant;

	public DefaultCacheManagementService(CacheServiceManager manager,
			CacheConfiguration conf, CacheManagementConfiguration cmc, Cache cache) {
		this.manager = manager;
		this.cache = cache;
		domain = cmc.getDomain();
		if (cmc.getRoot() == null) {
			group = new DefaultManagedGroup(conf.getName(),
					"This group contains all Cache management serviecs", false);
		} else {
			group = cmc.getRoot();
		}

		MBeanServer server = cmc.getMBeanServer();

		if (cmc.getRegistrant() == null) {
			registrant = Managements.register(server, domain, "name", "service", "group");
		} else {
			registrant = cmc.getRegistrant();
		}
	}

	/**
     * @see org.coconut.cache.internal.service.CacheServiceLifecycle#doStart()
     */
	public void doStart() throws JMException {
		ManagedGroup g = group.addChild("General",
				"General cache attributes and operations");
		if (cache instanceof CacheMXBean) {
			g.add(cache);
		}
	}

	/**
     * @see org.coconut.cache.service.management.CacheManagementService#getRoot()
     */
	public ManagedGroup getRoot() {
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

	@Override
	public void started(Cache<?, ?> cache) {
		try {
			registrant.visitManagedGroup(group);
		} catch (JMException e) {
			throw new CacheException(e);
		}
	}

}
