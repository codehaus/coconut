/*
 * Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */

package org.coconut.filter.spi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * The base annotation for all filter annotations. 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@Target(ElementType.ANNOTATION_TYPE)
public @interface FilterAnnotation {

}
