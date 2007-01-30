/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.core;

import org.coconut.core.Offerable;

public interface Sink<T> extends Offerable<T> {

    /**
     * If for some reason the object cannot be enqueued
     * @param o
     * @throws SinkException
     */
    void send(T o) throws SinkException;
    
    //or enqueue 
    
//    public void enqueue(Object o, Callback c);
//    public void enqueueMany(Object... o);
    
    
    //priority
    //time outs
    
    
}
