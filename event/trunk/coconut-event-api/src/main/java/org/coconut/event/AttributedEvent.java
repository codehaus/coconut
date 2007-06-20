/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event;

import java.util.Map;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface AttributedEvent {

   String CPU_TIME = "cpu_time"; // type long

    /**
     * Returns the sequence id of the object. Usually used for maintaining a
     * ordered collection of events.
     * <p>
     * As a general rule a sequence id is a positiv number.
     * <p>
     * If for some reason it is impossible to generate a sequence id or if it is
     * not needed, {@link Long#MIN_VALUE} should be returned.
     */
    String SEQUENCE_ID = "sequence_id"; // type long

    String TIMESTAMP = "timstamp"; // type long

    String WALLCLOCK_TIME = "wallclock_time"; // type long

    Map<String, Object> getAttributes();

}
