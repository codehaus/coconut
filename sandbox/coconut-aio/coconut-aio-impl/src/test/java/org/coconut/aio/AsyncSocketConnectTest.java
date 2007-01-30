/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.coconut.aio.AsyncSocket.Connected;
import org.coconut.aio.monitor.SocketMonitor;


/**
 * Base socket connect test
 * 
 * @version $Id: AsyncSocketConnectTest.java,v 1.1 2004/07/11 22:11:57 kasper
 *          Exp $
 */
@SuppressWarnings("unchecked")
public class AsyncSocketConnectTest extends AioTestCase {

    public void testConnectFuture() throws IOException, InterruptedException {
        final AsyncSocket socket = getFactory().openSocket();
        final int port = getNextPort();
        final BlockingQueue q = new LinkedBlockingQueue();
        final AsyncServerSocket sSocket = getFactory().openServerSocket().bind(createBindingAddress(port));

        sSocket.startAccepting(createQueueOfferableOnce(q));

        assertFalse(socket.isConnected());
        socket.connect(createConnectAddress(port)).getIO();
        assertTrue(socket.isConnected());

        ((AsyncServerSocket.SocketAccepted) awaitOnQueue(q)).getAcceptedSocket().closeNow().getIO();

        socket.closeNow().getIO();
        sSocket.close().getIO();
    }

    public void testConnectCallback() throws IOException, InterruptedException {
        final AsyncSocket socket = getFactory().openSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final BlockingQueue q2 = new LinkedBlockingQueue();
        final int port = getNextPort();
        final AsyncServerSocket sSocket = getFactory().openServerSocket().bind(createBindingAddress(port));

        sSocket.startAccepting(createQueueOfferableOnce(q));

        assertFalse(socket.isConnected());
        socket.connect(createConnectAddress(port)).setCallback(OWN_THREAD, createCallbackCompleted(q2));

        awaitOnQueue(q2);
        assertTrue(socket.isConnected());

        ((AsyncServerSocket.SocketAccepted) awaitOnQueue(q)).getAcceptedSocket().closeNow().getIO();

        socket.closeNow().getIO();
        sSocket.close().getIO();
    }

    public void testConnectOfferable() throws IOException, InterruptedException {
        final AsyncSocket socket = getFactory().openSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final BlockingQueue q2 = new LinkedBlockingQueue();
        final int port = getNextPort();
        final SocketAddress addr = createConnectAddress(port);
        final AsyncServerSocket sSocket = getFactory().openServerSocket().bind(createBindingAddress(port));

        sSocket.startAccepting(createQueueOfferableOnce(q));

        assertFalse(socket.isConnected());
        socket.connect(addr).setDestination(createQueueOfferableOnce(q2));

        Object o = awaitOnQueue(q2);
        assertTrue(o instanceof AsyncSocket.Connected);
        AsyncSocket.Connected ac = (Connected) o;
        assertSame(socket, ac.async());
        assertEquals(addr, ac.getSocketAddress());

        assertTrue(socket.isConnected());

        ((AsyncServerSocket.SocketAccepted) awaitOnQueue(q)).getAcceptedSocket().closeNow().getIO();

        socket.closeNow().getIO();
        sSocket.close().getIO();
    }

    public void testFutureConnectErrorneous() throws UnknownHostException, IOException, TimeoutException {
        final AsyncSocket socket = getFactory().openSocket();
        try {
            socket.connect(createConnectAddress(getNextPort())).getIO(5000, TimeUnit.MILLISECONDS);
        } catch (ConnectException e) {
            assertFalse(socket.isConnected());
            return;
        } finally {
            socket.closeNow();
        }
        fail("Did not timeout");
    }

