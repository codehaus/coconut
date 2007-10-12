package org.coconut.cache.test.util.lifecycle;

import org.coconut.cache.test.util.AbstractLifecycleVerifier;
import org.coconut.filter.Filter;

public class LifecycleFilter extends AbstractLifecycleVerifier implements Filter {
    public boolean accept(Object element) {
        return false;
    }
}
