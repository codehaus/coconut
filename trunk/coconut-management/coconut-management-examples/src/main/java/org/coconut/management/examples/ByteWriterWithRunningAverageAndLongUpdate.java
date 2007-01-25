/**
 * 
 */
package org.coconut.management.examples;

import java.util.concurrent.TimeUnit;

import org.coconut.management.Managements;
import org.coconut.management.ManagedExecutableGroup;
import org.coconut.management.monitor.LongCounter;
import org.coconut.management.monitor.TimedAverage;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ByteWriterWithRunningAverageAndLongUpdate {
    public static void main(String[] args) throws Exception {
        
        ManagedExecutableGroup mg = Managements.newExecutableGroup("BytesWritten");
        LongCounter lr = LongCounter.newConcurrent("BytesWritten",
                "Number of Bytes written to disk A");
        
        mg.add(lr);
        mg.add(new TimedAverage(lr));
        mg.add(new TimedAverage(lr, "Bytes written/s (last 10s)",
                "Number of Bytes written to disk A/second during the last 10 seconds"),
                10, TimeUnit.SECONDS);
        mg.startAndRegister("my.app:name=BytesTransferred");
        for (int i = 0; i < 10000; i++) {
            lr.addAndGet((i << 2) % 123);
            Thread.sleep(15);
        }
        mg.stopAndUnregister();
    }
}
