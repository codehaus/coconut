/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.sandbox;

/**
 * top
 *   network-swith
 *     computer
 *       Windows Performance Monitor
 *          Processor count... (if can't read this check parent)
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface MonitoredRessource {
    MonitoredRessource getParent();
}