    public void testCallbackConnectErrorneous() throws IOException, ClosedChannelException, InterruptedException {

//        final AsyncSocket socket = getFactory().openSocket();
//        final BlockingQueue q = new LinkedBlockingQueue();
//        final SocketAddress addr = createConnectAddress(getNextPort());
//
//        //socket.connect(addr).setCallback(OWN_THREAD, createCallbackFailed(q));
//        throw new UnsupportedOperationException("above line uncommented");
//        //assertTrue(awaitOnQueueLong(q) instanceof ConnectException);
//
//        //socket.closeNow();
    }

    public void testOfferableConnectErrorneous() throws IOException, ClosedChannelException, InterruptedException {

        final AsyncSocket socket = getFactory().openSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final SocketAddress addr = createConnectAddress(34534);

        socket.connect(addr).setDestination(createQueueOfferableOnce(q));
        Object o = awaitOnQueueLong(q);

        assertTrue(o instanceof AsyncSocket.ErroneousEvent);
        AsyncSocket.ErroneousEvent ee = (AsyncSocket.ErroneousEvent) o;
        assertEquals(socket, ee.async());
        assertEquals(ee.getMessage(), ee.getCause().getMessage());
        assertTrue(ee.getCause() instanceof ConnectException);
        assertTrue(ee.getEvent() instanceof AsyncSocket.Connected);

        socket.closeNow();
    }

    public void testPortsAndAddress() throws IOException, InterruptedException, ExecutionException {
        final int port = getNextPort();
        final AsyncSocket socket = getFactory().openSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final CountDownLatch r = startAccepting(q, createBindingAddress(port), 1);

        socket.connect(new InetSocketAddress("localhost", port)).get();

        assertEquals(socket.socket().getInetAddress(), socket.getInetAddress());
        assertEquals(socket.socket().getLocalAddress(), socket.getLocalAddress());
        assertEquals(socket.socket().getLocalSocketAddress(), socket.getLocalSocketAddress());
        assertEquals(socket.socket().getRemoteSocketAddress(), socket.getRemoteSocketAddress());
        assertEquals(socket.socket().getLocalPort(), socket.getLocalPort());
        assertEquals(socket.socket().getPort(), socket.getPort());

        ((SocketChannel) awaitOnQueue(q)).close();
        awaitOnLatch(r);
        socket.closeNow();
    }
    public void testConnectAlreadyConnected() throws IOException, ClosedChannelException, InterruptedException,
            TimeoutException {
        final int port = getNextPort();
        final BlockingQueue connectQueue = new LinkedBlockingQueue();
        final CountDownLatch listeningLatch = startAccepting(connectQueue, createBindingAddress(port), 1);
        final AsyncSocket socket = getFactory().openSocket();

        AsyncSocket as = (AsyncSocket) socket.connect(createConnectAddress(port)).getIO();

        awaitOnLatch(listeningLatch);

        assertTrue(socket.isConnected());

        try {
            socket.connect(createConnectAddress(port)).getIO(4000, TimeUnit.MILLISECONDS);
        } catch (IllegalStateException e) {
            assertTrue(e instanceof ConnectionPendingException || e instanceof AlreadyConnectedException);
            return;
        } finally {
            ((SocketChannel) awaitOnQueue(connectQueue)).close();
            as.closeNow();
            socket.closeNow();
        }
        fail("Did not throw an exception");
    }

    public void testConnectMonitor() throws IOException, InterruptedException {
        final AsyncSocket socket = getFactory().openSocket();
        final int port = getNextPort();
        final BlockingQueue q = new LinkedBlockingQueue();
        final BlockingQueue q2 = new LinkedBlockingQueue();
        final SocketAddress address = createConnectAddress(port);
        final AsyncServerSocket sSocket = getFactory().openServerSocket().bind(createBindingAddress(port));

        socket.setMonitor(new SocketMonitor() {
            public void connected(AsyncSocket s, SocketAddress address) {
                q2.add(new Object[] {s, address});
            }
        });

        sSocket.startAccepting(createQueueOfferableOnce(q));

        assertFalse(socket.isConnected());
        socket.connect(address).getIO();
        assertTrue(socket.isConnected());

        Object[] p = (Object[]) awaitOnQueue(q2);

        assertSame(socket, p[0]);
        assertSame(address, p[1]);

        ((AsyncServerSocket.SocketAccepted) awaitOnQueue(q)).getAcceptedSocket().closeNow().getIO();

        socket.closeNow().getIO();
        sSocket.close().getIO();
    }

