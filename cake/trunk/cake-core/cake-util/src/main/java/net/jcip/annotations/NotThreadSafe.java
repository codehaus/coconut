/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
/*
 * Copyright (c) 2005 Brian Goetz and Tim Peierls Released under the Creative Commons
 * Attribution License (http://creativecommons.org/licenses/by/2.5) Official home:
 * http://www.jcip.net Any republication or derived work distributed in source code form
 * must include this copyright and license notice.
 */
package net.jcip.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The class to which this annotation is applied is not thread-safe. This annotation primarily
 * exists for clarifying the non-thread-safety of a class that might otherwise be assumed to be
 * thread-safe, despite the fact that it is a bad idea to assume a class is thread-safe without good
 * reason.
 * 
 * @version $Id: NotThreadSafe.java 542 2008-01-02 21:50:05Z kasper $
 * @see ThreadSafe
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotThreadSafe {
}
