/**
 * 
 */
package org.coconut.apm;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface MetricManager extends ApmGroup {

    void start();

    void register(String objectName);

    void startAndRegister(String name) throws Exception;

    void stopAndUnregister(String name) throws Exception;

}
