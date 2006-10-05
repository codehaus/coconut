/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.xml.parsers.ParserConfigurationException;

import org.coconut.annotation.ThreadSafe;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.spi.CacheErrorHandler;
import org.coconut.cache.store.CacheStore;
import org.coconut.core.Clock;
import org.coconut.core.Log;
import org.coconut.filter.Filter;
import org.xml.sax.SAXException;

/**
 * This class is the primary class used for representing the configuration of a
 * cache. All general-purpose <tt>Cache</tt> implementation classes should
 * have a constructor with a single argument taking a CacheConfiguration.
 * <p>
 * <b>Usage Examples.</b> The following creates a new cache with the name
 * MyCache. The cache has a maximum of 1000 elements and uses a
 * least-recently-used policy to determine which elements to evict when the
 * capacity has been reached. Elements are also marked as expired after not
 * having been request in 1 hour. Finally the cache is registered as a mbean
 * with the platform mbean server using the name of the cache.
 * 
 * <pre>
 * CacheConfiguration&lt;String, Integer&gt; cc = newConf();
 * cc.setName(&quot;MyCache&quot;);
 * cc.eviction().setPolicy(Policies.newLRU()).setMaximumCapacity(1000);
 * cc.expiration().setDefaultTimeout(60 * 60, TimeUnit.SECONDS);
 * cc.jmx().setRegister(true);
 * Cache&lt;String, Integer&gt; instance = cc.newInstance(Select_A_Cache_Impl);
 * </pre>
 * 
 * <p>
 * In addition to the general methods available on CacheConfiguration This class
 * is using a number of inner classes to . This The following categories exist
 * <dl>
 * <dt>{@link Backend}</dt>
 * <dd>Handles storage and loading of entries from cache loaders and cache
 * stores</dd>
 * <dt>{@link Events}</dt>
 * <dd>Handles dispatching of events</dd>
 * <dt>{@link Eviction}</dt>
 * <dd>Handles maximum size of the cache and what entries will be evicted</dd>
 * <dt>{@link Expiration}</dt>
 * <dd>Handles expired items and timeout settings for entries</dd>
 * <dt>{@link JMX}</dt>
 * <dd>Handles JMX registration of the cache</dd>
 * <dt>{@link Locking}</dt>
 * <dd>Handles locking of individual items and lock ordering</dd>
 * </dl>
 * <p>
 * A few point on the naming of methods. Methods postfixed with <tt>Hint</tt>
 * are merely hits to implementation on how to .... and can as such be ignored
 * by any implementation. They are only used to assist an implementation in
 * choosing performance optimal configuration.
 * <p>
 * If Extension of this class is possible with the following TODO what about
 * extensions of this class?? could be usefull for special purpose caches, if so
 * we should change some of the static methods to allow overriding.
 * <p>
 * Currently no XML mappings exists for CacheConfigurations, however this is
 * planned see <a href="http://jira.codehaus.org/browse/COCACHE-2">COCACHE-2</a>
 * <p>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@ThreadSafe(false)
public final class CacheConfiguration<K, V> implements Cloneable {

    /**
     * The default configuration that can be used by any cache that is not
     * provided with a cache configuration object.
     */
    public static final CacheConfiguration DEFAULT_CONFIGURATION = CacheConfiguration
            .newConf();

    public class Backend {

        /**
         * Returns the CacheConfiguration that this instance is part of.
         * 
         * @return the CacheConfiguration that this instance is part of
         */
        public CacheConfiguration<K, V> c() {
            return CacheConfiguration.this;
        }

        public CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> getExtendedLoader() {
            return extendedLoader;
        }

        /**
         * Returns the CacheLoader that the cache should use for loading new
         * key-value bindings. If this method returns <code>null</code> no
         * initial loader will be set.
         */
        public CacheLoader<? super K, ? extends V> getLoader() {
            return loader;
        }

        /**
         * Returns the {@link CacheStore} that the cache should use.
         * 
         * @return the CacheStore that the cache should use.
         */
        public CacheStore<K, V> getStore() {
            return store;
        }

        /**
         * Returns <code>true</code> if a cache loader has been set. This can
         * both a normal loader that loads a single value for a key, or an
         * extended loader that loads a {@link CacheEntry} for specific key. Use
         * {@link #getLoader()} or {@link #getExtendedLoader()} to retrieve the
         * loaders respectively.
         * 
         * @return <code>true</code> if a cache loader has been set
         * @see #setLoader(CacheLoader)
         * @see #setExtendedLoader(CacheLoader)
         */
        public boolean hasLoader() {
            return loader != null || extendedLoader != null;
        }

        // /**
        // * The cache will try to interrupt the thread that is loading a a
        // * element if one with a corresponding key is added. Primarily usefull
        // * for distributed caches with very expensive values (in terms of
        // * calculation) where an update for another host might occur in the
        // * middle of a calculation. Hmm not sure this is so usefull... Lets
        // see
        // * if this is a problem before doing anything against it.
        // */
        // public void setInterruptLoad(boolean interrupt) {
        //
        // }

        /**
         * Sets the loader that should be used for loading new elements into the
         * cache. If the specified loader is <code>null</code> no loader will
         * be used for loading new key-value bindings. All values must then put
         * into the cache by using put or putAll.
         * 
         * @param loader
         *            the loader to set
         * @return the current CacheConfiguration
         */
        public Backend setExtendedLoader(
                CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader) {
            if (CacheConfiguration.this.loader != null) {
                throw new IllegalStateException(
                        "loader already set, cannot set an extended loader");
            }
            extendedLoader = loader;
            return this;
        }

        /**
         * Sets the loader that should be used for loading new elements into the
         * cache. If the specified loader is <code>null</code> no loader will
         * be used for loading new key-value bindings. All values must then put
         * into the cache by using put or putAll.
         * 
         * @param loader
         *            the loader to set
         * @return the current CacheConfiguration
         */
        public Backend setLoader(CacheLoader<? super K, ? extends V> loader) {
            if (extendedLoader != null) {
                throw new IllegalStateException(
                        "extended loader already set, cannot set a loader");
            }
            CacheConfiguration.this.loader = loader;
            return this;
        }

        public Backend setStore(CacheStore<K, V> cacheStore) {
            // check loader, extended loader
            store = cacheStore;
            return this;
        }

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
            return CacheConfiguration.this;
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

    private boolean statisticsEnabled = false;

    public class Statistics {
        public void setTimeUnit(TimeUnit unit) {
            if (unit == null) {
                throw new NullPointerException("unit is null");
            }
            statisticsTimeUnit = unit;
        }

        public TimeUnit getTimeUnit() {
            return statisticsTimeUnit;
        }

        public void setEnabled(boolean isEnabled) {
            statisticsEnabled = isEnabled;
        }

        public boolean getEnabled() {
            return statisticsEnabled;
        }

    }

    public class Eviction {

        /**
         * This is probably only usefull when we have configured the writeback
         * strategy. Or perhaps if we have lots of lock contension.
         * 
         * @return the number of items to prune
         */
        public int getCapacityNumberOfItemsToPrune() {
            return 1;
            // 0 = conservative prune as little as possible
        }

        // public static class EvictionStrategy {
        // public static EvictionStrategy PCT_25 = null;
        //
        // public static EvictionStrategy PCT_50 = null;
        //
        // public static EvictionStrategy PCT_75 = null;
        //
        // public static EvictionStrategy ALL = null;
        //
        // private final boolean isPct=true;
        // public static EvictionStrategy pctEviction(float pct) {
        // return null;
        // }
        // public static EvictionStrategy numEviction(float pct) {
        // return null;
        // }
        // public int getNumberToPrune(int currentSize) {
        // return 1;
        // }
        // }
        // hmm class CapacityStrategy

        // number of items to evict every time, is this usefull?
        //

        /**
         * Returns the CacheConfiguration that this instance is part of.
         * 
         * @return the CacheConfiguration that this instance is part of
         */
        public CacheConfiguration<K, V> c() {
            return CacheConfiguration.this;
        }

        /**
         * Integer.MAX_VALUE = unlimited
         * 
         * @return the maximum number of elements.
         */
        public int getMaximumCapacity() {
            return maximumElements;
        }

        public ReplacementPolicy getPolicy() {
            return replacementPolicy;

        }

        /**
         * Sets that maximum number of elements that a cache can contain. If the
         * limit is reached the cache should evict elements according to the
         * cache policy specified in setExpirationStrategy.
         * <p>
         * The default value is Integer.MAX_VALUE TODO we might allow 0 as
         * value, if its 0 it will imidiatly discard the element.
         * 
         * @param elements
         *            the maximum capacity.
         */
        public Eviction setMaximumCapacity(int elements) {
            if (elements <= 0) {
                throw new IllegalArgumentException(
                        "number of maximum elements must be greater then 0, was "
                                + elements);
            }
            maximumElements = elements;
            return this;
        }

        public Eviction setPolicy(ReplacementPolicy policy) {
            // policies are passed a CacheEntry, because it is the easiest way
            // to support cost/sized based policies.
            replacementPolicy = policy;
            return this;
        }

        public Eviction setUseSoftReferenceOnEvictedEntriesHits(boolean useit) {
            // the idea is to maintain a softreference to items that are
            // evicted.
            // when we run low on memory they will automatically be removed by
            // the
            // gc.
            // Its
            return this;
        }
    }

    public class Expiration {
        /**
         * Returns the CacheConfiguration that this instance is part of.
         * 
         * @return the CacheConfiguration that this instance is part of
         */
        public CacheConfiguration<K, V> c() {
            return CacheConfiguration.this;
        }

        public long getDefaultTimeout(TimeUnit unit) {
            if (defaultExpirationTimeoutDuration == Cache.NEVER_EXPIRE) {
                // don't convert relative to time unit
                return defaultExpirationTimeoutDuration;
            } else {
                return unit.convert(defaultExpirationTimeoutDuration,
                        TimeUnit.NANOSECONDS);
            }
        }

        public Filter<CacheEntry<K, V>> getFilter() {
            return expirationFilter;
        }

        public ExpirationStrategy getStrategy() {
            return expirationStrategy;
        }

        /**
         * Sets the default expiration time for elements added to the cache.
         * Elements added using the {@link Cache#put(Object, Object)} will
         * expire at <tt>time_of_insert + default_expiration_time</tt>.
         * Expired elements are handled accordingly to the
         * {@link ExpirationStrategy} set for the cache using
         * {@link #setFilter(Filter)}. The default expiration is infinite, that
         * is elements never expires..
         * 
         * @param duration
         *            the default timeout for elements added to the cache, must
         *            be positive
         * @param unit
         *            the time unit of the duration argument
         */
        public Expiration setDefaultTimeout(long duration, TimeUnit unit) {
            if (duration <= 0) {
                throw new IllegalArgumentException(
                        "duration must be greather then 0, was " + duration);
            } else if (unit == null) {
                throw new NullPointerException("unit is null");
            }
            if (duration == Cache.NEVER_EXPIRE) {
                defaultExpirationTimeoutDuration = Cache.NEVER_EXPIRE;
                // don't convert relative to time unit
            } else {
                defaultExpirationTimeoutDuration = unit.toNanos(duration);
            }
            return this;
        }

        /**
         * Sets a specific expiration that can be used in <tt>addition</tt> to
         * the time based expiration filter to check if items has expired. If no
         * filter has been set items are expired according to their registered
         * expiration time. If an expiration filter is set cache entries are
         * first checked against that filter then against the time based
         * expiration times.
         */
        public Expiration setFilter(Filter<CacheEntry<K, V>> filter) {
            expirationFilter = filter;
            return this;
        }

        /**
         * Sets the {@link ExpirationStrategy} that the cache should use. If no
         * expiration strategy is set the {@link #DEFAULT_EXPIRATION_STRATEGY}
         * default expiration strategy is used.
         * 
         * @param strategy
         *            the expiration strategy
         * @return the current CacheConfiguration
         * @throws NullPointerException
         *             if the specified expirationStrategy is <code>null</code>
         * @see #DEFAULT_EXPIRATION_STRATEGY
         */
        public Expiration setStrategy(ExpirationStrategy strategy) {
            if (strategy == null) {
                throw new NullPointerException(
                        "strategy is null, an expiration strategy must be defined");
            }
            expirationStrategy = strategy;
            return this;
        }

        // Perhaps a version where we could specify a time duration for how long
        // time
        // we want to serve expired content in the case of SERVE_OLD before
        // switching to STRICT.

        public Expiration setCustomExpirator(Filter<? extends Cache> filter) {
            // kunne også være rart med en version, der f.eks. kunne tage alle
            // store elementer
//            Filter f = new Filter<Cache>() {
//                private int evictFooElement;
//
//                public boolean accept(Cache element) {
//                    // TODO Auto-generated method stub
//                    return evictFooElement-- > 0;
//                }
//            };
            // mem notifier-> receive low
            // mem->f.evictFooElement=10000->cache.evict
            // to gange federe at sende som events
            return this;
        }
    }

    /**
     * A caches <tt>ExpirationStrategy</tt> indicates how the cache should
     * handle expired cache items. The following strategies must be supported by
     * any cache supporting expiration of cache items. Unless otherwise noted
     * this is a cache-wide setting and cannot be configured for individual
     * elements. Checks for expiration time are performed for the following
     * methods {@link Cache#containsKey(Object)},
     * {@link Cache#containsValue(Object)}, {@link Cache#peek},
     * {@link Cache#get(Object)}, {@link Cache#getAll} and for the ananologues
     * methods defined in the three <i>collection views</i>. The iterators for
     * the collection views will also check for expiration accordingly to the
     * expiration strategy. The {@link Cache#equals(Object)} method will
     * <tt>not</tt> check the expiration of any elements.
     * <p>
     * <ul>
     * <li>{@link #STRICT}<br>
     * Each time an element is requested through normal access or any of the
     * caches iterators the expiration status of the item is checked. If the
     * item has expired the requsting thread is blocked and any CacheLoader
     * defined is called to retrieve a new value. If no CacheLoader is defined
     * <tt>null</tt> is returned.</li>
     * <li>{@link #LAZY}<br>
     * As {@link #STRICT}, however, the current element will be served for any
     * request until the CacheLoader has retrieved a new value (most likely
     * asynchronously). If no CacheLoader is defined the cache will keep serving
     * the expired element and take no action.</li>
     * <li>{@link #ON_EVICT}<br>
     * Expiration of an item is only checked when {@link Cache#evict} is called.
     * All elements are served even if they are expired.</li>
     * </ul>
     * <p>
     * A call to {@link Cache#evict} will check all elements for expiration for
     * any of the three strategies.
     * <p>
     * The default expiration strategy is {@link #ON_EVICT}
     */
    public static enum ExpirationStrategy {
        /**
         * 
         */
        LAZY,

        /**
         * 
         */
        ON_EVICT,

        /**
         * 
         */
        STRICT;

    }

    /**
     * This class defines JMX configuration. To register a cache in the platform
     * MBeanServer with the name TODO, simply call
     * <code>setAutomaticRegister(true)</code>. If for some reason the cache
     * fails to properly register with the MBeanServer at construction time a
     * {@link CacheException} is thrown. The user remember to deregister the jmx
     * instance when shutting the down the server. (Or use a softreference)
     */
    public class JMX {

        /**
         * Returns the CacheConfiguration that this instance is part of.
         * 
         * @return the CacheConfiguration that this instance is part of
         */
        public CacheConfiguration<K, V> c() {
            return CacheConfiguration.this;
        }

        /**
         * @return the configured MBeanServer or the platform MBeanServer if no
         *         server has been set
         */
        public MBeanServer getMBeanServer() {
            if (mBeanServer == null) {
                mBeanServer = ManagementFactory.getPlatformMBeanServer();
            }
            return mBeanServer;
        }

        /**
         * @return the configured MBeanServer or the platform MBeanServer if no
         *         server has been set
         * @throws IllegalStateException
         *             if no ObjectName was configured
         */
        public ObjectName getObjectName() {
            if (oName == null) {
                try {
                    String name = getName();

                    ObjectName on = new ObjectName(JMX_PREFIX + name);
                    // we properly shouldn't keep a reference to this.
                    // because the name of the cache might change.
                    return on;
                } catch (MalformedObjectNameException e) {
                    // this only happens if the user has set a lame name
                    throw new IllegalStateException("The configured name of the cache, ",
                            e);
                }

            }
            return oName;
        }

        /**
         * Returns whether or not the cache is automatically registered with a
         * {@link javax.management.MBeanServer} when constructed.
         * 
         * @return <tt>true</tt> if the cache should be automatically
         *         registered with a {@link javax.management.MBeanServer},
         *         otherwise <tt>false</tt>
         * @see #setRegister(boolean)
         */
        public boolean isRegister() {
            return registerForJMXAutomatically;
        }

        /**
         * We can now deliver jmx events.
         * 
         * @param events
         *            the types of events that should be delivered
         * @return this configuration
         */
        public JMX registerEventsForNotification(Class<? extends CacheEvent>... events) {
            return this;
        }

        /**
         * Sets the {@link MBeanServer}} that the cache should register with.
         * If no value is set the platform {@link MBeanServer} will be used.
         * 
         * @param server
         *            the server that the cache should register with
         * @return this configuration
         * @throws NullPointerException
         *             if server is <tt>null</tt>
         */
        public JMX setMbeanServer(MBeanServer server) {
            if (server == null) {
                throw new NullPointerException("server is null");
            }
            mBeanServer = server;
            return this;
        }

        /**
         * Sets the specific {@link ObjectName} that this cache should register
         * with. If no ObjectName but the cache has been registered with a name
         * {@link #setName()} the cache will be registered under foo+name. If no
         * name has been set foo+S System.identityHashCode(cache) the name
         * should probably also be initialized to this value (hexed)
         * 
         * @param name
         *            the object name
         * @return this configuration
         */
        public JMX setObjectName(ObjectName name) {
            return this;
        }

        /**
         * Determines whether or not the cache will be register with its
         * MBeanServer at construction time.
         * 
         * @param registerAutomatic
         *            whether or not the cache should register with the
         *            configured MBeanServer at startup
         * @return this configuration
         * @see #isRegister()
         * @see #setObjectName(ObjectName)
         */
        public JMX setRegister(boolean registerAutomatic) {
            registerForJMXAutomatically = registerAutomatic;
            return this;
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

        public Locking setLocking(LockStrategy locking) {
            return this;
        }

        public Locking setAllowLockNonExistingElements(boolean allowLocking) {
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

    public final class Prefetch {
        // public void setPrefetch(long time, TimeUnit unit) {
        //
        // }
        //
        // /**
        // * The cache will automatically try to prefetch elements that are
        // close
        // * to expiration.
        // */
        // public void setAutomaticPrefetch() {
        //
        // }

        // default prefetch=1 = never prefetch
        /**
         * @param value
         */
        public void setPrefetchHint(double value) {

        }
        // det man i virkeligheden har brug for er en fuzzy configurator der
        // giver et tal mellem 0-1 alt efter hvor tæt på elementet er på
        // expiration
        // så kan f.eks. sige at når det når op på .99 så prefetcher vi.
        // kunne også være fornuftigt med en score fra cache policy
        // 1=so hot that we don't reckon it will be removed soon
        // 0.000111 well it will most likely be removed in .. seconds

        // data must be normalized to 0->1
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

    public static final Clock DEFAULT_CLOCK = Clock.MILLI_CLOCK;

    public static final ExpirationStrategy DEFAULT_EXPIRATION_STRATEGY = ExpirationStrategy.ON_EVICT;

    public static final String JMX_PREFIX = "org.coconut.cache:type=Cache,name=";

    public static void main(String[] args) throws SAXException, IOException,
            ParserConfigurationException {
        //     
        // DocumentBuilder parser =
        // DocumentBuilderFactory.newInstance().newDocumentBuilder();
        // System.out.println(ExpirationStrategy.valueOf("ON_EVICT"));
        // Document document = parser.parse(new File("c:/d.xml"));
        // System.out.println(document.getDocumentElement().getNodeName());
        // String cacheInstance =
        // document.getDocumentElement().getAttribute("classs");

        CacheConfiguration<String, Integer> cc = newConf();

        cc.setName("MyCache");
        // cc.backend().setLoader(myLoader);
        cc.eviction().setMaximumCapacity(1000);
        cc.expiration().setDefaultTimeout(100, TimeUnit.SECONDS);
        cc.jmx().setRegister(true);
        for (CacheEntry<String, Integer> entry : cc.newInstance(null).query(null)) {

        }
        // Cache<String,Integer> cache=cc.newInstance(UnlimitedCache.class);
        // // cc.parseDocument(document.getDocumentElement());
        // // System.out.println("bye" + cacheInstance.length());
        // cc.jmx().setRegister(true);
    }

    public static <K, V> CacheConfiguration<K, V> newConf() {
        return new CacheConfiguration<K, V>();
    }

    /**
     * Creates a new cache instance of the specified type using this
     * configuration.
     * 
     * @param clazz
     *            the clazz the type of cache that should be created
     * @return a new Cache instance
     * @throws IllegalArgumentException
     *             if specified class does not contain a public constructor
     *             taking a single CacheConfiguration argument
     * @throws NullPointerException
     *             if the specified class is <tt>null</tt>
     */
    public Cache<K, V> newInstance(Class<? extends Cache> clazz)
            throws IllegalArgumentException {
        if (clazz == null) {
            throw new NullPointerException("clazz is null");
        }
        try {
            Constructor<Cache<K, V>> c = (Constructor<Cache<K, V>>) clazz
                    .getDeclaredConstructor(CacheConfiguration.class);
            return c.newInstance(this);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not create cache instance", e);
        }
    }

    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private long defaultExpirationTimeoutDuration = Cache.NEVER_EXPIRE;

    private Filter<CacheEntry<K, V>> expirationFilter;

    private ExpirationStrategy expirationStrategy = DEFAULT_EXPIRATION_STRATEGY;

    private CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> extendedLoader;

    private Map<? extends K, ? extends V> initialMap;

    private CacheLoader<? super K, ? extends V> loader;

    private CacheErrorHandler<K, V> errorHandler = CacheErrorHandler.DEFAULT;

    private int maximumElements = Integer.MAX_VALUE;

    private MBeanServer mBeanServer;

    private String name;

    private ObjectName oName;

    /** Whether or not the cache is automatically registered for JMX usage. */
    private boolean registerForJMXAutomatically;

    private ReplacementPolicy replacementPolicy;

    private CacheStore<K, V> store;

    private Clock timingStrategy = Clock.MILLI_CLOCK;

    /**
     * Creates a new CacheConfiguration by copying an existing configuration.
     * 
     * @param otherConfiguration
     *            the configuration to copy from
     */
    public CacheConfiguration(CacheConfiguration<K, V> otherConfiguration) {
        throw new UnsupportedOperationException(); // TODO implement
    }

    /**
     * Creates a new Configuration with default settings. CacheConfiguration
     * instances should be created using the {@link #newConf()} method.
     */
    protected CacheConfiguration() {
        // package private for now, might change at a later time.
    }

    public Backend backend() {
        return new Backend();
    }

    /**
     * Creates a new CacheConfiguration by copying this configuration.
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        return new CacheConfiguration<K, V>(this);
    }

    /**
     * Returns a new Eviction object.
     */
    public Eviction eviction() {
        return new Eviction();
    }

    /**
     * Returns a {@link Expiration} instance that can be used to configure the
     * expiration settings for the cache. For example, default expiration times
     * or custom expiration filters.
     * 
     * @return returns a expiration configuration instance
     */
    public Expiration expiration() {
        return new Expiration();
    }

    /**
     * Returns the {@link org.coconut.core.Clock} that the cache should use.
     * 
     * @return the Clock that the cache should use
     * @see #setClock(Clock)
     */
    public Clock getClock() {
        return timingStrategy;
    }

    /**
     * Returns a map whose mappings are to be placed in the cache as initial
     * entries or <code>null</code> if no map has been specified.
     * 
     * @see #setInitialMap(Map)
     */
    public Map<? extends K, ? extends V> getInitialMap() {
        return initialMap;
    }

    /**
     * Returns the log configured for the cache.
     * 
     * @return the log configured for the cache, or <tt>null</tt> if no log is
     *         configured
     * @see #setLog(Log)
     */
    public CacheErrorHandler<K, V> getErrorHandler() {
        return errorHandler;
    }

    /**
     * Returns the name of the cache.
     * 
     * @return the name of the cache, or <tt>null</tt> if no name has been
     *         set.
     * @see #setName(String)
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a {@link JMX} instance that can be used to configure the JMX
     * settings for the cache.
     * 
     * @return returns a jmx configuration instance
     */
    public JMX jmx() {
        return new JMX();
    }

    /**
     * Sets the {@link org.coconut.core.Clock} that the cache should use.
     * Normally users should not need to set this, only if they want to provide
     * another timing mechanism then the built-in
     * {@link java.lang.System#currentTimeMillis()}. For example, a custom NTP
     * protocol.
     * 
     * @param timingStrategy
     *            the Timer to use
     * @return this configuration
     * @throws NullPointerException
     *             if the supplied timingStrategy is <tt>null</tt>
     */
    public CacheConfiguration<K, V> setClock(Clock timingStrategy) {
        if (timingStrategy == null) {
            throw new NullPointerException("timingStrategy is null");
        }
        this.timingStrategy = timingStrategy;
        return this;
    }

    /**
     * Sets a map whose mappings are to be placed in the Cache as initial
     * entries.
     * 
     * @param map
     *            the map to set
     * @return this configuration
     * @see #getInitialMap()
     * @throws NullPointerException
     *             if map is <tt>null</tt>
     */
    public CacheConfiguration<K, V> setInitialMap(Map<? extends K, ? extends V> map) {
        if (map == null) {
            throw new NullPointerException("map is null");
        }
        initialMap = map;
        return this;
    }

    /**
     * Sets the log that the cache should use for logging anomalies and errors.
     * If no log is set the cache will redirect all output to dev/null
     * 
     * @param log
     *            the log to use
     * @return this configuration
     * @see org.coconut.core.Logs
     * @see org.coconut.core.Log
     * @throws NullPointerException
     *             if log is <tt>null</tt>
     */
    public CacheConfiguration<K, V> setErrorHandler(CacheErrorHandler<K, V> errorHandler) {
        if (errorHandler == null) {
            throw new NullPointerException("errorHandler is null");
        }
        this.errorHandler = errorHandler;
        return this;
    }

    /**
     * Sets the name of the cache. The name should be unique among other
     * configured caches. Allthough not a strict requirement it is advised that
     * only alphanumeric characters are used in the name.
     * <p>
     * If no name is set, most cache implementation will generate an unique name
     * to reference the cache. How exactly this name is generated is
     * implementation specific.
     * 
     * @param name
     *            the name of the cache
     * @return this configuration
     * @see #getName()
     * @throws NullPointerException
     *             if name is <tt>null</tt>
     */
    public CacheConfiguration<K, V> setName(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        this.name = name;
        return this;
    }

    /**
     * Some cache implementations might allow additional properties to be set
     * then those defined by this class. This method can be used to set these
     * additional properties.
     * 
     * @param key
     *            the key of the property
     * @param value
     *            the value of the property
     * @return this configuration
     * @see #getProperties()
     * @see #getProperty(String)
     * @see #getProperty(String, Object)
     * @throws NullPointerException
     *             if key is <tt>null</tt>
     */
    public CacheConfiguration<K, V> setProperty(String key, Object value) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        additionalProperties.put(key, value);
        return this;
    }

    /**
     * Returns a map of any additional properties that is defined. This map is a
     * copy of the current properties.
     * 
     * @see #setProperty(String, Object)
     */
    public Map<String, ?> getProperties() {
        return Collections.unmodifiableMap(new HashMap<String, Object>(
                additionalProperties));
    }

    /**
     * Returns the property value for the specified key or <tt>null</tt> if no
     * such property exists. A <tt>null</tt> can also indicate that the key
     * was explicitly mapped to <tt>null</tt>.
     * 
     * @param key
     *            the key for which to retrieve the value
     * @return the value of the property or <tt>null</tt> if no such property
     *         exists
     * @throws NullPointerException
     *             if key is <tt>null</tt>
     */
    public Object getProperty(String key) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        return additionalProperties.get(key);
    }

    /**
     * Returns the property value for the specified key or the specified default
     * value if no such property exists. A property does not exists if it is
     * mapped to <tt>null</tt> either explicitly or because no such entry
     * exists.
     * 
     * @param key
     *            the key for which to retrieve the value
     * @param defaultValue
     *            the default value to return if the property does not exist
     * @return the value of the property or the specified default value if the
     *         property does not exist
     * @throws NullPointerException
     *             if key is <tt>null</tt>
     */
    public Object getProperty(String key, Object defaultValue) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }

        Object result = additionalProperties.get(key);
        return result == null ? defaultValue : result;
    }
    // public void saveConfigurationAsXml(OutputStream stream) {
    // Document doc;
    // }
    // protected void parseDocument(Element doc) {
    // Node loader = doc.getElementsByTagName("cache-loader").item(0);
    // System.out.println(loader.getAttributes().getNamedItem("class"));
    //
    // }
    //
    // @SuppressWarnings("unchecked")
    // public static <K, V> Cache<K, V> loadCache(InputStream is) throws
    // Exception {
    // CacheConfiguration<K, V> conf = loadConfiguration(is);
    // String name = null;
    //
    // Class c = Class.forName(name);
    // Constructor<Cache> cons = c.getConstructor(CacheConfiguration.class);
    // return cons.newInstance(conf);
    // }
    //
    // public static <K, V> CacheConfiguration<K, V>
    // loadConfiguration(InputStream is) {
    // BufferedInputStream bis = new BufferedInputStream(is);
    // bis.mark(Integer.MAX_VALUE);
    // Properties props = tryLoadProperties(bis);
    // if (props == null) {
    // // try load xml
    // }
    // return null;
    // }
    // /**
    // * Returns an unmodifiable view of the specified configuration. This
    // method
    // * allows modules to provide users with "read-only" access to
    // * configurations. Query (get) operations on the returned configuration
    // * "read through" to the specified configuration, and attempts to modify
    // the
    // * returned configuration result in an UnsupportedOperationException.
    // * <p>
    // * If you need an unmodifiable version that is not backed by the existing
    // * configuration use new CacheConfiguration(existing
    // * configuration).unmodifiableConfiguration();
    // *
    // * @return an unmodifiable version of the current configuration
    // */
    // public CacheConfiguration<K, V> unmodifiableConfiguration() {
    // throw new UnsupportedOperationException();
    // }
    //    
    //
    // private static Properties tryLoadFromXML(InputStream is) {
    // Properties props = new Properties();
    // try {
    // props.load(is);
    // System.out.println(props.size());
    // props.list(System.err);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return props;
    // }
    //
    // private static Properties tryLoadProperties(InputStream is) {
    // Properties props = new Properties();
    // try {
    // props.load(is);
    // System.out.println(props.size());
    // props.list(System.err);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return props;
    // }
    // public static <K, V> Cache<K, V> fromInputStream(InputStream stream) {
    // return CacheFactory.fromInputStream(stream);
    // }
    //
    //
    // /**
    // * Override current configuration with *non default* values from specified
    // * configuration
    // *
    // * @param overwriteWith
    // * @return
    // */
    // public CacheConfiguration<K, V> overwriteConfiguration(
    // CacheConfiguration<K, V> overwriteWith) {
    // //do we need this, not for now i think
    // return null;
    // }

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
