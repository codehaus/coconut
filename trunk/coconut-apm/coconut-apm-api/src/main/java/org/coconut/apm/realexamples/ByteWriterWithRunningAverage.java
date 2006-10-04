/**
 * 
 */
package org.coconut.apm.realexamples;

import java.util.concurrent.TimeUnit;

import org.coconut.apm.monitor.DefaultMetricManager;
import org.coconut.apm.monitor.LongCounter;
import org.coconut.apm.monitor.TimedAverage;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ByteWriterWithRunningAverage {
    public static void main(String[] args) throws Exception {
        DefaultMetricManager mg = new DefaultMetricManager();

        // adds a long counter that keeps track of the total number of bytes
        // written
        LongCounter lr =  LongCounter.newConcurrent("Bytes written",
                "Number of Bytes written to disk A");
        // add it to the group
        mg.addMetric(lr);
        // adds a counter to keep of the average number of bytes written per
        // second
        mg.add(new TimedAverage(lr), 1, TimeUnit.SECONDS);

        // start sampling and register with platform mbean server
        mg.startAndRegister("my.app:name=BytesTransferred");
        for (int i = 0; i < 10000; i++) {
            lr.addAndGet((i << 2) % 123);
            Thread.sleep(15);
        }
        mg.stopAndUnregister();
    }
}
