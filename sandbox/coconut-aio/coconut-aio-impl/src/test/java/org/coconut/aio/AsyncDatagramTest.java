/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.coconut.aio.monitor.DatagramMonitor;


/**
 * Async socket test
 * 
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class AsyncDatagramTest extends AioTestCase {
    public void testOpen() throws IOException {
        final AsyncDatagram socket = getFactory().openDatagram();
        assertNotNull(socket);
        assertTrue(socket.isOpen());

        assertFalse(socket.isConnected());
        assertNull(socket.getGroup());
        assertNull(socket.getReader());
        assertEquals(Long.MAX_VALUE, socket.getBufferLimit());
        assertEquals(Integer.MAX_VALUE, socket.getWriteQueueLimit());

        assertNull(socket.getDefaultExecutor());
        assertNull(socket.getDefaultDestination());

        assertNull(socket.getCloseHandler());
        assertNotNull(socket.toString());
        assertFalse(socket.toString().equals(""));

        assertNull(socket.getLocalSocketAddress());
        assertNull(socket.getRemoteSocketAddress());

        assertNull(socket.getInetAddress());
        assertTrue(socket.getLocalAddress().isAnyLocalAddress());

        assertEquals(0, socket.getLocalPort()); //nio bug should be -1
        assertEquals(-1, socket.getPort());

        assertTrue(socket.getId() > 0);

        socket.close().getIO();
    }

    public void testOpenExecutor() throws IOException {
        final AsyncDatagram socket = getFactory().openDatagram(OWN_THREAD);

        assertNotNull(socket);
        assertSame(OWN_THREAD, socket.getDefaultExecutor());
        assertNull(socket.getDefaultDestination());
        assertNotNull(socket);
        assertTrue(socket.isOpen());
        assertFalse(socket.isConnected());

        assertEquals(Long.MAX_VALUE, socket.getBufferLimit());
        assertEquals(Integer.MAX_VALUE, socket.getWriteQueueLimit());

        assertNull(socket.getCloseHandler());

        assertNull(socket.getLocalSocketAddress());
        assertNull(socket.getRemoteSocketAddress());

        assertNull(socket.getInetAddress());
        assertTrue(socket.getLocalAddress().isAnyLocalAddress());

        assertEquals(0, socket.getLocalPort()); //nio bug should be -1
        assertEquals(-1, socket.getPort());

        assertTrue(socket.getId() > 0);

        socket.close().getIO();
    }

    public void testOpenOfferable() throws IOException {
        final AsyncDatagram socket = getFactory().openDatagram(IGNORE_OFFERABLE);

        assertNotNull(socket);
        assertSame(IGNORE_OFFERABLE, socket.getDefaultDestination());
        assertNull(socket.getDefaultExecutor());
        assertTrue(socket.isOpen());
        assertFalse(socket.isConnected());

        assertEquals(Long.MAX_VALUE, socket.getBufferLimit());
        assertEquals(Integer.MAX_VALUE, socket.getWriteQueueLimit());

        assertNull(socket.getCloseHandler());

        assertNull(socket.getLocalSocketAddress());
        assertNull(socket.getRemoteSocketAddress());

        assertNull(socket.getInetAddress());
        assertTrue(socket.getLocalAddress().isAnyLocalAddress());

        assertEquals(0, socket.getLocalPort()); //nio bug should be 0
        assertEquals(-1, socket.getPort());

        assertTrue(socket.getId() > 0);

        socket.close().getIO();
    }

    public void testAttach() throws IOException {
        final AsyncDatagram socket = getFactory().openDatagram();
        final Object o = new Object();
        final Object o1 = new Object();

        assertNull(socket.attachment());
        assertNull(socket.attach(o));
        assertSame(o, socket.attachment());

        assertSame(o, socket.attach(o1));
        assertSame(o1, socket.attachment());

        socket.close().getIO();
    }

    public void testSetGetMonitor() throws IOException {
        final AsyncDatagram socket = getFactory().openDatagram();
        final DatagramMonitor monitor = new DatagramMonitor();
        final DatagramMonitor monitor1 = new DatagramMonitor();

        assertNull(socket.getMonitor());
        assertSame(socket, socket.setMonitor(monitor));
        assertSame(monitor, socket.getMonitor());

        assertSame(socket, socket.setMonitor(monitor1));
        assertSame(monitor1, socket.getMonitor());

        socket.close().getIO();
    }

    public void testIllegalDatagramGroup() throws IOException {
        final AsyncDatagram socket = getFactory().openDatagram();

        try {
            socket.setGroup(emptyDatagramGroup());
        } catch (IllegalArgumentException s) {
            return;
        } finally {
            socket.close().getIO();
        }
        fail("illegal socket was allowed");
    }

    public void testDefaultMonitor() throws IOException, InterruptedException {
        final AsyncDatagram socket = getFactory().openDatagram();
        final BlockingQueue q = new LinkedBlockingQueue();
        assertNull(AsyncDatagram.getDefaultMonitor());
        assertNull(socket.getMonitor());

        socket.close().getIO();
        final DatagramMonitor sm = new DatagramMonitor() {
            public void opened(AsyncDatagram socket) {
                q.add(socket);
            }
        };

        AsyncDatagram.setDefaultMonitor(sm);
        final AsyncDatagram socket1 = getFactory().openDatagram();
        assertSame(socket1, awaitOnQueue(q));
        assertSame(sm, AsyncDatagram.getDefaultMonitor());
        assertSame(sm, socket1.getMonitor());

        AsyncDatagram.setDefaultMonitor(null);
        socket1.close().getIO();
    }

    public void testQueue() throws IOException, InterruptedException {
        BlockingQueue q = new LinkedBlockingQueue();
        AsyncDatagram socket = getFactory().openDatagram(q);
        assertNotNull(socket);
        assertTrue(socket.isOpen());
        assertTrue(socket.getId() > 0);
        socket.close();
        Object o = awaitOnQueue(q);
        assertNotNull(o);
    }

}