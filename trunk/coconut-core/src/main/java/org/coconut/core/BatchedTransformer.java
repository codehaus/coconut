/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
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
