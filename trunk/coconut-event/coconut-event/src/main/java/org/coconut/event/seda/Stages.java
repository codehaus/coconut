/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event.seda;

import java.io.PrintStream;

import org.coconut.event.seda.management.StageManagerMXBean;
import org.coconut.event.seda.management.StageStatistics;
import org.coconut.internal.util.tabular.TabularFormatter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Stages.java 32 2006-08-21 14:13:00Z kasper $
 */
public class Stages {

    public static void printUsage(StageManagerMXBean bean, PrintStream ps) {
        TabularFormatter t = new TabularFormatter(bean.getStageCount() + 1, 10);
        t.setResultLine(true);
        t.setHeader("Name", "Processed", "Failed", "Events/S", "Time",
                "Avg Time", "CPU Time", "Avg CPU Time", "User/System Time",
                "Avg User/System Time");
        String[] stages = bean.getAllStageNames();
        StageStatistics[] info = bean.getStageInfo(stages);
        ps.println("Started " + info[0].getEventsAccepted() + " Finished "
                + info[info.length - 1].getEventsProcessed());
        long sumTime = 0;
        long sumCpuTime = 0;
        long sumUserTime = 0;
        long events = 0;
        long eventsFailed = 0;
        StageStatistics in = null;
        for (int i = 0; i < stages.length; i++) {

            in = info[i];
            // System.out.println(TO_NAME.transform(in));
            sumTime += in.getTotalTime();
            sumCpuTime += in.getTotalCpuTime();
            sumUserTime += in.getUserTime();
            events += in.getEventsAccepted();
            eventsFailed += in.getEventsFailed();
            long systemTime = in.getTotalCpuTime() - in.getUserTime();
            String avgTime = "NA";
            String avgCpuTime = "NA";
            String avgUserTime = "NA";
            String avgSystemTime = "NA";
            if (in.getEventsAccepted() > 0) {
                avgTime = TabularFormatter.formatTime(in.getTotalTime()
                        / in.getEventsAccepted());

                avgCpuTime = TabularFormatter.formatTime(in.getTotalCpuTime()
                        / in.getEventsAccepted());
                avgUserTime = TabularFormatter.formatTime(in.getUserTime()
                        / in.getEventsAccepted());
                avgSystemTime = TabularFormatter.formatTime(systemTime
                        / in.getEventsAccepted());
            }
            t.addNextRow(in.getName(), in.getEventsProcessed(), in
                    .getEventsFailed(), TabularFormatter.form(in
                    .getAverageEventsPerSecond()), TabularFormatter
                    .formatTime(in.getTotalTime()), avgTime, TabularFormatter
                    .formatTime(in.getTotalCpuTime()), avgCpuTime,
                    TabularFormatter.formatTime(in.getUserTime()) + " / "
                            + TabularFormatter.formatTime(systemTime),
                    avgUserTime + " / " + avgSystemTime);
        }

        long systemTime = sumCpuTime - sumUserTime;
        String avgTime = "NA";
        String avgCpuTime = "NA";
        String avgUserTime = "NA";
        String avgSystemTime = "NA";
        if (events > 0) {
            avgTime = TabularFormatter.formatTime(sumTime / events);
            avgCpuTime = TabularFormatter.formatTime(sumCpuTime / events);
            avgUserTime = TabularFormatter.formatTime(sumUserTime / events);
            avgSystemTime = TabularFormatter.formatTime(systemTime / events);
        }
        t.addNextRow("Total", events, eventsFailed, TabularFormatter.form(in
                .getAverageEventsPerSecond()), TabularFormatter
                .formatTime(sumTime), avgTime, TabularFormatter
                .formatTime(sumCpuTime), avgCpuTime, TabularFormatter
                .formatTime(sumUserTime)
                + " / " + TabularFormatter.formatTime(systemTime), avgUserTime
                + " / " + avgSystemTime);
        ps.print(t);
    }

}
