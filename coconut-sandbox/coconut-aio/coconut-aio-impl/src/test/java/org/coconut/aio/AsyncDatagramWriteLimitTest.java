/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import java.io.IOException;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class AsyncDatagramWriteLimitTest extends AioTestCase {

    public void testSetBufferLimitGetSet() throws IOException {
        final AsyncDatagram socket = getFactory().openDatagram();

        assertEquals(Long.MAX_VALUE, socket.getBufferLimit());

        assertSame(socket, socket.setBufferLimit(1000));
        assertEquals(1000, socket.getBufferLimit());

        try {
            socket.setBufferLimit(-1);
        } catch (IllegalArgumentException e) {
            return;
        } finally {
            socket.close();
        }
        fail("did not fail");
    }

    public void testSetQueueLimit() throws IOException {
        final AsyncDatagram socket = getFactory().openDatagram();

        assertEquals(Integer.MAX_VALUE, socket.getWriteQueueLimit());

        socket.setWriteQueueLimit(1000);
        assertEquals(1000, socket.getWriteQueueLimit());

        try {
            socket.setWriteQueueLimit(-1);
        } catch (IllegalArgumentException e) {
            return;
        } finally {
            socket.close();
        }
        fail("did not fail");
    }
}