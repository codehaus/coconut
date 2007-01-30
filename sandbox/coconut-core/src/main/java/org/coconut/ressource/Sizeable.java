/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.ressource;

/**
 * A mix-in style interface for marking objects that has a has a certain size.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Sizeable.java 49 2006-08-29 11:51:54Z kasper $
 */
public interface Sizeable {
    /**
     * Returns the size of the ressource.
     * 
     * @return the size of the ressource.
     */
    long getSize();
}
