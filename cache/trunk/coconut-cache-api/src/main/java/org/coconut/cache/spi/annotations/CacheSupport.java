/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.spi.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheSupport {
    boolean CacheEntrySupport() default false;
    boolean CacheWideLockSupport() default false;
    boolean CacheEntryLockSupport() default false;
    boolean CacheLoadingSupport() default false;
    boolean CacheStoreSupport() default false; 
    boolean ExpirationSupport() default false;
}
