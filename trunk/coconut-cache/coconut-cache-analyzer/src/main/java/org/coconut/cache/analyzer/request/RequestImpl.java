/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
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
