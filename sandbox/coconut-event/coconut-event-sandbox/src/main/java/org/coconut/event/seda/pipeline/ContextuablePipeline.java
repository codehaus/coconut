/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.seda.pipeline;

import java.util.concurrent.Future;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ContextuablePipeline {

    public Future process(Object o) {
        return null;
    }

    public Future processBlocking(Object o) throws InterruptedException {
        return null;
    }
}
