/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.testutil;

import org.coconut.core.EventProcessor;
import org.coconut.core.Offerable;
import org.coconut.core.Transformer;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DevNullHelper<F, T> implements Transformer<F, T>, EventProcessor<F>,Offerable<F> {

    /**
     * @see org.coconut.core.Transformer#transform(F)
     */
    public T transform(F from) {
        return null;
    }

    /**
     * @see org.coconut.core.EventHandler#handle(E)
     */
    public void process(F event) {
        // ignore
    }

    /**
     * @see org.coconut.core.Offerable#offer(java.lang.Object)
     */
    public boolean offer(F element) {
        return true;
    }

}
