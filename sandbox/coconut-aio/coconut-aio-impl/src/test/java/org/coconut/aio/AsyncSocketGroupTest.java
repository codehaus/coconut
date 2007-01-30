/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.coconut.aio.monitor.SocketGroupMonitor;
import org.coconut.core.EventProcessor;
import org.coconut.core.Offerable;

@SuppressWarnings("unchecked")
public class AsyncSocketGroupTest extends AioTestCase {

    public void testOpenGroup() {
        AsyncSocketGroup group = getFactory().openSocketGroup();
        assertTrue(group.getId() > 0);
        assertEquals(0, group.size());

        assertNull(group.getDefaultDestination());
        assertSame(group, group.setDefaultDestination(IGNORE_OFFERABLE));
        assertSame(IGNORE_OFFERABLE, group.getDefaultDestination());

        assertNull(group.getDefaultExecutor());
        assertSame(group, group.setDefaultExecutor(OWN_THREAD));
        assertSame(OWN_THREAD, group.getDefaultExecutor());
    }

    public void testGroupMonitor() {
        final AsyncSocketGroup group = getFactory().openSocketGroup();
        final SocketGroupMonitor sm = new SocketGroupMonitor();

        assertNull(group.getMonitor());
        assertSame(group, group.setMonitor(sm));
        assertSame(sm, group.getMonitor());

    }
    public void testGroupMonitorDefault() throws InterruptedException {
        getFactory().openSocketGroup();
        final BlockingQueue<AsyncSocketGroup> q = new LinkedBlockingQueue<AsyncSocketGroup>();
        assertNull(AsyncSocketGroup.getDefaultMonitor());

        final SocketGroupMonitor sm = new SocketGroupMonitor() {
            public void opened(AsyncSocketGroup group) {
                q.add(group);
            }
        };

        AsyncSocketGroup.setDefaultMonitor(sm);
        final AsyncSocketGroup group1 = getFactory().openSocketGroup();
        assertSame(group1, awaitOnQueue(q));
        assertSame(sm, AsyncSocketGroup.getDefaultMonitor());
        assertSame(sm, group1.getMonitor());
        AsyncSocketGroup.setDefaultMonitor(null);
    }

    public void testSetDefaultReader() {
        AsyncSocketGroup group = getFactory().openSocketGroup();
        assertNull(group.getDefaultReader());
        ReadHandler<AsyncSocket> o = new ReadHandler<AsyncSocket>() {
            public void handle(AsyncSocket arg0) {
            }
        };
        assertSame(group, group.setDefaultReader(o));
        assertSame(o, group.getDefaultReader());
    }

    public void testAddSocket() throws IOException {
        AsyncSocketGroup group = getFactory().openSocketGroup();
        AsyncSocket s = getFactory().openSocket();
        assertNull(s.getGroup());
        assertFalse(group.contains(s));
        group.add(s);
        assertEquals(1, group.size());
        assertSame(group, s.getGroup());
        for (Iterator iter = group.iterator(); iter.hasNext();) {
            assertSame(s, iter.next());
        }
        assertTrue(group.contains(s));
        assertTrue(group.remove(s));
        assertEquals(0, group.size());
        assertNull(s.getGroup());

        s.closeNow().getIO();
    }

