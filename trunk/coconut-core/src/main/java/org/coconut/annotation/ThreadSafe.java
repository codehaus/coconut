/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate whether or not a class is threadsafe. Most
 * coconut classes uses this annotation
 * <p>
 * TODO: do we want to use another annotation for stages, I really don't want to
 * start explaining why methods are also allowed to be threadsafe/none
 * threadsafe. It can so easily be misused especially regarding memory
 * read-write issues
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@Target( { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ThreadSafe {
    boolean value();
}
