/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark.sample;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.NeedsRefreshException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class OSCacheTester {
    public static void main(String[] args) throws NeedsRefreshException {
        Logger.getLogger("com.opensymphony.oscache.base.algorithm.AbstractConcurrentReadCache").setLevel(Level.OFF);
        Logger.getLogger("com.opensymphony.oscache.base.Cache").setLevel(Level.OFF);

        Cache c = new Cache(true, false, false);
        c.putInCache("foo", "doo");
        System.out.println(c.getFromCache("foo"));
    }
}
