/**
 * 
 */
package org.coconut.management.realexamples;

import org.coconut.management.monitor.LongCounter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ByteWriter {
    public static void main(String[] args) throws Exception {
        // create a long counter with a name & description
        LongCounter lr = LongCounter.newConcurrent("Bytes written",
                "Number of Bytes written to disk A");
        // register it with the platform Mbeanserver
        lr.register("my.app:name=BytesWritten");

        // simulate writing bytes to disk
        for (int i = 0; i < 10000; i++) {
            lr.addAndGet((i << 2) % 123);
            Thread.sleep(15);
        }

        // unregisters the counter with the platform MBeanserver
        lr.unregister();
    }
}
