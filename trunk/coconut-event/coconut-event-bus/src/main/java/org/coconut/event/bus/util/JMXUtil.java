package org.coconut.event.bus.util;

import java.io.PrintStream;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

import org.coconut.core.Offerable;
import org.coconut.core.Transformer;


/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class JMXUtil {

    public static <E> Offerable<E> broadcaster(final NotificationBroadcasterSupport nbs,
        final Transformer<E, Notification> t) {
        return new Offerable<E>() {
            public boolean offer(E element) {
                Notification n = t.transform(element);
                nbs.sendNotification(n);
                return true;
            }
        };
    }
    public static <E> Offerable<E> safeBroadcaster(final NotificationBroadcasterSupport nbs,
        final Transformer<E, Notification> t) {
        return new Offerable<E>() {
            public boolean offer(E element) {
                try {
                    Notification n = t.transform(element);
                    nbs.sendNotification(n);
                    return true;
                } catch (RuntimeException e) {
                    return false;
                }
            }
        };
    }
    public static <E> Offerable<E> safeBroadcaster(final NotificationBroadcasterSupport nbs,
        final Transformer<E, Notification> t, final PrintStream erroneousPrintStream) {
        return new Offerable<E>() {
            public boolean offer(E element) {
                try {
                    Notification n = t.transform(element);
                    nbs.sendNotification(n);
                    return true;
                } catch (RuntimeException e) {
                    e.printStackTrace(erroneousPrintStream);
                    return false;
                }
            }
        };
    }
}
