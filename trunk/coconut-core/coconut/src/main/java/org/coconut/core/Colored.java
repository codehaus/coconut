/*
 * Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */

package org.coconut.core;

/**
 * A mix-in style interface for marking objects with a specific color that can
 * be used for controlling concurrency.
 * 
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
