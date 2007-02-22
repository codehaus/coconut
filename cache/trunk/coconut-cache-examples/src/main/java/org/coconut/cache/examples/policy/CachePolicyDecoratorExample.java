/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.examples.policy;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.Caches;
import org.coconut.cache.policy.Policies;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;
import org.coconut.filter.StringFilters;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CachePolicyDecoratorExample {
    public static void main(String[] args) {
        CacheConfiguration conf = CacheConfiguration.create();
        Filter<String> f = Filters.not(StringFilters.startsWith("https"));
        conf.eviction().setPolicy(Policies.filteredMapKeyPolicy(Policies.newLRU(), f));
    }
}
