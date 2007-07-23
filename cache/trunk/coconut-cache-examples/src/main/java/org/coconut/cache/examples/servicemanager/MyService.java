/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.servicemanager;

import java.util.Map;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.servicemanager.AbstractCacheService;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class MyService extends AbstractCacheService {
    public MyService(String name) {
        super(name);
    }

    @Override
    public void initialize(CacheConfiguration<?, ?> configuration,
            Map<Class<?>, Object> serviceMap) {
        if (configuration.management().isEnabled()) {
            configuration.management().getRoot().addChild("ServiceName",
                    "Description of service");
        }
    }

}
