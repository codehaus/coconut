/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute;

/**
 * An mix-in interface for objects that have attributes attached.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface WithAttributes {
    /**
     * Returns a map of the objects attributes.
     *
     * @return a map of the objects attributes
     */
    AttributeMap getAttributes();
}
