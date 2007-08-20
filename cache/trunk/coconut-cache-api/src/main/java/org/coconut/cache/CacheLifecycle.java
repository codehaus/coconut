/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

/**
 * An interface which specifies the lifecyle strategy on a object.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheLifecycle {

    /**
     * Initializes the object. This is the first method in the cache lifecycle that is
     * called.
     * <p>
     * This method will be called from within the constructor of the cache. Any runtime
     * exception thrown by this method will not be handled.
     * <p>
     * TODO: do we call terminated, for components whose initialize method has already
     * been run, but where another components initialize method fails.
     * 
     * @param configuration
     *            the CacheConfiguration for the cache that this object belongs to
     */
    void initialize(CacheConfiguration<?, ?> configuration);

    /**
     * All services have been intialized correctly, and the cache is ready for use.
     * 
     * @param cache
     *            the cache that was started
     */
    void started(Cache<?, ?> cache);

    /**
     * This method is invoked as the last method in this lifecycle interface.
     * <p>
     * Method invoked when the Cache has terminated. Note: To properly nest multiple
     * overridings, subclasses should generally invoke <tt>super.terminated</tt> within
     * this method.
     */
    void terminated();
}
