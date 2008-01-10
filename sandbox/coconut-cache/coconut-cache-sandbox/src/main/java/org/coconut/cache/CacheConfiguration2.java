/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;


/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheConfiguration2<K, V> extends CacheConfiguration<K, V> {


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

    
    public void setInstallShutdownHooks(boolean installShutdownHooks) {
        
    }


    private boolean enableTiming = true;

    private boolean useHighResolutionCounter = true;



}
