/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.AssertionFailedError;

import org.coconut.aio.monitor.ServerSocketMonitor;
import org.coconut.core.EventProcessor;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@SuppressWarnings("unchecked")
public class AsyncServerSocketAcceptTest extends AioTestCase {

    public void testAccept1() throws IOException, InterruptedException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final int port = getNextPort();

        socket.bind(createBindingAddress(port));
        socket.startAccepting(OWN_THREAD, createCallbackCompleted(q)).getIO();

        SocketChannel s1 = SocketChannel.open();
        s1.connect(createConnectAddress(port));

        AsyncSocket s = ((AsyncSocket) awaitOnQueue(q));
        assertNull(s.getDefaultDestination());
        assertNull(s.getDefaultExecutor());
        assertTrue(s.isOpen());
        assertTrue(s.isBound());
        //s.isConnected() TODO fix

        s.closeNow().getIO();

        s1.close();
        socket.close().getIO();
    }

    public void testAcceptOfferable1() throws IOException, InterruptedException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final int port = getNextPort();

        socket.bind(createBindingAddress(port));
        socket.startAccepting(createQueueOfferableOnce(q));

        SocketChannel s1 = SocketChannel.open();
        s1.connect(createConnectAddress(port));

        AsyncServerSocket.SocketAccepted s = ((AsyncServerSocket.SocketAccepted) awaitOnQueue(q));
        assertSame(socket, s.async());
        assertTrue(s.getAcceptedSocket().isOpen());
        assertTrue(s.getAcceptedSocket().isBound());
        //s.isConnected() TODO fix

        s.getAcceptedSocket().closeNow().getIO();

        s1.close();
        socket.close().getIO();
    }

    public void testAccept100() throws IOException, InterruptedException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final int port = getNextPort();

        socket.bind(createBindingAddress(port));
        socket.startAccepting(OWN_THREAD, createCallbackCompleted(q));

        SocketChannel[] channels = new SocketChannel[100];
        for (int i = 0; i < 100; i++) {
            channels[i] = SocketChannel.open();
            channels[i].connect(createConnectAddress(port));
        }

        for (int i = 0; i < 100; i++) {
            ((AsyncSocket) awaitOnQueue(q)).closeNow().getIO();
            channels[i].close();
        }

        socket.close().getIO();
    }

    public void testBlockingAccept() throws IOException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final int port = getNextPort();

        socket.bind(createBindingAddress(port));

        SocketChannel s1 = SocketChannel.open();
        s1.connect(createConnectAddress(port));

        AsyncSocket newSocket = socket.accept();

        assertTrue(newSocket.isOpen());
        assertTrue(newSocket.isBound());

        s1.close();
        newSocket.closeNow().getIO();
        socket.close().getIO();
    }

    public void testAcceptGroupSettings() throws IOException, InterruptedException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final AsyncSocketGroup group = getFactory().openSocketGroup();
        final BlockingQueue q = new LinkedBlockingQueue();
        final int port = getNextPort();

        socket.bind(createBindingAddress(port));
        socket.startAccepting(OWN_THREAD, createCallbackCompleted(q));

        group.setDefaultExecutor(OWN_THREAD);
        group.setDefaultDestination(IGNORE_OFFERABLE);
        socket.setDefaultSocketGroup(group);

        SocketChannel s1 = SocketChannel.open();
        s1.connect(createConnectAddress(port));

        AsyncSocket s = ((AsyncSocket) awaitOnQueue(q));
        assertSame(IGNORE_OFFERABLE, s.getDefaultDestination());
        assertSame(OWN_THREAD, s.getDefaultExecutor());
        s.closeNow();
        s1.close();
        socket.close().getIO();
    }

    public void testAcceptMonitor() throws IOException, InterruptedException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final int port = getNextPort();

        ServerSocketMonitor m = new ServerSocketMonitor() {
            public void accepted(AsyncServerSocket socket, AsyncSocket acceptedSocket) {
                q.add(new Object[] {socket, acceptedSocket});
            }
        };
        socket.bind(createBindingAddress(port));
        socket.startAccepting(OWN_THREAD, IGNORE_CALLBACK);
        socket.setMonitor(m);

        SocketChannel s1 = SocketChannel.open();
        s1.connect(createConnectAddress(port));

        Object[] s = ((Object[]) awaitOnQueue(q));
        assertSame(socket, s[0]);
        assertTrue(s[1] instanceof AsyncSocket);
        ((AsyncSocket) s[1]).closeNow().getIO();

        s1.close();
        socket.close().getIO();
    }

    public void testAcceptMonitorErrorneous() throws IOException, InterruptedException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final EventProcessor<AsyncServerSocket> h = createQueueHandlerOnce(q);
        final int port = getNextPort();

        ServerSocketMonitor m = new ServerSocketMonitor() {
            public void accepted(AsyncServerSocket socket, AsyncSocket acceptedSocket) {
                try {
                    acceptedSocket.closeNow().getIO();
                } catch (IOException e) {
                    System.out.println("createQueueErroneousHandlerOnce called twice");
                    (new Exception()).printStackTrace();
                    throw new AssertionFailedError();
                }
                throw new IllegalStateException();
            }
        };
        socket.bind(createBindingAddress(port));
        socket.setCloseHandler(h);
        socket.startAccepting(OWN_THREAD, IGNORE_CALLBACK);
        socket.setMonitor(m);

        SocketChannel s1 = SocketChannel.open();
        s1.connect(createConnectAddress(port));

        AsyncServerSocket s = ((AsyncServerSocket) awaitOnQueue(q));
        assertSame(socket, s);

        s1.close();
        socket.close().getIO();
    }

}