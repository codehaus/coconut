package org.coconut.event;

import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.coconut.core.Offerable;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public interface ScheduledEventDispatcher<E> {

    /* could the event itself implement delayed */
    
    /* have this on the implementation */
    /* getDelay() returns Long.minimumvalue = cancel */
    ScheduledFuture<?> schedule(E event, Offerable<? extends E> o,
            Delayed nextTimeCalculator);

    ScheduledFuture<?> schedule(E event, Offerable<? extends E> o, long delay,
            TimeUnit unit);

    ScheduledFuture<?> scheduleAtFixedRate(E event, Offerable<? extends E> o,
            long initialDelay, long period, TimeUnit unit);

    ScheduledFuture<?> scheduleWithFixedDelay(E event,
            Offerable<? extends E> o, long initialDelay, long delay,
            TimeUnit unit);

    
    /* have this on the implementation */
    ScheduledFuture<?> schedule(Callable<E> eventFactory,
            Offerable<? extends E> o, Delayed nextTimeCalculator);

    ScheduledFuture<?> schedule(Callable<E> eventFactory,
            Offerable<? extends E> o, long delay, TimeUnit unit);

    ScheduledFuture<?> scheduleAtFixedRate(Callable<E> eventFactory,
            Offerable<? extends E> o, long initialDelay, long period,
            TimeUnit unit);

    ScheduledFuture<?> scheduleWithFixedDelay(Callable<E> eventFactory,
            Offerable<? extends E> o, long initialDelay, long delay,
            TimeUnit unit);
    // ScheduledSubcription scheduleWithCron(Callable<E> eventFactory, String
    // cronExpression);
}
