package coconut.core;

import java.util.concurrent.TimeUnit;

/**
 * Used for timestamping events
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: TimeStamped.java,v 1.1 2004/12/12 13:53:19 kasper Exp $
 */
public interface TimeStamped
{
    /**
     * This method is used for timestamping events
     * 
     * @return a new instance
     */
    long getTimeStamp(TimeUnit unit);
}
