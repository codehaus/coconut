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
 * The class to which this annotation is applied is immutable. This means that its state cannot be
 * seen to change by callers, which implies that
 * <ul>
 * <li> all public fields are final, </li>
 * <li> all public final reference fields refer to other immutable objects, and </li>
 * <li> constructors and methods do not publish references to any internal state which is
 * potentially mutable by the implementation. </li>
 * </ul>
 * Immutable objects may still have internal mutable state for purposes of performance optimization;
 * some state variables may be lazily computed, so long as they are computed from immutable state
 * and that callers cannot tell the difference.
 * <p>
 * Immutable objects are inherently thread-safe; they may be passed between threads or published
 * without synchronization.
 * 
 * @version $Id: Immutable.java 542 2008-01-02 21:50:05Z kasper $
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Immutable {

}
