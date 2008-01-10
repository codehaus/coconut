/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.management.CacheMXBean;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCache2<K, V> extends AbstractCache<K, V> {

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
