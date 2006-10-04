/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

/**
 * A mix-in style interface that can be used to define an order between objects.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface Sequenced {

    /**
     * Returns the sequence id of the object. Usually used for maintaining a
     * ordered collection of events.
     * <p>
     * As a general rule a sequence id is a positiv number.
     * <p>
     * If for some reason it is impossible to generate a sequence id or if it is
     * not needed, {@link LONG.MIN_VALUE} should be returned.
     */
    long getSequenceID();
}
