/**
 * 
 */
package org.coconut.apm.realexamples;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.coconut.apm.monitor.DefaultMetricManager;
import org.coconut.apm.monitor.DoubleSamplingCounter;
import org.coconut.apm.monitor.LongSamplingCounter;
import org.coconut.apm.monitor.SingleExponentialSmoothing;
import org.coconut.apm.monitor.TimedAverage;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class PacketLatency {
    public static void main(String[] args) throws Exception {
        DefaultMetricManager mg = new DefaultMetricManager();

        // create a long counter with a name & description
        // and register it with the platform Mbeanserver
        LongSamplingCounter lr = mg.addMetric(new LongSamplingCounter(
                "Packets Received (Bytes)"));
        // adds an average of the number of samplings pr second
        // mg.add(new TimedAverage(lr.getRunningTotal(), "Bytes/s"));
        TimedAverage ta = mg.add(new TimedAverage(lr.liveTotal(), "Bytes/s"), 1,
                TimeUnit.SECONDS);
        ta.addEventHandler(mg.addMetric(new DoubleSamplingCounter()));
        ta.addEventHandler(mg.addMetric(new SingleExponentialSmoothing(0.3, "Bytes/s (SES)")));

        mg.startAndRegister("my.app:name=PacketObserver");
        Random r = new Random();
        // simulate receiving a packet and report latency
        for (int i = 0; i < 10000; i++) {
            lr.report(Math.round((r.nextGaussian() * 256 + 2048)));
            Thread.sleep((long) Math.abs(r.nextGaussian() * 15));
        }

        // unregisters the counter with the platform MBeanserver
        mg.stopAndUnregister();
    }
}