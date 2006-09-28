/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event.testutil;

import org.coconut.core.EventHandler;
import org.coconut.core.Offerable;
import org.coconut.core.Transformer;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DevNullHelper<F, T> implements Transformer<F, T>, EventHandler<F>,Offerable<F> {

    /**
     * @see org.coconut.core.Transformer#transform(F)
     */
    public T transform(F from) {
        return null;
    }

    /**
     * @see org.coconut.core.EventHandler#handle(E)
     */
    public void handle(F event) {
        // ignore
    }

    /**
     * @see org.coconut.core.Offerable#offer(java.lang.Object)
     */
    public boolean offer(F element) {
        return true;
    }

}
