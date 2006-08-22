/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.analyzer.request;

import java.util.Date;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class RequestImpl {
    public static void main(String[] args) {
        System.out.println(new Date(((long)Integer.MAX_VALUE)*1000));
    }
}
