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
