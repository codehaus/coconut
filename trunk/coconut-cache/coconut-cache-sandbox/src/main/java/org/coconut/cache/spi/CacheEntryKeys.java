/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import org.coconut.cache.CacheEntry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheEntryKeys {

    public final static String LAST_LOAD_TIME = "last_load_time";

    public final static String STORAGE_SIZE_IN_BYTES = "last_load_time";

    /* including attributes and various */
    public final static String TOTAL_STORAGE_SIZE_IN_BYTES = "last_load_time";

    public static <K, V> Long getLastLoadTime(CacheEntry<K, V> ce) {
        // Long value = (Long) ce.getAttributes().get(LAST_LOAD_TIME);
        Long value = 0l;
        return value == null ? -1 : value;
    }
}
