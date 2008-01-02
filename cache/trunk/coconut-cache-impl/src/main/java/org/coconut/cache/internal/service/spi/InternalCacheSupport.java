/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.spi;

import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.EntryMap;
import org.coconut.cache.internal.service.eviction.EvictionSupport;
import org.coconut.cache.internal.service.loading.LoadSupport;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface InternalCacheSupport<K, V> extends LoadSupport<K, V>, EvictionSupport {

    V put(K key, V value, AttributeMap attributes);

    void putAll(Map<? extends K, ? extends V> keyValues, Map<? extends K, AttributeMap> attributes);

    void purgeExpired();

    void checkRunning(String operation);

    void checkRunning(String operation, boolean shutdown);

    EntryMap<K, V> getEntryMap();
}
