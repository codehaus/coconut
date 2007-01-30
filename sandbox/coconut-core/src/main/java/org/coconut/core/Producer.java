package coconut.core;

/**
 * A class used for producing elements.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Producer.java,v 1.1 2004/12/12 13:53:19 kasper Exp $
 */
public interface Producer<T> {
    T produce();
}
