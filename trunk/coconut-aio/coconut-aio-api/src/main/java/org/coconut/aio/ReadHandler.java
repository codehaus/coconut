package org.coconut.aio;

import java.io.IOException;
/**
 * Handles socket reads
 *  
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface ReadHandler<E> {

    /**
     * A user can choose to start reading on a particular socket by setting a
     * readhandler. Whenever data becomes available on the socket, this method
     * is called with the socket as an argument.
     * 
     * Note: this call is done in the thread-context of the internal aio
     * subsystem so operation should be short. For example, by handing off the
     * processing to another thread.
     * 
     * If this methods throws any exception during the processing, the socket
     * will close immediatly.
     * 
     * @return An unique socket id
     */
	void handle(E socket) throws IOException;
}
