package org.coconut.aio;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@SuppressWarnings("unchecked")
public class AsyncServerSocketStopAcceptingTest extends AioTestCase {

    public void testStopNoAcceptOkay() throws IOException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        socket.stopAccepting().getIO();
        socket.close().getIO();
    }

    public void testStopAcceptingFuture() throws IOException, InterruptedException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final int port = getNextPort();

        socket.bind(createBindingAddress(port));

        socket.startAccepting(OWN_THREAD, createCallbackCompleted(q));

        SocketChannel s1 = SocketChannel.open();
        s1.connect(createConnectAddress(port));
        ((AsyncSocket) awaitOnQueue(q)).closeNow().getIO();
        s1.close();

        socket.stopAccepting().getIO();

        SocketChannel s2 = SocketChannel.open();
        s2.connect(createConnectAddress(port));
        assertNull(q.poll(200, TimeUnit.MILLISECONDS));
        s2.close();
        socket.close().getIO();
    }

    public void testStopAcceptingOfferable() throws IOException, InterruptedException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final BlockingQueue q1 = new LinkedBlockingQueue();
        final int port = getNextPort();

        socket.bind(createBindingAddress(port));

        socket.startAccepting(OWN_THREAD, createCallbackCompleted(q));

        //connect first socket
        SocketChannel s1 = SocketChannel.open();
        s1.connect(createConnectAddress(port));
        ((AsyncSocket) awaitOnQueue(q)).closeNow().getIO();
        s1.close();

        //stop accepting
        assertTrue(socket.isAccepting());
        socket.stopAccepting().setDestination(createQueueOfferableOnce(q1));

        Object o = awaitOnQueue(q1);
        assertFalse(socket.isAccepting());
        assertTrue(o instanceof AsyncServerSocket.AcceptingStopped);
        AsyncServerSocket.AcceptingStopped c = (AsyncServerSocket.AcceptingStopped) o;
        assertSame(socket, c.async());

        //try connect an additional socket
        SocketChannel s2 = SocketChannel.open();
        s2.connect(createConnectAddress(port));
        assertNull(q.poll(200, TimeUnit.MILLISECONDS));
        s2.close();

        socket.close().getIO();
    }

}