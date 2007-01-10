/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults.support;

import java.util.Map;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.cache.spi.service.AbstractCacheService;
import org.coconut.management.Apms;
import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ManagementCacheService<K, V> extends AbstractCacheService<K, V> {

    private final ManagedGroup group;

    public ManagementCacheService(CacheConfiguration<K, V> conf) {
        super(conf);
        String name = "org.coconut.cache:name=" + conf.getName();
                //+ ",group=$1,subgroup=$2";
        group = Apms.newGroup(name, conf.jmx().getMBeanServer());
    }

    public ManagedGroup getGroup() {
        return group;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheService#doStart(org.coconut.cache.spi.AbstractCache,
     *      java.util.Map)
     */
    @Override
    protected void doStart(AbstractCache<K, V> cache, Map<String, Object> properties) {
        if (cache.getConfiguration().jmx().isRegister()) {
            try {
                //,group=$1,subgroup=$2
                group.register("org.coconut.cache:name=$0");
            } catch (Exception e) {
                throw new CacheException("Could not start cache", e);
            }
        }
    }
}
