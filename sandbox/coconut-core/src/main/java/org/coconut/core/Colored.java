/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package coconut.core;

/**
 * A mix-in style interface for marking objects with a specific color that can
 * be used for controlling concurrency.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: Colored.java,v 1.2 2005/01/19 16:25:10 kasper Exp $
 */
public interface Colored {
    /**
     * Returns the color of the object.
     * 
     * @return the color of the object
     */
    int getColor();
}
