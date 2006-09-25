package org.coconut.event;

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
