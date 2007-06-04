/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.event;

import org.coconut.cache.service.event.CacheEvent;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EventConfiguration {

    //disable gets
    /**
     * Note this setting is only used if an EventStrategy has been set.
     */
    public static enum EventNumbering {

        /**
         * nextId>=previousID
         */
        INCREASING,

        /**
         * No Event sequence number is generated and
         * {@link java.lang.Long#MIN_VALUE} is returned as sequence id.
         */
        NONE,

        /**
         * nextId=lastPreviousId+1 Important, might in some data structure that
         * sorts incoming elements. no gaps allowed then
         */
        ORDERED,

        /**
         * nextId>previousID
         */
        STRICTLY_INCREASING,

        /**
         * see timing policy generate timestamp.
         */
        TIME_STAMP;
    }

    public class Events {

        public boolean getSendEarly(Class<? extends CacheEvent> event) {
            return true;
        }

        public boolean isEnabled() {
            return false;
        }

        public void setEnabled(boolean isEnabled) {

        }

        /**
         * This can be used to indicate that certain events
         * 
         * @param event
         * @param sendEarly
         */
        public Events setSendEarly(Class<? extends CacheEvent> event, boolean sendEarly) {
            return this;
        }
    }

    /*
     * none -> seqid=0; unique inc -> unique increasing numbers putconsist ->
     * put(a,"1") , put(a,"2") . "1".seqid<"2".seqId strong -> put(a,"1") ,
     * get(a)="1" -> put.seqid<get.seqid VeryStrong -> clear ->
     */
    public static enum EventStrategy {
        /**
         * No events are ever posted. Trying to create a subscription on the
         * caches eventsbus will throw an {@link UnsupportedOperationException}.
         */
        NO_EVENTS,

        /**
         * update operations are synchronized
         */
        NORMAL,

        /**
         * update and get operations are synchronized
         */
        STRONG,

        /**
         * No order . Information purposes
         */
        WEAK;
    }

    // deliver synchronized?
}
