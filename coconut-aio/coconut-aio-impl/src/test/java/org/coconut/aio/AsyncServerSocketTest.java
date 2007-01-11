/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.AssertionFailedError;

import org.coconut.aio.monitor.ServerSocketMonitor;

/**
 * Base server socket test
 * 
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class AsyncServerSocketTest extends AioTestCase {

    public void testOpen() throws IOException {
        AsyncServerSocket socket = getFactory().openServerSocket();
        assertNotNull(socket);
        assertTrue(socket.isOpen());
        assertFalse(socket.isBound());
        assertFalse(socket.isAccepting());
        assertNull(socket.getDefaultExecutor());
        assertNull(socket.getDefaultDestination());
        assertNull(socket.attachment());
        assertNotNull(socket.toString());
        assertFalse(socket.toString().equals(""));
        assertNull(socket.getCloseHandler());
        assertNull(socket.getDefaultSocketGroup());
        assertNull(socket.getInetAddress());
        assertNull(socket.getLocalSocketAddress());
        assertEquals(-1, socket.getLocalPort());
        assertTrue(socket.getId() > 0);

        socket.close().getIO();
    }

    public void testOpenExecutor() throws IOException {

        AsyncServerSocket socket = getFactory().openServerSocket(OWN_THREAD);
        assertNotNull(socket);
        assertSame(OWN_THREAD, socket.getDefaultExecutor());
        assertNull(socket.getDefaultDestination());
        assertTrue(socket.isOpen());
        assertFalse(socket.isBound());
        assertFalse(socket.isAccepting());
        assertNull(socket.attachment());
        assertNull(socket.getCloseHandler());
        assertNull(socket.getDefaultSocketGroup());
        assertNull(socket.getInetAddress());
        assertNull(socket.getLocalSocketAddress());
        assertEquals(-1, socket.getLocalPort());
        assertTrue(socket.getId() > 0);

        socket.close().getIO();
    }

    public void testOpenOfferable() throws IOException {
        AsyncServerSocket socket = getFactory().openServerSocket(IGNORE_OFFERABLE);
        assertNotNull(socket);
        assertSame(IGNORE_OFFERABLE, socket.getDefaultDestination());
        assertNull(socket.getDefaultExecutor());
        assertTrue(socket.isOpen());
        assertFalse(socket.isBound());
        assertFalse(socket.isAccepting());
        assertNull(socket.attachment());
        assertNull(socket.getCloseHandler());
        assertNull(socket.getDefaultSocketGroup());
        assertNull(socket.getInetAddress());
        assertNull(socket.getLocalSocketAddress());
        assertEquals(-1, socket.getLocalPort());
        assertTrue(socket.getId() > 0);

        socket.close().getIO();
    }

    public void testAttach() throws IOException {
        AsyncServerSocket socket = getFactory().openServerSocket();
        assertNull(socket.attachment());

        Object o = new Object();
        socket.attach(o);
        assertSame(o, socket.attachment());

        Object o1 = new Object();
        assertSame(o, socket.attach(o1));
        assertSame(o1, socket.attachment());

        socket.close().getIO();
    }

    public void testDefaultMonitor() throws IOException, AssertionFailedError, InterruptedException {
        final BlockingQueue q = new LinkedBlockingQueue();
        final ServerSocketMonitor monitor = new ServerSocketMonitor() {
            public void opened(AsyncServerSocket socket) {
                q.add(socket);
            }
        };

        assertNull(AsyncServerSocket.getDefaultMonitor());

        AsyncServerSocket.setDefaultMonitor(monitor);
        assertSame(monitor, AsyncServerSocket.getDefaultMonitor());

        AsyncServerSocket socket = getFactory().openServerSocket();
        assertSame(monitor, socket.getMonitor());

        assertSame(socket, awaitOnQueue(q));

        AsyncServerSocket.setDefaultMonitor(null);

        socket.close().getIO();
    }

    public void testDefaultSocketGroup() throws IOException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        AsyncSocketGroup grp = getFactory().openSocketGroup();
        assertSame(socket, socket.setDefaultSocketGroup(grp));
        assertSame(socket.getDefaultSocketGroup(), grp);
        socket.close().getIO();
    }

    public void testIllegalDefaultSocketGroup() throws IOException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        try {
            socket.setDefaultSocketGroup(emptySocketGroup());
        } catch (IllegalArgumentException e) {
            return;
        } finally {
            socket.close().getIO();
        }
        fail("Did not reject group");

    }

    public void testQueue() throws IOException, InterruptedException {
        BlockingQueue q = new LinkedBlockingQueue();
        AsyncServerSocket socket = getFactory().openServerSocket(q);
        assertNotNull(socket);
        assertTrue(socket.isOpen());
        assertTrue(socket.getId() > 0);
        socket.close();
        Object o = awaitOnQueue(q);
        assertNotNull(o);
    }
    
}