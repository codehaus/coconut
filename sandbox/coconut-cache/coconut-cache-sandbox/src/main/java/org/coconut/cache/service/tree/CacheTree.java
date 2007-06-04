/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.tree;

import java.util.Collection;

import org.coconut.cache.Cache;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheTree<K, V> extends Cache<K, V> {
    String getShortName();
    CacheTree<K, V> getChild(String shortName);
    Collection<? extends CacheTree<K, V>> getAllChildren();
}
