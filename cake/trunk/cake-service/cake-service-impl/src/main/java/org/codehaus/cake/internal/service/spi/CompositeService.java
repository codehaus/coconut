/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.internal.service.spi;

import java.util.Collection;

public interface CompositeService {
    Collection<?> getChildServices();
}
