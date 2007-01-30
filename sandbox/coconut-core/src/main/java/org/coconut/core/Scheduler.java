package coconut.core;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public interface Scheduler {
    long delta(TimeUnit unit);
    long deltaNow(TimeUnit unit);
    void rescheduleAt(Date data);
    void rescheduleAt(long time, TimeUnit unit);
    void rescheduleAtFixedRate(long initialDelay, long period, TimeUnit unit);
    void rescheduleUsingCron(String cron);
    void rescheduleWithFixedDelay(long initialDelay, long delay, TimeUnit unit);
    void stop();
}
