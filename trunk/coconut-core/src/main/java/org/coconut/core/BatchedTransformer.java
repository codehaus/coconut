/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.core;

import java.util.List;

import org.coconut.core.Transformer;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface BatchedTransformer<F, T> extends Transformer<F, T> {
    List<? extends T> transformAll(List<? extends F> list);
}
