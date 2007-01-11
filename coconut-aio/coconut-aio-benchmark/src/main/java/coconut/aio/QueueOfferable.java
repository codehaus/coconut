/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package coconut.aio;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.coconut.core.EventProcessor;
import org.coconut.core.Offerable;

public class QueueOfferable implements Offerable, Runnable {

    private final BlockingQueue queue = new LinkedBlockingQueue();
    private final EventProcessor handler;
    public QueueOfferable(EventProcessor handler) {
        this.handler = handler;
    }
    public QueueOfferable start() {
        Executors.newSingleThreadExecutor().submit(this);
        return this;
    }
    /**
     * @see coconut.core.Offerable#offer(java.lang.Object)
     */
    public boolean offer(Object o) {
        queue.add(o);
        return true;
    }
    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        for (;;) {
            try {
                Object o = queue.take();
                handler.process(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}