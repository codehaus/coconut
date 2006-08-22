/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import org.coconut.aio.monitor.ServerSocketMonitor;
import org.coconut.core.EventHandler;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@SuppressWarnings("unchecked")
public class AsyncServerSocketCloseTest extends AioTestCase {

    public void testCloseFuture() throws IOException {
        AsyncServerSocket socket = getFactory().openServerSocket();
        assertTrue(socket.isOpen());
        socket.close().getIO();
        assertFalse(socket.isOpen());
    }

    public void testCloseCallback() throws InterruptedException, IOException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AsyncServerSocket socket = getFactory().openServerSocket(OWN_THREAD);

        socket.close().setCallback(createCallbackCompleted(latch));
        awaitOnLatch(latch);

        assertFalse(socket.isOpen());
    }

    public void testCloseOfferable() throws InterruptedException, IOException {
        final BlockingQueue q = new LinkedBlockingQueue();
        final AsyncServerSocket socket = getFactory().openServerSocket();

        socket.close().setDestination(createQueueOfferableOnce(q));

        Object o = awaitOnQueue(q);
        assertTrue(o instanceof AsyncServerSocket.Closed);
        AsyncServerSocket.Closed c = (AsyncServerSocket.Closed) o;
        assertNull(c.getCause());

        assertFalse(socket.isOpen());
    }

    public void testCloseHandler() throws IOException, InterruptedException {

        final AsyncServerSocket socket = getFactory().openServerSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final EventHandler h = createQueueHandlerOnce(q);

        assertNull(socket.getCloseHandler());

        socket.setCloseHandler(h);
        assertSame(h, socket.getCloseHandler());
        socket.close().getIO();

        assertSame(socket, awaitOnQueue(q));

    }

    public void testCloseMonitor() throws IOException, InterruptedException {

        final AsyncServerSocket socket = getFactory().openServerSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        assertNull(socket.getMonitor());

        ServerSocketMonitor m = new ServerSocketMonitor() {
            public void closed(AsyncServerSocket s, Throwable error) {
                if (error == null)
                    q.add(s);
            }
        };

        assertSame(socket, socket.setMonitor(m));
        assertSame(m, socket.getMonitor());

        socket.close().getIO();
        assertSame(socket, awaitOnQueue(q));
    }

    public void testCloseErrorneous() throws IOException, ClosedChannelException, InterruptedException {
        final int port = getNextPort();
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final EventHandler h = createQueueHandlerOnce(q);
        final SocketAddress adr = createBindingAddress(port);

        socket.bind(adr);
        socket.setCloseHandler(h);

        socket.startAccepting(IGNORE_OFFERABLE, new AcceptPolicy() {
            public int acceptNext(AsyncServerSocket arg0) {
            	throw new IllegalStateException();
            }
        });

        final SocketChannel channel = SocketChannel.open();
        try {
        	channel.connect(createConnectAddress(port));
		} catch (Exception e) {
			//ignore linux throws SocketException:Connection reset by peer
		}
        
        try {
            assertSame(socket, awaitOnQueue(q));
        } finally {
            socket.close().getIO();
            channel.close();
        }
    }

    public void testCloseErrorneousHandler() throws IOException, ClosedChannelException, InterruptedException {

        final int port = getNextPort();
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final EventHandler h = createQueueErroneousHandlerOnce(q);
        final SocketAddress adr = createBindingAddress(port);

        socket.bind(adr);
        socket.setCloseHandler(h);

        socket.startAccepting(IGNORE_OFFERABLE, new AcceptPolicy() {
            public int acceptNext(AsyncServerSocket s) {
                throw new IllegalStateException();
            }
        });

        final SocketChannel channel = SocketChannel.open();
        try {
        	channel.connect(createConnectAddress(port));
		} catch (Exception e) {
			//ignore linux throws SocketException:Connection reset by peer
		}
        try {
            Object[] pair = (Object[]) awaitOnQueue(q);
            assertSame(socket, pair[0]);
            assertTrue(pair[1] instanceof IllegalStateException);
        } finally {
            socket.close().getIO();
            channel.close();
        }
    }

    public void testCloseMonitorErroneous() throws IOException, InterruptedException {

        final int port = getNextPort();
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final EventHandler h = createQueueErroneousHandlerOnce(q);
        final SocketAddress adr = createBindingAddress(port);

        socket.bind(adr);
        socket.setCloseHandler(h);

        socket.startAccepting(IGNORE_OFFERABLE, new AcceptPolicy() {
            public int acceptNext(AsyncServerSocket arg0) {
                throw new IllegalStateException();
            }
        });

        ServerSocketMonitor m = new ServerSocketMonitor() {
            public void closed(AsyncServerSocket s, Throwable error) {
                q.add(new Object[] {s, error});
            }
        };

        assertSame(socket, socket.setMonitor(m));
        assertSame(m, socket.getMonitor());

        final SocketChannel channel = SocketChannel.open();
        try {
        	channel.connect(createConnectAddress(port));
		} catch (Exception e) {
			//ignore linux throws SocketException:Connection reset by peer
		}

        Object[] p = (Object[]) awaitOnQueue(q);
        assertSame(socket, p[0]);
        assertTrue(p[1] instanceof IllegalStateException);

        channel.close();
        socket.close().getIO();

    }

}