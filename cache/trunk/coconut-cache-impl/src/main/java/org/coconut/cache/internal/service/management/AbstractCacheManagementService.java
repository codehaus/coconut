/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.management;

import org.coconut.cache.Cache;
import org.coconut.cache.internal.service.service.AbstractInternalCacheService;
import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheManagementService extends AbstractInternalCacheService
		implements CacheManagementService {
	/**
	 * 
	 */
	public AbstractCacheManagementService() {
		super(CacheManagementConfiguration.SERVICE_NAME);
	}
	
	
	static class DelegatedManagementService implements CacheManagementService {
		private final CacheManagementService delegate;

		/**
         * @param name
         */
		public DelegatedManagementService(CacheManagementService service) {
		    if (service == null) {
                throw new NullPointerException("service is null");
            }
			this.delegate = service;
		}

		/**
         * @see org.coconut.cache.service.management.CacheManagementService#getRoot()
         */
		public ManagedGroup getRoot() {
			return delegate.getRoot();
		}
	}
	
	static class CacheMXBeanWrapper implements CacheMXBean {
	    private final Cache<?,?> cache;

        CacheMXBeanWrapper(Cache<?, ?> cache) {
            if (cache == null) {
                throw new NullPointerException("cache is null");
            }
            this.cache = cache;
        }

        public void clear() {
            cache.clear();
        }

        public void evict() {
            cache.evict();
        }

        public long getCapacity() {
            return cache.getCapacity();
        }

        public String getName() {
            return cache.getName();
        }

        public int getSize() {
            return cache.size();
        }
	}
}
