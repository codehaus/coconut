package org.codehaus.cake.container.lifecycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Stoppable {
    /**
     * Stops the service.
     * 
     * @param shutdown
     *            shutdown helper
     * @throws Exception
     *             if this service could not shutdown properly
     */
}
