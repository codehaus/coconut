package coconut.core;

/**
 * Used for creating new instance of a particular type
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: Factory.java,v 1.1 2004/12/12 13:53:19 kasper Exp $
 */
public interface Factory<T> {
    /**
     * This method is used to create new instances of a particul type
     * 
     * @return a new instance
     */
    T newInstance();
}
