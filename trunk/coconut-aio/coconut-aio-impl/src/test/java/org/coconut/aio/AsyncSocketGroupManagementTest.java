package org.coconut.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.coconut.aio.management.ManagementFactory;
import org.coconut.aio.management.SocketGroupInfo;
import org.coconut.aio.management.SocketGroupMXBean;
import org.coconut.core.Offerable;

@SuppressWarnings("unchecked")
public class AsyncSocketGroupManagementTest extends AioTestCase {

    private AsyncSocketGroup group;

    private SocketGroupMXBean mxBean;

    private long id;

    public void setUp() {
        super.setUp();
        group = getFactory().openSocketGroup();
        mxBean = ManagementFactory.getSocketGroupMXBean();
        id = group.getId();
    }

    public void testManagementForNonExistantGroup() {
        assertEquals(0, mxBean.getBytesRead(Integer.MAX_VALUE));
        assertEquals(0, mxBean.getBytesWritten(Integer.MAX_VALUE));
        assertEquals(0, mxBean.getSocketsInGroup(Integer.MAX_VALUE).length);
        assertNull(mxBean.getSocketGroupInfo(Integer.MAX_VALUE));
    }

    public void testGroupInit() {
        assertEquals(0, mxBean.getBytesRead(id));
        assertEquals(0, mxBean.getBytesWritten(id));
        assertEquals(0, mxBean.getPeakSocketCount(id));
        assertEquals(0, mxBean.getSize(id));
        assertEquals(0, mxBean.getSocketsInGroup(id).length);
        assertEquals(0, mxBean.getTotalSocketCount(id));

        SocketGroupInfo info = mxBean.getSocketGroupInfo(id);
        assertEquals(0, info.getBytesRead());
        assertEquals(0, info.getBytesWritten());
        assertEquals(id, info.getId());
        assertEquals(0, info.getSize());
    }

    public void testSocketsInGroup() throws IOException {
        AsyncSocket s1 = getFactory().openSocket().setGroup(group);
        AsyncSocket s2 = getFactory().openSocket().setGroup(group);
        AsyncSocket s3 = getFactory().openSocket().setGroup(group);

        long[] ids = mxBean.getSocketsInGroup(id);
        assertEquals(3, ids.length);
        List l = Arrays.asList(new Long[] { ids[0], ids[1], ids[2] });
        l.contains(s1.getId());
        l.contains(s2.getId());
        l.contains(s3.getId());

        s1.closeNow().getIO();
        s2.closeNow().getIO();
        s3.closeNow().getIO();
    }

    public void testPeakSocketCount() throws IOException {
        assertEquals(0, mxBean.getPeakSocketCount(id));

        final AsyncSocket socket = getFactory().openSocket().setGroup(group);
        assertEquals(1, mxBean.getPeakSocketCount(id));

        final AsyncSocket socket2 = getFactory().openSocket().setGroup(group);
        assertEquals(2, mxBean.getPeakSocketCount(id));

        socket.closeNow().getIO();
        socket2.closeNow().getIO();
        assertEquals(2, mxBean.getPeakSocketCount(id));
    }

    public void testPeakSocketCountReset() throws IOException {
        final AsyncSocket socket = getFactory().openSocket().setGroup(group);
        final AsyncSocket socket2 = getFactory().openSocket().setGroup(group);
        assertEquals(2, mxBean.getPeakSocketCount(id));
        mxBean.resetPeakSocketCount(id);
        assertEquals(2, mxBean.getPeakSocketCount(id));

        socket.closeNow().getIO();
        mxBean.resetPeakSocketCount(id);
        assertEquals(1, mxBean.getPeakSocketCount(id));

        socket2.closeNow().getIO();
        mxBean.resetPeakSocketCount(id);
        assertEquals(0, mxBean.getPeakSocketCount(id));

    }

