/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.management.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to mark methods that should be exposed via JMX. Should only be used
 * on JavaBean getters or setters.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: ManagedAttribute.java 473 2007-11-19 12:58:01Z kasper $
 */
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ManagedAttribute {

    /**
     * The name of the attribute.
     */
    String defaultValue() default "";

    /**
     * The description of the atttribute.
     */
    String description() default "";

    /**
     * Whether or not this attribute is write only. Should only be used on a setter.
     */
    boolean isWriteOnly() default false;
}
