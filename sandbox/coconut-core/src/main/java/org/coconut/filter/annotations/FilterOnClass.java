/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package coconut.filter.annotation;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@FilterAnnotation()
public @interface FilterOnClass {
    Class[] value();
}
