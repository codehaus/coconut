/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.seda.pipeline;

import java.util.concurrent.BlockingQueue;

import org.coconut.concurrent.CapacityArrayQueue;
import org.coconut.event.Stage;
import org.coconut.internal.util.Queues;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractLinearPipeline implements LinearPipeline {

    final CapacityArrayQueue inputQueue;

    final BlockingQueue outputQueue;

    AbstractLinearPipeline() {
        inputQueue = null;
        outputQueue = null;
    }
//
//    AbstractLinearPipeline(BlockingQueue outputQueue) {
//        if (outputQueue == null) {
//            throw new NullPointerException("outputQueue is null");
//        }
//        this.outputQueue = outputQueue;
//        this.inputQueue = null;
//    }

    AbstractLinearPipeline(boolean useInputQueue, BlockingQueue<?> outputQueue) {
        if (outputQueue == null) {
            throw new NullPointerException("outputQueue is null");
        }
        this.outputQueue = outputQueue;
        this.inputQueue = useInputQueue ? new CapacityArrayQueue(30) : null;
    }

    /**
     * @see org.coconut.event.seda.pipeline.LinearPipeline#getLength()
     */
    public int getLength() {
        return getStages().size();
    }

    public BlockingQueue<?> getInputQueue() {
        // if (inputQueue == null) {
        // throw new IllegalStateException(
        // "Pipeline was not initialized with an input queue");
        // }
        return inputQueue == null ? null : Queues.noOutputSide(inputQueue);
    }

    public BlockingQueue<?> getOutputQueue() {
        // if (outputQueue == null) {
        // throw new IllegalStateException(
        // "Pipeline was not initialized with an output queue");
        // }
        return outputQueue == null ? null : outputQueue;
        // return outputQueue;
    }

    /**
     * @see org.coconut.event.seda.StageManager#getStage(java.lang.String)
     */
    public Stage getStage(String name) {
        for (Stage s : getStages()) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

}
