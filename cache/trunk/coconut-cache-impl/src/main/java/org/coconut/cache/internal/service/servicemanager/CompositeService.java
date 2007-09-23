package org.coconut.cache.internal.service.servicemanager;

import java.util.Collection;

public interface CompositeService {
    Collection<?> getChildServices();
}
