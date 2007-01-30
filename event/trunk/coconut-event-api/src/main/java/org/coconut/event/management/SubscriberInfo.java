/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.management;

public class SubscriberInfo {

    private String name;

    public String getName() {
        return name;
    }

    long getNumberOfEventsReceived() {
        return 0;
    }

    long getNumberOfDeliveries() {
        return 0;
    }

    long getNumberOfDeliveryFailures() {
        return 0;
    }
}
