/*
 * Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */

package coconut.filter.annotation;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FilterAcceptTypes {
    Class[] value();
}
