/**
 * 
 */
package org.coconut.apm.sandbox;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Usager {

    private int highActive;

    private int lowActive;

    private int currentActive;

    private long longestActive;

    private long shortestActive;

    public Runnable start() {
        synchronized (this) {
            currentActive++;
            if (highActive > currentActive) {
                highActive = currentActive;
            }
        }
        return new Runnable() {
            public void run() {
                synchronized (Usager.this) {
                    currentActive--;
                    if (lowActive < currentActive) {
                        lowActive = currentActive;
                    }
                }
            }
        };
    }
}
