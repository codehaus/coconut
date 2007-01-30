/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

/**
 * A mix-in style interface that can be used to define an order between objects.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Sequenced.java 200 2007-01-25 17:04:12Z kasper $
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
