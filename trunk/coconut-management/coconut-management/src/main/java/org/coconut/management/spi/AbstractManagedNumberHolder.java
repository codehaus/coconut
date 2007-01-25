/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.spi;

import org.coconut.core.EventProcessor;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractManagedNumberHolder extends AbstractApmNumber implements
        Runnable, EventProcessor<Number> {
    private final Number n;

    public AbstractManagedNumberHolder() {
        this.n = null;
    }

    public AbstractManagedNumberHolder(Number n) {
        this.n = n;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        if (n == null) {
            throw new IllegalStateException("No Number configured for object");
        }
        update(n);
    }

    /**
     * @see org.coconut.core.EventHandler#handle(java.lang.Object)
     */
    public void process(Number event) {
        if (n != null) {
            throw new IllegalStateException("Number configured for object");
        }
        update(event);
    }

    protected abstract void update(Number value);
}