    public void testConnectMonitorErrorneous() throws IOException, InterruptedException {
        final AsyncSocket socket = getFactory().openSocket();
        final int port = getNextPort();
        final BlockingQueue<Object> q2 = new LinkedBlockingQueue<Object>();
        final SocketAddress address = createConnectAddress(port);

        socket.setMonitor(new SocketMonitor() {
            public void connectFailed(AsyncSocket s, SocketAddress address, Throwable e) {
                q2.add(new Object[] {s, address, e});
            }
        });

        assertFalse(socket.isConnected());
        socket.connect(address);

        Object[] o = (Object[]) awaitOnQueueLong(q2);
        assertSame(socket, o[0]);
        assertEquals(address, o[1]);
        assertTrue(o[2] instanceof ConnectException);
        socket.closeNow().getIO();
    }

    public void testConnectMonitorError() throws IOException, InterruptedException {
        final AsyncSocket socket = getFactory().openSocket();
        final int port = getNextPort();
        final BlockingQueue q = new LinkedBlockingQueue();
        final BlockingQueue q2 = new LinkedBlockingQueue();
        final BlockingQueue q3 = new LinkedBlockingQueue();
        final SocketAddress address = createConnectAddress(port);
        final AsyncServerSocket sSocket = getFactory().openServerSocket().bind(createBindingAddress(port));

        socket.setMonitor(new SocketMonitor() {
            public void connected(AsyncSocket s, SocketAddress address) {
                q2.add(new Object[] {s, address});
                throw new IllegalStateException();
            }
        });

        socket.setCloseHandler(createQueueErroneousHandlerOnce(q3));

        sSocket.startAccepting(createQueueOfferableOnce(q));

        assertFalse(socket.isConnected());
        socket.connect(address).getIO();

        Object[] p = (Object[]) awaitOnQueue(q2);

        assertSame(socket, p[0]);
        assertSame(address, p[1]);

        ((AsyncServerSocket.SocketAccepted) awaitOnQueue(q)).getAcceptedSocket().closeNow().getIO();

        Object[] pair = (Object[]) awaitOnQueueLong(q3);
        assertSame(socket, pair[0]);
        assertTrue(pair[1] instanceof IllegalStateException);

        socket.closeNow().getIO();
        sSocket.close().getIO();
    }

    public void testConnectMonitorErrorneousError() throws IOException, InterruptedException {
        final AsyncSocket socket = getFactory().openSocket();
        final int port = getNextPort();
        final BlockingQueue q2 = new LinkedBlockingQueue();
        final BlockingQueue q3 = new LinkedBlockingQueue();
        final SocketAddress address = createConnectAddress(port);

        socket.setCloseHandler(createQueueErroneousHandlerOnce(q3));

        socket.setMonitor(new SocketMonitor() {
            public void connectFailed(AsyncSocket s, SocketAddress address, Throwable e) {
                q2.add(new Object[] {s, address, e});
                throw new IllegalStateException();
            }
        });

        assertFalse(socket.isConnected());
        socket.connect(address);

        Object[] o = (Object[]) awaitOnQueueLong(q2);
        assertSame(socket, o[0]);
        assertEquals(address, o[1]);
        assertTrue( o[2] instanceof ConnectException);

        Object[] pair = (Object[]) awaitOnQueueLong(q3);
        assertSame(socket, pair[0]);
        assertTrue(pair[1] instanceof IllegalStateException);

        socket.closeNow().getIO();
    }

}