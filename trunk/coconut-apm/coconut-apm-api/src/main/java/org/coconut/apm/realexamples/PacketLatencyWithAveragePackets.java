/**
 * 
 */
package org.coconut.apm.realexamples;

import java.util.Random;

import org.coconut.apm.monitor.LongSamplingCounter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class PacketLatencyWithAveragePackets {
    public static void main(String[] args) throws Exception {
        // create a long counter with a name & description
        // and register it with the platform Mbeanserver
        LongSamplingCounter lr = new LongSamplingCounter("PacketLatency");
        // register it with the platform Mbeanserver
        lr.register("my.app:name=PacketLatency");

        Random r = new Random();
        // simulate receiving a packet and report latency
        for (int i = 0; i < 10000; i++) {
            lr.report(Math.round((r.nextGaussian() * 20 + 80)));
            Thread.sleep(15);
        }

        // unregisters the counter with the platform MBeanserver
        lr.unregister();
    }
}
