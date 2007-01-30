/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event;

import java.util.List;

import org.coconut.core.Transformer;

/**
 * Two versions
 * <p>
 * one where the first stage produces elements
 * <p>
 * two one where the input side is a blocking queue. The pipeline should
 * automatically balance this queue
 * <p>
 * One where all elements must be processed in order. one thread pr stage.
 * <p>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface LinearPipeline extends StageManager {
    /**
     * Adds a stage with the specified name and transformer to the end of the
     * pipeline.
     * 
     * @param name
     *            the unique name of the stage
     * @param t
     *            the transformer used for transforming elements
     * @return the constructed stage
     */
    Stage addStage(String name, Transformer<?, ?> t);

    //Stage addStage(String name, Pool<? extends Transformer> t);

    /**
     * Returns the length of the pipeline
     * 
     * @return
     */
    int getLength();

    /**
     * This method returns all the stages that has been registered for this
     * pipeline. This method will return the stages in the order they are
     * registered in the pipeline.
     * 
     * @return a <code>List</code> with all registered stages
     */
    List<? extends Stage> getStages();
}
