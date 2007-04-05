/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.examples.policy;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.policy.Policies;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;
import org.coconut.filter.StringFilters;

/**
 * The following example create a cache configuration that uses a LRU
 * replacement policy and caches all entries except those who have a key
 * starting with <tt>https</tt>. The usage example is, for example, a web
 * agent that is allowed to cache ordinary (http) pages but not secure (https)
 * pages.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CachePolicyDecoratorExample {
    public static void main(String[] args) {
        CacheConfiguration<String, ?> conf = CacheConfiguration.create();
        Filter<String> f = Filters.not(StringFilters.startsWith("https"));
        conf.serviceEviction().setPolicy(Policies.filteredMapKeyPolicy(Policies.newLRU(), f));
    }
}
