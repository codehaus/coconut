package org.coconut.aio;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.coconut.aio.management.ManagementFactory;
import org.coconut.aio.management.ServerSocketInfo;
import org.coconut.aio.management.ServerSocketMXBean;


/**
 * Accept server socket management
 * 
 * @version $Id: AsyncServerSocketManagementTest.java,v 1.3 2004/06/08 12:49:54
 *          kav Exp $
 */
public class AsyncServerSocketManagementTest extends AioTestCase {

    public void testServerSocketManagement() throws IOException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final ServerSocketMXBean mxBean = ManagementFactory.getServerSocketMXBean();
        final ServerSocketInfo info = mxBean.getServerSocketInfo(socket.getId());

        assertNotNull(info);
        assertEquals(socket.getId(), info.getId());
        assertEquals(socket.isBound(), info.isBound());
        assertEquals(socket.getInetAddress(), info.getInetAddress());
        assertEquals(socket.getLocalPort(), info.getLocalPort());
        assertEquals(socket.getLocalSocketAddress(), info.getLocalSocketAddress());
        assertEquals(0, info.getTotalAccepts());
        assertEquals(0, mxBean.getTotalAcceptCount(info.getId()));

        socket.close().getIO();
    }

    public void testManagementBound() throws IOException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final ServerSocketMXBean mxBean = ManagementFactory.getServerSocketMXBean();

        socket.bind(createBindingAddress(getNextPort()));

        final ServerSocketInfo info = mxBean.getServerSocketInfo(socket.getId());

        assertEquals(true, info.isBound());
        assertEquals(socket.getInetAddress(), info.getInetAddress());
        assertEquals(socket.getLocalPort(), info.getLocalPort());
        assertEquals(socket.getLocalSocketAddress(), info.getLocalSocketAddress());

        socket.close().getIO();
    }

    public void testManagementAccept() throws IOException, InterruptedException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final ServerSocketMXBean mxBean = ManagementFactory.getServerSocketMXBean();
        final BlockingQueue q = new LinkedBlockingQueue();
        final int port = getNextPort();

        long accepts = mxBean.getTotalAcceptCount();

        socket.bind(createBindingAddress(port));

        socket.startAccepting(OWN_THREAD, createCallbackCompleted(q));

        //First connect

        AsyncSocket s1 = getFactory().openSocket();
        s1.connect(createConnectAddress(port)).getIO();

        ((AsyncSocket) awaitOnQueue(q)).closeNow().getIO();

        ServerSocketInfo info = mxBean.getServerSocketInfo(socket.getId());
        assertEquals(1, info.getTotalAccepts());
        assertEquals(1, mxBean.getTotalAcceptCount(info.getId()));
        assertEquals(accepts + 1, mxBean.getTotalAcceptCount());

        s1.closeNow().getIO();

        //Second connect
        AsyncSocket s2 = getFactory().openSocket();
        s2.connect(createConnectAddress(port)).getIO();

        ((AsyncSocket) awaitOnQueue(q)).closeNow().getIO();

        info = mxBean.getServerSocketInfo(socket.getId());
        assertEquals(2, info.getTotalAccepts());
        assertEquals(2, mxBean.getTotalAcceptCount(info.getId()));
        assertEquals(accepts + 2, mxBean.getTotalAcceptCount());

        s2.closeNow().getIO();

        socket.close().getIO();
    }

    public void testGetAllServerSockets() throws IOException {
        final ServerSocketMXBean mxBean = ManagementFactory.getServerSocketMXBean();
        long[] allServerSockets = mxBean.getAllServerSocketIds();
        long[] all = mxBean.getAllServerSocketIds();
        long count = mxBean.getTotalServerSocketsCount();

        ServerSocketInfo infos[] = mxBean.getServerSocketInfo(allServerSockets);

        final AsyncServerSocket socket = getFactory().openServerSocket();

        long[] la = mxBean.getAllServerSocketIds();
        assertEquals(allServerSockets.length + 1, la.length);
        assertEquals(all.length + 1, mxBean.getAllServerSocketIds().length);
        assertEquals(count + 1, mxBean.getTotalServerSocketsCount());
        assertEquals(infos.length + 1, mxBean.getServerSocketInfo(la).length);

        socket.close().getIO();
    }
    public void testManagementNoneExisting() {
        final ServerSocketMXBean mxBean = ManagementFactory.getServerSocketMXBean();

        assertEquals(0, mxBean.getTotalAcceptCount(Integer.MAX_VALUE));
        assertNull(mxBean.getServerSocketInfo(Integer.MAX_VALUE));

        ServerSocketInfo[] infos = mxBean.getServerSocketInfo(new long[]{Integer.MAX_VALUE, Integer.MAX_VALUE + 1});
        assertNull(infos[0]);
        assertNull(infos[1]);
    }

    public void testManagementPeakServer() throws IOException {

        final ServerSocketMXBean mxBean = ManagementFactory.getServerSocketMXBean();
        mxBean.resetPeakServerSocketCount();

        assertEquals(0, mxBean.getPeakServerSocketCount());
        assertEquals(0, mxBean.getServerSocketCount());
        final AsyncServerSocket socket = getFactory().openServerSocket();
        assertEquals(1, mxBean.getPeakServerSocketCount());
        assertEquals(1, mxBean.getServerSocketCount());
        final AsyncServerSocket socket2 = getFactory().openServerSocket();
        assertEquals(2, mxBean.getPeakServerSocketCount());
        assertEquals(2, mxBean.getServerSocketCount());
        socket.close().getIO();
        
        mxBean.resetPeakServerSocketCount();
        assertEquals(1, mxBean.getPeakServerSocketCount());
        assertEquals(1, mxBean.getServerSocketCount());
        socket2.close().getIO();
        assertEquals(1, mxBean.getPeakServerSocketCount());
        assertEquals(0, mxBean.getServerSocketCount());
    }
}