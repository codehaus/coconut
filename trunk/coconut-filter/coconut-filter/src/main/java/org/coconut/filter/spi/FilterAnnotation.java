/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
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
