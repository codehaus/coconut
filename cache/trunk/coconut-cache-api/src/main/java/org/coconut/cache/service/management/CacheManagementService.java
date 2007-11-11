/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.management;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheServices;
import org.coconut.management.ManagedGroup;

/**
 * This is the main interface for controlling the remote management of a cache at runtime.
 * 
 * <p>
 * An instance of this interface can be retrieved by using {@link Cache#getService(Class)}
 * to look it up.
 * 
 * <pre>
 * Cache&lt;?, ?&gt; c = someCache;
 * CacheEvictionService&lt;?, ?&gt; ces = c.getService(CacheEvictionService.class);
 * ces.trimToSize(10);
 * </pre>
 * 
 * Or by using {@link CacheServices}
 * 
 * <pre>
 * Cache&lt;?, ?&gt; c = someCache;
 * CacheEvictionService&lt;?, ?&gt; ces = CacheServices.eviction(c);
 * ces.setMaximumSize(10000);
 * </pre>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CacheManagementService extends ManagedGroup {

}
