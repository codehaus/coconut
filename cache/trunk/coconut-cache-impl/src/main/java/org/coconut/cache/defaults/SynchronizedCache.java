/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.spi.CacheServiceSupport;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@ThreadSafe
@CacheServiceSupport( { CacheEventService.class, CacheManagementService.class })
public class SynchronizedCache<K, V> extends UnsynchronizedCache<K, V>  {
    //this class only override UnsynchronizedCache to make it compile when
    //we change the Cache interface
    


}
