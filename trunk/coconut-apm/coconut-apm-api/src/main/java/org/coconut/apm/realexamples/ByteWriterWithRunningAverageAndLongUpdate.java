/**
 * 
 */
package org.coconut.apm.realexamples;

import java.util.concurrent.TimeUnit;

import org.coconut.apm.Apms;
import org.coconut.apm.ExecutableApmGroup;
import org.coconut.apm.monitor.LongCounter;
import org.coconut.apm.monitor.TimedAverage;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ByteWriterWithRunningAverageAndLongUpdate {
    public static void main(String[] args) throws Exception {
        
        ExecutableApmGroup mg = Apms.newExecutableGroup("BytesWritten");
        LongCounter lr = mg.add(LongCounter.newConcurrent("BytesWritten",
                "Number of Bytes written to disk A"));

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
