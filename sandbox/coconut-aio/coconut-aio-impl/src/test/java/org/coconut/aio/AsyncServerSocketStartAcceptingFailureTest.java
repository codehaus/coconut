/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.coconut.aio.AsyncServerSocket.ErroneousEvent;
import org.coconut.core.Offerable;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@SuppressWarnings("unchecked")
public class AsyncServerSocketStartAcceptingFailureTest extends AioTestCase {

    public void testStartFailed() throws IOException, InterruptedException {
        final AsyncServerSocket socket = getFactory().openServerSocket();
        final BlockingQueue q = new LinkedBlockingQueue();
        Offerable o = createQueueOfferableOnce(q);

        socket.close();
        socket.startAccepting(AioTestCase.IGNORE_OFFERABLE).setDestination(o);

        Object obj = awaitOnQueue(q);
        assertTrue(obj instanceof AsyncServerSocket.ErroneousEvent);
        AsyncServerSocket.ErroneousEvent event = (ErroneousEvent) obj;
        assertTrue(event.getCause() instanceof IOException);
        assertEquals(event.getMessage(), event.getCause().getMessage());
        assertTrue(event.getEvent() instanceof AsyncServerSocket.AcceptingStarted);
        assertEquals(event.async(), socket);

        socket.close().getIO();
    }

    public void testStartAcceptingNullFailure1() throws IOException {
        AsyncServerSocket socket = getFactory().openServerSocket();
        try {
            socket.startAccepting(null);
        } catch (NullPointerException e) {
            return;
        } finally {
            socket.close();
        }
        fail("did not fail");
    }
    public void testStartAcceptingNullFailure2() throws IOException {
        AsyncServerSocket socket = getFactory().openServerSocket();
        try {
            socket.startAccepting(IGNORE_OFFERABLE, null);
        } catch (NullPointerException e) {
            return;
        } finally {
            socket.close();
        }
        fail("did not fail");
    }
    public void testStartAcceptingNullFailure3() throws IOException {
        AsyncServerSocket socket = getFactory().openServerSocket();
        try {
            socket.startAccepting(null, ACCEPT_ALL);
        } catch (NullPointerException e) {
            return;
        } finally {
            socket.close();
        }
        fail("did not fail");
    }
    public void testStartAcceptingNullFailure4() throws IOException {
        AsyncServerSocket socket = getFactory().openServerSocket();
        try {
            socket.startAccepting(null, IGNORE_CALLBACK);
        } catch (NullPointerException e) {
            return;
        } finally {
            socket.close();
        }
        fail("did not fail");
    }
    public void testStartAcceptingNullFailure5() throws IOException {
        AsyncServerSocket socket = getFactory().openServerSocket();
        try {
            socket.startAccepting(OWN_THREAD, null);
        } catch (NullPointerException e) {
            return;
        } finally {
            socket.close();
        }
        fail("did not fail");
    }
    public void testStartAcceptingNullFailure6() throws IOException {
        AsyncServerSocket socket = getFactory().openServerSocket();
        try {
            socket.startAccepting(null, IGNORE_CALLBACK, ACCEPT_ALL);
        } catch (NullPointerException e) {
            return;
        } finally {
            socket.close();
        }
        fail("did not fail");
    }
    public void testStartAcceptingNullFailure7() throws IOException {
        AsyncServerSocket socket = getFactory().openServerSocket();
        try {
            socket.startAccepting(OWN_THREAD, null, ACCEPT_ALL);
        } catch (NullPointerException e) {
            return;
        } finally {
            socket.close();
        }
        fail("did not fail");
    }
    public void testStartAcceptingNullFailure8() throws IOException {
        AsyncServerSocket socket = getFactory().openServerSocket();
        try {
            socket.startAccepting(OWN_THREAD, IGNORE_CALLBACK, null);
        } catch (NullPointerException e) {
            return;
        } finally {
            socket.close();
        }
        fail("did not fail");
    }
}