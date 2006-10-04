/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.seda.pipeline;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Processor {
    
    private volatile boolean isDone=false;
    private LightweightPipeline lp;

    static class MyStage {
        EventProcessor ep;
    }
    
    void setDone() {
        isDone=true;
    }
    Runnable getNextTask() {
        if (isDone) {
            return null;
        }
        return null;
    }
    
    //private void 
}
