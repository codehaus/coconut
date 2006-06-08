package org.coconut.aio;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import org.coconut.aio.monitor.DatagramMonitor;
import org.coconut.core.EventHandler;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@SuppressWarnings("unchecked")
public class AsyncDatagramCloseTest extends AioTestCase {

    public void testCloseFuture() throws IOException {
        AsyncDatagram socket = getFactory().openDatagram();
        assertTrue(socket.isOpen());
        socket.close().getIO();
        assertFalse(socket.isOpen());
    }

    public void testCloseCallback() throws InterruptedException, IOException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AsyncDatagram socket = getFactory().openDatagram(OWN_THREAD);

        socket.close().setCallback(createCallbackCompleted(latch));
        awaitOnLatch(latch);

        assertFalse(socket.isOpen());
    }

    public void testCloseOfferable() throws InterruptedException, IOException {
        final BlockingQueue q = new LinkedBlockingQueue();

        AsyncDatagram socket = getFactory().openDatagram();
        socket.close().setDestination(createQueueOfferableOnce(q));

        Object o = awaitOnQueue(q);
        assertTrue(o instanceof AsyncDatagram.Closed);
        AsyncDatagram.Closed c = (AsyncDatagram.Closed) o;
        assertNull(c.getCause());

        assertFalse(socket.isOpen());
    }

    public void testCloseHandler() throws IOException, InterruptedException {

        final AsyncDatagram socket = getFactory().openDatagram();
        final BlockingQueue q = new LinkedBlockingQueue();
        final EventHandler h = createQueueHandlerOnce(q);

        assertNull(socket.getCloseHandler());

        socket.setCloseHandler(h);
        assertSame(h, socket.getCloseHandler());
        socket.close();

        assertSame(socket, awaitOnQueue(q));

    }

    
    public void testMonitorClosed() throws IOException, InterruptedException {

        AsyncDatagram socket = getFactory().openDatagram();
        final BlockingQueue q = new LinkedBlockingQueue();

        socket.setMonitor(new DatagramMonitor() {
            public void closed(AsyncDatagram s, Throwable t) {
                q.add(s);
            }
        });

        socket.close().getIO();
        assertSame(socket, awaitOnQueue(q));
    }

}