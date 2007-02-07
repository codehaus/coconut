/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.util;

import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.spi.XmlConfigurator;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class InternalCacheutil {

    public static boolean isThreadSafe(CacheConfiguration conf) {
        Class c = null;
        String s = (String) conf.getProperty(XmlConfigurator.CACHE_INSTANCE_TYPE);
        try {
            c = Class.forName(s);
        } catch (ClassNotFoundException e1) {
            throw new Error("Could not find class " + s + ", this is highly irregular");
        }
        return c.isAnnotationPresent(ThreadSafe.class);
    }
}
