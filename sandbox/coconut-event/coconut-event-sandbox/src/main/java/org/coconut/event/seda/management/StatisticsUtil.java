/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.seda.management;

import org.coconut.event.seda.management.StageManagerMXBean;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: StatisticsUtil.java 26 2006-07-14 11:42:29Z kasper $
 */
public class StatisticsUtil {

    /**
     * Returns the overhead of the container.
     * 
     * @param bean
     * @return
     */
    public static double getCpuOverhead(StageManagerMXBean bean) {
        long total = bean.getTotalCpuTime();
        long stage = 0;
        for (StageStatistics ss : bean.getStageInfo()) {
            stage += ss.getTotalCpuTime();
        }
        System.out.println(total);
        System.out.println(stage);
        if (stage > 0) {
            return ((double) (total - stage)) / stage;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

}
