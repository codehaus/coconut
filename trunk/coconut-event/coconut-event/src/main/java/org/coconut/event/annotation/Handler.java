/*
 * Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */

package org.coconut.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for marking methods that can be targeted for event delivery.
 * <p>
 * The following class specifies two methods that can be used for event
 * delivery: canHandleNumbers and canHandleStrings. TODO: expand
 * 
 * <pre>
 * public class SquareHandler {
 *     @Handler
 *     public void handlesNumbers(Number n) {
 *         System.out.println(n * n);
 *     }
 *     @Handler
 *     public void handlesStrings(String n) {
 *         System.out.println(n);
 *     }
 *     public void DoesNotHandleAnything(String n) {
 *     //ignore
 *     }
 * }
 * </pre>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD) 
public @interface Handler {
    
}
