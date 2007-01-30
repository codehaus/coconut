package coconut.core;

/**
 * A <code>Commitable</code> is an object returned from a
 * <code>prepareOffer</code> method that allows you to either
 * commit or abort the enqueue operation.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Commitable.java,v 1.1 2004/12/12 13:53:19 kasper Exp $
 */
public interface Commitable
{
    /**
     * Commit a previously prepared provisional enqueue operation (from
     * the <code>prepareOffer</code> method). Causes the provisionally
     * enqueued elements to appear on the queue for future dequeue
     * operations.  Note that once a <code>prepareEnqueue</code> has returned
     * a Commitable, the queue cannot reject the entries. A special case exist 
     * when the queue has been closed in which case the container either throws
     * a RuntimeException or ignores and logs it silently
     */
    void commit();

    /**
     * Abort a previously prepared provisional enqueue operation (from
     * the <code>prepareOffer</code> method). Causes the queue to discard
     * the provisionally enqueued elements.
     */
    void abort();
}
