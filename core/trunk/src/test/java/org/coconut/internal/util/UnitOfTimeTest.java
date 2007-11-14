/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class UnitOfTimeTest extends TestCase {

    public static long SHORT_DELAY_MS;
    public static long SMALL_DELAY_MS;
    public static long MEDIUM_DELAY_MS;
    public static long LONG_DELAY_MS;

    /**
     * Returns the shortest timed delay. This could
     * be reimplemented to use for example a Property.
     */
    protected long getShortDelay() {
        return 50;
    }


    /**
     * Sets delays as multiples of SHORT_DELAY.
     */
    protected  void setDelays() {
        SHORT_DELAY_MS = getShortDelay();
        SMALL_DELAY_MS = SHORT_DELAY_MS * 5;
        MEDIUM_DELAY_MS = SHORT_DELAY_MS * 10;
        LONG_DELAY_MS = SHORT_DELAY_MS * 50;
    }

    /**
     * Flag set true if any threadAssert methods fail
     */
    volatile boolean threadFailed;

    /**
     * Initializes test to indicate that no thread assertions have failed
     */
    public void setUp() {
        setDelays();
        threadFailed = false;
    }

    /**
     * Triggers test case failure if any thread assertions have failed
     */
    public void tearDown() {
        assertFalse(threadFailed);
    }

    /**
     * Fail, also setting status to indicate current testcase should fail
     */
    public void threadFail(String reason) {
        threadFailed = true;
        fail(reason);
    }
    
    /**
     * threadFail with message "should throw exception"
     */
    public void threadShouldThrow() {
        threadFailed = true;
        fail("should throw exception");
    }

    /**
     * threadFail with message "Unexpected exception"
     */
    public void threadUnexpectedException() {
        threadFailed = true;
        fail("Unexpected exception");
    }
    /**
     * fail with message "Unexpected exception"
     */
    public void unexpectedException() {
        fail("Unexpected exception");
    }
    /**
     * convert correctly converts sample values across the units
     */
    public void testConvert() {
        for (long t = 0; t < 88888; ++t) {
            assertEquals(t*60*60*24, 
                         UnitOfTime.SECONDS.convert(t, 
                                                  UnitOfTime.DAYS));
            assertEquals(t*60*60, 
                         UnitOfTime.SECONDS.convert(t, 
                                                  UnitOfTime.HOURS));
            assertEquals(t*60, 
                         UnitOfTime.SECONDS.convert(t, 
                                                  UnitOfTime.MINUTES));
            assertEquals(t, 
                         UnitOfTime.SECONDS.convert(t, 
                                                  UnitOfTime.SECONDS));
            assertEquals(t, 
                         UnitOfTime.SECONDS.convert(1000L*t, 
                                                  UnitOfTime.MILLISECONDS));
            assertEquals(t, 
                         UnitOfTime.SECONDS.convert(1000000L*t, 
                                                  UnitOfTime.MICROSECONDS));
            assertEquals(t, 
                         UnitOfTime.SECONDS.convert(1000000000L*t, 
                                                  UnitOfTime.NANOSECONDS));


            assertEquals(1000L*t*60*60*24, 
                         UnitOfTime.MILLISECONDS.convert(t, 
                                                  UnitOfTime.DAYS));
            assertEquals(1000L*t*60*60, 
                         UnitOfTime.MILLISECONDS.convert(t, 
                                                  UnitOfTime.HOURS));
            assertEquals(1000L*t*60, 
                         UnitOfTime.MILLISECONDS.convert(t, 
                                                  UnitOfTime.MINUTES));
            assertEquals(1000L*t, 
                         UnitOfTime.MILLISECONDS.convert(t, 
                                                  UnitOfTime.SECONDS));
            assertEquals(t, 
                         UnitOfTime.MILLISECONDS.convert(t, 
                                                  UnitOfTime.MILLISECONDS));
            assertEquals(t, 
                         UnitOfTime.MILLISECONDS.convert(1000L*t, 
                                                  UnitOfTime.MICROSECONDS));
            assertEquals(t, 
                         UnitOfTime.MILLISECONDS.convert(1000000L*t, 
                                                  UnitOfTime.NANOSECONDS));

            assertEquals(1000000L*t*60*60*24, 
                         UnitOfTime.MICROSECONDS.convert(t, 
                                                  UnitOfTime.DAYS));
            assertEquals(1000000L*t*60*60, 
                         UnitOfTime.MICROSECONDS.convert(t, 
                                                  UnitOfTime.HOURS));
            assertEquals(1000000L*t*60, 
                         UnitOfTime.MICROSECONDS.convert(t, 
                                                  UnitOfTime.MINUTES));
            assertEquals(1000000L*t, 
                         UnitOfTime.MICROSECONDS.convert(t, 
                                                  UnitOfTime.SECONDS));
            assertEquals(1000L*t, 
                         UnitOfTime.MICROSECONDS.convert(t, 
                                                  UnitOfTime.MILLISECONDS));
            assertEquals(t, 
                         UnitOfTime.MICROSECONDS.convert(t, 
                                                  UnitOfTime.MICROSECONDS));
            assertEquals(t, 
                         UnitOfTime.MICROSECONDS.convert(1000L*t, 
                                                  UnitOfTime.NANOSECONDS));

            assertEquals(1000000000L*t*60*60*24, 
                         UnitOfTime.NANOSECONDS.convert(t, 
                                                  UnitOfTime.DAYS));
            assertEquals(1000000000L*t*60*60, 
                         UnitOfTime.NANOSECONDS.convert(t, 
                                                  UnitOfTime.HOURS));
            assertEquals(1000000000L*t*60, 
                         UnitOfTime.NANOSECONDS.convert(t, 
                                                  UnitOfTime.MINUTES));
            assertEquals(1000000000L*t, 
                         UnitOfTime.NANOSECONDS.convert(t, 
                                                  UnitOfTime.SECONDS));
            assertEquals(1000000L*t, 
                         UnitOfTime.NANOSECONDS.convert(t, 
                                                  UnitOfTime.MILLISECONDS));
            assertEquals(1000L*t, 
                         UnitOfTime.NANOSECONDS.convert(t, 
                                                  UnitOfTime.MICROSECONDS));
            assertEquals(t, 
                         UnitOfTime.NANOSECONDS.convert(t, 
                                                  UnitOfTime.NANOSECONDS));
        }
    }

    /**
     * toNanos correctly converts sample values in different units to
     * nanoseconds
     */
    public void testToNanos() {
        for (long t = 0; t < 88888; ++t) {
            assertEquals(t*1000000000L*60*60*24, 
                         UnitOfTime.DAYS.toNanos(t));
            assertEquals(t*1000000000L*60*60, 
                         UnitOfTime.HOURS.toNanos(t));
            assertEquals(t*1000000000L*60, 
                         UnitOfTime.MINUTES.toNanos(t));
            assertEquals(1000000000L*t, 
                         UnitOfTime.SECONDS.toNanos(t));
            assertEquals(1000000L*t, 
                         UnitOfTime.MILLISECONDS.toNanos(t));
            assertEquals(1000L*t, 
                         UnitOfTime.MICROSECONDS.toNanos(t));
            assertEquals(t, 
                         UnitOfTime.NANOSECONDS.toNanos(t));
        }
    }

    /**
     * toMicros correctly converts sample values in different units to
     * microseconds
     */
    public void testToMicros() {
        for (long t = 0; t < 88888; ++t) {
            assertEquals(t*1000000L*60*60*24, 
                         UnitOfTime.DAYS.toMicros(t));
            assertEquals(t*1000000L*60*60, 
                         UnitOfTime.HOURS.toMicros(t));
            assertEquals(t*1000000L*60, 
                         UnitOfTime.MINUTES.toMicros(t));
            assertEquals(1000000L*t, 
                         UnitOfTime.SECONDS.toMicros(t));
            assertEquals(1000L*t, 
                         UnitOfTime.MILLISECONDS.toMicros(t));
            assertEquals(t, 
                         UnitOfTime.MICROSECONDS.toMicros(t));
            assertEquals(t, 
                         UnitOfTime.NANOSECONDS.toMicros(t*1000L));
        }
    }

    /**
     * toMillis correctly converts sample values in different units to
     * milliseconds
     */
    public void testToMillis() {
        for (long t = 0; t < 88888; ++t) {
            assertEquals(t*1000L*60*60*24, 
                         UnitOfTime.DAYS.toMillis(t));
            assertEquals(t*1000L*60*60, 
                         UnitOfTime.HOURS.toMillis(t));
            assertEquals(t*1000L*60, 
                         UnitOfTime.MINUTES.toMillis(t));
            assertEquals(1000L*t, 
                         UnitOfTime.SECONDS.toMillis(t));
            assertEquals(t, 
                         UnitOfTime.MILLISECONDS.toMillis(t));
            assertEquals(t, 
                         UnitOfTime.MICROSECONDS.toMillis(t*1000L));
            assertEquals(t, 
                         UnitOfTime.NANOSECONDS.toMillis(t*1000000L));
        }
    }

    /**
     * toSeconds correctly converts sample values in different units to
     * seconds
     */
    public void testToSeconds() {
        for (long t = 0; t < 88888; ++t) {
            assertEquals(t*60*60*24, 
                         UnitOfTime.DAYS.toSeconds(t));
            assertEquals(t*60*60, 
                         UnitOfTime.HOURS.toSeconds(t));
            assertEquals(t*60, 
                         UnitOfTime.MINUTES.toSeconds(t));
            assertEquals(t, 
                         UnitOfTime.SECONDS.toSeconds(t));
            assertEquals(t, 
                         UnitOfTime.MILLISECONDS.toSeconds(t*1000L));
            assertEquals(t, 
                         UnitOfTime.MICROSECONDS.toSeconds(t*1000000L));
            assertEquals(t, 
                         UnitOfTime.NANOSECONDS.toSeconds(t*1000000000L));
        }
    }

    /**
     * toMinutes correctly converts sample values in different units to
     * minutes
     */
    public void testToMinutes() {
        for (long t = 0; t < 88888; ++t) {
            assertEquals(t*60*24, 
                         UnitOfTime.DAYS.toMinutes(t));
            assertEquals(t*60, 
                         UnitOfTime.HOURS.toMinutes(t));
            assertEquals(t, 
                         UnitOfTime.MINUTES.toMinutes(t));
            assertEquals(t, 
                         UnitOfTime.SECONDS.toMinutes(t*60));
            assertEquals(t, 
                         UnitOfTime.MILLISECONDS.toMinutes(t*1000L*60));
            assertEquals(t, 
                         UnitOfTime.MICROSECONDS.toMinutes(t*1000000L*60));
            assertEquals(t, 
                         UnitOfTime.NANOSECONDS.toMinutes(t*1000000000L*60));
        }
    }

    /**
     * toHours correctly converts sample values in different units to
     * hours
     */
    public void testToHours() {
        for (long t = 0; t < 88888; ++t) {
            assertEquals(t*24, 
                         UnitOfTime.DAYS.toHours(t));
            assertEquals(t, 
                         UnitOfTime.HOURS.toHours(t));
            assertEquals(t, 
                         UnitOfTime.MINUTES.toHours(t*60));
            assertEquals(t, 
                         UnitOfTime.SECONDS.toHours(t*60*60));
            assertEquals(t, 
                         UnitOfTime.MILLISECONDS.toHours(t*1000L*60*60));
            assertEquals(t, 
                         UnitOfTime.MICROSECONDS.toHours(t*1000000L*60*60));
            assertEquals(t, 
                         UnitOfTime.NANOSECONDS.toHours(t*1000000000L*60*60));
        }
    }

    /**
     * toDays correctly converts sample values in different units to
     * days
     */
    public void testToDays() {
        for (long t = 0; t < 88888; ++t) {
            assertEquals(t, 
                         UnitOfTime.DAYS.toDays(t));
            assertEquals(t, 
                         UnitOfTime.HOURS.toDays(t*24));
            assertEquals(t, 
                         UnitOfTime.MINUTES.toDays(t*60*24));
            assertEquals(t, 
                         UnitOfTime.SECONDS.toDays(t*60*60*24));
            assertEquals(t, 
                         UnitOfTime.MILLISECONDS.toDays(t*1000L*60*60*24));
            assertEquals(t, 
                         UnitOfTime.MICROSECONDS.toDays(t*1000000L*60*60*24));
            assertEquals(t, 
                         UnitOfTime.NANOSECONDS.toDays(t*1000000000L*60*60*24));
        }
    }


    /**
     * convert saturates positive too-large values to Long.MAX_VALUE 
     * and negative to LONG.MIN_VALUE
     */
    public void testConvertSaturate() {
        assertEquals(Long.MAX_VALUE,
                     UnitOfTime.NANOSECONDS.convert(Long.MAX_VALUE / 2,
                                                  UnitOfTime.SECONDS));
        assertEquals(Long.MIN_VALUE,
                     UnitOfTime.NANOSECONDS.convert(-Long.MAX_VALUE / 4,
                                                  UnitOfTime.SECONDS));
        assertEquals(Long.MAX_VALUE,
                     UnitOfTime.NANOSECONDS.convert(Long.MAX_VALUE / 2,
                                                  UnitOfTime.MINUTES));
        assertEquals(Long.MIN_VALUE,
                     UnitOfTime.NANOSECONDS.convert(-Long.MAX_VALUE / 4,
                                                  UnitOfTime.MINUTES));
        assertEquals(Long.MAX_VALUE,
                     UnitOfTime.NANOSECONDS.convert(Long.MAX_VALUE / 2,
                                                  UnitOfTime.HOURS));
        assertEquals(Long.MIN_VALUE,
                     UnitOfTime.NANOSECONDS.convert(-Long.MAX_VALUE / 4,
                                                  UnitOfTime.HOURS));
        assertEquals(Long.MAX_VALUE,
                     UnitOfTime.NANOSECONDS.convert(Long.MAX_VALUE / 2,
                                                  UnitOfTime.DAYS));
        assertEquals(Long.MIN_VALUE,
                     UnitOfTime.NANOSECONDS.convert(-Long.MAX_VALUE / 4,
                                                  UnitOfTime.DAYS));

    }

    /**
     * toNanos saturates positive too-large values to Long.MAX_VALUE 
     * and negative to LONG.MIN_VALUE
     */
    public void testToNanosSaturate() {
            assertEquals(Long.MAX_VALUE,
                         UnitOfTime.MILLISECONDS.toNanos(Long.MAX_VALUE / 2));
            assertEquals(Long.MIN_VALUE,
                         UnitOfTime.MILLISECONDS.toNanos(-Long.MAX_VALUE / 3));
    }


    /**
     * toString returns string containing common name of unit
     */
    public void testToString() {
        String s = UnitOfTime.SECONDS.toString();
        assertTrue(s.indexOf("ECOND") >= 0);
    }

    
    /**
     *  Timed wait without holding lock throws
     *  IllegalMonitorStateException
     */
    public void testTimedWait_IllegalMonitorException() {
    //created a new thread with anonymous runnable

        Thread t = new Thread(new Runnable() {
                public void run() {
                    Object o = new Object();
                    UnitOfTime tu = UnitOfTime.MILLISECONDS;
                    try {
                        tu.timedWait(o,LONG_DELAY_MS);
                        threadShouldThrow();
                    }
                    catch (InterruptedException ie) {
                        threadUnexpectedException();
                    } 
                    catch(IllegalMonitorStateException success) {
                    }
                    
                }
            });
        t.start();
        try {
            Thread.sleep(SHORT_DELAY_MS);
            t.interrupt();
            t.join();
        } catch(Exception e) {
            unexpectedException();
        }
    }
    
    /**
     * timedWait throws InterruptedException when interrupted
     */
    public void testTimedWait() {
    Thread t = new Thread(new Runnable() {
        public void run() {
            Object o = new Object();
            
            UnitOfTime tu = UnitOfTime.MILLISECONDS;
            try {
            synchronized(o) {
                tu.timedWait(o,MEDIUM_DELAY_MS);
            }
                        threadShouldThrow();
            }
            catch(InterruptedException success) {} 
            catch(IllegalMonitorStateException failure) {
            threadUnexpectedException();
            }
        }
        });
    t.start();
        try {
            Thread.sleep(SHORT_DELAY_MS);
            t.interrupt();
            t.join();
        } catch(Exception e) {
            unexpectedException();
        }
    }
    
    
    /**
     * timedJoin throws InterruptedException when interrupted
     */
    public void testTimedJoin() {
    Thread t = new Thread(new Runnable() {
        public void run() {
            UnitOfTime tu = UnitOfTime.MILLISECONDS;    
            try {
            Thread s = new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        Thread.sleep(MEDIUM_DELAY_MS);
                                    } catch(InterruptedException success){}
                                }
                            });
            s.start();
            tu.timedJoin(s,MEDIUM_DELAY_MS);
                        threadShouldThrow();
            }
            catch(Exception e) {}
        }
        });
    t.start();
        try {
            Thread.sleep(SHORT_DELAY_MS);
            t.interrupt();
            t.join();
        } catch(Exception e) {
            unexpectedException();
        }
    }
    
    /**
     *  timedSleep throws InterruptedException when interrupted
     */
    public void testTimedSleep() {
    //created a new thread with anonymous runnable

    Thread t = new Thread(new Runnable() {
        public void run() {
            UnitOfTime tu = UnitOfTime.MILLISECONDS;
            try {
            tu.sleep(MEDIUM_DELAY_MS);
                        threadShouldThrow();
            }
            catch(InterruptedException success) {} 
        }
        });
    t.start();
        try {
            Thread.sleep(SHORT_DELAY_MS);
            t.interrupt();
            t.join();
        } catch(Exception e) {
            unexpectedException();
        }
    }

    /**
     * a deserialized serialized unit is equal 
     */
    public void testSerialization() {
        UnitOfTime q = UnitOfTime.MILLISECONDS;

        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(10000);
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
            out.writeObject(q);
            out.close();

            ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(bin));
            UnitOfTime r = (UnitOfTime)in.readObject();
            
            assertEquals(q.toString(), r.toString());
        } catch(Exception e){
            e.printStackTrace();
            unexpectedException();
        }
    }


}
