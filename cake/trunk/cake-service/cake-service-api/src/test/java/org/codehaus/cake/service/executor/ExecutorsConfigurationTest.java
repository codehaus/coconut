/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.service.executor;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class ExecutorsConfigurationTest {
    @Test
    public void domain() {
        ExecutorsManager em = new ExecutorsManager() {};
        ExecutorsConfiguration emb = new ExecutorsConfiguration();
        assertNull(emb.getExecutorManager());
        assertSame(emb, emb.setExecutorManager(em));
        assertSame(em, emb.getExecutorManager());
    }
}
