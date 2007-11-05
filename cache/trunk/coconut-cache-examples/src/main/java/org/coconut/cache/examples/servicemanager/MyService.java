/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.servicemanager;

import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class MyService extends AbstractCacheLifecycle {
    public MyService(String name) {
        super(name);
    }

//    @Override
//    public void initialize(CacheConfiguration<?, ?> configuration) {
//        if (configuration.management().isEnabled()) {
//            configuration.management().getRoot().addChild("ServiceName",
//                    "Description of service");
//        }
//    }

}