    public void testAddIllegal() {
        try {
            getFactory().openSocketGroup().add(emptySocket());
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Did not fail");
    }

    public void testAddSocketSelf() throws IOException {
        AsyncSocketGroup group = getFactory().openSocketGroup();
        AsyncSocket s = getFactory().openSocket();
        assertNull(s.getGroup());
        s.setGroup(group);
        assertEquals(1, group.size());
        assertSame(group, s.getGroup());
        for (Iterator iter = group.iterator(); iter.hasNext();) {
            assertSame(s, iter.next());
        }

        s.setGroup(null);
        assertEquals(0, group.size());
        assertNull(s.getGroup());

        s.closeNow();
    }

    public void testAddSocketDefaults() throws IOException {
        AsyncSocketGroup group = getFactory().openSocketGroup();
        AsyncSocket s = getFactory().openSocket();

        ReadHandler<AsyncSocket> h = new ReadHandler<AsyncSocket>() {
            public void handle(AsyncSocket socket) {
            }
        };
        group.setDefaultReader(h);
        assertNull(s.getReader());
        group.add(s);
        //Thread.sleep(100);
        //assertEquals(s.getReader(), h); fix

        AsyncSocket s1 = getFactory().openSocket();
        assertNull(s1.getDefaultExecutor());
        Executor ex = Executors.newCachedThreadPool();
        group.setDefaultExecutor(ex);
        group.add(s1);
        assertSame(ex, s1.getDefaultExecutor());

        AsyncSocket s2 = getFactory().openSocket();
        assertNull(s2.getDefaultDestination());
        Offerable<AsyncSocket.Event> o = new Offerable<AsyncSocket.Event>() {
            public boolean offer(AsyncSocket.Event o) {
                return true;
            }
        };
        group.setDefaultDestination(o);
        group.add(s2);
        assertSame(o, s2.getDefaultDestination());

        s.closeNow().getIO();
        s1.closeNow().getIO();
        s2.closeNow().getIO();
    }

    public void testTestRemove() throws IOException {
        AsyncSocketGroup group = getFactory().openSocketGroup();
        AsyncSocket s = getFactory().openSocket();
        assertFalse(group.remove(s));

        group.add(s);
        assertTrue(group.contains(s));
        s.closeNow();
        assertFalse(group.contains(s));
        s.setGroup(group);
        assertFalse(group.contains(s));

        s.closeNow().getIO();
    }

    public void testJoinHandler() throws IOException, InterruptedException {
        final AsyncSocketGroup group = getFactory().openSocketGroup();
        final AsyncSocket s = getFactory().openSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final EventProcessor h = createQueueHandlerOnce(q);

        assertNull(group.getJoinHandler());

        assertSame(group, group.setJoinHandler(h));
        assertSame(h, group.getJoinHandler());

        group.add(s);
        assertSame(s, awaitOnQueue(q));

        s.closeNow().getIO();
    }

    public void testAddMonitor() throws IOException, InterruptedException {
        final AsyncSocketGroup group = getFactory().openSocketGroup();
        final AsyncSocket socket = getFactory().openSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final SocketGroupMonitor h = new SocketGroupMonitor() {
            public void join(AsyncSocketGroup group, AsyncSocket socket) {
                q.add(new Object[] {group, socket});
            }
        };
        group.setMonitor(h);
        socket.setGroup(group);
        Object[] p = (Object[]) awaitOnQueue(q);

        assertSame(group, p[0]);
        assertSame(socket, p[1]);

        socket.closeNow().getIO();
    }

    public void testLeaveHandler() throws IOException, InterruptedException {
        final AsyncSocketGroup group = getFactory().openSocketGroup();
        final AsyncSocket s = getFactory().openSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final EventProcessor h = createQueueHandlerOnce(q);

        assertNull(group.getLeaveHandler());

        assertSame(group, group.setLeaveHandler(h));
        assertSame(h, group.getLeaveHandler());

        group.add(s);
        assertTrue(group.remove(s));
        assertFalse(group.remove(s));
        assertSame(s, awaitOnQueue(q));

        s.closeNow().getIO();
    }

    public void testLeaveMonitor() throws IOException, InterruptedException {
        final AsyncSocketGroup group = getFactory().openSocketGroup();
        final AsyncSocket socket = getFactory().openSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        final SocketGroupMonitor h = new SocketGroupMonitor() {
            public void leave(AsyncSocketGroup group, AsyncSocket socket, Throwable cause) {
                q.add(new Object[] {group, socket});
            }
        };
        group.setMonitor(h);

        socket.setGroup(group);
        socket.setGroup(null);

        Object[] p = (Object[]) awaitOnQueue(q);

        assertSame(group, p[0]);
        assertSame(socket, p[1]);

        socket.closeNow().getIO();
    }
}