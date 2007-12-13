/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.coconut.attribute.Attributes;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class PolicyTestUtils {
    static final Integer[] VALUES;

    private PolicyTestUtils() {}

    static {
        VALUES = new Integer[1000];
        for (int i = 0; i < VALUES.length; i++) {
            VALUES[i] = i;
        }
    }

    public static int[] addToPolicy(ReplacementPolicy<Integer> policy, int start, int stop) {
        int[] result = new int[stop - start + 1];
        for (int i = start; i <= stop; i++) {
            result[i] = policy.add(VALUES[i], Attributes.EMPTY_MAP);
        }
        return result;
    } 

    public static Integer[] evict(ReplacementPolicy<Integer> policy, int num) {
        if (num > 1000)
            throw new IllegalArgumentException("must be <1000");
        Integer[] result = new Integer[num];
        for (int i = 0; i < result.length; i++) {
            result[i] = policy.evictNext();
        }
        return result;
    }

    public static List<Integer> evictList(ReplacementPolicy<Integer> policy, int num) {
        Integer[] result = new Integer[num];
        for (int i = 0; i < result.length; i++) {
            result[i] = policy.evictNext();
        }
        return Arrays.asList(result);
    }

    public static List<Integer> empty(ReplacementPolicy<Integer> policy) {
        LinkedList<Integer> list = new LinkedList<Integer>();
        for (;;) {
            Integer i = policy.evictNext();
            if (i != null)
                list.add(i);
            else
                break;
        }
        return list;
    }

}
