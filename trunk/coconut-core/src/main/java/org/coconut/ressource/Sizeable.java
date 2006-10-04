/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.ressource;

/**
 * A mix-in style interface for marking objects that has a has a certain size.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface Sizeable {
    /**
     * Returns the size of the ressource.
     * 
     * @return the size of the ressource.
     */
    long getSize();
}
