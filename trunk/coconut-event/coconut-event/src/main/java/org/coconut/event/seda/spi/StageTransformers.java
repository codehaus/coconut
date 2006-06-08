/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event.seda.spi;

import org.coconut.core.Transformer;
import org.coconut.core.Transformers;
import org.coconut.event.seda.management.StageStatistics;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class StageTransformers {
    public final static Transformer<StageStatistics, String> INFO_TO_NAME = Transformers
            .transform(StageStatistics.class, "getName");
}
