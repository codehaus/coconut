/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.coconut.cache.Cache;

/**
 * This class is used to indicate the implementing class of a {@link Cache}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CacheTCKImplementationSpecifier {
    /**
     * Returns the class of the cache implementation that should be tested.
     * 
     * @return the class of the cache implementation that should be tested
     */
    Class<? extends Cache> value();
}
