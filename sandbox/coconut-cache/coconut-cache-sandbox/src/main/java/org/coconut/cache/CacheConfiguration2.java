/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.coconut.cache.spi.AbstractCache;
import org.coconut.cache.spi.CacheUtil;
import org.coconut.cache.spi.XmlConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheConfiguration2<K, V> extends CacheConfiguration<K, V> {

    
    public void setInstallShutdownHooks(boolean installShutdownHooks) {
        
    }
    /**
     * WRITE_THROUGH = put/load value, make avilable for others, persist,
     * return. Is there any _observable_ difference between WRITE_THROUGH &
     * WRITE_THROUGH_ASYNC??? I can't think of a reason why we want all but the
     * caller to get() to see a value???
     * <p>
     * WRITE_THROUGH_SAFE = put/load value, persist, make avilable for others,
     * return
     * <p>
     * WRITE_THROUGH_ASYNC = put/load value, make available for other, return,
     * persist asynchronously later
     * <p>
     * WRITE_BACK = put/load value, make available for other, return, persist at
     * latests possible time. perhaps drop WRITE_THROUGH_ASYNC
     */
    public static enum StorageStrategy {
        WRITE_BACK, WRITE_THROUGH, WRITE_THROUGH_ASYNC, WRITE_THROUGH_SAFE;
    }

    // /**
    // * @return the configured MBeanServer or the platform MBeanServer if
    // no
    // * server has been set
    // * @throws IllegalStateException
    // * if no ObjectName was configured
    // */
    // public ObjectName getObjectName() {
    // if (oName == null) {
    // try {
    // String name = getName();
    // ObjectName on = new ObjectName(DEFAULT_JMX_DOMAIN + ":name=" + name);
    // // we properly shouldn't keep a reference to this.
    // // because the name of the cache might change.
    // return on;
    // } catch (MalformedObjectNameException e) {
    // // this only happens if the user has set a lame name
    // // TODO lets make sure
    // throw new IllegalStateException("The configured name of the cache, ",
    // e);
    // }
    //
    // }
    // return oName;
    // }



    /**
     * Returns an unmodifiable view of the specified configuration. This method
     * allows modules to provide users with "read-only" access to
     * configurations. Query (get) operations on the returned configuration
     * "read through" to the specified configuration, and attempts to modify the
     * returned configuration result in an UnsupportedOperationException.
     * <p>
     * If you need an unmodifiable version that is not backed by the existing
     * configuration use new CacheConfiguration(existing
     * configuration).unmodifiableConfiguration();
     * 
     * @return an unmodifiable version of the current configuration
     */
    public CacheConfiguration<K, V> unmodifiableConfiguration() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a new CacheConfiguration by copying this configuration.
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        return new CacheConfiguration2<K, V>(this);
    }

    /**
     * Creates a new CacheConfiguration by copying an existing configuration.
     * 
     * @param otherConfiguration
     *            the configuration to copy from
     */
    public CacheConfiguration2(CacheConfiguration2<K, V> otherConfiguration) {
        throw new UnsupportedOperationException(); // TODO implement
    }

    private boolean enableTiming = true;

    public static void main2(String[] args) throws Exception {
        //     
        // DocumentBuilder parser =
        // DocumentBuilderFactory.newInstance().newDocumentBuilder();
        // System.out.println(ExpirationStrategy.valueOf("ON_EVICT"));
        // Document document = parser.parse(new File("c:/d.xml"));
        // System.out.println(document.getDocumentElement().getNodeName());
        // String cacheInstance =
        // document.getDocumentElement().getAttribute("classs");

        // Loading / Storing / Prefetch / storage
        // events
        // statistics/jmx ? monitoring????
        // locking /transactions (thread safety)
        //        
        CacheConfiguration<String, Integer> cc = create();

        cc.setName("MyCache");
        // cc.backend().setLoader(myLoader);
        cc.eviction().setMaximumSize(1000);
        cc.expiration().setDefaultTimeout(100, TimeUnit.SECONDS);
        cc.jmx().setAutoRegister(true);

        // Cache<String,Integer> cache=cc.newInstance(UnlimitedCache.class);
        // // cc.parseDocument(document.getDocumentElement());
        // // System.out.println("bye" + cacheInstance.length());
        // cc.jmx().setRegister(true);
    }

    /**
     * Note this setting is only used if an EventStrategy has been set.
     */
    public static enum EventNumbering {

        /**
         * nextId>=previousID
         */
        INCREASING,

        /**
         * No Event sequence number is generated and
         * {@link java.lang.Long#MIN_VALUE} is returned as sequence id.
         */
        NONE,

        /**
         * nextId=lastPreviousId+1 Important, might in some data structure that
         * sorts incoming elements. no gaps allowed then
         */
        ORDERED,

        /**
         * nextId>previousID
         */
        STRICTLY_INCREASING,

        /**
         * see timing policy generate timestamp.
         */
        TIME_STAMP;
    }

    public class Events {

        public CacheConfiguration<K, V> c() {
            return CacheConfiguration2.this;
        }

        public boolean getSendEarly(Class<? extends CacheEvent> event) {
            return true;
        }

        public boolean isEnabled() {
            return false;
        }

        public void setEnabled(boolean isEnabled) {

        }

        /**
         * This can be used to indicate that certain events
         * 
         * @param event
         * @param sendEarly
         */
        public Events setSendEarly(Class<? extends CacheEvent> event, boolean sendEarly) {
            return this;
        }
    }

    /*
     * none -> seqid=0; unique inc -> unique increasing numbers putconsist ->
     * put(a,"1") , put(a,"2") . "1".seqid<"2".seqId strong -> put(a,"1") ,
     * get(a)="1" -> put.seqid<get.seqid VeryStrong -> clear ->
     */
    public static enum EventStrategy {
        /**
         * No events are ever posted. Trying to create a subscription on the
         * caches eventsbus will throw an {@link UnsupportedOperationException}.
         */
        NO_EVENTS,

        /**
         * update operations are synchronized
         */
        NORMAL,

        /**
         * update and get operations are synchronized
         */
        STRONG,

        /**
         * No order . Information purposes
         */
        WEAK;
    }

    private TimeUnit statisticsTimeUnit = TimeUnit.MILLISECONDS;

    private boolean useHighResolutionCounter = true;

    public class Statistics2 extends CacheConfiguration.Statistics {

        public TimeUnit getTimeUnit() {
            return statisticsTimeUnit;
        }

        public void setTimeUnit(TimeUnit unit) {
            if (unit == null) {
                throw new NullPointerException("unit is null");
            }
            statisticsTimeUnit = unit;
        }
    }

    public class Locking {

        // when we lock, we need all keys to implement comparable
        // unless we have used setLockKeyComparator(Comparator<K>))
        // we could have a setUseOnlyExclusiveLock method, calling readLock
        // on ReadWriteLock would result in a unsupportedOperationException
        //
        public Comparator<K> getLockKeyComparator() {
            return null;
        }

        public Locking setAllowLockNonExistingElements(boolean allowLocking) {
            return this;
        }

        public Locking setLocking(LockStrategy locking) {
            return this;
        }

        /**
         * This property will we ignored if Locking is set to NO_LOCKING How
         * long time we . If set to 0 = infinite
         * 
         * @param timeout
         *            the default lock timeout
         * @param unit
         *            the unit of the timeout
         * @return this configuration
         */
        public Locking setLockInternalTimeout(long timeout, TimeUnit unit) {
            // difference for put/get???
            // well this needs to throw some kind of exception if it fails??
            // not sure this under the 80/20 rule
            return this;
        }

        /**
         * Too avoid deadlock when multiple locks on the different objects are
         * requested. A lock comparator can be set to make sure that locks are
         * allways acquired in the specified order. If no lock comparator is set
         * the default order among the keys are used. This is only needed if
         * acquiring locks on multiple objects on the same time.
         * 
         * @param lockKeyComperator
         *            the comperator to use for generating combo locks
         * @return this configuration
         */
        public Locking setLockKeyComparator(Comparator<K> lockKeyComperator) {
            // if null natural lock order among keys is used
            return this;
        }

        // This will override
        // public void setInternalLockTimeoutPut(long timeout, TimeUnit unit) {
        // //difference for put/get???
        //

        /**
         * if this false the users needs to explicitly lock things.
         * 
         * @param lockOnGetPut
         *            whether or not to use locks explicitly
         * @return this configuration
         */
        public Locking setLockOnPutGet(boolean lockOnGetPut) {
            return this;
        }
    }

    /**
     * What about peek does it lock it???
     */
    public static enum LockStrategy {
        /**
         * 
         */
        NO_LOCKING,

        /**
         * Trying to acquire a readLock on the ReadWriteLock will throw an
         * unsupported operation.
         */
        READ_WRITE,

        /**
         * Trying to acquire a readLock on the ReadWriteLock will throw an
         * unsupported operation.
         */
        WRITE_ONLY;
    }

    // public static <K, V> Cache<K, V> fromFile(File file)
    // throws FileNotFoundException {
    // return fromInputStream(new FileInputStream(file));
    // }
    //
    // public static <K, V> Cache<K, V> fromClasspath(String path) {
    // return fromInputStream(Cache.class.getResourceAsStream(path));
    // }

    // /**
    // * These are items that does not effect the semantics of a cache, but are
    // * merely hints to the cache implementation. Implementations are free to
    // * ignore these.
    // * <p>
    // * TODO or just as normal methods on cacheconfiguration postfixed with
    // hint.
    // */
    // public class Hints {
    //
    // }
}
