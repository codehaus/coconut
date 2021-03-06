/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used on a {@link org.coconut.cache.Cache} implementation to document what type of
 * services the cache supports.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
/*@Documented*/
public @interface CacheServiceSupport {
    /**
     * Returns the type of services the cache implementation supports.
     */
    Class[] value();
}
