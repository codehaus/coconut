/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.management.CacheMXBean;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCache2<K, V> extends AbstractCache<K, V> {

    /**
     * Returns <tt>true</tt> if this cache has been shut down.
     * 
     * @return <tt>true</tt> if this cache has been shut down
     */
    public abstract boolean isShutdown();

    /**
     * Returns <tt>true</tt> if the cache tasks and all its associated
     * services have completed following shut down. Note that
     * <tt>isTerminated</tt> is never <tt>true</tt> unless either
     * <tt>shutdown</tt> was called first.
     * 
     * @return <tt>true</tt> if the cache have completed following shut down
     */
    public abstract boolean isTerminated();

    /**
     * Blocks until the cache and all its associated services have completed
     * after a shutdown request, or the timeout occurs, or the current thread is
     * interrupted, whichever happens first.
     * 
     * @param timeout
     *            the maximum time to wait
     * @param unit
     *            the time unit of the timeout argument
     * @return <tt>true</tt> if this cache terminated and <tt>false</tt> if
     *         the timeout elapsed before termination
     * @throws InterruptedException
     *             if interrupted while waiting
     */
    public abstract boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException;
    

    /**
     * Returns the CacheMXBean for this cache.
     * 
     * @throws UnsupportedOperationException
     *             if this operation is not supported
     */
    public CacheMXBean getMXBean() throws Exception {
        throw new UnsupportedOperationException(
                "This cache does not support jmx monitoring");
    }

//  /**
//  * Returns a unique id for this cache.
//  */
//  public UUID getID() {
//  return id;
//  }
 //
//  /**
//  * This can be overridden to provide custom handling for cases where the
//  * cache is unable to find a mapping for a given key. This can be used,
//  for
//  * example, to provide a failfast behaviour if the cache is supposed to
//  * contain a value for any given key.
//  *
//  * <pre>
//  * public class MyCacheImpl&lt;K, V&gt; extends AbstractCache&lt;K, V&gt;
//  {
//  * protected V handleNullGet(K key) {
//  * throw new CacheRuntimeException(&quot;No value defined for Key
//  [key=&quot; + key + &quot;]&quot;);
//  * }
//  * }
//  * </pre>
//  *
//  * @param key
//  * the key for which no value could be found
//  * @return <tt>null</tt> or any value that should be used instead
//  */
//  protected V handleNullGet(K key) {
//  return null; // by default just return null
//  }

}
