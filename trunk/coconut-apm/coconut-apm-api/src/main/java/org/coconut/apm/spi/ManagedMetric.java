/**
 * 
 */
package org.coconut.apm.spi;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ManagedMetric {
    void prepare(JMXConfigurator jmx);
}
