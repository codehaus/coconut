package org.coconut.event.management;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public interface EventsBusMBean {
    void resetStatistics();

    /**
     * Returns the number of events that was accepted (TODO define accepted) by
     * the {@link EventBus#offer()} or offerAll method.
     */
    int getNumberOfEventsReceived();

    int getNumberOfDeliveries();

    int getNumberOfDeliveryFailures();

    int getNumberOfSubscribers();

    String[] getAllSubscriberNames();

    void cancel(String subcriber);
    
    SubscriberInfo getInfo(String subscriber);
    SubscriberInfo[] getInfos(String[] subscriber);
}