    public void testPeakSocketCountResetNonExisting() throws IOException {
        mxBean.resetPeakSocketCount(Integer.MAX_VALUE);
    }

    public void testSize() throws IOException {
        assertEquals(0, mxBean.getSize(id));
        assertEquals(0, mxBean.getSocketGroupInfo(id).getSize());

        final AsyncSocket socket = getFactory().openSocket().setGroup(group);
        assertEquals(1, mxBean.getSize(id));
        assertEquals(1, mxBean.getSocketGroupInfo(id).getSize());

        final AsyncSocket socket2 = getFactory().openSocket().setGroup(group);
        assertEquals(2, mxBean.getSize(id));
        assertEquals(2, mxBean.getSocketGroupInfo(id).getSize());

        socket.closeNow().getIO();

        assertEquals(1, mxBean.getSize(id));
        assertEquals(1, mxBean.getSocketGroupInfo(id).getSize());

        socket2.closeNow().getIO();
        assertEquals(0, mxBean.getSize(id));
        assertEquals(0, mxBean.getSocketGroupInfo(id).getSize());
    }

    public void testTotalSocketCount() throws IOException {
        assertEquals(0, mxBean.getTotalSocketCount(group.getId()));
        final AsyncSocket socket = getFactory().openSocket().setGroup(group);
        assertEquals(1, mxBean.getTotalSocketCount(group.getId()));

        final AsyncSocket socket2 = getFactory().openSocket().setGroup(group);
        assertEquals(2, mxBean.getTotalSocketCount(group.getId()));

        socket.closeNow().getIO();
        socket2.closeNow().getIO();
        assertEquals(2, mxBean.getTotalSocketCount(group.getId()));
    }

    public void testManagemenGroupInfo() throws IOException {
        SocketGroupInfo info = mxBean.getSocketGroupInfo(group.getId());

        assertEquals(0, info.getSize());
        assertEquals(group.getId(), info.getId());

        AsyncSocket s1 = getFactory().openSocket().setGroup(group);
        info = mxBean.getSocketGroupInfo(group.getId());
        assertEquals(1, info.getSize());

        AsyncSocket s2 = getFactory().openSocket().setGroup(group);
        info = mxBean.getSocketGroupInfo(group.getId());
        assertEquals(2, info.getSize());

        s1.closeNow().getIO();
        s2.closeNow().getIO();
    }

    public void testGroupManagementGetAll() {
        long[] l = mxBean.getAllSocketGroupIds();
        int length = l.length;
        getFactory().openSocketGroup();
        assertEquals(length + 1, mxBean.getAllSocketGroupIds().length);
        // Could GC in the middle
    }

    public void testManagementReadWritten2() throws IOException,
            InterruptedException {
        final int port = getNextPort();
        final AsyncServerSocket socket = getFactory().openServerSocket().bind(
                createBindingAddress(port));
        final BlockingQueue q = new LinkedBlockingQueue();
        final BlockingQueue q2 = new LinkedBlockingQueue();
        final Offerable o = createQueueOfferableOnce(q);
        final AsyncSocket socketWritten = getFactory().openSocket().setGroup(
                group);

        final SocketGroupInfo infoWritten = mxBean.getSocketGroupInfo(group
                .getId());

        assertEquals(0, infoWritten.getBytesWritten());
        assertEquals(0, mxBean.getBytesWritten(group.getId()));

        socket.startAccepting(o);

        socketWritten.connect(createConnectAddress(port)).getIO();

        final AsyncSocket socketRead = ((AsyncServerSocket.SocketAccepted) awaitOnQueue(q))
                .getAcceptedSocket().setGroup(group);
        final SocketGroupInfo infoRead = mxBean.getSocketGroupInfo(group
                .getId());
        assertEquals(0, infoRead.getBytesRead());
        assertEquals(0, mxBean.getBytesRead(group.getId()));

        socketWritten.writeAsync(ByteBuffer.wrap("Hello".getBytes())).getIO();

        ReadHandler handler = new ReadHandler() {
            public void handle(Object socket) throws IOException {
                q2.offer(socket);
            }
        };
        socketRead.setReader(handler);
        awaitOnQueue(q2);

        readAndEqual(socketRead, "Hello");

        assertEquals(5, mxBean.getSocketGroupInfo(group.getId())
                .getBytesWritten());
        assertEquals(5, mxBean.getBytesWritten(group.getId()));

        assertEquals(5, mxBean.getSocketGroupInfo(group.getId()).getBytesRead());
        assertEquals(5, mxBean.getBytesRead(group.getId()));

        socket.close().getIO();
        socketRead.closeNow().getIO();
        socketWritten.closeNow().getIO();
    }

