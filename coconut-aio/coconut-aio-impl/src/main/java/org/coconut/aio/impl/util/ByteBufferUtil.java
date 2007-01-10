/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.impl.util;

import java.nio.ByteBuffer;

/**
 * Bytebuffer util
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ByteBufferUtil {
    public static long calcSize(ByteBuffer buffer) {
        return buffer.limit() - buffer.position();
    }
    public static long calcSize(ByteBuffer[] buffers) {
        return calcSize(buffers, 0, buffers.length);
    }
    public static long calcSize(ByteBuffer[] buffers, int offset, int length) {
        long totalLength = 0;
        for (int i = 0; i < length; i++) {
            totalLength += calcSize(buffers[i + offset]);
        }
        return totalLength;
    }
}