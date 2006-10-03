/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.spi;

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
    boolean JMXSupport() default false;
    boolean CacheEntrySupport() default false;
    boolean eventSupport() default false;
    boolean CacheWideLockSupport() default false;
    boolean CacheEntryLockSupport() default false;
    boolean CacheLoadingSupport() default false;
    boolean CacheStoreSupport() default false; 
    boolean ExpirationSupport() default false;
    boolean statisticsSupport() default false;
    boolean querySupport() default false;
}
