package org.coconut.event.management;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public interface EventsBusMBean {
    void resetStatistics();

    /**
     * Returns the number of events that was accepted by the
     * {@link EventBus#offer(Object)},{@link EventBus#process(Object)} or
     * {@link EventBus#offerAll(Collection)} method.
     */
    long getNumberOfIncomingEvents();

    /**
     * Returns the number of events that have been delivered, the number of
     * times process has been invoked.
     */
    long getTotalNumberOfDeliveries();

    long getTotalNumberOfSuccesfullyDeliveries();

    int getNumberOfSubscribers();

    String[] getAllSubscriberNames();

    void cancel(String subcriber);

    SubscriberInfo getInfo(String subscriber);

    SubscriberInfo[] getInfos(String[] subscriber);
}
