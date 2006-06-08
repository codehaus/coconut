package org.coconut.aio.impl.nio;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.core.EventHandler;


/**
 * A selector doing the actual selection process. This selector is single
 * threaded.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
final class AsyncSingleSelector implements Runnable {
    private final AtomicInteger state; 

    private Thread thread;
    private final Queue<Runnable> eventQueue = new ConcurrentLinkedQueue<Runnable>();
    //
    //private final ExceptionHandler errorHandler;
    private final SelectorMonitor monitor;
    private int selectTimeOut;

    private final Selector selector;
    private final ThreadFactory factory;
    AsyncSingleSelector(ThreadFactory fac, int selectTimeOut) throws IOException {
    	this.selector = Selector.open();
        this.thread = fac.newThread(this);

        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            public void uncaughtException(Thread t, Throwable e) {
                System.err.print("Unhandled exception " + t.getName());
                e.printStackTrace();
                thread = factory.newThread(AsyncSingleSelector.this);
                state.set(SelectorState.NOT_INITED.getValue());
                start();
            }
        });
        //this.errorHandler = netHandler.errorHandler;
        this.selectTimeOut = selectTimeOut;
        this.factory = fac;
        state = new AtomicInteger(SelectorState.NOT_INITED.getValue());
        monitor = new SelectorMonitor(); //just use the default (no-op) for
        monitor.opened(null);

    }

    /**
     * Return the thread id for thread used for doing the selection.
     * 
     * @return the id of the thread
     */
    long getThreadId() {
        return thread.getId();
    }

    /**
     * Start the selector
     *  
     */
    void start() {
        if (!state.compareAndSet(SelectorState.NOT_INITED.getValue(), SelectorState.INITED.getValue()))
            throw new IllegalStateException("state was " + state.get() + "expected -1");
        thread.start();
    }

    /**
     * The Actual loop doing the selector processing and dispatching
     *  
     */
    public void run() {
        if (!state.compareAndSet(SelectorState.INITED.getValue(), SelectorState.STARTED.getValue()))
            throw new IllegalStateException("state was " + state.get());

        while (state.get() == SelectorState.STARTED.getValue()) {
            Runnable event = eventQueue.poll();
            while (event != null) {
                event.run();
                event = eventQueue.poll();
            }

            int selectSize = 0;

            try {
                monitor.preSelect(selectTimeOut);

                if (selectTimeOut == 0)
                    selectSize = selector.selectNow();
                else if (selectTimeOut == -1)
                    selectSize = selector.select();
                else
                    selectSize = selector.select(selectTimeOut);

                monitor.postSelect(selectSize, null);
            } catch (IOException e) {
                //errorHandler.handleSelectSelect(this, e);
                //TODO error handling
                monitor.postSelect(0, e);
            }

            if (selectSize != 0) {
                final Set<SelectionKey> keys = selector.selectedKeys();

                for (Iterator<SelectionKey> iter = keys.iterator(); iter.hasNext();) {
                    final SelectionKey key = iter.next();
                    iter.remove();
                    try {
                        ((EventHandler) key.attachment()).handle(key);
                    } catch (Exception e1) {
                        //TODO methods should not throw exceptions
                        //Perhaps create a Safe event-handler?
                    }
                }
            }
        }
        //exit from the main loop, close down

        try {
            selector.close();
            monitor.closed(null);
        } catch (IOException e) {
            //errorHandler.handleSelectClose(this, e);
            //TODO error handling
            monitor.closed(e);
        }

        if (!state.compareAndSet(SelectorState.STOP_NOW.getValue(), SelectorState.STOPPED.getValue()))
            throw new IllegalStateException("state was " + state.get());

    }

    /**
     * Shut down the selector
     */
    void shutdown() {
        if (state.compareAndSet(SelectorState.STARTED.getValue(), SelectorState.STOP_NOW.getValue())) {
            selector.wakeup();
            monitor.wakeup();
        } else
            throw new IllegalStateException("Not running, or allready finished");
    }

    /**
     * Uses for registering channels for later selection
     * 
     * @param channel
     *            the to register on
     * @param ops
     *            the operation we want to register (see nio docs)
     * @param handler
     *            the eventhandler that events are dispatch to.
     * @return the selectionkey registered
     * @throws IOException
     */
    Callable registerChannel(final SelectableChannel channel, final int ops, final EventHandler handler) throws IOException {
        SelectionKey k = null;
        try {
            k = channel.register(selector, ops, handler);
        } catch (CancelledKeyException cke) {
            //sometimes we might first deregister a key
            //and then register the same channel again
            //if this happens without doing a select inbetween
            //we get a CancelledKeyException
            selector.selectNow(); //clear cancelled keys
            k = channel.register(selector, ops, handler);
        }
        final SelectionKey key = k;
        return new Callable() {
            public Object call() throws IOException {
                key.cancel();
                return null;
            }
        };

    }
    /**
     * Adds an event to the event processing queue
     * 
     * @param event
     *            the event to add
     */
    void addFuture(Runnable runnable) {
        eventQueue.add(runnable);
        selector.wakeup();
        monitor.wakeup();
    }
    
    private enum SelectorState {
        NOT_INITED(-1),INITED(0), STARTED(1),STOP_NOW(2),STOPPED(3);
        private final int value;
        private SelectorState(int value) {
            this.value=value;
        }
        private int getValue() {
            return value;
        }
    }
}