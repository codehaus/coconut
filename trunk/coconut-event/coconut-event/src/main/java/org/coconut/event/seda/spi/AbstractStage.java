/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event.seda.spi;

import org.coconut.event.seda.Stage;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class AbstractStage implements Stage {

    private final String name;

    private final int id;

    public AbstractStage(String name) {
        this(name, -1);
    }

    public AbstractStage(String name, int id) {
        this.name = name;
        this.id = id;
    }

    protected int getId() {
        return id;
    }

    /**
     * @see org.coconut.event.seda.Stage#getName()
     */
    public String getName() {
        return name;
    }

    protected abstract Object process(Object event);

    protected abstract Object[] processAll(Object[] events);
    
}
