/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.apm;

import org.coconut.apm.spi.JMXConfigurator;
import org.coconut.core.Named;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface Apm extends Named {

    void configureJMX(JMXConfigurator jmx);

    String getDescription();
}
