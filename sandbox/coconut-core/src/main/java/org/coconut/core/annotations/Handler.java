/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package coconut.core.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD) 
public @interface Handler {
    
}
