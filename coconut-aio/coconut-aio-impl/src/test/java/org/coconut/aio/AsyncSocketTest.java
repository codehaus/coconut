/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.coconut.aio.monitor.SocketMonitor;


/**
 * Async socket test
 * 
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class AsyncSocketTest extends AioTestCase {
    public void testOpen() throws IOException {
        final AsyncSocket socket = getFactory().openSocket();
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
        assertEquals(0, socket.getPort()); //nio bug should be -1

        assertTrue(socket.getId() > 0);

        socket.closeNow().getIO();
    }

    public void testOpenExecutor() throws IOException {
        final AsyncSocket socket = getFactory().openSocket(OWN_THREAD);

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
        assertEquals(0, socket.getPort()); //nio bug should be -1

        assertTrue(socket.getId() > 0);

        socket.closeNow().getIO();
    }

    public void testOpenOfferable() throws IOException {
        final AsyncSocket socket = getFactory().openSocket(IGNORE_OFFERABLE);

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
        assertEquals(0, socket.getPort()); //nio bug should be 0

        assertTrue(socket.getId() > 0);

        socket.closeNow().getIO();
    }

    public void testAttach() throws IOException {
        final AsyncSocket socket = getFactory().openSocket();
        final Object o = new Object();
        final Object o1 = new Object();

        assertNull(socket.attachment());
        assertNull(socket.attach(o));
        assertSame(o, socket.attachment());

        assertSame(o, socket.attach(o1));
        assertSame(o1, socket.attachment());

        socket.closeNow().getIO();
    }

    public void testSetGetMonitor() throws IOException {
        final AsyncSocket socket = getFactory().openSocket();
        final SocketMonitor monitor = new SocketMonitor();
        final SocketMonitor monitor1 = new SocketMonitor();

        assertNull(socket.getMonitor());
        assertSame(socket, socket.setMonitor(monitor));
        assertSame(monitor, socket.getMonitor());

        assertSame(socket, socket.setMonitor(monitor1));
        assertSame(monitor1, socket.getMonitor());

        socket.closeNow().getIO();
    }

    public void testIllegalSocketGroup() throws IOException {
        final AsyncSocket socket = getFactory().openSocket();

        try {
            socket.setGroup(emptySocketGroup());
        } catch (IllegalArgumentException s) {
            return;
        } finally {
            socket.closeNow().getIO();
        }
        fail("illegal socket was allowed");
    }

    public void testDefaultMonitor() throws IOException, InterruptedException {
        final AsyncSocket socket = getFactory().openSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        assertNull(AsyncSocket.getDefaultMonitor());
        assertNull(socket.getMonitor());

        socket.closeNow().getIO();
        final SocketMonitor sm = new SocketMonitor() {
            public void opened(AsyncSocket socket) {
                q.add(socket);
            }
        };

        AsyncSocket.setDefaultMonitor(sm);
        final AsyncSocket socket1 = getFactory().openSocket();
        assertSame(socket1, awaitOnQueue(q));
        assertSame(sm, AsyncSocket.getDefaultMonitor());
        assertSame(sm, socket1.getMonitor());

        AsyncSocket.setDefaultMonitor(null);
        socket1.closeNow().getIO();
    }

    public void testQueue() throws IOException, InterruptedException {
        BlockingQueue q = new LinkedBlockingQueue();
        AsyncSocket socket = getFactory().openSocket(q);
        assertNotNull(socket);
        assertTrue(socket.isOpen());
        assertTrue(socket.getId() > 0);
        socket.closeNow();
        Object o = awaitOnQueue(q);
        assertNotNull(o);
    }
    
    public void testColor() throws IOException {
        final AsyncSocket socket = getFactory().openSocket();
        assertTrue(socket.getColor() == socket.getColor()); //fake
        socket.closeNow().getIO();
    }

}