    /*
     * public void testGroupMonitor() throws IOException, InterruptedException {
     * final AsyncSocketGroup group = getFactory().openSocketGroup(); final
     * SocketGroupMonitor sm = new SocketGroupMonitor();
     * assertNull(group.getMonitor()); assertSame(group, group.setMonitor(sm));
     * assertSame(sm,group.getMonitor()); } public void
     * testGroupMonitorDefault() throws IOException, InterruptedException {
     * final AsyncSocketGroup group = getFactory().openSocketGroup(); final
     * BlockingQueue q = new LinkedBlockingQueue();
     * assertNull(AsyncSocketGroup.getDefaultMonitor()); final
     * SocketGroupMonitor sm = new SocketGroupMonitor() { public void
     * opened(AsyncSocketGroup group) { q.add(group); } };
     * AsyncSocketGroup.setDefaultMonitor(sm); final AsyncSocketGroup group1 =
     * getFactory().openSocketGroup(); assertSame(group1, awaitOnQueue(q));
     * assertSame(sm, AsyncSocketGroup.getDefaultMonitor()); assertSame(sm,
     * group1.getMonitor()); } public void testSetDefaultReader() {
     * AsyncSocketGroup group = getFactory().openSocketGroup();
     * assertNull(group.getDefaultReader()); ReadHandler o = new ReadHandler() {
     * public void handle(Object arg0) { } }; assertSame(group,
     * group.setDefaultReader(o)); assertSame(o, group.getDefaultReader()); }
     * public void testAddSocket() throws IOException { AsyncSocketGroup group =
     * getFactory().openSocketGroup(); AsyncSocket s =
     * getFactory().openSocket(); assertNull(s.getGroup());
     * assertFalse(group.contains(s)); group.add(s); assertEquals(1,
     * group.size()); assertSame(group, s.getGroup()); for (Iterator iter =
     * group.iterator(); iter.hasNext();) { assertSame(s, iter.next()); }
     * assertTrue(group.contains(s)); assertTrue(group.remove(s));
     * assertEquals(0, group.size()); assertNull(s.getGroup());
     * s.close().getIO(); } public void testAddIllegal() { try {
     * getFactory().openSocketGroup().add(new EmptyAsyncSocket()); } catch
     * (IllegalArgumentException e) { return; } fail("Did not fail"); } public
     * void testAddSocketSelf() throws IOException { AsyncSocketGroup group =
     * getFactory().openSocketGroup(); AsyncSocket s =
     * getFactory().openSocket(); assertNull(s.getGroup()); s.setGroup(group);
     * assertEquals(1, group.size()); assertSame(group, s.getGroup()); for
     * (Iterator iter = group.iterator(); iter.hasNext();) { assertSame(s,
     * iter.next()); } s.setGroup(null); assertEquals(0, group.size());
     * assertNull(s.getGroup()); s.close(); } public void
     * testAddSocketDefaults() throws IOException, InterruptedException {
     * AsyncSocketGroup group = getFactory().openSocketGroup(); AsyncSocket s =
     * getFactory().openSocket(); ReadHandler h = new ReadHandler() { public
     * void handle(Object arg0) { } }; group.setDefaultReader(h);
     * assertNull(s.getReader()); group.add(s); //Thread.sleep(100); //TODO fix
     * //assertEquals(s.getReader(), h);//TODO fix AsyncSocket s1 =
     * getFactory().openSocket(); assertNull(s1.getDefaultExecutor()); Executor
     * ex = Executors.newCachedThreadPool(); group.setDefaultExecutor(ex);
     * group.add(s1); assertSame(ex, s1.getDefaultExecutor()); AsyncSocket s2 =
     * getFactory().openSocket(); assertNull(s2.getDefaultDestination());
     * Offerable o = new Offerable() { public boolean offer(Object o) { return
     * true; } }; group.setDefaultDestination(o); group.add(s2); assertSame(o,
     * s2.getDefaultDestination()); s.close().getIO(); } public void
     * testTestRemove() throws IOException { AsyncSocketGroup group =
     * getFactory().openSocketGroup(); AsyncSocket s =
     * getFactory().openSocket(); assertFalse(group.remove(s)); group.add(s);
     * assertTrue(group.contains(s)); s.close(); assertFalse(group.contains(s));
     * s.setGroup(group); assertFalse(group.contains(s)); s.close().getIO(); }
     * public void testJoinHandler() throws IOException, InterruptedException {
     * final AsyncSocketGroup group = getFactory().openSocketGroup(); final
     * AsyncSocket s = getFactory().openSocket(); final BlockingQueue q = new
     * LinkedBlockingQueue(); final Handler h = createQueueHandlerOnce(q);
     * assertNull(group.getJoinHandler()); assertSame(group,
     * group.setJoinHandler(h)); assertSame(h, group.getJoinHandler());
     * group.add(s); assertSame(s, awaitOnQueue(q)); s.close().getIO(); } public
     * void testAddMonitor() throws IOException, InterruptedException { final
     * AsyncSocketGroup group = getFactory().openSocketGroup(); final
     * AsyncSocket socket = getFactory().openSocket(); final BlockingQueue q =
     * new LinkedBlockingQueue(); final SocketGroupMonitor h = new
     * SocketGroupMonitor() { public void join(AsyncSocketGroup group,
     * AsyncSocket socket) { q.add(new Pair(group,socket)); } };
     * group.setMonitor(h); socket.setGroup(group); Pair p=(Pair)
     * awaitOnQueue(q); assertSame(group, p.getFirst()); assertSame(socket,
     * p.getSecond()); socket.close().getIO(); } public void testLeaveHandler()
     * throws IOException, InterruptedException { final AsyncSocketGroup group =
     * getFactory().openSocketGroup(); final AsyncSocket s =
     * getFactory().openSocket(); final BlockingQueue q = new
     * LinkedBlockingQueue(); final Handler h = createQueueHandlerOnce(q);
     * assertNull(group.getLeaveHandler()); assertSame(group,
     * group.setLeaveHandler(h)); assertSame(h, group.getLeaveHandler());
     * group.add(s); assertTrue(group.remove(s)); assertFalse(group.remove(s));
     * assertSame(s, awaitOnQueue(q)); s.close().getIO(); } public void
     * testLeaveMonitor() throws IOException, InterruptedException { final
     * AsyncSocketGroup group = getFactory().openSocketGroup(); final
     * AsyncSocket socket = getFactory().openSocket(); final BlockingQueue q =
     * new LinkedBlockingQueue(); final SocketGroupMonitor h = new
     * SocketGroupMonitor() { public void leave(AsyncSocketGroup group,
     * AsyncSocket socket, Throwable cause) { q.add(new Pair(group,socket)); } };
     * group.setMonitor(h); socket.setGroup(group); socket.setGroup(null); Pair
     * p=(Pair) awaitOnQueue(q); assertSame(group, p.getFirst());
     * assertSame(socket, p.getSecond()); socket.close().getIO(); }
     */
}