/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.seda.pipeline;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.coconut.event.Stage;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AbstractStage implements Stage {

    private final String name;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * @param name
     *            the name of the stage
     */
    public AbstractStage(final String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        this.name = name;
    }

    /**
     * @see org.coconut.event.seda.Stage#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * @see org.coconut.event.seda.Stage#readLock()
     */
    public Lock readLock() {
        return lock.readLock();
    }

    /**
     * @see org.coconut.event.seda.Stage#writeLock()
     */
    public Lock writeLock() {
        return lock.writeLock();
    }

}
