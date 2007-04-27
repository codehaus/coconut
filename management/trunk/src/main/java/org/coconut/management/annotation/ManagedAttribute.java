/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to mark methods that should be exposed via JMX. Can
 * only be used on JavaBean getters or setters.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ManagedAttribute {

    /**
     * The name of the attribute
     */
    String defaultValue() default "$methodname";

    /**
     * The description of the atttribute
     */
    String description() default "";

    /**
     * Whether or not this attribute is read only.
     */
    boolean readOnly() default false;
}
