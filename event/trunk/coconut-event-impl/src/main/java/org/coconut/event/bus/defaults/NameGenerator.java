package org.coconut.event.bus.defaults;

import java.util.concurrent.atomic.AtomicLong;

import org.coconut.operations.Ops.Generator;

public class NameGenerator implements Generator<String> {

    private final String prefix;

    private final AtomicLong idGenerator = new AtomicLong();

    public String generate() {
        return prefix + idGenerator.incrementAndGet();
    }

    public NameGenerator(String prefix) {
//        if (prefix == null) {
//            throw new NullPointerException("prefix is null");
//        }
        this.prefix = prefix;
    }

}
