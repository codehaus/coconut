package coconut.core;

import java.util.concurrent.TimeUnit;

/**
 * In classical flow network theory, a Source is an origin of events.
 * This is really the other side of the coconut.core.Sink interface
 * 
 * @version $Id: Source.java,v 1.1 2004/12/12 13:53:19 kasper Exp $
 */
public interface Source<E>
{
    /**
     * @return
     */
    E poll();

    /**
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     */
    E poll(long timeout, TimeUnit unit) throws InterruptedException;
    
    /**
     * @return
     * @throws InterruptedException
     */
    E take() throws InterruptedException;
    
    /**
     * @param array
     * @param max
     * @return
     */
    int poll(E[] array);

    /**
     * @param array
     * @param max
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     */
    int poll(E[] array,  long timeout, TimeUnit unit) throws InterruptedException;
    
    /**
     * @param array
     * @return
     * @throws InterruptedException
     */
    int take(E[] array) throws InterruptedException;
}
 
