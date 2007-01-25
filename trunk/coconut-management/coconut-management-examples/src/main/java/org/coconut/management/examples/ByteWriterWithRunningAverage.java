/**
 * 
 */
package org.coconut.management.examples;

import java.util.concurrent.TimeUnit;

import org.coconut.management.Managements;
import org.coconut.management.ManagedExecutableGroup;
import org.coconut.management.defaults.DefaultExecutableGroup;
import org.coconut.management.monitor.LongCounter;
import org.coconut.management.monitor.TimedAverage;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ByteWriterWithRunningAverage {
    public static void main(String[] args) throws Exception {
        ManagedExecutableGroup group = Managements.newExecutableGroup("BytesTransfered");
        // adds a long counter that keeps track of the total number of bytes
        // written
        LongCounter lr = LongCounter.newConcurrent("Bytes written",
                "Number of Bytes written to disk A");
        // add it to the group
        group.add(lr);
        // adds a counter to keep of the average number of bytes written per
        // second
        group.add(new TimedAverage(lr), 1, TimeUnit.SECONDS);
        
        // start sampling and register with platform mbean server
        group.startAndRegister("my.app:name=BytesTransferred");
        for (int i = 0; i < 10000; i++) {
            lr.addAndGet((i << 2) % 123);
            Thread.sleep(15);
        }
        group.stopAndUnregister();
    }
}
