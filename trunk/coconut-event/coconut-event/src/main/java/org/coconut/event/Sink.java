package org.coconut.event;

import org.coconut.core.Offerable;

public interface Sink<T> extends Offerable<T> {

    void send(T o) throws SinkException;
    
    //or enqueue 
    
//    public void enqueue(Object o, Callback c);
//    public void enqueueMany(Object... o);
    
    
    //priority
    //time outs
    
    
}
