/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class IOBottlenecks {


    final static byte[] BYTE_ARRAY_00_FF;

    static {
        BYTE_ARRAY_00_FF = new byte[256];
        for (int i = 0; i < BYTE_ARRAY_00_FF.length; i++) {
            BYTE_ARRAY_00_FF[i] = (byte) i;
        }
    }
    
    final static class IOReadWorker implements Runnable {

        private final static int BUFFER_SIZE = 8192;

        private final static ThreadLocal<byte[]> LOCAL = new ThreadLocal<byte[]>() {
            protected byte[] initialValue() {
                return new byte[BUFFER_SIZE];
            }
        };

        private File f;

        IOReadWorker(int size) throws IOException {
            f = File.createTempFile("coconut-event_", "ioload");
            f.createNewFile();
            f.deleteOnExit();
            OutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(f));
            for (int i = 0; i < size; i++) {
                stream.write(i);
            }
            stream.close();
        }

        IOReadWorker(File file) {
            this.f = file;
            if (!file.exists()) {
                throw new IllegalArgumentException("File does not exist, "
                        + file);
            } else if (!file.canRead()) {
                throw new IllegalArgumentException("File cannot be read, "
                        + file);
            }
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            byte[] buffer = LOCAL.get();
            try {
                FileInputStream fis = new FileInputStream(f);
                while (fis.read(buffer) >= 0){}
                    
                fis.close();
            } catch (IOException e) {
                throw new IllegalStateException("Could not process file", e);
            }
        }
    }
}
