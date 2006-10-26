/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.examples;

import org.coconut.management.ApmGroup;
import org.coconut.management.monitor.LongCounter;
import org.coconut.management.monitor.LongSamplingCounter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class NewMettric {
    public static void main(String[] args) {
        ApmGroup m = null; // new MEDMED("PipelineFoo");
        ApmGroup stages = m.addGroup("Stages","");
        ApmGroup stage = stages.addGroup("ConvertToFooStage","");

        ApmGroup processed = stages.addGroup("Events","");
        processed.add(LongCounter.newConcurrent("Total Processed", ""));

        processed.add(LongCounter.newConcurrent("Total Failed", ""));
        processed.add(LongCounter.newConcurrent("Total Dropped", ""));
        // total.addMetric("Cummulativ History");

        processed.add(new LongSamplingCounter("Total Processed Pr/Second",""));

        stage.add(LongCounter.newConcurrent("Cpu Time", ""));
        stage.add(LongCounter.newConcurrent("System Time", ""));
        stage.add(LongCounter.newConcurrent("User Time", ""));

        stage.add(new LongSamplingCounter("Active Threads",""));

        System.out.println(m);
    }
}
