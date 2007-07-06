
public class CacheAttributes2 {


    /**
     * This key can be used to indicate how long time a cache entry should live in memory
     * before it is evicted to secondary storage such as a disk. The time-to-idle value
     * should be a long and should be measured in nano seconds. Use
     * {@link java.util.concurrent.TimeUnit} to convert between different time units.
     */
    public static final String TIME_TO_IDLE_NS = "time_to_idle_ns";
    
    //no events will posted for this operation
    public static String NO_EVENTS="no_events";
}
