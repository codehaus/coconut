/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import org.coconut.aio.monitor.SocketMonitor;
import org.coconut.core.EventProcessor;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@SuppressWarnings("unchecked")
public class AsyncSocketCloseTest extends AioTestCase {

    public void testCloseFuture() throws IOException {
        AsyncSocket socket = getFactory().openSocket();
        assertTrue(socket.isOpen());
        socket.closeNow().getIO();
        assertFalse(socket.isOpen());
    }

    public void testCloseCallback() throws InterruptedException, IOException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AsyncSocket socket = getFactory().openSocket(OWN_THREAD);

        socket.closeNow().setCallback(createCallbackCompleted(latch));
        awaitOnLatch(latch);

        assertFalse(socket.isOpen());
    }

    public void testCloseOfferable() throws InterruptedException, IOException {
        final BlockingQueue q = new LinkedBlockingQueue();

        AsyncSocket socket = getFactory().openSocket();
        socket.closeNow().setDestination(createQueueOfferableOnce(q));

        Object o = awaitOnQueue(q);
        assertTrue(o instanceof AsyncSocket.Closed);
        AsyncSocket.Closed c = (AsyncSocket.Closed) o;
        assertNull(c.getCause());

        assertFalse(socket.isOpen());
    }

    public void testCloseHandler() throws IOException, InterruptedException {

        final AsyncSocket socket = getFactory().openSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final EventProcessor h = createQueueHandlerOnce(q);

        assertNull(socket.getCloseHandler());

        socket.setCloseHandler(h);
        assertSame(h, socket.getCloseHandler());
        socket.closeNow();

        assertSame(socket, awaitOnQueue(q));

    }

    public void testCloseErrorneous() throws IOException, ClosedChannelException, InterruptedException {
        final AsyncSocket socket = getFactory().openSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final EventProcessor h = createQueueHandlerOnce(q);
        final SocketAddress adr = createBindingAddress(getNextPort());
        final SocketChannel channel = SocketChannel.open();

        channel.socket().bind(adr);

        socket.setCloseHandler(h);
        try {
            socket.connect(adr);
            assertSame(socket, awaitOnQueue(q));
        } finally {
            channel.close();
            socket.closeNow();
        }
    }

    public void testCloseErrorneousHandler() throws IOException, ClosedChannelException, InterruptedException {

        final AsyncSocket socket = getFactory().openSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final EventProcessor h = createQueueErroneousHandlerOnce(q);
        final SocketAddress adr = createBindingAddress(getNextPort());
        final SocketChannel channel = SocketChannel.open();

        channel.socket().bind(adr);

        socket.setCloseHandler(h);
        try {
            socket.connect(adr);
            Object[] pair = (Object[]) awaitOnQueue(q);
            assertSame(socket, pair[0]);
            //((Throwable) pair.getSecond()).printStackTrace();
            assertTrue(pair[1] instanceof BindException || pair[1] instanceof ConnectException);
        } finally {
            channel.close();
            socket.closeNow();
        }
    }

    public void testMonitorClosed() throws IOException, InterruptedException {

        AsyncSocket socket = getFactory().openSocket();
        final BlockingQueue q = new LinkedBlockingQueue();

        socket.setMonitor(new SocketMonitor() {
            public void closed(AsyncSocket s, Throwable t) {
                q.add(s);
            }
        });

        socket.closeNow().getIO();
        assertSame(socket, awaitOnQueue(q));
    }

}