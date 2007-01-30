/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio.util.monitor.debug;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Bla bla bla
 * 
 * @version $Id$
 */
public class DebugUtil
{

    /**
     * Instances should NOT be constructed in standard programming.
     */
    private DebugUtil() {
    }

    public static void main(String[] args) throws IOException
    {
        dump("", new byte[]{0, 3, 4, 6, 34, 65, 56, 78, 56, 67, 68, 69, 80, 127, -120, -1, -1}, 0,
                System.out, 0);
    }
    /**
     * dump an array of bytes to an OutputStream
     *
     * @param data the byte array to be dumped
     * @param offset its offset, whatever that might mean
     * @param stream the OutputStream to which the data is to be
     *               written
     * @param index initial index into the byte array
     *
     * @exception IOException is thrown if anything goes wrong writing
     *            the data to stream
     * @exception ArrayIndexOutOfBoundsException if the index is
     *            outside the data array's bounds
     * @exception IllegalArgumentException if the output stream is
     *            null
     */
    public static void dumpEvent(String event, OutputStream stream) throws IOException
    {
        stream.write(event.getBytes());
        stream.write(EOL.getBytes());
    }
    public static void dump(ByteBuffer buffer, OutputStream stream) throws IOException
    {
        int size = buffer.limit() - buffer.position();
        byte[] bytes = new byte[size];
        System.arraycopy(buffer.array(), buffer.position(), bytes, 0, size);
        dump("", bytes, 0, stream, 0);
    }
    public static void dumpAndPrintException(String prefix, byte[] data, long offset,
            OutputStream stream, int index)
    {
        try
        {
            dump(prefix, data, offset, stream, index);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void dumpAndPrintException(byte[] data, long offset, OutputStream stream,
            int index)
    {
        try
        {
            dump("", data, offset, stream, index);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void dump(String prefix, byte[] data, long offset, OutputStream stream, int index)
            throws IOException
    {
        
        if ((index > data.length)) { throw new ArrayIndexOutOfBoundsException(
                "illegal index: " + index + " into array of length " + data.length); }
        if (prefix == null) prefix="";
        if (stream == null) { throw new IllegalArgumentException("cannot write to nullstream"); }
        long display_offset = offset + index;

        StringBuilder header = new StringBuilder(74+prefix.length());
        StringBuilder buffer = new StringBuilder(74+prefix.length());
        

            header.append(prefix);
        
        header.append("position 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F ------text------");
        header.append(EOL);
        stream.write(header.toString().getBytes());
        for (int j = index; j < data.length; j += 16)
        {
            buffer.append(prefix);
            int chars_read = data.length - j;

            if (chars_read > 16)
            {
                chars_read = 16;
            }
            buffer.append(dump(display_offset)).append(' ');
            for (int k = 0; k < 16; k++)
            {
                if (k < chars_read)
                {
                    buffer.append(dump(data[k + j]));
                }
                else
                {
                    buffer.append("  ");
                }
                buffer.append(' ');
            }
            for (int k = 0; k < chars_read; k++)
            {
                if ((data[k + j] >= ' ') && (data[k + j] < 127))
                {
                    buffer.append((char) data[k + j]);
                }
                else
                {
                    buffer.append('.');
                }
            }
            buffer.append(EOL);
            stream.write(buffer.toString().getBytes());
            stream.flush();
            buffer.setLength(0);
            display_offset += chars_read;
        }
    }
    private static final String EOL = System.getProperty("line.separator");
    private static final StringBuilder _lbuffer = new StringBuilder(8);
    private static final StringBuilder _cbuffer = new StringBuilder(2);
    private static final char _hexcodes[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
            'B', 'C', 'D', 'E', 'F'};
    private static final int _shifts[] = {28, 24, 20, 16, 12, 8, 4, 0};

    private static StringBuilder dump(long value)
    {
        _lbuffer.setLength(0);
        for (int j = 0; j < 8; j++)
        {
            _lbuffer.append(_hexcodes[((int) (value >> _shifts[j])) & 15]);
        }
        return _lbuffer;
    }

    private static StringBuilder dump(byte value)
    {
        _cbuffer.setLength(0);
        for (int j = 0; j < 2; j++)
        {
            _cbuffer.append(_hexcodes[(value >> _shifts[j + 6]) & 15]);
        }
        return _cbuffer;
    }
}