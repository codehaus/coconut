/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.store.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;



/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class SimpleFileStore<K, V>  {
/*
    private static String ff = "c:/cache/" + System.currentTimeMillis() + "/";

    public static void main(String[] args) throws Exception {
        File base = new File(ff);
        if (!base.exists())
            base.mkdir();

        MutableInt mi = new MutableInt();
        OldCacheStore<MutableInt, MutableInt> cs = new SimpleFileStore<MutableInt, MutableInt>();
        long l = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            mi.value = i;
            cs.store(mi, mi);
        }
        System.out.println(System.currentTimeMillis() - l);
    }
    */
//    /**
//     * @throws ClassNotFoundException
//     * @throws IOException
//     * @see org.coconut.cache.util.AbstractCacheLoader#load(K)
//     */
//    public V load(K key) throws IOException, ClassNotFoundException {
//        File f = getFolder(key);
//        if (!f.exists())
//            return null;
//        File file = getFile(key);
//
//        final FileInputStream str;
//        try {
//            str = new FileInputStream(file);
//        } catch (FileNotFoundException e) {
//            return null; // no value found
//        }
//        ObjectInputStream oos = new ObjectInputStream(str);
//        K oKey = (K) oos.readObject();
//        if (key.equals(oKey)) {
//            V oValue = (V) oos.readObject();
//            return oValue;
//        }
//        oos.close();
//        return null;
//    }
//    /**
//     * @see org.coconut.cache.OldCacheStore#erase(K)
//     */
//    public void erase(K key) throws IOException {
//        File f = getFile(key);
//        f.delete();
//    }
//
//    /**
//     * @see org.coconut.cache.OldCacheStore#store(K, V)
//     */
//    public void store(K key, V value) throws Exception {
//
//        File f = getFolder(key);
//        if (!f.exists())
//            f.mkdir();
//        File file = getFile(key);
//        // RandomAccessFile raf = new RandomAccessFile(file, "rw");
//
//        FileOutputStream str = new FileOutputStream(file);
//        ObjectOutputStream oos = new ObjectOutputStream(str);
//        oos.writeObject(key);
//        oos.writeObject(value);
//        oos.close();
//
//    }
////    private File getFolder(K key) {
////        String filename = getFolderName(key.hashCode());
////        File f = new File(ff + filename);
////        return f;
////    }
////    private File getFile(K key) {
////        File f = new File(ff + getFolderName(key.hashCode()) + getFileName(key.hashCode()));
////        return f;
////    }
//    private static String getFolderName(int i) {
//        char[] chars = new char[3];
//        chars[0] = DIGITS[(i >> 28) & 0xf];
//        chars[1] = DIGITS[(i >> 24) & 0xf];
//        chars[2] = '/';
//        return new String(chars);
//    }
//    private static String getFileName(int i) {
//        char[] chars = new char[6];
//        chars[0] = DIGITS[(i >> 20) & 0xf];
//        chars[1] = DIGITS[(i >> 16) & 0xf];
//        chars[2] = DIGITS[(i >> 12) & 0xf];
//        chars[3] = DIGITS[(i >> 8) & 0xf];
//        chars[4] = DIGITS[(i >> 4) & 0xf];
//        chars[5] = DIGITS[i & 0xf];
//        return new String(chars);
//    }
//
//    private final static char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
//        'B', 'C', 'D', 'E', 'F' };
//
//    private static class MutableInt implements Serializable {
//        /**
//         * Comment for <code>serialVersionUID</code>
//         */
//        private static final long serialVersionUID = 1L;
//        int value;
//
//        public int hashCode() {
//            return value;
//        }
//    }
//
//    /**
//     * @see org.coconut.cache.store.OldCacheStore#entries()
//     */
//    public Iterable entries() throws Exception {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    /**
//     * @see org.coconut.cache.OldCacheStore#entries()
//     */
////    public Iterable<Entry< ? extends K, ? extends V>> entries() throws Exception {
////        return null;
////    }

}
