package org.codehaus.cake.test.tck;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.codehaus.cake.container.Container;

/**
 * This class is used to indicate the implementing class of a {@link Container}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheTCKImplementationSpecifier.java 415 2007-11-09 08:25:23Z kasper $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TckImplementationSpecifier {
    /**
     * Returns the class of the cache implementation that should be tested.
     * 
     * @return the class of the cache implementation that should be tested
     */
    Class<? extends Container> value();
}
