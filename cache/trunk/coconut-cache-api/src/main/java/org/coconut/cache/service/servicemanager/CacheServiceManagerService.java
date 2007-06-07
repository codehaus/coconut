/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import java.util.concurrent.TimeUnit;


/**
 * Move getAll services to here?
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheServiceManagerService {
    void registerService(CacheService lifecycle);
    
    boolean    awaitTermination(long timeout, TimeUnit unit);
    boolean    isShutdown();
    boolean    isTerminated();
    void   shutdown();
}
