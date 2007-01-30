/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.AssertionFailedError;

/**
 * Accept test case
 * 
 * @version $Id: AsyncServerSocketAcceptTest.java,v 1.3 2004/06/08 12:49:54 kav
 *          Exp $
 */
@SuppressWarnings("unchecked")
public class AsyncServerSocketStartAcceptingTest extends AioTestCase {

    public void testStartAcceptingOfferableFuture() throws IOException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        assertFalse(socket.isAccepting());

        socket.bind(createBindingAddress(getNextPort()));

        socket.startAccepting(IGNORE_OFFERABLE).getIO();
        assertTrue(socket.isAccepting());

        socket.close().getIO();
        assertFalse(socket.isAccepting());
    }

    public void testStartAcceptingOfferableOfferable() throws IOException, InterruptedException, AssertionFailedError {
        final BlockingQueue q = new LinkedBlockingQueue();
        final AsyncServerSocket socket = getFactory().openServerSocket();

        socket.bind(createBindingAddress(getNextPort()));
        socket.startAccepting(IGNORE_OFFERABLE).setDestination(createQueueOfferableOnce(q));

        Object o = awaitOnQueue(q);
        assertTrue(socket.isAccepting());
        assertTrue(o instanceof AsyncServerSocket.AcceptingStarted);
        AsyncServerSocket.AcceptingStarted c = (AsyncServerSocket.AcceptingStarted) o;
        assertSame(socket, c.async());

        socket.close().getIO();
        assertFalse(socket.isAccepting());
    }

    public void testStartAcceptingOfferableCallback() throws IOException, InterruptedException, AssertionFailedError {
        final CountDownLatch latch = new CountDownLatch(1);
        final AsyncServerSocket socket = getFactory().openServerSocket();

        socket.bind(createBindingAddress(getNextPort()));
        socket.startAccepting(IGNORE_OFFERABLE).setCallback(OWN_THREAD, createCallbackCompleted(latch));

        awaitOnLatch(latch);
        assertTrue(socket.isAccepting());

        socket.close().getIO();
        assertFalse(socket.isAccepting());
    }

    public void testStartAcceptingOfferableFuturePolicy() throws IOException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        assertFalse(socket.isAccepting());

        socket.bind(createBindingAddress(getNextPort()));

        socket.startAccepting(IGNORE_OFFERABLE, ACCEPT_ALL).getIO();
        assertTrue(socket.isAccepting());

        socket.close().getIO();
        assertFalse(socket.isAccepting());
    }

    public void testStartAcceptingOfferableOfferablePolicy() throws IOException, InterruptedException,
            AssertionFailedError {
        final BlockingQueue q = new LinkedBlockingQueue();
        final AsyncServerSocket socket = getFactory().openServerSocket();

        socket.bind(createBindingAddress(getNextPort()));
        socket.startAccepting(IGNORE_OFFERABLE, ACCEPT_ALL).setDestination(createQueueOfferableOnce(q));

        Object o = awaitOnQueue(q);
        assertTrue(socket.isAccepting());
        assertTrue(o instanceof AsyncServerSocket.AcceptingStarted);
        AsyncServerSocket.AcceptingStarted c = (AsyncServerSocket.AcceptingStarted) o;
        assertSame(socket, c.async());
        assertSame(ACCEPT_ALL, c.getPolicy());
        socket.close().getIO();
        assertFalse(socket.isAccepting());
    }

    public void testStartAcceptingOfferableCallbackPolicy() throws IOException, InterruptedException,
            AssertionFailedError {
        final CountDownLatch latch = new CountDownLatch(1);
        final AsyncServerSocket socket = getFactory().openServerSocket();

        socket.bind(createBindingAddress(getNextPort()));
        socket.startAccepting(IGNORE_OFFERABLE, ACCEPT_ALL).setCallback(OWN_THREAD, createCallbackCompleted(latch));

        awaitOnLatch(latch);
        assertTrue(socket.isAccepting());

        socket.close().getIO();
        assertFalse(socket.isAccepting());
    }

    public void testStartAcceptingCallbackFuture() throws IOException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        assertFalse(socket.isAccepting());

        socket.bind(createBindingAddress(getNextPort()));

        socket.startAccepting(OWN_THREAD, IGNORE_CALLBACK).getIO();
        assertTrue(socket.isAccepting());

        socket.close().getIO();
        assertFalse(socket.isAccepting());
    }

    public void testStartAcceptingCallbackOfferable() throws IOException, InterruptedException, AssertionFailedError {
        final BlockingQueue q = new LinkedBlockingQueue();
        final AsyncServerSocket socket = getFactory().openServerSocket();

        socket.bind(createBindingAddress(getNextPort()));
        socket.startAccepting(OWN_THREAD, IGNORE_CALLBACK).setDestination(createQueueOfferableOnce(q));

        Object o = awaitOnQueue(q);
        assertTrue(socket.isAccepting());
        assertTrue(o instanceof AsyncServerSocket.AcceptingStarted);
        AsyncServerSocket.AcceptingStarted c = (AsyncServerSocket.AcceptingStarted) o;
        assertSame(socket, c.async());

        socket.close().getIO();
        assertFalse(socket.isAccepting());
    }

    public void testStartAcceptingCallbackCallback() throws IOException, InterruptedException, AssertionFailedError {
        final CountDownLatch latch = new CountDownLatch(1);
        final AsyncServerSocket socket = getFactory().openServerSocket();

        socket.bind(createBindingAddress(getNextPort()));
        socket.startAccepting(OWN_THREAD, IGNORE_CALLBACK).setCallback(OWN_THREAD, createCallbackCompleted(latch));

        awaitOnLatch(latch);
        assertTrue(socket.isAccepting());

        socket.close().getIO();
        assertFalse(socket.isAccepting());
    }

    public void testStartAcceptingCallbackFuturePolicy() throws IOException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        assertFalse(socket.isAccepting());

        socket.bind(createBindingAddress(getNextPort()));

        socket.startAccepting(OWN_THREAD, IGNORE_CALLBACK, ACCEPT_ALL).getIO();
        assertTrue(socket.isAccepting());

        socket.close().getIO();
        assertFalse(socket.isAccepting());
    }

    public void testStartAcceptingCallbackOfferablePolicy() throws IOException, InterruptedException,
            AssertionFailedError {
        final BlockingQueue q = new LinkedBlockingQueue();
        final AsyncServerSocket socket = getFactory().openServerSocket();

        socket.bind(createBindingAddress(getNextPort()));
        socket.startAccepting(OWN_THREAD, IGNORE_CALLBACK, ACCEPT_ALL).setDestination(createQueueOfferableOnce(q));

        Object o = awaitOnQueue(q);
        assertTrue(socket.isAccepting());
        assertTrue(o instanceof AsyncServerSocket.AcceptingStarted);
        AsyncServerSocket.AcceptingStarted c = (AsyncServerSocket.AcceptingStarted) o;
        assertSame(socket, c.async());
        assertSame(ACCEPT_ALL, c.getPolicy());
        socket.close().getIO();
        assertFalse(socket.isAccepting());
    }

    public void testStartAcceptingCallbackCallbackPolicy() throws IOException, InterruptedException,
            AssertionFailedError {
        final CountDownLatch latch = new CountDownLatch(1);
        final AsyncServerSocket socket = getFactory().openServerSocket();

        socket.bind(createBindingAddress(getNextPort()));
        socket.startAccepting(OWN_THREAD, IGNORE_CALLBACK, ACCEPT_ALL).setCallback(OWN_THREAD,
                createCallbackCompleted(latch));

        awaitOnLatch(latch);
        assertTrue(socket.isAccepting());

        socket.close().getIO();
        assertFalse(socket.isAccepting());
    }

    /*
     * 
     * public void testStartAcceptingOfferablePolicy() throws IOException,
     * InterruptedException, AssertionFailedError { final int port =
     * getNextPort(); final CountDownLatch latch = new CountDownLatch(1); final
     * AcceptPolicy policy = new AcceptPolicy() { public int
     * acceptNext(AsyncServerSocket arg0) { return 1; } };
     * 
     * AsyncServerSocket socket = getFactory().openServerSocket(new Offerable() {
     * public boolean offer(Object started) { assertTrue(started instanceof
     * AsyncServerSocket.AcceptingStarted); AsyncServerSocket.AcceptingStarted s =
     * (AcceptingStarted) started; assertSame(policy, s.getPolicy());
     * latch.countDown(); return true; } }); try { socket.bind(new
     * InetSocketAddress(port)); socket.startAccepting(IGNORE_OFFERABLE,
     * policy); if (!latch.await(1, TimeUnit.SECONDS)) throw new
     * AssertionFailedError("Did not send to sink");
     * 
     * assertTrue(socket.isAccepting()); } finally { socket.close().getIO(); }
     *  }
     * 
     * public void testStopAccepting() throws IOException { AsyncServerSocket
     * socket = getFactory().openServerSocket(); final int port = getNextPort(); try {
     * socket.bind(new InetSocketAddress(port));
     * socket.startAccepting(IGNORE_OFFERABLE).getIO();
     * socket.stopAccepting().getIO(); assertFalse(socket.isAccepting()); }
     * finally { socket.close().getIO(); } }
     * 
     *  
     */
}