/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.tree;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheTreeService<K, V> {
    // key is name;
    // fullname=parent_name.conf.getName();

    CacheTree<K, V> addChild(CacheTree<K, V> parent, CacheConfiguration<K, V> conf);

    boolean removeChild(CacheTree<?, ?> cache);

    void removeAllChildren(CacheTree<?, ?> cache);

    boolean isRoot();

    int getLevel();

    Cache<K, V> getRoot();

    Cache<K, V> getParent();
}
