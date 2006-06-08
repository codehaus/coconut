package org.coconut.aio.util.monitor.debug;

import java.nio.ByteBuffer;

/**
 * Byteutil Just dublicate the buffers when pushing. Instead of copying the
 * positions.
 * 
 * @version $Id$
 */
public class ByteUtil {
    private final static ThreadLocal positionStack = new ThreadLocal();

    private ByteUtil() {

    }

    public static byte[] popBytes() {
        Mem mem = (Mem) positionStack.get();
        int bytesWritten = 0;
        for (int i = 0; i < mem.length; i++) {
            bytesWritten += mem.buffers[i + mem.offset].position();
        }
        byte[] bytes = new byte[bytesWritten]; // assume its integer

        int byteIndex = 0;
        for (int i = 0; i < mem.length; i++) {
            ByteBuffer buffer = mem.buffers[mem.offset + i];
            int previousPosition = mem.positions[i];
            int currentPosition = buffer.position();

            if (currentPosition != previousPosition) {
                buffer.position(previousPosition);
                // retrieve the bytes, and reset position
                buffer.get(bytes, byteIndex, currentPosition - previousPosition);
                byteIndex += currentPosition - previousPosition;
            }
        }
        return bytes;
    }

    public static void pushBytes(ByteBuffer[] buffers, int offset, int length) {
        positionStack.set(new Mem(buffers, offset, length));
    }

    private static final class Mem {
        final ByteBuffer[] buffers;
        final int offset;
        final int length;
        final int[] positions;

        /**
         * @param buffers
         * @param offset
         * @param length
         */
        public Mem(final ByteBuffer[] buffers, final int offset, final int length) {
            super();
            this.buffers = buffers;
            this.offset = offset;
            this.length = length;
            positions = new int[buffers.length];
            for (int i = 0; i < length; i++) {
                positions[i] = buffers[i + offset].position();
            }
        }

    }
}