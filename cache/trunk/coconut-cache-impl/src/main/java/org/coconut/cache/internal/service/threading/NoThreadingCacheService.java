/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.threading;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class NoThreadingCacheService implements InternalCacheThreadingService{

    /**
     * @see org.coconut.cache.internal.service.threading.InternalCacheThreadingService#isActive()
     */
    public boolean isActive() {
        return false;
    }

    /**
     * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
     */
    public void execute(Runnable command) {
       command.run();//??
    }

}
