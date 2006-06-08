package org.coconut.event.util;

import java.util.concurrent.Delayed;

import org.coconut.core.Clock;

//TODO move to sandbox
public class TimeUtil {

    public Delayed fromCron(String cronExpression, Clock timer) {
        //don't know about this, think Delayed is one-shot, it no repeating
        return null;
    }
    
    public Delayed fromCron(String cronExpression) {
        return null;
    }
}
