/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.service;

import java.util.Map;

import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.AbstractCacheService;
import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractInternalCacheService extends AbstractCacheService  {

	/**
     * @param name
     */
	public AbstractInternalCacheService(String name) {
		super(name);
	}

    @Override
    public void start(Map<Class<?>, Object> allServices) {
        CacheManagementService cms = (CacheManagementService) allServices
                .get(CacheManagementService.class);
        if (cms != null) {
            ManagedGroup group = cms.getRoot();
            registerMXBeans(group);
        }
        super.start(allServices);
    }
    
    protected void registerMXBeans(ManagedGroup root) {
        
    }
}
