/**
 * 
 */
package org.coconut.apm.realexamples;

import java.util.Random;

import org.coconut.apm.monitor.DefaultMetricManager;
import org.coconut.apm.monitor.EnumCounter;
import org.coconut.apm.monitor.TimedAverage;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EnumRoller {
    public static void main(String[] args) throws Exception {
        DefaultMetricManager mg = new DefaultMetricManager();

        EnumCounter<Dice> dice = new EnumCounter<Dice>(Dice.class, "DiceRoll");
        mg.addMetric(dice);
        for (Dice d : Dice.values()) {
            mg.addMetric(new TimedAverage(dice.liveCount(d)));
        }
        mg.startAndRegister("my.app:name=Rolled Dices");
        for (int i = 0; i < 10000; i++) {
            dice.handle(Dice.roll());
            Thread.sleep(15);
        }
        mg.stopAndUnregister();
    }

    static enum Dice {
        D1, D2, D3, D4, D5, D6;
        private final static Random rnd = new Random();

        public static Dice roll() {
            return Dice.values()[rnd.nextInt(6)];
        }
    }
}
