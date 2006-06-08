package org.coconut.aio.spi;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.Executor;

import org.coconut.aio.AsyncDatagram;
import org.coconut.aio.AsyncDatagramGroup;
import org.coconut.aio.AsyncFile;
import org.coconut.aio.AsyncServerSocket;
import org.coconut.aio.AsyncSocket;
import org.coconut.aio.AsyncSocketGroup;
import org.coconut.core.Offerable;


/**
 * For people needing separate aio instances
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public interface AsyncFactory {

    AsyncServerSocket openServerSocket() throws IOException;
    AsyncDatagram openDatagram() throws IOException;
    AsyncFile openFile() throws IOException;
    AsyncSocket openSocket() throws IOException;

    AsyncServerSocket openServerSocket(Offerable< ? super AsyncServerSocket.Event> destination)
        throws IOException;
    AsyncDatagram openDatagram(Offerable< ? super AsyncDatagram.Event> destination)
        throws IOException;
    AsyncFile openFile(Offerable< ? super AsyncFile.Event> destination) throws IOException;
    AsyncSocket openSocket(Offerable< ? super AsyncSocket.Event> destination) throws IOException;

    AsyncServerSocket openServerSocket(Executor executor) throws IOException;
    AsyncDatagram openDatagram(Executor executor) throws IOException;
    AsyncFile openFile(Executor executor) throws IOException;
    AsyncSocket openSocket(Executor executor) throws IOException;

    AsyncServerSocket openServerSocket(Queue< ? super AsyncServerSocket.Event> queue)
        throws IOException;
    AsyncDatagram openDatagram(Queue< ? super AsyncDatagram.Event> queue) throws IOException;
    AsyncFile openFile(Queue< ? super AsyncFile.Event> queue) throws IOException;
    AsyncSocket openSocket(Queue< ? super AsyncSocket.Event> queue) throws IOException;

    AsyncDatagramGroup openDatagramGroup();
    AsyncSocketGroup openSocketGroup();

}