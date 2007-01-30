/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import org.coconut.aio.monitor.ServerSocketMonitor;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@SuppressWarnings("unchecked")
public class AsyncServerSocketBindTest extends AioTestCase {

    public void testBind() throws IOException {
        final int port = getNextPort();
        final AsyncServerSocket socket = getFactory().openServerSocket();

        assertFalse(socket.isBound());
        assertSame(socket, socket.bind(createBindingAddress(port)));
        assertTrue(socket.isBound());
        assertEquals(port, socket.getLocalPort());
        assertTrue(socket.getInetAddress().isAnyLocalAddress());
        socket.close().getIO();
    }

    public void testBindBacklog() throws IOException {
        final int port = getNextPort();
        final AsyncServerSocket socket = getFactory().openServerSocket();

        assertFalse(socket.isBound());
        socket.bind(createBindingAddress(port), 100);
        assertTrue(socket.isBound());
        assertEquals(port, socket.getLocalPort());
        assertTrue(socket.getInetAddress().isAnyLocalAddress());
        socket.close().getIO();
    }

    public void testBindMonitor() throws IOException, InterruptedException {

        final AsyncServerSocket socket = getFactory().openServerSocket();
        final SocketAddress adr = createBindingAddress(getNextPort());
        final BlockingQueue q = new LinkedBlockingQueue();

        socket.setMonitor(new ServerSocketMonitor() {
            public void bound(AsyncServerSocket s, SocketAddress address) {
                q.add(new Object[] {s, address});
            }
        });

        socket.bind(adr);

        Object[] p = (Object[]) awaitOnQueue(q);

        assertSame(socket, p[0]);
        assertSame(adr, p[1]);

        socket.close().getIO();

    }

    public void testBindBacklogMonitor() throws IOException, InterruptedException {

        final AsyncServerSocket socket = getFactory().openServerSocket();
        final SocketAddress adr = createBindingAddress(getNextPort());
        final BlockingQueue q = new LinkedBlockingQueue();

        socket.setMonitor(new ServerSocketMonitor() {
            public void bound(AsyncServerSocket s, SocketAddress address) {
                q.add(new Object[] {s, address});
            }
        });

        socket.bind(adr, 100);

        Object[] p = (Object[]) awaitOnQueue(q);

        assertSame(socket, p[0]);
        assertSame(adr, p[1]);

        socket.close().getIO();
    }

    public void testBindRuntimeException() throws IOException {
        final AsyncServerSocket socket = getFactory().openServerSocket();

        AioTestSecurityManager.getInstance().setListenAllowed(false);

        try {
            socket.bind(createBindingAddress(getNextPort()));
        } catch (SecurityException e) {
            return;
        } finally {
            AioTestSecurityManager.getInstance().setListenAllowed(true);
            socket.close().getIO();
        }
        fail("Bind did not fail");
    }

    public void testBindBacklogRuntimeException() throws IOException {
        final AsyncServerSocket socket = getFactory().openServerSocket();

        AioTestSecurityManager.getInstance().setListenAllowed(false);

        try {
            socket.bind(new InetSocketAddress(getNextPort()), 100);
        } catch (SecurityException e) {
            return;
        } finally {
            AioTestSecurityManager.getInstance().setListenAllowed(true);
            socket.close().getIO();
        }
        fail("Bind did not fail");
    }

    public void testBindRuntimeExceptionMonitor() throws IOException, InterruptedException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final SocketAddress adr = createBindingAddress(getNextPort());
        final CountDownLatch latch = new CountDownLatch(1);

        socket.setMonitor(new ServerSocketMonitor() {
            public void bindFailed(AsyncServerSocket s, SocketAddress address, Throwable t) {
                assertSame(socket, s);
                assertTrue(t instanceof SecurityException);
                assertEquals(adr, address);
                latch.countDown();
            }
        });

        AioTestSecurityManager.getInstance().setListenAllowed(false);

        try {
            socket.bind(adr);
        } catch (SecurityException e) {
            awaitOnLatch(latch);
            return;
        } finally {
            AioTestSecurityManager.getInstance().setListenAllowed(true);
            socket.close().getIO();
        }
        fail("Bind did not fail");
    }

    public void testBindBacklogRuntimeExceptionMonitor() throws IOException, InterruptedException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final SocketAddress adr = createBindingAddress(getNextPort());
        final CountDownLatch latch = new CountDownLatch(1);

        socket.setMonitor(new ServerSocketMonitor() {
            public void bindFailed(AsyncServerSocket s, SocketAddress address, Throwable t) {
                assertSame(socket, s);
                assertTrue(t instanceof SecurityException);
                assertEquals(adr, address);
                latch.countDown();
            }
        });

        AioTestSecurityManager.getInstance().setListenAllowed(false);

        try {
            socket.bind(adr, 100);
        } catch (SecurityException e) {
            awaitOnLatch(latch);
            return;
        } finally {
            AioTestSecurityManager.getInstance().setListenAllowed(true);
            socket.close().getIO();
        }
        fail("Bind did not fail");
    }

    public void testBindIOException() throws IOException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final SocketAddress adr = createBindingAddress(getNextPort());
        final ServerSocketChannel channel = ServerSocketChannel.open();

        channel.socket().bind(adr);

        try {
            socket.bind(adr);
        } catch (IOException e) {
            return;
        } finally {
            channel.close();
            socket.close().getIO();
        }
        fail("Bind did not fail");
    }

    public void testBindBacklogIOException() throws IOException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final SocketAddress adr = createBindingAddress(getNextPort());
        final ServerSocketChannel channel = ServerSocketChannel.open();

        channel.socket().bind(adr);

        try {
            socket.bind(adr, 100);
        } catch (IOException e) {
            return;
        } finally {
            channel.close();
            socket.close().getIO();
        }
        fail("Bind did not fail");
    }

    public void testBindIOExceptionMonitor() throws IOException, InterruptedException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final ServerSocketChannel channel = ServerSocketChannel.open();
        final CountDownLatch latch = new CountDownLatch(1);
        final SocketAddress adr = createBindingAddress(getNextPort());

        channel.socket().bind(adr);

        socket.setMonitor(new ServerSocketMonitor() {
            public void bindFailed(AsyncServerSocket s, SocketAddress address, Throwable t) {
                assertSame(socket, s);
                assertTrue(t instanceof IOException);
                assertEquals(adr, address);
                latch.countDown();
            }
        });

        try {
            socket.bind(adr);
        } catch (IOException e) {
            awaitOnLatch(latch);
            return;
        } finally {
            channel.close();
            socket.close().getIO();
        }
        fail("Bind did not fail");
    }

    public void testBindBacklogIOExceptionMonitor() throws IOException, InterruptedException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final ServerSocketChannel channel = ServerSocketChannel.open();
        final CountDownLatch latch = new CountDownLatch(1);
        final SocketAddress adr = createBindingAddress(getNextPort());

        channel.socket().bind(adr);

        socket.setMonitor(new ServerSocketMonitor() {
            public void bindFailed(AsyncServerSocket s, SocketAddress address, Throwable t) {
                assertSame(socket, s);
                assertTrue(t instanceof IOException);
                assertEquals(adr, address);
                latch.countDown();
            }
        });

        try {
            socket.bind(adr, 100);
        } catch (IOException e) {
            awaitOnLatch(latch);
            return;
        } finally {
            channel.close();
            socket.close().getIO();
        }
        fail("Bind did not fail");
    }

}