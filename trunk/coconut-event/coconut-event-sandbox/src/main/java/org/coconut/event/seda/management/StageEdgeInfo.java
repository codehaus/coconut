/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.seda.management;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: StageEdgeInfo.java 26 2006-07-14 11:42:29Z kasper $
 */
public class StageEdgeInfo {
    private final String from;

    private final String to;

    // private long events;
    public StageEdgeInfo(String from, String to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the name of the stage from which events are dispatched from.
     * 
     * @return
     */
    String getFrom() {
        return from;
    }

    /**
     * Returns the name of the stage where events are processed.
     */
    String getTo() {
        return to;
    }

}
