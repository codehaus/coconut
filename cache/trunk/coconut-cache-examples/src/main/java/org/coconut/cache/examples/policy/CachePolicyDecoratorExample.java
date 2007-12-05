/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.policy;

import org.coconut.cache.CacheConfiguration;
import org.coconut.operations.Predicates;
import org.coconut.operations.StringPredicates;
import org.coconut.operations.Ops.Predicate;

/**
 * The following example create a cache configuration that uses a LRU
 * replacement policy and caches all entries except those who have a key
 * starting with <tt>https</tt>. The usage example is, for example, a web
 * agent that is allowed to cache ordinary (http) pages but not secure (https)
 * pages.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CachePolicyDecoratorExample {
    public static void main(String[] args) {
        CacheConfiguration<String, ?> conf = CacheConfiguration.create();
        Predicate<String> f = Predicates.not(StringPredicates.startsWith("https"));
        //conf.eviction().setPolicy(Policies.filteredMapKeyPolicy(Policies.newLRU(), f));
    }
}
