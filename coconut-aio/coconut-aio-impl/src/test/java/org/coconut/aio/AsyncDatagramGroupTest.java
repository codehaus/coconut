/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.coconut.aio.monitor.DatagramGroupMonitor;
import org.coconut.core.EventHandler;
import org.coconut.core.Offerable;

@SuppressWarnings("unchecked")
public class AsyncDatagramGroupTest extends AioTestCase {

    public void testOpenGroup() {
        AsyncDatagramGroup group = getFactory().openDatagramGroup();
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
        final AsyncDatagramGroup group = getFactory().openDatagramGroup();
        final DatagramGroupMonitor sm = new DatagramGroupMonitor();

        assertNull(group.getMonitor());
        assertSame(group, group.setMonitor(sm));
        assertSame(sm, group.getMonitor());

        
    }
    public void testGroupMonitorDefault() throws InterruptedException {
        getFactory().openDatagramGroup();
        final BlockingQueue q = new LinkedBlockingQueue();
        assertNull(AsyncDatagramGroup.getDefaultMonitor());

        final DatagramGroupMonitor sm = new DatagramGroupMonitor() {
            public void opened(AsyncDatagramGroup group) {
                q.add(group);
            }
        };

        AsyncDatagramGroup.setDefaultMonitor(sm);
        final AsyncDatagramGroup group1 = getFactory().openDatagramGroup();
        assertSame(group1, awaitOnQueue(q));
        assertSame(sm, AsyncDatagramGroup.getDefaultMonitor());
        assertSame(sm, group1.getMonitor());
        AsyncDatagramGroup.setDefaultMonitor(null);
    }

    public void testSetDefaultReader() {
        AsyncDatagramGroup group = getFactory().openDatagramGroup();
        assertNull(group.getDefaultReader());
        ReadHandler o = new ReadHandler() {
            public void handle(Object arg0) {
            }
        };
        assertSame(group, group.setDefaultReader(o));
        assertSame(o, group.getDefaultReader());
    }

    public void testAddDatagram() throws IOException {
        AsyncDatagramGroup group = getFactory().openDatagramGroup();
        AsyncDatagram s = getFactory().openDatagram();
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

        s.close().getIO();
    }

    public void testAddIllegal() {
        try {
            getFactory().openDatagramGroup().add(emptyDatagram());
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("Did not fail");
    }

    public void testAddDatagramSelf() throws IOException {
        AsyncDatagramGroup group = getFactory().openDatagramGroup();
        AsyncDatagram s = getFactory().openDatagram();
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

        s.close();
    }

    public void testAddDatagramDefaults() throws IOException {
        AsyncDatagramGroup group = getFactory().openDatagramGroup();
        AsyncDatagram s = getFactory().openDatagram();

        ReadHandler h = new ReadHandler() {
            public void handle(Object arg0) {
            }
        };
        group.setDefaultReader(h);
        assertNull(s.getReader());
        group.add(s);
        //Thread.sleep(100); //TODO fix
        //assertEquals(s.getReader(), h);//TODO fix

        AsyncDatagram s1 = getFactory().openDatagram();
        assertNull(s1.getDefaultExecutor());
        Executor ex = Executors.newCachedThreadPool();
        group.setDefaultExecutor(ex);
        group.add(s1);
        assertSame(ex, s1.getDefaultExecutor());

        AsyncDatagram s2 = getFactory().openDatagram();
        assertNull(s2.getDefaultDestination());
        Offerable o = new Offerable() {
            public boolean offer(Object o) {
                return true;
            }
        };
        group.setDefaultDestination(o);
        group.add(s2);
        assertSame(o, s2.getDefaultDestination());

        s.close().getIO();
        s1.close().getIO();
        s2.close().getIO();
    }

    public void testTestRemove() throws IOException {
        AsyncDatagramGroup group = getFactory().openDatagramGroup();
        AsyncDatagram s = getFactory().openDatagram();
        assertFalse(group.remove(s));

        group.add(s);
        assertTrue(group.contains(s));
        s.close();
        assertFalse(group.contains(s));
        s.setGroup(group);
        assertFalse(group.contains(s));

        s.close().getIO();
    }

    public void testJoinHandler() throws IOException, InterruptedException {
        final AsyncDatagramGroup group = getFactory().openDatagramGroup();
        final AsyncDatagram s = getFactory().openDatagram();
        final BlockingQueue q = new LinkedBlockingQueue();
        final EventHandler h = createQueueHandlerOnce(q);

        assertNull(group.getJoinHandler());

        assertSame(group, group.setJoinHandler(h));
        assertSame(h, group.getJoinHandler());

        group.add(s);
        assertSame(s, awaitOnQueue(q));

        s.close().getIO();
    }

    public void testAddMonitor() throws IOException, InterruptedException {
        final AsyncDatagramGroup group = getFactory().openDatagramGroup();
        final AsyncDatagram socket = getFactory().openDatagram();
        final BlockingQueue<Object> q = new LinkedBlockingQueue<Object>();
        final DatagramGroupMonitor h = new DatagramGroupMonitor() {
            public void join(AsyncDatagramGroup group, AsyncDatagram socket) {
                q.add(new Object[] {group, socket});
            }
        };
        group.setMonitor(h);
        socket.setGroup(group);
        Object[] p = (Object[]) awaitOnQueue(q);

        assertSame(group, p[0]);
        assertSame(socket, p[1]);

        socket.close().getIO();
    }

    public void testLeaveHandler() throws IOException, InterruptedException {
        final AsyncDatagramGroup group = getFactory().openDatagramGroup();
        final AsyncDatagram s = getFactory().openDatagram();
        final BlockingQueue q = new LinkedBlockingQueue();
        final EventHandler h = createQueueHandlerOnce(q);

        assertNull(group.getLeaveHandler());

        assertSame(group, group.setLeaveHandler(h));
        assertSame(h, group.getLeaveHandler());

        group.add(s);
        assertTrue(group.remove(s));
        assertFalse(group.remove(s));
        assertSame(s, awaitOnQueue(q));

        s.close().getIO();
    }

    public void testLeaveMonitor() throws IOException, InterruptedException {
        final AsyncDatagramGroup group = getFactory().openDatagramGroup();
        final AsyncDatagram socket = getFactory().openDatagram();
        final BlockingQueue q = new LinkedBlockingQueue();
        final DatagramGroupMonitor h = new DatagramGroupMonitor() {
            public void leave(AsyncDatagramGroup group, AsyncDatagram socket, Throwable cause) {
                q.add(new Object[] {group, socket});
            }
        };
        group.setMonitor(h);

        socket.setGroup(group);
        socket.setGroup(null);

        Object[] p = (Object[]) awaitOnQueue(q);

        assertSame(group, p[0]);
        assertSame(socket, p[1]);

        socket.close().getIO();
    }
}