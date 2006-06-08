/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event.seda.annotation;

import static org.coconut.event.sandbox.annotation.RessourceUsage.HIGH_ALLOCATION;
import org.coconut.event.sandbox.annotation.StageDef;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@StageDef(value = "ping", sinks = { "pang", "pong" }, memoryUsage = HIGH_ALLOCATION)
public class StageDefTes {

}
