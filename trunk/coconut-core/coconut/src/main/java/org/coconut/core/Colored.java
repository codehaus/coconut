/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

/**
 * A mix-in style interface for marking objects with a specific color that can
 * be used for controlling concurrency.
 * 
 * TODO: Replace with something aka partioned
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface Colored {

    /**
     * Returns the color of the object.
     * 
     * @return the color of the object
     */
    int getColor();
}
