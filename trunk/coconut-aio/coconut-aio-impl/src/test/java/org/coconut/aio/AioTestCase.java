package org.coconut.aio;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.ServerSocketChannel;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.extensions.RepeatedTest;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.coconut.aio.management.ManagementFactory;
import org.coconut.core.Callback;
import org.coconut.core.ErroneousHandler;
import org.coconut.core.EventHandler;
import org.coconut.core.Offerable;
import org.coconut.test.MockTestCase;

/**
 * Base AioTestCase Clean up
 * 
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public abstract class AioTestCase extends MockTestCase {

    private static DefaultTestProvider fac;
    public static int startport = 23000;
    public static int finishport = 27000;
    // public static int finishport = 23040;
    public static int currentport = startport;
    public static boolean persistPort = true;
    public static final int defaultShortTimeoutMillies = 1000;
    public static final int defaultLongTimeoutMillies = 5000;
    private final static ByteBuffer nonsensBuffer;
    static {
        byte[] bytes = new byte[8192];
        for (int i = 0; i < 8192; i++) {
            bytes[i] = (byte) (i % 256);
        }
        nonsensBuffer = ByteBuffer.wrap(bytes);
    }

    public ByteBuffer getNonsensBuffer() {
        return nonsensBuffer.duplicate();
    }

    public static final Offerable IGNORE_OFFERABLE = new Offerable() {
        public boolean offer(Object ignore) {
            return true;
        }
    };
    public static final Callback IGNORE_CALLBACK = new Callback() {
        public void completed(Object arg0) {}
        public void failed(Throwable arg0) {}
    };
    public static final EventHandler IGNORE_HANDLER = new EventHandler() {
        public void handle(Object arg0) {}
    };
    public static final ReadHandler IGNORE_READ_HANDLER = new ReadHandler() {
        public void handle(Object arg0) {}
    };

    public static final AcceptPolicy ACCEPT_ALL = new AcceptPolicy() {
        public int acceptNext(AsyncServerSocket arg0) {
            return Integer.MAX_VALUE;
        }
    };

    public static final Executor OWN_THREAD = new Executor() {
        public void execute(Runnable r) {
            r.run();
        }
    };

    public static long SHORT_DELAY_MS;
    public static long SMALL_DELAY_MS;
    public static long MEDIUM_DELAY_MS;
    public static long LONG_DELAY_MS;

    /**
     * Return the shortest timed delay. This could be reimplemented to use for
     * example a Property.
     */
    protected long getShortDelay() {
        return 50;
    }

    /**
     * Set delays as multiples of SHORT_DELAY.
     */
    protected void setDelays() {
        SHORT_DELAY_MS = getShortDelay();
        SMALL_DELAY_MS = SHORT_DELAY_MS * 5;
        MEDIUM_DELAY_MS = SHORT_DELAY_MS * 10;
        LONG_DELAY_MS = SHORT_DELAY_MS * 50;
    }

    /**
     * Flag set true if any threadAssert methods fail
     */
    public volatile boolean threadFailed;

    /**
     * Initialize test to indicate that no thread assertions have failed
     */
    public void setUp() {
        setDelays();
        threadFailed = false;
    }

    /**
     * Trigger test case failure if any thread assertions have failed
     */
    public void tearDown() {
        assertFalse(threadFailed);
        if (fac != null) {
            fac.shutdown();
        }
    }

    /**
     * Fail, also setting status to indicate current testcase should fail
     */
    public void threadFail(String reason) {
        threadFailed = true;
        fail(reason);
    }

    /**
     * If expression not true, set status to indicate current testcase should
     * fail
     */
    public void threadAssertTrue(boolean b) {
        if (!b) {
            threadFailed = true;
            assertTrue(b);
        }
    }

    /**
     * If expression not false, set status to indicate current testcase should
     * fail
     */
    public void threadAssertFalse(boolean b) {
        if (b) {
            threadFailed = true;
            assertFalse(b);
        }
    }

    /**
     * If argument not null, set status to indicate current testcase should fail
     */
    public void threadAssertNull(Object x) {
        if (x != null) {
            threadFailed = true;
            assertNull(x);
        }
    }

    /**
     * If arguments not equal, set status to indicate current testcase should
     * fail
     */
    public void threadAssertEquals(long x, long y) {
        if (x != y) {
            threadFailed = true;
            assertEquals(x, y);
        }
    }

    /**
     * If arguments not equal, set status to indicate current testcase should
     * fail
     */
    public void threadAssertEquals(Object x, Object y) {
        if (x != y && (x == null || !x.equals(y))) {
            threadFailed = true;
            assertEquals(x, y);
        }
    }

    /**
     * threadFail with message "should throw exception"
     */
    public void threadShouldThrow() {
        threadFailed = true;
        fail("should throw exception");
    }

    /**
     * threadFail with message "Unexpected exception"
     */
    public void threadUnexpectedException() {
        threadFailed = true;
        fail("Unexpected exception");
    }

   //private AtomicInteger //rts = new AtomicInteger(3244);

    public synchronized int getNextPort() {
        // TODO fix hack
        if (persistPort) {
            int port = startport;
            RandomAccessFile raf = null;
            File dir = new File("target");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File("target/aiohack.tmp");
            try {
                raf = new RandomAccessFile(file, "rw");
                port = raf.readInt() + 1;
            } catch (IOException ioe) {}
            if (port > finishport)
                port = startport;
            try {
                raf.setLength(0);
                raf.writeInt(port);
                raf.close();
            } catch (IOException ioe) {

            }
            return port;
        } else {
            currentport++;
            if (currentport > finishport)
                currentport = startport;
            return currentport;
        }
    }
    public SocketAddress createBindingAddress(int port) {
        return new InetSocketAddress(port);
    }
    public SocketAddress createConnectAddress(int port) throws UnknownHostException {
        InetAddress adr = InetAddress.getLocalHost();
        return new InetSocketAddress(adr, port);
    }

    public File createTmpFile() throws IOException {
        File f = File.createTempFile("coconut", "aio");
        f.deleteOnExit();
        return f;
    }
    public File createTmpFile(byte[] data) throws IOException {
        File f = createTmpFile();
        RandomAccessFile file = new RandomAccessFile(f, "rws");
        file.getChannel().write(ByteBuffer.wrap(data));
        file.close();
        return f;
    }

    protected static ByteBuffer getBytebuffer(String string) {
        return ByteBuffer.wrap(string.getBytes());
    }

    public void awaitOnLatch(CountDownLatch latch) throws InterruptedException {
        if (!latch.await(defaultShortTimeoutMillies, TimeUnit.MILLISECONDS))
            throw new AssertionFailedError();
    }

    public void awaitOnLatchLong(CountDownLatch latch) throws InterruptedException {
        for (int i = 0; i < 60; i++) {
            if (latch.await(1000, TimeUnit.MILLISECONDS))
                return;
            System.out.println(i);
            ((new Exception())).printStackTrace();
        }
        throw new AssertionFailedError();
    }

    public Object awaitOnQueue(BlockingQueue queue) throws InterruptedException {
        Object o = queue.poll(defaultShortTimeoutMillies, TimeUnit.MILLISECONDS);
        if (o == null) {
            (new Exception()).printStackTrace();
            throw new AssertionFailedError();
        }
        return o;
    }

    public synchronized static DefaultTestProvider getFactory() {
        if (fac == null)
            fac = new DefaultTestProvider();
        return fac;
    }

    public Object awaitOnQueueLong(BlockingQueue queue) throws InterruptedException {
        Object o = queue.poll(defaultLongTimeoutMillies, TimeUnit.MILLISECONDS);
        if (o == null) {
            (new Exception()).printStackTrace();
            throw new AssertionFailedError();
        }
        return o;
    }

    public ByteBuffer read(ScatteringByteChannel channel, ByteBuffer data) throws IOException {
        int length = data.remaining();
        channel.read(data);
        if (data.position() < length) {

            Thread.yield();
            channel.read(data);
        }
        if (data.position() < length) {
            // System.out.println("read 2");
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            channel.read(data);
        }
        if (data.position() < length) // try REALLY hard
        {
            // System.out.println("read 3");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            channel.read(data);
        }
        data.flip();
        return data;
    }
    public void read(ScatteringByteChannel channel, ByteBuffer[] data, int offset, int lenght)
        throws IOException {
        long length = 0;
        for (int i = 0; i < data.length; i++) {
            length += data[i].remaining();
        }
        if (offset == 0) {
            lenght -= channel.read(data);
        } else {
            lenght -= channel.read(data, offset, lenght);
        }
        if (lenght > 0) {

            Thread.yield();
            if (offset == 0) {
                lenght -= channel.read(data);
            } else {
                lenght -= channel.read(data, offset, lenght);
            }
        }
        if (lenght > 0) {
            // System.out.println("read 2");
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (offset == 0) {
                lenght -= channel.read(data);
            } else {
                lenght -= channel.read(data, offset, lenght);
            }
        }
        if (lenght > 0) // try REALLY hard
        {
            // System.out.println("read 3");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (offset == 0) {
                lenght -= channel.read(data);
            } else {
                lenght -= channel.read(data, offset, lenght);
            }
        }
        for (int i = 0; i < data.length; i++) {
            data[i].flip();
        }
    }

    public void readAndEqual(ReadableByteChannel channel, String expectedString) throws IOException {
        // System.out.println("trying to read");

        ByteBuffer data = ByteBuffer.allocate(expectedString.length() + 5);
        ByteBuffer expectedData = ByteBuffer.wrap(expectedString.getBytes());
        channel.read(data);
        // System.out.println("read " + count);
        if (data.position() < expectedData.limit()) {

            Thread.yield();
            channel.read(data);
        }
        if (data.position() < expectedData.limit()) {
            // System.out.println("read 2");
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            channel.read(data);
        }
        if (data.position() < expectedData.limit()) // try REALLY hard
        {
            // System.out.println("read 3");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            channel.read(data);
        }

        data.flip();
        if (!expectedData.equals(data)) {
            if (expectedData.position() == expectedData.limit())
                System.err.println("Expected NOTHING");
            else {
                System.err.println("Expected:");
                try {
                    DebugUtil.dump(data, System.err);
                } catch (Exception e) {
                    // ignore
                }

            }

            if (data.position() == data.limit())
                System.err.println("But got NOTHING before socket closed");
            else {
                System.err.println("But got:");
                DebugUtil.dump(data, System.err);
            }
            throw new AssertionFailedError("Buffers not equal");
        }
    }

    public void readAndEqualGathering(ScatteringByteChannel channel, String expectedString)
        throws IOException {
        final int l = expectedString.length();
        ByteBuffer expectedData = ByteBuffer.wrap(expectedString.getBytes());

        ByteBuffer[] bufs = new ByteBuffer[l + 1];
        for (int i = 0; i < l + 1; i++) {
            bufs[i] = ByteBuffer.allocate(1);
        }
        channel.read(bufs);

        if (bufs[l - 1].hasRemaining() == true) {

            Thread.yield();
            channel.read(bufs);
        }
        if (bufs[l - 1].hasRemaining() == true) {

            // System.out.println("read 2");
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            channel.read(bufs);
        }
        if (bufs[l - 1].hasRemaining() == true) // try REALLY hard
        {
            // System.out.println("read 3");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            channel.read(bufs);
        }
        

        for (int i = 0; i < l - 1; i++) {
            bufs[i].flip();
            byte a = bufs[i].get();
            byte b = expectedData.get(i);
            if (a != b) {
                System.out.println("got " + bufs[i]);
                System.out.println("expected " + expectedData.get(i));
                throw new AssertionFailedError("Buffers not equal");
            }
        }
        if (!bufs[l].hasRemaining())
            throw new AssertionFailedError("Buffers not equal");

    }

    public static void main(String[] args) throws Exception {
        System.out.println("Running tests");
        int iters = 1;
        if (args.length > 0)
            iters = Integer.parseInt(args[0]);
        if (args.length > 2) {
            AioTestCase.startport = Integer.parseInt(args[1]);
            AioTestCase.currentport = AioTestCase.startport;
            AioTestCase.finishport = Integer.parseInt(args[2]);
            AioTestCase.persistPort = false;
        }

        Test s = suites();

        iters = 10000;

        // s.addTest(new TestSuite(AsyncServerSocketAcceptTest.class));

        RepeatedTest t = new RepeatedTest(s, 2);

        for (int i = 0; i < iters; ++i) {
            t.run(new MyTestResult());
            long[] idSockets = ManagementFactory.getSocketMXBean().getAllSocketIds();
            long[] idServerSockets = ManagementFactory.getServerSocketMXBean()
                .getAllServerSocketIds();
            long[] idDatagramIds = ManagementFactory.getDatagramMXBean().getAllDatagramIds();
            if (idSockets.length > 0 || idServerSockets.length > 0) {
                System.out.println("Possible memory leak");
                for (int j = 0; j < idSockets.length; j++) {
                    Throwable tt = getFactory().hm.get(new Long(idSockets[j]));
                    tt.printStackTrace();
                }
                for (int j = 0; j < idServerSockets.length; j++) {
                    Throwable tt = getFactory().hm.get(new Long(idServerSockets[j]));
                    tt.printStackTrace();
                }
                for (int j = 0; j < idDatagramIds.length; j++) {
                    Throwable tt = getFactory().hm.get(new Long(idDatagramIds[j]));
                    tt.printStackTrace();
                }
            }
            if (i % 10 == 0)
                System.out.println(i);
            // junit.textui.TestRunner.run(t);
            // junit.textui.TestRunner.
            // System.gc();
            // System.runFinalization();
        }

        System.exit(0);
    }

    static class MyTestResult extends TestResult {

        public synchronized void addError(Test test, Throwable t) {
            System.err.println(test + " failed");
            t.printStackTrace();
        }
        public synchronized void addFailure(Test test, AssertionFailedError t) {
            System.err.println(test + " failed");
            t.printStackTrace();
        }
    }

    public static Test suites() {
        TestSuite suite = new TestSuite("AIO Unit Tests");

        suite.addTest(new TestSuite(AsyncDatagramBindTest.class));
        suite.addTest(new TestSuite(AsyncDatagramCloseTest.class));
        suite.addTest(new TestSuite(AsyncDatagramGroupManagementTest.class));
        suite.addTest(new TestSuite(AsyncDatagramGroupTest.class));
        suite.addTest(new TestSuite(AsyncDatagramManagementTest.class));
        suite.addTest(new TestSuite(AsyncDatagramTest.class));
        suite.addTest(new TestSuite(AsyncDatagramWriteLimitTest.class));
        suite.addTest(new TestSuite(AsyncDatagramWriteTest.class));

        suite.addTest(new TestSuite(AsyncServerSocketAcceptTest.class));
        suite.addTest(new TestSuite(AsyncServerSocketBindTest.class));
        suite.addTest(new TestSuite(AsyncServerSocketCloseTest.class));
        suite.addTest(new TestSuite(AsyncServerSocketManagementTest.class));
        suite.addTest(new TestSuite(AsyncServerSocketStartAcceptingFailureTest.class));
        suite.addTest(new TestSuite(AsyncServerSocketStartAcceptingTest.class));
        suite.addTest(new TestSuite(AsyncServerSocketStopAcceptingTest.class));
        suite.addTest(new TestSuite(AsyncServerSocketTest.class));

        suite.addTest(new TestSuite(AsyncSocketBindTest.class));
        suite.addTest(new TestSuite(AsyncSocketConnectTest.class));
        suite.addTest(new TestSuite(AsyncSocketCloseTest.class));
        suite.addTest(new TestSuite(AsyncSocketGroupManagementTest.class));
        suite.addTest(new TestSuite(AsyncSocketGroupTest.class));
        suite.addTest(new TestSuite(AsyncSocketManagementTest.class));
        suite.addTest(new TestSuite(AsyncSocketReadTest.class));
        suite.addTest(new TestSuite(AsyncSocketTest.class));
        suite.addTest(new TestSuite(AsyncSocketWriteLimitTest.class));
        suite.addTest(new TestSuite(AsyncSocketWriteTest.class));

        return suite;

    }

    public Callback createCallbackCompleted(final CountDownLatch latch) {
        return new Callback() {
            public void completed(Object arg0) {
                latch.countDown();
            }
            public void failed(Throwable ingore) {
                System.out.println("createCallbackCompleted failed called");
                (new Exception()).printStackTrace();
                throw new AssertionFailedError();
            }
        };
    }
    public Callback createCallbackCompleted(final Queue q) {
        return new Callback() {
            public void completed(Object o) {
                q.add(o);
            }
            public void failed(Throwable ingore) {
                System.out.println("createCallbackCompleted failed called");
                (new Exception()).printStackTrace();
                throw new AssertionFailedError();
            }
        };
    }
    public static <E> Callback<E> createCallbackFailed(final Queue<Object> q) {
        return new Callback<E>() {
            public void completed(E o) {
                System.out.println("createCallbackFailed failed called");
                (new Exception()).printStackTrace();
                throw new AssertionFailedError();

            }
            public void failed(Throwable ignore) {
                q.add(ignore);
            }
        };
    }
    public static Offerable< ? super Object> createQueueOfferableOnce(
        final BlockingQueue<Object> queue) {
        return new Offerable() {
            private int count = 0;

            public boolean offer(Object o) {
                if (count++ > 0) {
                    System.out.println("createQueueOfferableOnce called twice");
                    (new Exception()).printStackTrace();
                    throw new AssertionFailedError();
                }
                queue.add(o);
                return true;
            }
        };
    }
    public EventHandler createQueueHandlerOnce(final BlockingQueue queue) {
        return new EventHandler() {
            private int count = 0;

            public void handle(Object o) {
                if (count++ > 0) {
                    System.out.println("createQueueHandlerOnce called twice");
                    (new Exception()).printStackTrace();
                    throw new AssertionFailedError();
                }
                queue.add(o);
            }
        };
    }
    public EventHandler createQueueErroneousHandlerOnce(final BlockingQueue<Object> queue) {
        return new ErroneousHandler() {
            private int count = 0;

            public void handle(Object o) {
                System.out.println("createQueueErroneousHandlerOnce handle called");
                (new Exception()).printStackTrace();
                throw new AssertionFailedError();
            }
            public void handleFailed(Object o, Throwable t) {
                if (count++ > 0) {
                    System.out.println("createQueueErroneousHandlerOnce called twice");
                    (new Exception()).printStackTrace();
                    throw new AssertionFailedError();
                }
                queue.add(new Object[] {o, t});
            }
        };
    }

    CountDownLatch startAccepting(final Queue<Object> q, SocketAddress address,
        final int numberOfAccepts) throws IOException {
        final ServerSocketChannel ch = ServerSocketChannel.open();
        final CountDownLatch latch = new CountDownLatch(1);

        ch.socket().bind(address);

        final ExecutorService es = Executors.newSingleThreadExecutor();
        es.submit(new Runnable() {
            public void run() {
                int num = numberOfAccepts;
                while (num-- > 0) {
                    try {
                        q.add(ch.accept());
                    } catch (IOException e) {
                        System.out.println("connect(Queue q) 1 accept failed");
                        e.printStackTrace();
                        latch.countDown();
                        throw new AssertionFailedError();
                    }
                }
                try {
                    ch.close();
                } catch (IOException e1) {
                    System.out.println("connect(Queue q) 3 accept failed");
                    e1.printStackTrace();
                    throw new AssertionFailedError();
                } finally {
                    latch.countDown();
                    es.shutdown();
                }

            }
        });
        return latch;
    }
    public AsyncSocket emptySocket() {
        return (AsyncSocket) mock(AsyncSocket.class).proxy();
    }
    public AsyncSocketGroup emptySocketGroup() {
        return (AsyncSocketGroup) mock(AsyncSocketGroup.class).proxy();
    }
    public AsyncDatagram emptyDatagram() {
        return (AsyncDatagram) mock(AsyncDatagram.class).proxy();
    }
    public AsyncDatagramGroup emptyDatagramGroup() {
        return (AsyncDatagramGroup) mock(AsyncDatagramGroup.class).proxy();
    }
}
