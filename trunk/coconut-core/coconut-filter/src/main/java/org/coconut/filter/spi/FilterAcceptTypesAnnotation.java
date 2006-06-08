/*
 * Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */

package org.coconut.filter.spi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that can be used on a Filter to specify which types of objects
 * the Filter will accept without throwing ClassCastException.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FilterAcceptTypesAnnotation {
    Class[] value();
}