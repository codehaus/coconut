/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public class CollectionUtils {

    public static final Map.Entry<Integer, String> M1 = newEntry(1, "A");

    public static final Map.Entry<Integer, String> M1_NULL_VALUE = newEntry(
            null, "A");

    public static final Map.Entry<Integer, String> M1_KEY_NULL = newEntry(1,
            null);

    public static final Map.Entry<Integer, String> M2 = newEntry(2, "B");

    public static final Map.Entry<Integer, String> M3 = newEntry(3, "C");

    public static final Map.Entry<Integer, String> M4 = newEntry(4, "D");

    public static final Map.Entry<Integer, String> M5 = newEntry(5, "E");

    public static final Map.Entry<Integer, String> M6 = newEntry(6, "F");

    public static final Map.Entry<Integer, String> M7 = newEntry(7, "G");

    public static final Map.Entry<Integer, String> M8 = newEntry(8, "H");

    public static final Map.Entry<Integer, String> M9 = newEntry(9, "I");

    public static final Map<Integer, String> M1_TO_M5_MAP = asMap(M1, M2, M3,
            M4, M5);

    public static final Set<Map.Entry<Integer, String>> M1_TO_M5_SET = new HashSet<Map.Entry<Integer, String>>(
            Arrays.asList(M1, M2, M3, M4, M5));

    public static final Collection<String> M1_TO_M5_VALUES = Arrays.asList(M1
            .getValue(), M2.getValue(), M3.getValue(), M4.getValue(), M5
            .getValue());

    public static final Collection<Integer> M1_TO_M5_KEY_SET = Arrays.asList(M1
            .getKey(), M2.getKey(), M3.getKey(), M4.getKey(), M5.getKey());

    static final Integer[] M1_TO_M5_KEY_ARRAY = M1_TO_M5_KEY_SET.toArray(new Integer[0]);

    
    public static final Map.Entry<Integer, String> MNAN1 = newEntry(1, "B");

    public static final Map.Entry<Integer, String> MNAN2 = newEntry(2, "A");

    public static final Map.Entry<Integer, String> MNAN3 = newEntry(3, "D");

    public static final Map.Entry<Integer, String> MNAN4 = newEntry(4, "C");

    public static Map.Entry<Integer, String> newEntry(Integer key, String value) {
        return new ImmutableMapEntry<Integer, String>(key, value);

    }

    public static Collection<Integer> seq(int start, int stop) {
        List<Integer> l = new ArrayList<Integer>(Math.abs(stop - start));
        for (int i = Math.min(start, stop); i <= Math.max(start, stop); i++) {
            l.add(i);
        }
        if (stop < start)
            Collections.reverse(l);
        return l;

    }

    public static Map<Integer, Integer> seqMap(int start, int end) {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = start; i < end + 1; i++) {
            map.put(i, i);
        }
        return map;
    }

    public static Map<Integer, String> asMap(
            Map.Entry<Integer, String>... entries) {
        Map<Integer, String> map = new LinkedHashMap<Integer, String>();
        for (Map.Entry<Integer, String> name : entries) {
            map.put(name.getKey(), name.getValue());
        }
        return map;
    }

    public static Set<Integer> asSet(int... data) {
        return new HashSet<Integer>(asList(data));
    }

    public static String getValue(int key) {
        return "" + (char) (key + 64);
    }
    public static Collection<Integer> asList(int... data) {
        ArrayList<Integer> list = new ArrayList<Integer>(data.length);
        for (int i : data)
            list.add(i);
        return list;
    }

}
