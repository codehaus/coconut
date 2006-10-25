/**
 * 
 */
package org.coconut.management.realexamples;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.coconut.management.Apms;
import org.coconut.management.ExecutableApmGroup;
import org.coconut.management.monitor.DoubleSamplingCounter;
import org.coconut.management.monitor.LongSamplingCounter;
import org.coconut.management.monitor.SingleExponentialSmoothing;
import org.coconut.management.monitor.TimedAverage;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class PacketLatency {
    public static void main(String[] args) throws Exception {
        ExecutableApmGroup grp = Apms.newExecutableGroup();

        // create a long counter with a name & description
        // and register it with the platform Mbeanserver
        LongSamplingCounter lr = grp.add(new LongSamplingCounter(
                "Packets Received (Bytes)", ""));
        // adds an average of the number of samplings pr second
        // mg.add(new TimedAverage(lr.getRunningTotal(), "Bytes/s"));
        TimedAverage ta = grp.add(new TimedAverage(lr.liveTotal(), "Bytes/s"), 1,
                TimeUnit.SECONDS);
        ta.addEventHandler(grp.add(new DoubleSamplingCounter()));
        ta.addEventHandler(grp.add(new SingleExponentialSmoothing(0.3, "Bytes/s (SES)")));

        grp.startAndRegister("my.app:name=PacketObserver");
        Random r = new Random();
        // simulate receiving a packet and report latency
        for (int i = 0; i < 10000; i++) {
            lr.report(Math.round((r.nextGaussian() * 256 + 2048)));
            Thread.sleep((long) Math.abs(r.nextGaussian() * 15));
        }

        // unregisters the counter with the platform MBeanserver
        grp.stopAndUnregister();
    }
}
