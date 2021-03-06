package coconut.core.annotation;

import java.util.concurrent.TimeUnit;

/**
 *
 *  
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 */
public @interface FixedDelaySchedule {
    long initialDelay() default 0;
    long delay();
    TimeUnit unit();
}
