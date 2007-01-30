/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class AsyncSocketWriteLimitTest extends AioTestCase {

    public void testSetBufferLimitGetSet() throws IOException {
        final AsyncSocket socket = getFactory().openSocket();

        assertEquals(Long.MAX_VALUE, socket.getBufferLimit());

        assertSame(socket, socket.setBufferLimit(1000));
        assertEquals(1000, socket.getBufferLimit());

        try {
            socket.setBufferLimit(-1);
        } catch (IllegalArgumentException e) {
            return;
        } finally {
            socket.closeNow();
        }
        fail("did not fail");
    }

    public void testSetQueueLimit() throws IOException {
        final AsyncSocket socket = getFactory().openSocket();

        assertEquals(Integer.MAX_VALUE, socket.getWriteQueueLimit());

        socket.setWriteQueueLimit(1000);
        assertEquals(1000, socket.getWriteQueueLimit());

        try {
            socket.setWriteQueueLimit(-1);
        } catch (IllegalArgumentException e) {
            return;
        } finally {
            socket.closeNow();
        }
        fail("did not fail");
    }
}