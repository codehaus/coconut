/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.test.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.coconut.cache.service.loading.AbstractCacheLoader;
import org.coconut.test.TestUtil;

/**
 * A simple cache loader used for testing. Will return 1->A, 2->B, 3->C, 4->D, 5->E and
 * <code>null</code> for any other key.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class IntegerToStringLoader extends AbstractCacheLoader<Integer, String> {

    public static final Attribute RESULT_ATTRIBUTE_KEY = TestUtil.dummy(Attribute.class);

    private final AtomicLong totalLoads = new AtomicLong();

    private volatile Thread loaderThread;

    private volatile CountDownLatch latch;

    private volatile Integer latestKey;

    private volatile AttributeMap latestMap;

    private volatile int base;

    private volatile boolean doReturnNull;

    public void setDoReturnNull(boolean doReturnNull) {
        this.doReturnNull = doReturnNull;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public void incBase() {
        base++;
    }

    public long getNumberOfLoads() {
        return totalLoads.get();
    }

    public Thread getLoaderThread() {
        return loaderThread;
    }

    public CountDownLatch initializeLatch(int count) {
        latch = new CountDownLatch(count);
        return latch;
    }

    /** {@inheritDoc} */
    public String load(Integer key, AttributeMap ignore) throws Exception {
        latestKey = key;
        latestMap = ignore;
        loaderThread = Thread.currentThread();
        totalLoads.incrementAndGet();
        if (latch != null) {
            latch.countDown();
        }
        if (ignore != null && ignore.containsKey(RESULT_ATTRIBUTE_KEY)) {
            return (String) ignore.get(RESULT_ATTRIBUTE_KEY);
        }
        if (1 <= key && key <= 5 && !doReturnNull) {
            return "" + (char) (key + 64 + base);
        } else {
            return null;
        }
    }

    public Integer getLatestKey() {
        return latestKey;
    }

    public AttributeMap getLatestAttributeMap() {
        return latestMap;
    }
}
