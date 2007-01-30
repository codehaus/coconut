package coconut.core;

/**
 * Calculate the size of a particular object
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Sizeable.java,v 1.1 2004/12/12 13:53:19 kasper Exp $
 */
public interface Sizeable<T>
{
    double getSize(T type);
}
